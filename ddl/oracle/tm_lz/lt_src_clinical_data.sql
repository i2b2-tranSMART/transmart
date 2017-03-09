--
-- Type: TABLE; Owner: TM_LZ; Name: LT_SRC_CLINICAL_DATA
--
 CREATE TABLE "TM_LZ"."LT_SRC_CLINICAL_DATA" 
  (	"STUDY_ID" VARCHAR2(25 BYTE), 
"SITE_ID" VARCHAR2(50 BYTE), 
"SUBJECT_ID" VARCHAR2(100 BYTE), 
"VISIT_NAME" VARCHAR2(100 BYTE), 
"SAMPLE_TYPE" VARCHAR2(100 BYTE), 
"DATA_LABEL" VARCHAR2(500 BYTE), 
"DATA_VALUE" VARCHAR2(500 BYTE), 
"CATEGORY_CD" VARCHAR2(250 BYTE), 
"DATA_LABEL_CTRL_VOCAB_CODE" VARCHAR2(200 BYTE), 
"DATA_VALUE_CTRL_VOCAB_CODE" VARCHAR2(500 BYTE), 
"DATA_LABEL_COMPONENTS" VARCHAR2(1000 BYTE), 
"UNITS_CD" VARCHAR2(50 BYTE), 
"VISIT_DATE" VARCHAR2(200 BYTE), 
"LINK_TYPE" VARCHAR2(20 BYTE), 
"LINK_VALUE" VARCHAR2(200 BYTE), 
"END_DATE" VARCHAR2(50 BYTE), 
"VISIT_REFERENCE" VARCHAR2(100 BYTE), 
"DATE_IND" CHAR(1 BYTE), 
"OBS_STRING" VARCHAR2(100 BYTE), 
"VALUETYPE_CD" VARCHAR2(50 BYTE), 
"DATE_TIMESTAMP" DATE, 
"CTRL_VOCAB_CODE" VARCHAR2(200 BYTE), 
"MODIFIER_CD" VARCHAR2(100 BYTE), 
"SAMPLE_CD" VARCHAR2(200 BYTE)
  ) SEGMENT CREATION IMMEDIATE
NOCOMPRESS NOLOGGING
 TABLESPACE "TRANSMART" 
  CACHE ;
--
-- Type: INDEX; Owner: TM_LZ; Name: IDX_SCD_STUDY
--
CREATE INDEX "TM_LZ"."IDX_SCD_STUDY" ON "TM_LZ"."LT_SRC_CLINICAL_DATA" ("STUDY_ID")
TABLESPACE "INDX" ;


