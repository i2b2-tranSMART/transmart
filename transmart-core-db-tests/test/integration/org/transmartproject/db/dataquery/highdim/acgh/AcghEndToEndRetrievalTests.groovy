/*
 * Copyright © 2013-2014 The Hyve B.V.
 *
 * This file is part of transmart-core-db.
 *
 * Transmart-core-db is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * transmart-core-db.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.transmartproject.db.dataquery.highdim.acgh

import com.google.common.collect.Lists
import grails.test.mixin.TestMixin
import groovy.test.GroovyAssert
import org.hibernate.SessionFactory
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.transmartproject.core.dataquery.TabularResult
import org.transmartproject.core.dataquery.assay.Assay
import org.transmartproject.core.dataquery.highdim.AssayColumn
import org.transmartproject.core.dataquery.highdim.HighDimensionDataTypeResource
import org.transmartproject.core.dataquery.highdim.HighDimensionResource
import org.transmartproject.core.dataquery.highdim.acgh.AcghValues
import org.transmartproject.core.dataquery.highdim.assayconstraints.AssayConstraint
import org.transmartproject.core.dataquery.highdim.chromoregion.Region
import org.transmartproject.core.dataquery.highdim.chromoregion.RegionRow
import org.transmartproject.core.dataquery.highdim.dataconstraints.DataConstraint
import org.transmartproject.core.dataquery.highdim.projections.Projection
import org.transmartproject.core.exceptions.InvalidArgumentsException
import org.transmartproject.core.querytool.ConstraintByOmicsValue
import org.transmartproject.db.dataquery.highdim.DeGplInfo
import org.transmartproject.db.dataquery.highdim.chromoregion.DeChromosomalRegion
import org.transmartproject.db.test.RuleBasedIntegrationTestMixin

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.transmartproject.db.dataquery.highdim.acgh.AcghModule.ACGH_VALUES_PROJECTION
import static org.transmartproject.db.dataquery.highdim.acgh.AcghTestData.TRIAL_NAME
import static org.transmartproject.db.test.Matchers.hasSameInterfaceProperties

@TestMixin(RuleBasedIntegrationTestMixin)
class AcghEndToEndRetrievalTests {

    HighDimensionResource highDimensionResourceService

    HighDimensionDataTypeResource<RegionRow> acghResource

    TabularResult<AssayColumn, RegionRow> dataQueryResult

    Projection<AcghValues> projection

    SessionFactory sessionFactory

    AcghTestData testData = new AcghTestData()

    private static final String concept_code = 'concept code #1'

    String conceptKey

    @Before
    void setUp() {
        testData.saveAll()
        sessionFactory.currentSession.flush()

        acghResource = highDimensionResourceService.getSubResourceForType 'acgh'

        /* projection never varies in our tests */
        projection = acghResource.createProjection([:], ACGH_VALUES_PROJECTION)

        conceptKey = '\\\\' + testData.concept.tableAccesses[0].tableCode + testData.concept.conceptDimensions[0].conceptPath
    }

    @After
    void tearDown() {
        dataQueryResult?.close()
    }

    @Test
    void basicTest() {
        def assayConstraints = [
            acghResource.createAssayConstraint(
                AssayConstraint.TRIAL_NAME_CONSTRAINT, name: TRIAL_NAME),
            acghResource.createAssayConstraint(
                AssayConstraint.PATIENT_SET_CONSTRAINT,
                result_instance_id: testData.allPatientsQueryResult.id),
        ]
        def dataConstraints = []

        dataQueryResult = acghResource.retrieveData assayConstraints, dataConstraints, projection

        assertThat dataQueryResult, allOf(
            is(notNullValue()),
            hasProperty('indicesList', contains(
                /* they're ordered by assay id */
                hasSameInterfaceProperties(Assay, testData.assays[1], ['platform']),
                hasSameInterfaceProperties(Assay, testData.assays[0], ['platform']),
            )),
            hasProperty('rowsDimensionLabel', equalTo('Regions')),
            hasProperty('columnsDimensionLabel', equalTo('Sample codes')),
        )

        List<AssayColumn> assayColumns = dataQueryResult.indicesList

        Iterator<RegionRow> rows = dataQueryResult.rows
        def regionRows = Lists.newArrayList(rows)

        assertThat regionRows, hasSize(2)
        /* results are ordered (asc) by region id */
        assertThat regionRows[0], allOf(
            hasSameInterfaceProperties(Region, testData.regions[1], ['platform']),
            hasProperty('label', equalTo(testData.regions[1].name)),
            hasProperty('bioMarker', equalTo(testData.regions[1].geneSymbol)),
            hasProperty('platform', allOf(
                hasProperty('id', equalTo(testData.regionPlatform.id)),
                hasProperty('title', equalTo(testData.regionPlatform.title)),
                hasProperty('organism', equalTo(testData.regionPlatform.organism)),
                hasProperty('annotationDate', equalTo(testData.regionPlatform.annotationDate)),
                hasProperty('markerType', equalTo(testData.regionPlatform.markerType)),
                hasProperty('genomeReleaseId', equalTo(testData.regionPlatform.genomeReleaseId)),
            )),
        )

        assertThat regionRows[1], allOf(
            hasSameInterfaceProperties(Region, testData.regions[0], ['platform']),
            hasProperty('label', equalTo(testData.regions[0].name)),
            hasProperty('bioMarker', equalTo(testData.regions[0].geneSymbol)),
            hasProperty('platform', allOf(
                hasProperty('id', equalTo(testData.regionPlatform.id)),
                hasProperty('title', equalTo(testData.regionPlatform.title)),
                hasProperty('organism', equalTo(testData.regionPlatform.organism)),
                hasProperty('annotationDate', equalTo(testData.regionPlatform.annotationDate)),
                hasProperty('markerType', equalTo(testData.regionPlatform.markerType)),
                hasProperty('genomeReleaseId', equalTo(testData.regionPlatform.genomeReleaseId)),
            )),
        )

        assertThat regionRows[1][assayColumns[1]],
            hasSameInterfaceProperties(AcghValues, testData.acghData[0])
        assertThat regionRows[1][assayColumns[0]],
            hasSameInterfaceProperties(AcghValues, testData.acghData[1])
        assertThat regionRows[0][assayColumns[1]],
            hasSameInterfaceProperties(AcghValues, testData.acghData[2])
        assertThat regionRows[0][assayColumns[0]],
            hasSameInterfaceProperties(AcghValues, testData.acghData[3])
    }

    @Test
    void testSegments_meetOne() {
        def assayConstraints = [
            acghResource.createAssayConstraint(
                AssayConstraint.PATIENT_SET_CONSTRAINT,
                result_instance_id: testData.allPatientsQueryResult.id),
        ]
        def dataConstraints = [
            // start matches start of regions[0]
            acghResource.createDataConstraint(
                DataConstraint.CHROMOSOME_SEGMENT_CONSTRAINT,
                chromosome: '1', start: 33, end: 44
            )
        ]

        dataQueryResult = acghResource.retrieveData assayConstraints, dataConstraints, projection

        def regionRows = Lists.newArrayList(dataQueryResult.rows)

        assertThat regionRows, hasSize(1)
        assertThat regionRows[0], hasSameInterfaceProperties(
            Region, testData.regions[0], [ 'platform' ])
    }

    @Test
    void testSegments_meetBoth() {
        def assayConstraints = [
            acghResource.createAssayConstraint(
                AssayConstraint.PATIENT_SET_CONSTRAINT,
                result_instance_id: testData.allPatientsQueryResult.id),
        ]
        def dataConstraints = [
            // start matches start of regions[0]
            acghResource.createDataConstraint(
                DataConstraint.DISJUNCTION_CONSTRAINT,
                subconstraints: [
                    (DataConstraint.CHROMOSOME_SEGMENT_CONSTRAINT): [
                        /* test region wider then the segment */
                        [ chromosome: '1', start: 44, end: 8888 ],
                        /* segment aligned at the end of test region;
                         *segment shorter than region */
                        [ chromosome: '2', start: 88, end: 99 ],
                    ]
                ]
            )
        ]

        def anotherPlatform = new DeGplInfo(
            title:          'Another Test Region Platform',
            organism:       'Homo Sapiens',
            annotationDate: Date.parse('yyyy-MM-dd', '2013-08-03'),
            markerType:     'Chromosomal',
            genomeReleaseId:'hg19',
        )
        anotherPlatform.id = 'test-another-platform'
        anotherPlatform.save failOnError: true, flush: true

        // this region should not appear in the result set
        def anotherRegion = new DeChromosomalRegion(
            platform:       anotherPlatform,
            chromosome:     '1',
            start:          1,
            end:            10,
            numberOfProbes: 42,
            name:           'region 1:1-10',
	    gplId: ''
        )
        anotherRegion.id = -2000L
        anotherRegion.save failOnError: true, flush: true

        dataQueryResult = acghResource.retrieveData assayConstraints, dataConstraints, projection

        def regionRows = Lists.newArrayList(dataQueryResult.rows)

        assertThat regionRows, hasSize(2)
        assertThat regionRows, contains(
            hasSameInterfaceProperties(
                Region, testData.regions[1], [ 'platform' ]),
            hasSameInterfaceProperties(
                Region, testData.regions[0], [ 'platform' ]))
    }

    @Test
    void testSegments_meetNone() {
        def assayConstraints = [
            acghResource.createAssayConstraint(
                AssayConstraint.PATIENT_SET_CONSTRAINT,
                result_instance_id: testData.allPatientsQueryResult.id),
        ]
        def dataConstraints = [
            // start matches start of regions[0]
            acghResource.createDataConstraint(
                DataConstraint.DISJUNCTION_CONSTRAINT,
                subconstraints: [
                    (DataConstraint.CHROMOSOME_SEGMENT_CONSTRAINT): [
                        [ chromosome: 'X' ],
                        [ chromosome: '1', start: 1,   end: 32 ],
                        [ chromosome: '2', start: 100, end: 1000 ],
                    ]
                ]
            )
        ]

        dataQueryResult = acghResource.retrieveData assayConstraints, dataConstraints, projection

        assertThat dataQueryResult, hasProperty('indicesList', is(not(empty())))
        assertThat Lists.newArrayList(dataQueryResult.rows), is(empty())
    }

    @Test
    void testResultRowsAreCoreApiRegionRows() {
        def assayConstraints = [
            acghResource.createAssayConstraint(
                AssayConstraint.TRIAL_NAME_CONSTRAINT, name: TRIAL_NAME) ]

        dataQueryResult = acghResource.retrieveData assayConstraints, [], projection

        assertThat Lists.newArrayList(dataQueryResult), everyItem(
            isA(org.transmartproject.core.dataquery.highdim.chromoregion.RegionRow))
    }

    @Test
    void testWithGeneConstraint() {
        def assayConstraints = [
            acghResource.createAssayConstraint(
                AssayConstraint.TRIAL_NAME_CONSTRAINT, name: TRIAL_NAME),
            acghResource.createAssayConstraint(
                AssayConstraint.PATIENT_SET_CONSTRAINT,
                result_instance_id: testData.allPatientsQueryResult.id),
        ]
        def dataConstraints = [
            acghResource.createDataConstraint([keyword_ids: [
		testData.searchKeywords.
		find({ it.keyword == 'AURKA' }).id]],
                DataConstraint.SEARCH_KEYWORD_IDS_CONSTRAINT
            )
        ]
        def projection = acghResource.createProjection([:], ACGH_VALUES_PROJECTION)

        dataQueryResult = acghResource.retrieveData(
            assayConstraints, dataConstraints, projection)

        def resultList = Lists.newArrayList dataQueryResult

        assertThat resultList, allOf(
            hasSize(1),
            everyItem(hasProperty('data', hasSize(2))),
            contains(hasProperty('bioMarker', equalTo('AURKA')))
        )
    }

    @Test
    void testSearchAnnotationGeneSymbol() {
        def symbols = acghResource.searchAnnotation(concept_code, 'A', 'geneSymbol')
        assertThat symbols, allOf(
            hasSize(2),
            contains(
                equalTo('ADIRF'),
                equalTo('AURKA')
            )
        )
    }

    @Test
    void testSearchAnnotationCytoband() {
        def cyto = acghResource.searchAnnotation(concept_code, 'cyto', 'cytoband')
        assertThat cyto, allOf(
            hasSize(2),
            contains(
                equalTo('cytoband1'),
                equalTo('cytoband2')
            )
        )
    }

    @Test
    void testSearchAnnotationRegionName() {
        def names = acghResource.searchAnnotation(concept_code, 'region 1', 'name')
        assertThat names, allOf(
            hasSize(1),
            contains(
                equalTo('region 1:33-9999')
            )
        )
    }

    @Test
    void testSearchAnnotationGeneSymbolNoResult() {
        def empty = acghResource.searchAnnotation(concept_code, 'FOO', 'geneSymbol')
        assertThat empty, hasSize(0)
    }

    @Test
    void testSearchAnnotationInvalidProperty() {
        GroovyAssert.shouldFail(InvalidArgumentsException.class) {acghResource.searchAnnotation(concept_code, 'A', 'FOO')}
    }

    @Test
    void testGeneSymbolAnnotationConstraint() {
        def constraint = new ConstraintByOmicsValue(
            omicsType: ConstraintByOmicsValue.OmicsType.CHROMOSOMAL,
            property: 'geneSymbol',
            selector: 'AURKA',
            projectionType: Projection.PROB_GAIN
        )

        def distribution = acghResource.getDistribution(constraint, conceptKey, null)
        def correctValues = testData.acghData.findAll {it.region.geneSymbol == 'AURKA'}.collectEntries {[it.patient.id, it.probabilityOfGain]}
        assertThat distribution.size(), greaterThanOrEqualTo(1)
        assert distribution.equals(correctValues)
    }

    @Test
    void testCytobandAnnotationConstraint() {
        def constraint = new ConstraintByOmicsValue(
            omicsType: ConstraintByOmicsValue.OmicsType.CHROMOSOMAL,
            property: 'cytoband',
            selector: 'cytoband1',
            projectionType: Projection.PROB_GAIN
        )

        def distribution = acghResource.getDistribution(constraint, conceptKey, null)
        def correctValues = testData.acghData.findAll {it.region.cytoband == 'cytoband1'}.collectEntries {[it.patient.id, it.probabilityOfGain]}
        assertThat distribution.size(), greaterThanOrEqualTo(1)
        assert distribution.equals(correctValues)
    }

    @Test
    void testNameAnnotationConstraint() {
        def constraint = new ConstraintByOmicsValue(
            omicsType: ConstraintByOmicsValue.OmicsType.CHROMOSOMAL,
            property: 'name',
            selector: 'region 1:33-9999',
            projectionType: Projection.PROB_GAIN
        )

        def distribution = acghResource.getDistribution(constraint, conceptKey, null)
        def correctValues = testData.acghData.findAll {it.region.name == 'region 1:33-9999'}.collectEntries {[it.patient.id, it.probabilityOfGain]}
        assertThat distribution.size(), greaterThanOrEqualTo(1)
        assert distribution.equals(correctValues)
    }

    @Test
    void testChipCopyNumberValueConstraint() {
        def constraint = new ConstraintByOmicsValue(
            omicsType: ConstraintByOmicsValue.OmicsType.CHROMOSOMAL,
            property: 'cytoband',
            selector: 'cytoband1',
            projectionType: Projection.CHIP_COPYNUMBER_VALUE,
            operator: 'BETWEEN',
            constraint: '0.10:0.12'
        )

        def distribution = acghResource.getDistribution(constraint, conceptKey, null)
        def correctValues = testData.acghData.findAll {it.region.cytoband == 'cytoband1' && 0.10 <= it.chipCopyNumberValue && it.chipCopyNumberValue <= 0.12}
            .collectEntries {[it.patient.id, it.chipCopyNumberValue]}
        assertThat distribution.size(), greaterThanOrEqualTo(1)
        assert distribution.equals(correctValues)
    }

    @Test
    void testSegmentCopyNumberValueConstraint() {
        def constraint = new ConstraintByOmicsValue(
            omicsType: ConstraintByOmicsValue.OmicsType.CHROMOSOMAL,
            property: 'cytoband',
            selector: 'cytoband1',
            projectionType: Projection.SEGMENT_COPY_NUMBER_VALUE,
            operator: 'BETWEEN',
            constraint: '0.10:0.15'
        )

        def distribution = acghResource.getDistribution(constraint, conceptKey, null)
        def correctValues = testData.acghData.findAll {it.region.cytoband == 'cytoband1' && 0.10 <= it.segmentCopyNumberValue && it.segmentCopyNumberValue <= 0.15}
            .collectEntries {[it.patient.id, it.segmentCopyNumberValue]}
        assertThat distribution.size(), greaterThanOrEqualTo(1)
        assert distribution.equals(correctValues)
    }

    @Test
    void testFlagConstraint() {
        def constraint = new ConstraintByOmicsValue(
            omicsType: ConstraintByOmicsValue.OmicsType.CHROMOSOMAL,
            property: 'cytoband',
            selector: 'cytoband1',
            projectionType: Projection.FLAG,
            operator: 'BETWEEN',
            constraint: '-1:0'
        )

        def distribution = acghResource.getDistribution(constraint, conceptKey, null)
        def correctValues = testData.acghData.findAll {it.region.cytoband == 'cytoband1' && -1 <= it.flag && it.flag <= 0}
            .collectEntries {[it.patient.id, it.flag]}
        assertThat distribution.size(), greaterThanOrEqualTo(1)
        assert distribution.equals(correctValues)
    }

    @Test
    void testProbAmpConstraint() {
        def constraint = new ConstraintByOmicsValue(
            omicsType: ConstraintByOmicsValue.OmicsType.CHROMOSOMAL,
            property: 'cytoband',
            selector: 'cytoband1',
            projectionType: Projection.PROB_AMP,
            operator: 'BETWEEN',
            constraint: '0.10:0.20'
        )

        def distribution = acghResource.getDistribution(constraint, conceptKey, null)
        def correctValues = testData.acghData.findAll {it.region.cytoband == 'cytoband1' && 0.10 <= it.probabilityOfAmplification && it.probabilityOfAmplification <= 0.20}
            .collectEntries {[it.patient.id, it.probabilityOfAmplification]}
        assertThat distribution.size(), greaterThanOrEqualTo(1)
        assert distribution.equals(correctValues)
    }

    @Test
    void testProbGainConstraint() {
        def constraint = new ConstraintByOmicsValue(
            omicsType: ConstraintByOmicsValue.OmicsType.CHROMOSOMAL,
            property: 'cytoband',
            selector: 'cytoband1',
            projectionType: Projection.PROB_GAIN,
            operator: 'BETWEEN',
            constraint: '0.10:0.15'
        )

        def distribution = acghResource.getDistribution(constraint, conceptKey, null)
        def correctValues = testData.acghData.findAll {it.region.cytoband == 'cytoband1' && 0.10 <= it.probabilityOfGain && it.probabilityOfGain <= 0.15}
            .collectEntries {[it.patient.id, it.probabilityOfGain]}
        assertThat distribution.size(), greaterThanOrEqualTo(1)
        assert distribution.equals(correctValues)
    }

    @Test
    void testProbLossConstraint() {
        def constraint = new ConstraintByOmicsValue(
            omicsType: ConstraintByOmicsValue.OmicsType.CHROMOSOMAL,
            property: 'cytoband',
            selector: 'cytoband1',
            projectionType: Projection.PROB_LOSS,
            operator: 'BETWEEN',
            constraint: '0.10:0.15'
        )

        def distribution = acghResource.getDistribution(constraint, conceptKey, null)
        def correctValues = testData.acghData.findAll {it.region.cytoband == 'cytoband1' && 0.10 <= it.probabilityOfLoss && it.probabilityOfLoss <= 0.15}
            .collectEntries {[it.patient.id, it.probabilityOfLoss]}
        assertThat distribution.size(), greaterThanOrEqualTo(1)
        assert distribution.equals(correctValues)
    }

    @Test
    void testProbNormConstraint() {
        def constraint = new ConstraintByOmicsValue(
            omicsType: ConstraintByOmicsValue.OmicsType.CHROMOSOMAL,
            property: 'cytoband',
            selector: 'cytoband1',
            projectionType: Projection.PROB_NORM,
            operator: 'BETWEEN',
            constraint: '0.10:0.13'
        )

        def distribution = acghResource.getDistribution(constraint, conceptKey, null)
        def correctValues = testData.acghData.findAll {it.region.cytoband == 'cytoband1' && 0.10 <= it.probabilityOfNormal && it.probabilityOfNormal <= 0.13}
            .collectEntries {[it.patient.id, it.probabilityOfNormal]}
        assertThat distribution.size(), greaterThanOrEqualTo(1)
        assert distribution.equals(correctValues)
    }
}
