--
-- Type: TABLE; Owner: TM_WZ; Name: DE_SUBJECT_RBM_DATA
--
 CREATE TABLE "TM_WZ"."DE_SUBJECT_RBM_DATA" 
  (	"TRIAL_NAME" VARCHAR2(100 BYTE), 
"ANTIGEN_NAME" VARCHAR2(100 BYTE), 
"N_VALUE" NUMBER, 
"PATIENT_ID" NUMBER(38,0), 
"GENE_SYMBOL" VARCHAR2(100 BYTE), 
"GENE_ID" NUMBER(10,0), 
"ASSAY_ID" NUMBER, 
"NORMALIZED_VALUE" NUMBER(18,5), 
"CONCEPT_CD" NVARCHAR2(100), 
"TIMEPOINT" VARCHAR2(100 BYTE), 
"DATA_UID" VARCHAR2(100 BYTE), 
"VALUE" NUMBER(18,4), 
"LOG_INTENSITY" NUMBER, 
"MEAN_INTENSITY" NUMBER, 
"STDDEV_INTENSITY" NUMBER, 
"MEDIAN_INTENSITY" NUMBER, 
"ZSCORE" NUMBER(18,4), 
"RBM_PANEL" VARCHAR2(50 BYTE), 
"UNIT" VARCHAR2(50 CHAR), 
"ID" NUMBER(38,0) NOT NULL ENABLE
  ) SEGMENT CREATION IMMEDIATE
NOCOMPRESS LOGGING
 TABLESPACE "TRANSMART" ;
