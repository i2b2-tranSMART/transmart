--
-- Type: TABLE; Owner: BIOMART; Name: MIRNA_BIO_MARKER
--
 CREATE TABLE "BIOMART"."MIRNA_BIO_MARKER" 
  (	"BIO_MARKER_ID" NUMBER(18,0) NOT NULL ENABLE, 
"BIO_MARKER_NAME" NVARCHAR2(200), 
"BIO_MARKER_DESCRIPTION" NVARCHAR2(1000), 
"ORGANISM" NVARCHAR2(200), 
"PRIMARY_SOURCE_CODE" NVARCHAR2(200), 
"PRIMARY_EXTERNAL_ID" NVARCHAR2(200), 
"BIO_MARKER_TYPE" NVARCHAR2(200) NOT NULL ENABLE, 
 CONSTRAINT "MIRNA_BM_ORG_PRI_EID_KEY" UNIQUE ("ORGANISM", "PRIMARY_EXTERNAL_ID")
 USING INDEX
 TABLESPACE "INDX"  ENABLE
  ) SEGMENT CREATION DEFERRED
 TABLESPACE "TRANSMART" ;

