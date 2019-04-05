package org.transmart.biomart

import grails.test.mixin.integration.Integration
import groovy.sql.Sql
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.hibernate.SessionFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Specification
import test.Column
import test.ExportedSchema
import test.ForeignKey
import test.SchemaExporter
import test.Sequence
import test.Table

import javax.sql.DataSource

/**
 * @author <a href='mailto:burt_beckwith@hms.harvard.edu'>Burt Beckwith</a>
 */
@Integration
abstract class AbstractDomainSpec extends Specification {

	protected static final String AUTOINC = 'bigint generated by default as identity'
	protected static final long SHARED_ID = 100000

	protected final Logger log = LoggerFactory.getLogger(getClass())

	protected ExportedSchema es
	protected Sql sql

	DataSource dataSource
	GrailsApplication grailsApplication
	SessionFactory sessionFactory

	void setup() {
		sql = new Sql(dataSource)
	}

	protected void exportSchema() {
		es = new SchemaExporter().export(grailsApplication.mainContext)
	}

	protected Table assertTable(String name, String schema = null) {
		Table table = es.table(name, schema)
		assert table

		assert table.name == name
		assert table.schema == schema

		table
	}

	protected boolean assertPk(Table table, String columnName, String type = 'bigint') {
		assert table.primaryKeyColumns.size() == 1
		assert table.primaryKeyColumns[0] == columnName.toUpperCase()

		// id
		assertColumn table, columnName, type, false, true
	}

	protected boolean assertCompoundPk(Map<String, String> columnsAndTypes, Table table) {
		assert table.primaryKeyColumns.size() == columnsAndTypes.size()

		columnsAndTypes.eachWithIndex { String columnName, String type, int index ->
			assert table.primaryKeyColumns[index] == columnName.toUpperCase()
			assert table.columns.contains(new Column(columnName, type, false, true))
		}
	}

	protected boolean assertSequence(String name) {
		es.sequences.contains new Sequence(name)
	}

	protected boolean assertColumn(Table table, String name, String type, boolean nullable = false,
	                               boolean unique = false) {
		Column column = new Column(name, type, nullable, unique)
		if (table.columns.remove(column)) {
			return true
		}

		for (Column c in table.columns) {
			if (c.name == name) {
				assert c == column
				return true // TODO
			}
		}

		false
	}

	protected boolean assertForeignKeyColumn(Table table, String referenced, String column, String type = 'bigint',
	                                         boolean nullable = false, boolean unique = false) {
		assertColumn table, column, type, nullable, unique
		assertForeignKey table, referenced, column
	}

	protected boolean assertForeignKey(Table table, String referenced, String column) {
		ForeignKey foreignKey = new ForeignKey(table.name, column, referenced)
		if (table.foreignKeys.remove(foreignKey)) {
			return true
		}

		for (ForeignKey fk in table.foreignKeys) {
			if (fk.column == column) {
				assert fk == foreignKey
				return true // TODO
			}
		}

		false
	}

	protected <T> T save(T t) {
		def assignedId = t.id
		t.id = null

		t.save flush: true, failOnError: true

		if (assignedId) {
			String table = tableName(t.getClass())
			String pk = pkColumnNames(t.getClass())[0]
			assert 1 == sql.executeUpdate('update ' + table + ' set ' + pk + '=? where ' + pk + '=?', assignedId, t.id)
			t.discard()
			t = t.getClass().get(assignedId)
		}

		t
	}

	protected void flushAndClear() {
		sessionFactory.currentSession.flush()
		sessionFactory.currentSession.clear()
	}

