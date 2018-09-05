--
-- Type: TABLE; Owner: BIOMART; Name: CTD_DISEASE
--
 CREATE TABLE "BIOMART"."CTD_DISEASE" 
  (	"CTD_STUDY_ID" NUMBER, 
"COMMON_NAME" VARCHAR2(4000 BYTE), 
"ICD10" VARCHAR2(4000 BYTE), 
"MESH" VARCHAR2(4000 BYTE), 
"DISEASE_SEVERITY" VARCHAR2(4000 BYTE)
  ) SEGMENT CREATION IMMEDIATE
 TABLESPACE "TRANSMART" ;

