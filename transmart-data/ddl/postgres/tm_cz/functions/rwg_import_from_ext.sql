-----------------------------------------------------------------------
--             DO NOT EDIT THIS FILE. IT IS AUTOGENERATED            --
-- Edit the original file in the macroed_functions directory instead --
-----------------------------------------------------------------------
-- Generated by Ora2Pg, the Oracle database Schema converter, version 11.4
-- Copyright 2000-2013 Gilles DAROLD. All rights reserved.
-- DATASOURCE: dbi:Oracle:host=mydb.mydom.fr;sid=SIDNAME


CREATE OR REPLACE FUNCTION rwg_import_from_ext (
  trialID text
 ,currentJobID bigint DEFAULT null
)
 RETURNS BIGINT AS $body$
DECLARE

/*************************************************************************
* Copyright 2008-2012 Janssen Research & Development, LLC.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************/
	newJobFlag    smallint;
	databaseName  varchar(100);
	procedureName varchar(100);
	jobID         bigint;
	stepCt        bigint;
	rowCt         bigint;
	errorNumber   varchar;
	errorMessage  varchar;
BEGIN
	--Set Audit Parameters
	newJobFlag := 0; -- False (Default)
	jobID := currentJobID;
	select current_user INTO databaseName; --(sic)
	procedureName := 'rwg_import_from_ext';

	--Audit JOB Initialization
	--If Job ID does not exist, then this is a single procedure run and we need to create it
	IF (coalesce(jobID::text, '') = '' OR jobID < 1)
		THEN
		newJobFlag := 1; -- True
		SELECT cz_start_audit(procedureName, databaseName) INTO jobID;
	END IF;
	PERFORM cz_write_audit(jobId, databaseName, procedureName,
		'Start FUNCTION', 0, stepCt, 'Done');
	stepCt := 1;

	delete from TM_LZ.Rwg_Analysis where upper(study_id) =upper(trialID);
	GET DIAGNOSTICS rowCt := ROW_COUNT;

	perform cz_write_audit(jobId,databaseName,procedureName,'Delete existing records from TM_LZ.Rwg_Analysis',rowCt,stepCt,'Done');
	stepCt := stepCt + 1;

	delete from TM_LZ.Rwg_Cohorts where upper(study_id) =upper(trialID);
	GET DIAGNOSTICS rowCt := ROW_COUNT;

	perform cz_Write_Audit(Jobid,Databasename,Procedurename,'Delete existing records from TM_LZ.Rwg_Cohorts',rowCt,Stepct,'Done');
	stepCt := stepCt + 1;

	delete from TM_LZ.Rwg_Samples where upper(study_id) =upper(trialID);
	perform cz_Write_Audit(Jobid,Databasename,Procedurename,'Delete existing records from TM_LZ.Rwg_Samples',rowCt,Stepct,'Done');
	stepCt := stepCt + 1;

	-- not used??
	-- delete from TM_LZ.RWG_BAAD_ID where upper(study_id) =upper(trialID);
	--Cz_Write_Audit(Jobid,Databasename,Procedurename,'Delete existing records from TM_LZ.RWG_BAAD_ID',rowCt,Stepct,'Done');
	--stepCt := stepCt + 1;
	--Insert Analysis
	BEGIN
	INSERT INTO TM_LZ.Rwg_Analysis
	(
		Study_Id,
		Cohorts,
		ANALYSIS_ID,
		pvalue_cutoff,
		foldchange_cutoff ,
		lsmean_cutoff,
		Analysis_Type,
		Data_Type,
		Platform,
		Long_Desc,
		Short_Desc,
		import_date
	)
	SELECT
		Upper(Replace(  Study_Id,'"','')),
		REGEXP_REPLACE(upper(Replace(  Cohorts,'"','')), '\s*', '', 'g'),
		Replace(  ANALYSIS_ID ,'"',''),
		Replace( pvalue_cutoff ,'"','')::double precision,
		Replace(  foldchange_cutoff ,'"','')::double precision,
		Replace( lsmean_cutoff ,'"','')::double precision,
		Replace(  Analysis_Type,'"',''),
		Replace(  Data_Type,'"',''),
		Replace(   Platform,'"',''),
		Replace(  Long_Desc,'"',''),
		Replace(   short_desc,'"',''),
		now()
    FROM  TM_LZ.Rwg_Analysis_Ext
    WHERE upper(study_id) = upper(trialID);
	GET DIAGNOSTICS rowCt := ROW_COUNT;
	PERFORM cz_write_audit(jobId, databaseName, procedureName,
		'Insert records from TM_LZ.Rwg_Analysis_Ext to TM_LZ.Rwg_Analysis', rowCt, stepCt, 'Done');
	stepCt := stepCt + 1;
	EXCEPTION
		WHEN OTHERS THEN
		errorNumber := SQLSTATE;
		errorMessage := SQLERRM;
		PERFORM cz_error_handler(jobID, procedureName, errorNumber, errorMessage);
		PERFORM cz_end_audit (jobID, 'FAIL');
		RETURN -16;
	END;

	--	update bio_assay_analysis_id for any existing analysis_id (20130111 JEA)
	BEGIN
	UPDATE tm_lz.rwg_analysis t
	SET
		bio_assay_analysis_id = (
			SELECT
				b.bio_assay_analysis_id
			FROM
				biomart.bio_assay_analysis b
			WHERE
				b.etl_id = trialID || ':RWG'
				AND b.analysis_name = t.analysis_id )
	WHERE
		t.study_id = trialID
		AND EXISTS (
			SELECT
				1
			FROM
				biomart.bio_assay_analysis x
			WHERE
				x.etl_id = trialID || ':RWG'
				AND t.analysis_id = x.analysis_name );

	GET DIAGNOSTICS rowCt := ROW_COUNT;
	PERFORM cz_write_audit(jobId, databaseName, procedureName,
		'Update bio_assay_analysis_id on existing rwg_analysis records', rowCt, stepCt, 'Done');
	stepCt := stepCt + 1;
	EXCEPTION
		WHEN OTHERS THEN
		errorNumber := SQLSTATE;
		errorMessage := SQLERRM;
		PERFORM cz_error_handler(jobID, procedureName, errorNumber, errorMessage);
		PERFORM cz_end_audit (jobID, 'FAIL');
		RETURN -16;
	END;

    --Insert Cohorts
	BEGIN
	INSERT
	INTO TM_LZ.Rwg_Cohorts
	(
		Study_Id,
		Cohort_Id,
		Cohort_Title, Disease, Long_Desc,
		Organism, Pathology, Sample_Type, Short_Desc, Treatment,IMPORT_DATE
	)
	SELECT
	Upper(Replace(Study_Id,'"','')),
	trim(upper(Replace(Cohort_Id,'"',''))),
	Replace(Cohort_Title, '"',''),
	Replace(Disease, '"',''),
	Replace(Long_Desc, '"',''),
	Replace(Organism, '"',''),
	Replace(Pathology, '"',''),
	Replace(Sample_Type, '"',''),
	Replace(Short_Desc, '"',''),
	Replace(Treatment,'"',''),
	now()
	From  TM_LZ.Rwg_Cohorts_Ext
	where upper(study_id) = upper(trialID);

	GET DIAGNOSTICS rowCt := ROW_COUNT;
	PERFORM cz_write_audit(jobId, databaseName, procedureName,
		'Insert records from TM_LZ.Rwg_Cohorts_Ext to TM_LZ.Rwg_Cohorts', rowCt, stepCt, 'Done');
	stepCt := stepCt + 1;
	EXCEPTION
		WHEN OTHERS THEN
		errorNumber := SQLSTATE;
		errorMessage := SQLERRM;
		PERFORM cz_error_handler(jobID, procedureName, errorNumber, errorMessage);
		PERFORM cz_end_audit (jobID, 'FAIL');
		RETURN -16;
	END;

	--Insert samples
	BEGIN
	INSERT
	INTO TM_LZ.Rwg_Samples(
		study_id, COHORTS, EXPR_ID, IMPORT_DATE
	)
	SELECT
		Upper(Replace(  Study_Id,'"','')),
		trim(upper(Replace(  Cohorts,'"',''))),
		Replace(  Expr_Id, '"',''),
		now()
	FROM  TM_LZ.Rwg_Samples_Ext
	WHERE upper(study_id) = upper(trialID);

	GET DIAGNOSTICS rowCt := ROW_COUNT;
	PERFORM cz_write_audit(jobId, databaseName, procedureName,
		'Insert records from TM_LZ.Rwg_Samples_Ext to TM_LZ.Rwg_Samples', rowCt, stepCt, 'Done');
	stepCt := stepCt + 1;
	EXCEPTION
		WHEN OTHERS THEN
		errorNumber := SQLSTATE;
		errorMessage := SQLERRM;
		PERFORM cz_error_handler(jobID, procedureName, errorNumber, errorMessage);
		PERFORM cz_end_audit (jobID, 'FAIL');
		RETURN -16;
	END;

	---Cleanup OVERALL JOB if this proc is being run standalone
	IF newJobFlag = 1 THEN
		PERFORM cz_end_audit(jobID, 'SUCCESS');
	END IF;

	RETURN 0;
EXCEPTION
	WHEN OTHERS THEN
	errorNumber := SQLSTATE;
		errorMessage := SQLERRM;
		PERFORM cz_error_handler(jobID, procedureName, errorNumber, errorMessage);
		PERFORM cz_end_audit (jobID, 'FAIL');
		RETURN -16;
END;

$body$
LANGUAGE PLPGSQL;


