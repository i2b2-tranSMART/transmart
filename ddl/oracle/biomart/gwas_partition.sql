--
-- Type: TABLE; Owner: BIOMART; Name: GWAS_PARTITION
--
 CREATE TABLE "BIOMART"."GWAS_PARTITION" 
  (	"BIO_ASY_ANALYSIS_GWAS_ID" NUMBER(18,0) NOT NULL ENABLE, 
"BIO_ASSAY_ANALYSIS_ID" NUMBER(18,0) NOT NULL ENABLE, 
"RS_ID" NVARCHAR2(50), 
"P_VALUE_CHAR" VARCHAR2(100 BYTE), 
"P_VALUE" BINARY_DOUBLE, 
"LOG_P_VALUE" BINARY_DOUBLE, 
"ETL_ID" NUMBER(18,0), 
"EXT_DATA" VARCHAR2(4000 BYTE)
  )
COMPRESS BASIC  NOLOGGING 
 TABLESPACE "TRANSMART" 
 PARTITION BY LIST ("BIO_ASSAY_ANALYSIS_ID") 
(PARTITION "NULL_PARTITION"  VALUES (NULL) TABLESPACE "TRANSMART") ;
--
-- Type: REF_CONSTRAINT; Owner: BIOMART; Name: BIO_AA_GWAS_FK
--
ALTER TABLE "BIOMART"."GWAS_PARTITION" ADD CONSTRAINT "BIO_AA_GWAS_FK" FOREIGN KEY ("BIO_ASSAY_ANALYSIS_ID")
 REFERENCES "BIOMART"."BIO_ASSAY_ANALYSIS" ("BIO_ASSAY_ANALYSIS_ID") ENABLE;
