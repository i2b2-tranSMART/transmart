--
-- Type: TABLE; Owner: I2B2DEMODATA; Name: SAMPLE_DIMENSION
--
 CREATE TABLE "I2B2DEMODATA"."SAMPLE_DIMENSION"
  (	"SAMPLE_CD" VARCHAR2(200 BYTE) NOT NULL ENABLE,
 CONSTRAINT "SAMPLE_DIMENSION_PK" PRIMARY KEY ("SAMPLE_CD")
 USING INDEX
 TABLESPACE "I2B2_INDEX"  ENABLE
  ) SEGMENT CREATION IMMEDIATE
 TABLESPACE "I2B2" ;
