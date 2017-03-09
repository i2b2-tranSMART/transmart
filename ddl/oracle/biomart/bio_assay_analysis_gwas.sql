--
-- Type: TABLE; Owner: BIOMART; Name: BIO_ASSAY_ANALYSIS_GWAS
--
 CREATE TABLE "BIOMART"."BIO_ASSAY_ANALYSIS_GWAS" 
  (	"BIO_ASY_ANALYSIS_GWAS_ID" NUMBER(18,0) NOT NULL ENABLE, 
"BIO_ASSAY_ANALYSIS_ID" NUMBER(18,0) NOT NULL ENABLE, 
"RS_ID" NVARCHAR2(50), 
"P_VALUE_CHAR" VARCHAR2(100 BYTE), 
"P_VALUE" BINARY_DOUBLE, 
"LOG_P_VALUE" BINARY_DOUBLE, 
"ETL_ID" NUMBER(18,0), 
"EXT_DATA" VARCHAR2(4000 BYTE),
"EFFECT_ALLELE" VARCHAR2(100 BYTE),
"OTHER_ALLELE" VARCHAR2(100 BYTE),
"BETA" VARCHAR2(100 BYTE),
"STANDARD_ERROR" VARCHAR2(100 BYTE),
CONSTRAINT "BIO_ASY_ANALYSIS_GWAS_ID_PK" PRIMARY KEY ("BIO_ASY_ANALYSIS_GWAS_ID")
 USING INDEX
 TABLESPACE "INDX" ENABLE
)
COMPRESS BASIC  NOLOGGING 
 TABLESPACE "TRANSMART" 
 PARTITION BY LIST ("BIO_ASSAY_ANALYSIS_ID") 
 (PARTITION "NULL_PARTITION"  VALUES (NULL) TABLESPACE "TRANSMART");