	protected boolean populateData() {
		save new AdHocProperty(key: 'x', objectId: -1, value: 'x')

		save new AnalysisMetadata(analysisName: 'x', study: 'x')

		save new BioAnalysisAttribute(bioAssayAnalysisID: -1)

		save new Experiment(accession: 'x')

		save new BioAssay(assayPlatformId: -1, experiment: Experiment.list()[0], type: 'x')

		save new BioAssayAnalysis(analysisMethodCode: 'x', assayDataType: 'x', dataCount: -1, etlId: 'x', teaDataCount: -1)

		save new BioAssayFeatureGroup(name: 'x', type: 'x')

		save new BioAssayPlatform(accession: 'x', array: 'x', organism: 'x', vendor: 'x')

		save new BioAssayAnalysisData(adjustedPvalue: -1, adjustedPValueCode: 'x', analysis: BioAssayAnalysis.list()[0],
				assayPlatform: BioAssayPlatform.list()[0], cutValue: -1, experiment: Experiment.list()[0],
				featureGroup: BioAssayFeatureGroup.list()[0], featureGroupName: 'x', foldChangeRatio: -1, numericValue: -1,
				numericValueCode: 'x', preferredPvalue: -1, rawPvalue: -1, resultsValue: 'x', rhoValue: -1, rValue: -1,
				teaNormalizedPValue: -1)

		save new BioAssayAnalysisDataIdx(display_idx: -1, ext_type: 'x', field_idx: -1, field_name: 'x')

		save new BioAssayAnalysisDataTea(adjustedPvalue: -1, adjustedPValueCode: 'x', analysis: BioAssayAnalysis.list()[0],
				assayPlatform: BioAssayPlatform.list()[0], cutValue: -1, experiment: Experiment.list()[0],
				experimentType: 'x', featureGroup: BioAssayFeatureGroup.list()[0], featureGroupName: 'x',
				foldChangeRatio: -1, numericValue: -1, numericValueCode: 'x', preferredPvalue: -1, rawPvalue: -1,
				resultsValue: 'x', rhoValue: -1, rValue: -1, teaNormalizedPValue: -1, teaRank: -1)

		save new BioAssayAnalysisExt(bioAssayAnalysis: BioAssayAnalysis.list()[0])

		save new BioAssayAnalysisPlatform()

		save new BioAssayCohort(cohortId: 'x', cohortTitle: 'x', disease: 'x', longDesc: 'x', organism: 'x',
				pathology: 'x', sampleType: 'x', shortDesc: 'x', studyId: 'x', treatment: 'x')

		save new BioAssayData(bioAssayDatasetId: -1, bioAssayId: -1, bioSampleId: -1, experiment: Experiment.list()[0],
				featureGroupName: 'x', floatValue: -1, log10Value: -1, log2Value: -1, numericValue: -1, textValue: 'x')

		save new BioMarker(bioMarkerType: 'x')

		save new BioAssayDataAnnotation(bioMarker: BioMarker.list()[0], probeset: BioAssayFeatureGroup.list()[0])

		save new BioAssayDataset(bioAssay: BioAssay.list()[0], experiment: Experiment.list()[0])

		save new BioSubject(type: 'x')

		save new CellLine(attcNumber: 'x', bioDiseaseId: -1, cellLineName: 'x', description: 'x', disease: 'x',
				diseaseStage: 'x', diseaseSubtype: 'x', metastaticSite: 'x', origin: 'x', primarySite: 'x', species: 'x')

		save new BioSample(bioSubject: BioSubject.list()[0], cellLine: CellLine.list()[0],
				experiment: Experiment.list()[0], name: 'x', type: 'x')

		save new BioAssayDataStatistics(dataset: BioAssayDataset.list()[0], experiment: Experiment.list()[0],
				featureGroup: BioAssayFeatureGroup.list()[0], featureGroupName: 'x', maxValue: -1, meanValue: -1,
				minValue: -1, quartile1: -1, quartile2: -1, quartile3: -1, sample: BioSample.list()[0], sampleCount: -1,
				stdDevValue: -1, valueNormalizeMethod: 'x')

		save new BioAssaySample(bioAssayId: -1, bioClinicTrialTimepointId: -1, bioSampleId: -1)

		save new BioAssayStatsExpMarker(experiment: Experiment.list()[0], marker: BioMarker.list()[0])

		save new BioData(type: 'x', uniqueId: 'x')

		save new BioDataAttribute(bioDataId: -1, propertyCode: 'x', propertyUnit: 'x', propertyValue: 'x')

		save new BioDataCorrelationDescr(correlation: 'x', description: 'x', source: 'x', sourceCode: 'x',
				status: 'x', typeName: 'x')

		save new BioDataCorrelation(associatedBioDataId: BioMarker.list()[0].id, bioDataId: BioMarker.list()[0].id,
				correlationDescr: BioDataCorrelationDescr.list()[0])

		save new BioMarkerCorrelationMV(assoBioMarkerId: -1, bioMarkerId: -1, correlType: 'x')

		save new BioDataExternalCode(bioDataId: BioData.list()[0].id, code: 'x')

		save new BioMarkerExpAnalysisMV(analysis: BioAssayAnalysis.list()[0], experiment: Experiment.list()[0],
				marker: BioMarker.list()[0])

		save new BioSpeciesOrganism()

		save new LiteratureReferenceData(backReferences: 'x', component: 'x', componentClass: 'x', disease: 'x',
				diseaseDescription: 'x', diseaseGrade: 'x', diseaseIcd10: 'x', diseaseMesh: 'x', diseaseSite: 'x',
				diseaseStage: 'x', diseaseTypes: 'x', etlId: 'x', geneId: 'x', moleculeType: 'x', physiology: 'x',
				referenceId: 'x', referenceTitle: 'x', referenceType: 'x', statClinical: 'x', statClinicalCorrelation: 'x',
				statCoefficient: 'x', statDescription: 'x', statPValue: 'x', statTests: 'x', studyType: 'x', variant: 'x')

		save new CgdcpData(bioCurationDatasetId: -1, cellLineId: -1, dataType: 'x', evidenceCode: 'x',
				nciDiseaseConceptCode: 'x', nciDrugConceptCode: 'x', nciRoleCode: 'x', negationIndicator: 'n',
				reference: LiteratureReferenceData.list()[0], statement: 'x', statementStatus: 'x')

		save new ClinicalTrial(accession: 'x', blindingProcedure: 'x', dosingRegimen: 'x', durationOfStudyWeeks: -1,
				exclusionCriteria: 'x', genderRestrictionMfb: 'x', groupAssignment: 'x', inclusionCriteria: 'x', maxAge: -1,
				minAge: -1, numberOfPatients: -1, numberOfSites: -1, primaryEndPoints: 'x', routeOfAdministration: 'x',
				secondaryEndPoints: 'x', secondaryIds: 'x', studyOwner: 'x', studyPhase: 'x', studyType: 'x', subjects: 'x',
				trialNumber: 'x', typeOfControl: 'x')

		save new ClinicalTrialPatientGroup(clinicalTrial: ClinicalTrial.list()[0])

		save new ClinicalTrialTimePoint(clinicalTrial: ClinicalTrial.list()[0], endDate: new Date(),
				startDate: new Date(), timePoint: 'x', timePointCode: 'x')

		save new Compound()

		save new ConceptCode(codeName: 'x')

		save new ContentRepository(repositoryType: 'x')

		save new Content(repository: ContentRepository.list()[0], type: 'x')

		save new ContentReference(bioDataId: -1, content: Content.list()[0], type: 'x')

		save new CurationDataset(bioAnalysisPlatformId: -1, curationType: 'x')

		save new Disease(ccsCategory: 'x', disease: 'x', icd10Code: 'x', icd9Code: 'x', meshCode: 'x', preferredName: 'x')

		save new Literature(bioCurationDatasetId: -1, dataType: 'x', reference: LiteratureReferenceData.list()[0].id,
				statement: 'x', statementStatus: 'x')

		save new LiteratureModelData(animalWildType: 'x', bodySubstance: 'x', cellLine: 'x', cellType: 'x', challenge: 'x',
				component: 'x', controlChallenge: 'x', description: 'x', etlId: 'x', experimentalModel: 'x', geneId: 'x',
				modelType: 'x', sentization: 'x', stimulation: 'x', tissue: 'x', zygosity: 'x')

		save new LiteratureAlterationData(alterationType: 'x', bioCurationDatasetId: -1, clinAsmMarkerType: 'x',
				clinAsmUnit: 'x', clinAsmValue: 'x', clinAtopy: 'x', clinBaselinePercent: 'x', clinBaselineValue: 'x',
				clinBaselineVariable: 'x', clinCellularCount: 'x', clinCellularSource: 'x', clinCellularType: 'x',
				clinPriorMedDose: 'x', clinPriorMedName: 'x', clinPriorMedPercent: 'x', clinSmoker: 'x',
				clinSubmucosaMarkerType: 'x', clinSubmucosaUnit: 'x', clinSubmucosaValue: 'x', control: 'x',
				controlExpNumber: 'x', controlExpPercent: 'x', controlExpSd: 'x', controlExpUnit: 'x', controlExpValue: 'x',
				dataType: 'x', description: 'x', effect: 'x', epigeneticRegion: 'x', epigeneticType: 'x', etlId: 'x',
				glcControlPercent: 'x', glcMolecularChange: 'x', glcNumber: 'x', glcPercent: 'x', glcType: 'x',
				inVitroModel: LiteratureModelData.list()[0], inVivoModel: LiteratureModelData.list()[0], lohLoci: 'x',
				lossExpNumber: 'x', lossExpPercent: 'x', lossExpSd: 'x', lossExpUnit: 'x', lossExpValue: 'x',
				mutationChange: 'x', mutationSites: 'x', mutationType: 'x', overExpNumber: 'x', overExpPercent: 'x',
				overExpSd: 'x', overExpUnit: 'x', overExpValue: 'x', patientsNumber: 'x', patientsPercent: 'x',
				popBodySubstance: 'x', popCellType: 'x', popDescription: 'x', popExclusionCriteria: 'x',
				popExperimentalModel: 'x', popInclusionCriteria: 'x', popLocalization: 'x', popNumber: 'x', popPhase: 'x',
				popStatus: 'x', popTissue: 'x', popType: 'x', popValue: 'x', ptmChange: 'x', ptmRegion: 'x', ptmType: 'x',
				reference: LiteratureReferenceData.list()[0], statement: 'x', statementStatus: 'x', techniques: 'x',
				totalExpNumber: 'x', totalExpPercent: 'x', totalExpSd: 'x', totalExpUnit: 'x', totalExpValue: 'x')

		save new LiteratureAssocMoleculeDetailsData(bioLitAltDataId: LiteratureAlterationData.list()[0].id,
				coExpNumber: 'x', coExpPercent: 'x', coExpSd: 'x', coExpUnit: 'x', coExpValue: 'x', description: 'x',
				etlId: 'x', molecule: 'x', moleculeType: 'x', mutationChange: 'x', mutationNumber: 'x',
				mutationPercent: 'x', mutationSites: 'x', mutationType: 'x', overExpNumber: 'x', overExpPercent: 'x',
				overExpSd: 'x', overExpUnit: 'x', overExpValue: 'x', targetExpNumber: 'x', targetExpPercent: 'x',
				targetExpSd: 'x', targetExpUnit: 'x', targetExpValue: 'x', targetOverExpNumber: 'x',
				targetOverExpPercent: 'x', targetOverExpSd: 'x', targetOverExpUnit: 'x', targetOverExpValue: 'x',
				techniques: 'x', totalExpNumber: 'x', totalExpPercent: 'x', totalExpSd: 'x', totalExpUnit: 'x',
				totalExpValue: 'x')

		save new LiteratureInhibitorData(administration: 'x', bioCurationDatasetId: -1, casid: 'x', concentration: 'x',
				dataType: 'x', description: 'x', effectAdverse: 'x', effectBeneficial: 'x', effectDescription: 'x',
				effectDownstream: 'x', effectMolecular: 'x', effectNumber: 'x', effectPercent: 'x', effectPharmacos: 'x',
				effectPotentials: 'x', effectResponseRate: 'x', effectSd: 'x', effectUnit: 'x', effectValue: 'x', etlId: 'x',
				inhibitor: 'x', inhibitorStandardName: 'x', reference: LiteratureReferenceData.list()[0], statement: 'x',
				statementStatus: 'x', techniques: 'x', timeExposure: 'x', treatment: 'x', trialBodySubstance: 'x',
				trialCellLine: 'x', trialCellType: 'x', trialDescription: 'x', trialDesigns: 'x',
				trialExperimentalModel: 'x', trialInclusionCriteria: 'x', trialPatientsNumber: 'x', trialPhase: 'x',
				trialStatus: 'x', trialTissue: 'x', trialType: 'x')

		save new LiteratureInteractionData(bioCurationDatasetId: -1, dataType: 'x', effect: 'x', etlId: 'x',
				interactionMode: 'x', inVitroModel: LiteratureModelData.list()[0],
				inVivoModel: LiteratureModelData.list()[0], localization: 'x', mechanism: 'x',
				reference: LiteratureReferenceData.list()[0], region: 'x', regulation: 'x', sourceComponent: 'x',
				sourceGeneId: 'x', statement: 'x', statementStatus: 'x', targetComponent: 'x', targetGeneId: 'x',
				techniques: 'x')

		save new LiteratureInteractionModelMV(experimentalModel: 'x')

		save new LiteratureProteinEffectData(bioCurationDatasetId: -1, dataType: 'x', description: 'x', etlId: 'x',
				inVitroModel: LiteratureModelData.list()[0], inVivoModel: LiteratureModelData.list()[0],
				reference: LiteratureReferenceData.list()[0], statement: 'x', statementStatus: 'x')

		save new LiteratureSummaryData(alterationType: 'x', dataType: 'x', diseaseSite: 'x', etlId: 'x', summary: 'x',
				target: 'x', totalAffectedCases: 'x', totalFrequency: 'x', variant: 'x')

		save new Observation(code: 'x', codeSource: 'x', description: 'x', etlId: -1, name: 'x', type: 'x')

		save new Patient()

		save new PatientEvent(bioClinicTrialTimepointId: -1, bioPatientId: -1)

		save new PatientEventAttribute(bioClinicTrialAttributeId: -1, bioPatientAttrCode: 'x',
				bioPatientAttributeId: -1, bioPatientEventId: -1)

		save new Taxonomy(label: 'x', name: 'x', ncbiTaxId: 'x')

		true
	}

	protected String tableName(Class c) {
		sessionFactory.getClassMetadata(c).tableName
	}

	protected String[] pkColumnNames(Class c) {
		sessionFactory.getClassMetadata(c).identifierColumnNames
	}
}
