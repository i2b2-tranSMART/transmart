--
-- Type: TABLE; Owner: SEARCHAPP; Name: SEARCH_GENE_SIGNATURE
--
 CREATE TABLE "SEARCHAPP"."SEARCH_GENE_SIGNATURE" 
  (	"SEARCH_GENE_SIGNATURE_ID" NUMBER NOT NULL ENABLE, 
"NAME" VARCHAR2(100 BYTE) NOT NULL ENABLE, 
"DESCRIPTION" VARCHAR2(1000 BYTE), 
"UNIQUE_ID" VARCHAR2(50 BYTE), 
"CREATE_DATE" TIMESTAMP (6) NOT NULL ENABLE, 
"CREATED_BY_AUTH_USER_ID" NUMBER NOT NULL ENABLE, 
"LAST_MODIFIED_DATE" TIMESTAMP (6), 
"MODIFIED_BY_AUTH_USER_ID" NUMBER, 
"VERSION_NUMBER" VARCHAR2(50 BYTE), 
"PUBLIC_FLAG" NUMBER(1,0) DEFAULT 0 NOT NULL ENABLE, 
"DELETED_FLAG" NUMBER(1,0) DEFAULT 0 NOT NULL ENABLE, 
"PARENT_GENE_SIGNATURE_ID" NUMBER, 
"SOURCE_CONCEPT_ID" NUMBER, 
"SOURCE_OTHER" VARCHAR2(255 BYTE), 
"OWNER_CONCEPT_ID" NUMBER, 
"STIMULUS_DESCRIPTION" VARCHAR2(1000 BYTE), 
"STIMULUS_DOSING" VARCHAR2(255 BYTE), 
"TREATMENT_DESCRIPTION" VARCHAR2(1000 BYTE), 
"TREATMENT_DOSING" VARCHAR2(255 BYTE), 
"TREATMENT_BIO_COMPOUND_ID" NUMBER, 
"TREATMENT_PROTOCOL_NUMBER" VARCHAR2(50 BYTE), 
"PMID_LIST" VARCHAR2(255 BYTE), 
"SPECIES_CONCEPT_ID" NUMBER NOT NULL ENABLE, 
"SPECIES_MOUSE_SRC_CONCEPT_ID" NUMBER, 
"SPECIES_MOUSE_DETAIL" VARCHAR2(255 BYTE), 
"TISSUE_TYPE_CONCEPT_ID" NUMBER, 
"EXPERIMENT_TYPE_CONCEPT_ID" NUMBER, 
"EXPERIMENT_TYPE_IN_VIVO_DESCR" VARCHAR2(255 BYTE), 
"EXPERIMENT_TYPE_ATCC_REF" VARCHAR2(255 BYTE), 
"ANALYTIC_CAT_CONCEPT_ID" NUMBER, 
"ANALYTIC_CAT_OTHER" VARCHAR2(255 BYTE), 
"BIO_ASSAY_PLATFORM_ID" NUMBER NOT NULL ENABLE, 
"ANALYST_NAME" VARCHAR2(100 BYTE), 
"NORM_METHOD_CONCEPT_ID" NUMBER, 
"NORM_METHOD_OTHER" VARCHAR2(255 BYTE), 
"ANALYSIS_METHOD_CONCEPT_ID" NUMBER, 
"ANALYSIS_METHOD_OTHER" VARCHAR2(255 BYTE), 
"MULTIPLE_TESTING_CORRECTION" NUMBER(1,0), 
"P_VALUE_CUTOFF_CONCEPT_ID" NUMBER NOT NULL ENABLE, 
"UPLOAD_FILE" VARCHAR2(255 BYTE) NOT NULL ENABLE, 
"SEARCH_GENE_SIG_FILE_SCHEMA_ID" NUMBER DEFAULT 1 NOT NULL ENABLE, 
"FOLD_CHG_METRIC_CONCEPT_ID" NUMBER DEFAULT NULL NOT NULL ENABLE, 
"EXPERIMENT_TYPE_CELL_LINE_ID" NUMBER, 
"QC_PERFORMED" NUMBER(1,0), 
"QC_DATE" DATE, 
"QC_INFO" VARCHAR2(255 BYTE), 
"DATA_SOURCE" VARCHAR2(255 BYTE), 
"CUSTOM_VALUE1" VARCHAR2(255 BYTE), 
"CUSTOM_NAME1" VARCHAR2(255 BYTE), 
"CUSTOM_VALUE2" VARCHAR2(255 BYTE), 
"CUSTOM_NAME2" VARCHAR2(255 BYTE), 
"CUSTOM_VALUE3" VARCHAR2(255 BYTE), 
"CUSTOM_NAME3" VARCHAR2(255 BYTE), 
"CUSTOM_VALUE4" VARCHAR2(255 BYTE), 
"CUSTOM_NAME4" VARCHAR2(255 BYTE), 
"CUSTOM_VALUE5" VARCHAR2(255 BYTE), 
"CUSTOM_NAME5" VARCHAR2(255 BYTE), 
"VERSION" VARCHAR2(255 BYTE), 
 CONSTRAINT "SEARCH_GENE_SIG_DESCR_PK" PRIMARY KEY ("SEARCH_GENE_SIGNATURE_ID")
 USING INDEX
 TABLESPACE "INDX"  ENABLE
  ) SEGMENT CREATION IMMEDIATE
 TABLESPACE "TRANSMART" ;

--
-- Type: REF_CONSTRAINT; Owner: SEARCHAPP; Name: GENE_SIG_FILE_SCHEMA_FK1
--
ALTER TABLE "SEARCHAPP"."SEARCH_GENE_SIGNATURE" ADD CONSTRAINT "GENE_SIG_FILE_SCHEMA_FK1" FOREIGN KEY ("SEARCH_GENE_SIG_FILE_SCHEMA_ID")
 REFERENCES "SEARCHAPP"."SEARCH_GENE_SIG_FILE_SCHEMA" ("SEARCH_GENE_SIG_FILE_SCHEMA_ID") ENABLE;

--
-- Type: REF_CONSTRAINT; Owner: SEARCHAPP; Name: GENE_SIG_CREATE_AUTH_USER_FK1
--
ALTER TABLE "SEARCHAPP"."SEARCH_GENE_SIGNATURE" ADD CONSTRAINT "GENE_SIG_CREATE_AUTH_USER_FK1" FOREIGN KEY ("CREATED_BY_AUTH_USER_ID")
 REFERENCES "SEARCHAPP"."SEARCH_AUTH_USER" ("ID") ENABLE;

--
-- Type: REF_CONSTRAINT; Owner: SEARCHAPP; Name: GENE_SIG_PARENT_FK1
--
ALTER TABLE "SEARCHAPP"."SEARCH_GENE_SIGNATURE" ADD CONSTRAINT "GENE_SIG_PARENT_FK1" FOREIGN KEY ("PARENT_GENE_SIGNATURE_ID")
 REFERENCES "SEARCHAPP"."SEARCH_GENE_SIGNATURE" ("SEARCH_GENE_SIGNATURE_ID") ENABLE;

--
-- Type: REF_CONSTRAINT; Owner: SEARCHAPP; Name: GENE_SIG_MOD_AUTH_USER_FK1
--
ALTER TABLE "SEARCHAPP"."SEARCH_GENE_SIGNATURE" ADD CONSTRAINT "GENE_SIG_MOD_AUTH_USER_FK1" FOREIGN KEY ("MODIFIED_BY_AUTH_USER_ID")
 REFERENCES "SEARCHAPP"."SEARCH_AUTH_USER" ("ID") ENABLE;

