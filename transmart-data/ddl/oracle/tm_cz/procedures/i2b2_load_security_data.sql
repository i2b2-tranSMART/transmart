--
-- Type: PROCEDURE; Owner: TM_CZ; Name: I2B2_LOAD_SECURITY_DATA
--
  CREATE OR REPLACE PROCEDURE "TM_CZ"."I2B2_LOAD_SECURITY_DATA"
(
  currentJobID NUMBER := null
)
AS
  -- JEA@20111206	Changed to use security token (tval_char) from SECURITY concept in observation_fact
  -- JEA@20111221	Added distinct to subselect to fix duplicate record issue
  -- JEA@20120214	Added coalesce to EXP:PUBLIC where no SECURITY records found (upper-level nodes)

  -- tranSMART version also checks /Public Studies/SECURITY/ node

  --Audit variables
  newJobFlag INTEGER(1);
  databaseName VARCHAR(100);
  procedureName VARCHAR(100);
  jobID number(18,0);
  stepCt number(18,0);

  secNodeExists		int;

BEGIN

  --Set Audit Parameters
  newJobFlag := 0; -- False (Default)
  jobID := currentJobID;

  SELECT sys_context('USERENV', 'CURRENT_SCHEMA') INTO databaseName FROM dual;
  procedureName := $$PLSQL_UNIT;

  --Audit JOB Initialization
  --If Job ID does not exist, then this is a single procedure run and we need to create it
  IF(jobID IS NULL or jobID < 1)
  THEN
    newJobFlag := 1; -- True
    cz_start_audit (procedureName, databaseName, jobID);
  END IF;

  stepCt := 0;

  Execute immediate ('truncate table I2B2METADATA.i2b2_secure');

  stepCt := stepCt + 1;
  cz_write_audit(jobId,databaseName,procedureName,'Truncate I2B2METADATA.i2b2_secure',0,stepCt,'Done');

  insert into I2B2METADATA.i2b2_secure(
    C_HLEVEL,
    C_FULLNAME,
    C_NAME,
    C_SYNONYM_CD,
    C_VISUALATTRIBUTES,
    C_TOTALNUM,
    C_BASECODE,
    C_METADATAXML,
    C_FACTTABLECOLUMN,
    C_TABLENAME,
    C_COLUMNNAME,
    C_COLUMNDATATYPE,
    C_OPERATOR,
    C_DIMCODE,
    C_COMMENT,
    C_TOOLTIP,
    UPDATE_DATE,
    DOWNLOAD_DATE,
    IMPORT_DATE,
    SOURCESYSTEM_CD,
    VALUETYPE_CD,
	secure_obj_token)
  select
    b.C_HLEVEL,
    b.C_FULLNAME,
    b.C_NAME,
    b.C_SYNONYM_CD,
    b.C_VISUALATTRIBUTES,
    b.C_TOTALNUM,
    b.C_BASECODE,
    b.C_METADATAXML,
    b.C_FACTTABLECOLUMN,
    b.C_TABLENAME,
    b.C_COLUMNNAME,
    b.C_COLUMNDATATYPE,
    b.C_OPERATOR,
    b.C_DIMCODE,
    b.C_COMMENT,
    b.C_TOOLTIP,
    b.UPDATE_DATE,
    b.DOWNLOAD_DATE,
    b.IMPORT_DATE,
    b.SOURCESYSTEM_CD,
    b.VALUETYPE_CD,
	coalesce(f.tval_char,'EXP:PUBLIC')
    from I2B2METADATA.I2B2 b
		,(select distinct modifier_cd, tval_char from i2b2demodata.observation_fact where concept_cd = 'SECURITY') f
	where b.sourcesystem_cd = f.modifier_cd(+);
    stepCt := stepCt + 1;
    cz_write_audit(jobId,databaseName,procedureName,'Insert security data into I2B2METADATA.i2b2_secure',SQL%ROWCOUNT,stepCt,'Done');

    commit;

	--	check if SECURITY node exists in i2b2

	select count(*) into secNodeExists
	from i2b2metadata.i2b2
	where c_fullname = '\Public Studies\SECURITY\';

	if secNodeExists = 0 then
		insert into i2b2metadata.i2b2
		(c_hlevel
		,c_fullname
		,c_name
		,c_synonym_cd
		,c_visualattributes
		,c_totalnum
		,c_basecode
		,c_metadataxml
		,c_facttablecolumn
		,c_tablename
		,c_columnname
		,c_columndatatype
		,c_operator
		,c_dimcode
		,c_comment
		,c_tooltip
		,update_date
		,download_date
		,import_date
		,sourcesystem_cd
		,valuetype_cd
		,i2b2_id
		)
		select 1 as c_hlevel
			  ,'\Public Studies\SECURITY\' as c_fullname
			  ,'SECURITY' as c_name
			  ,'N' as c_synonym_cd
			  ,'CA' as c_visualattributes
			  ,null as c_totalnum
			  ,null as c_basecode
			  ,null as c_metadataxml
			  ,'concept_cd' as c_facttablecolumn
			  ,'concept_dimension' as c_tablename
			  ,'concept_path' as c_columnname
			  ,'T' as c_columndatatype
			  ,'LIKE' as c_operator
			  ,'\Public Studies\SECURITY\' as c_dimcode
			  ,null as c_comment
			  ,'\Public Studies\SECURITY\' as c_tooltip
			  ,sysdate as update_date
			  ,null as download_date
			  ,sysdate as import_date
			  ,null as sourcesystem_cd
			  ,null as valuetype_cd
			  ,I2B2_ID_SEQ.nextval as i2b2_id
		from dual;

		stepCt := stepCt + 1;
		cz_write_audit(jobId,databaseName,procedureName,'Insert \Public Studies\SECURITY\ node to i2b2',SQL%ROWCOUNT,stepCt,'Done');
		COMMIT;
	end if;

	--	check if SECURITY node exists in concept_dimension

	select count(*) into secNodeExists
	from i2b2demodata.concept_dimension
	where concept_path = '\Public Studies\SECURITY\';

	if secNodeExists = 0 then
		insert into i2b2demodata.concept_dimension
		(concept_cd
		,concept_path
		,name_char
		,update_date
		,download_date
		,import_date
		,sourcesystem_cd
		)
		select 'SECURITY'
			 ,'\Public Studies\SECURITY\'
			 ,'SECURITY'
			 ,sysdate
			 ,sysdate
			 ,sysdate
			 ,null
		from dual;
		stepCt := stepCt + 1;
		cz_write_audit(jobId,databaseName,procedureName,'Insert \Public Studies\SECURITY\ node to concept_dimension',SQL%ROWCOUNT,stepCt,'Done');
		commit;
	end if;

    ---Cleanup OVERALL JOB if this proc is being run standalone
  IF newJobFlag = 1
  THEN
    cz_end_audit (jobID, 'SUCCESS');
  END IF;

  EXCEPTION
  WHEN OTHERS THEN
    --Handle errors.
    cz_error_handler (jobID, procedureName);
    --End Proc
    cz_end_audit (jobID, 'FAIL');

end;
/
