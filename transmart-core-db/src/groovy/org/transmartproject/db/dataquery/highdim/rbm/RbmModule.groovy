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

package org.transmartproject.db.dataquery.highdim.rbm

import grails.orm.HibernateCriteriaBuilder
import org.hibernate.ScrollableResults
import org.hibernate.engine.SessionImplementor
import org.hibernate.sql.JoinFragment
import org.hibernate.transform.Transformers
import org.springframework.beans.factory.annotation.Autowired
import org.transmartproject.core.dataquery.TabularResult
import org.transmartproject.core.dataquery.highdim.AssayColumn
import org.transmartproject.core.dataquery.highdim.projections.Projection
import org.transmartproject.core.querytool.HighDimensionFilterType
import org.transmartproject.db.dataquery.highdim.AbstractHighDimensionDataTypeModule
import org.transmartproject.db.dataquery.highdim.DeSubjectSampleMapping
import org.transmartproject.db.dataquery.highdim.DefaultHighDimensionTabularResult
import org.transmartproject.db.dataquery.highdim.RepeatedEntriesCollectingTabularResult
import org.transmartproject.db.dataquery.highdim.correlations.CorrelationTypesRegistry
import org.transmartproject.db.dataquery.highdim.correlations.SearchKeywordDataConstraintFactory
import org.transmartproject.db.dataquery.highdim.parameterproducers.AllDataProjectionFactory
import org.transmartproject.db.dataquery.highdim.parameterproducers.DataRetrievalParameterFactory
import org.transmartproject.db.dataquery.highdim.parameterproducers.SimpleAnnotationConstraintFactory
import org.transmartproject.db.dataquery.highdim.parameterproducers.SimpleRealProjectionsFactory

import static org.transmartproject.db.util.GormWorkarounds.createCriteriaBuilder

class RbmModule extends AbstractHighDimensionDataTypeModule {

    final String name = 'rbm'
    final String description = 'RBM data'
    final List<String> platformMarkerTypes = ['RBM']
    final Map<String, Class> dataProperties = typesMap(DeSubjectRbmData,
            ['value', 'logIntensity', 'zscore'])
    final Map<String, Class> rowProperties = typesMap(RbmRow,
            ['antigenName', 'unit', 'uniprotName'])

    @Autowired DataRetrievalParameterFactory standardAssayConstraintFactory
    @Autowired DataRetrievalParameterFactory standardDataConstraintFactory
    @Autowired CorrelationTypesRegistry correlationTypesRegistry

    protected List<DataRetrievalParameterFactory> createAssayConstraintFactories() {
        [ standardAssayConstraintFactory ]
    }

    protected List<DataRetrievalParameterFactory> createDataConstraintFactories() {
        [standardDataConstraintFactory,
         new SimpleAnnotationConstraintFactory(field: 'p', annotationClass: DeRbmAnnotation.class),
         new SearchKeywordDataConstraintFactory(correlationTypesRegistry,
						'PROTEIN', 'p', 'uniprotId')]
    }

    protected List<DataRetrievalParameterFactory> createProjectionFactories() {
        [ new SimpleRealProjectionsFactory(
            (Projection.LOG_INTENSITY_PROJECTION): 'logIntensity',
            (Projection.DEFAULT_REAL_PROJECTION): 'value',
            (Projection.ZSCORE_PROJECTION):       'zscore'),
         new AllDataProjectionFactory(dataProperties, rowProperties)]
    }

    HibernateCriteriaBuilder prepareDataQuery(Projection projection, SessionImplementor session) {
        HibernateCriteriaBuilder criteriaBuilder = createCriteriaBuilder(DeSubjectRbmData, 'rbmdata', session)

        criteriaBuilder.with {
            createAlias 'annotations', 'p', JoinFragment.INNER_JOIN

            projections {
                property 'assay.id', 'assayId'
                property 'p.id', 'annotationId'
                property 'p.antigenName', 'antigenName'
                property 'p.uniprotName', 'uniprotName'
                property 'unit', 'unit'
            }

            order 'p.id', 'asc'
            order 'p.uniprotId', 'asc'
            order 'assay.id', 'asc'
            instance.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
        }
        criteriaBuilder
    }

    TabularResult transformResults(ScrollableResults results, List<AssayColumn> assays, Projection projection) {
        Map assayIndexes = createAssayIndexMap assays

        DefaultHighDimensionTabularResult preliminaryResult = new DefaultHighDimensionTabularResult(
            rowsDimensionLabel: 'Antigenes',
            columnsDimensionLabel: 'Sample codes',
            indicesList: assays,
            results: results,
            //TODO Remove this. On real data missing assays are signaling about problems
            allowMissingAssays: true,
            assayIdFromRow: { it[0].assayId },
            inSameGroup: {a, b -> a.annotationId == b.annotationId && a.uniprotId == b.uniprotId },
            finalizeGroup: {List list ->
                def firstNonNullCell = list.find()
                new RbmRow(
                    annotationId:  firstNonNullCell[0].annotationId,
                    antigenName:   firstNonNullCell[0].antigenName,
                    unit:          firstNonNullCell[0].unit,
                    uniprotName:   firstNonNullCell[0].uniprotName,
                    assayIndexMap: assayIndexes,
                    data:          list.collect { projection.doWithResult it?.getAt(0) }
                )
            }
        )

        new RepeatedEntriesCollectingTabularResult(
            tabularResult: preliminaryResult,
            collectBy: { it.antigenName },
            resultItem: {collectedList ->
                if (collectedList) {
                    new RbmRow(
                        annotationId:   collectedList[0].annotationId,
                        antigenName:    collectedList[0].antigenName,
                        unit:           collectedList[0].unit,
                        uniprotName:    collectedList*.uniprotName.join('/'),
                        assayIndexMap:  collectedList[0].assayIndexMap,
                        data:           collectedList[0].data
                    )
		}
	    }
        )
    }

    List<String> searchAnnotation(String concept_code, String search_term, String search_property) {
        if (!getSearchableAnnotationProperties().contains(search_property)) {
            return []
	}

        DeRbmAnnotation.createCriteria().list {
            ilike(search_property, search_term + '%')
            'in'('gplId', DeSubjectSampleMapping.createCriteria().listDistinct {
                eq('conceptCode', concept_code)
                projections {
                    distinct('platform')
                }
            }.collect {it.id})
            projections {
                distinct(search_property)
            }
            order(search_property, 'ASC')
            maxResults 100
        }
    }

    List<String> getSearchableAnnotationProperties() {
        ['antigenName','uniprotName','geneSymbol']
    }

    HighDimensionFilterType getHighDimensionFilterType() {
        HighDimensionFilterType.SINGLE_NUMERIC
    }

    List<String> getSearchableProjections() {
        [Projection.LOG_INTENSITY_PROJECTION, Projection.DEFAULT_REAL_PROJECTION, Projection.ZSCORE_PROJECTION]
    }
}
