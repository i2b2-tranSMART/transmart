--
-- Type: TABLE; Owner: DEAPP; Name: DE_SUBJECT_MICROARRAY_LOGS
--
 CREATE TABLE "DEAPP"."DE_SUBJECT_MICROARRAY_LOGS" 
  (	"PROBESET" VARCHAR2(50 BYTE), 
"RAW_INTENSITY" NUMBER, 
"PVALUE" FLOAT(126), 
"REFSEQ" VARCHAR2(50 BYTE), 
"GENE_SYMBOL" VARCHAR2(50 BYTE), 
"ASSAY_ID" NUMBER(18,0), 
"PATIENT_ID" NUMBER(18,0), 
"SUBJECT_ID" VARCHAR2(100 BYTE), 
"TRIAL_NAME" VARCHAR2(15 BYTE), 
"TIMEPOINT" VARCHAR2(30 BYTE), 
"LOG_INTENSITY" NUMBER
  ) SEGMENT CREATION IMMEDIATE
NOCOMPRESS LOGGING
 TABLESPACE "TRANSMART" ;
