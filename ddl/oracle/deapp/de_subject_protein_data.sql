--
-- Type: TABLE; Owner: DEAPP; Name: DE_SUBJECT_PROTEIN_DATA
--
 CREATE TABLE "DEAPP"."DE_SUBJECT_PROTEIN_DATA" 
  (	"TRIAL_NAME" VARCHAR2(50 BYTE), 
"PROTEIN_ANNOTATION_ID" NUMBER(38,0), 
"COMPONENT" VARCHAR2(100 BYTE), 
"PATIENT_ID" NUMBER(38,0), 
"GENE_SYMBOL" VARCHAR2(100 BYTE), 
"GENE_ID" VARCHAR2(200 BYTE), 
"ASSAY_ID" NUMBER, 
"SUBJECT_ID" VARCHAR2(100 BYTE), 
"INTENSITY" NUMBER, 
"ZSCORE" NUMBER, 
"LOG_INTENSITY" NUMBER, 
"TIMEPOINT" VARCHAR2(100 BYTE)
  ) SEGMENT CREATION IMMEDIATE
 TABLESPACE "TRANSMART" ;

--
-- Type: REF_CONSTRAINT; Owner: DEAPP; Name: FK_PROTEIN_ANNOTATION_ID
--
ALTER TABLE "DEAPP"."DE_SUBJECT_PROTEIN_DATA" ADD CONSTRAINT "FK_PROTEIN_ANNOTATION_ID" FOREIGN KEY ("PROTEIN_ANNOTATION_ID")
 REFERENCES "DEAPP"."DE_PROTEIN_ANNOTATION" ("ID") ENABLE;

