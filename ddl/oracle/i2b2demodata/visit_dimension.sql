--
-- Type: TABLE; Owner: I2B2DEMODATA; Name: VISIT_DIMENSION
--
 CREATE TABLE "I2B2DEMODATA"."VISIT_DIMENSION" 
  (	"ENCOUNTER_NUM" NUMBER(38,0) NOT NULL ENABLE, 
"PATIENT_NUM" NUMBER(38,0) NOT NULL ENABLE, 
"INOUT_CD" VARCHAR2(50 BYTE), 
"LOCATION_CD" VARCHAR2(50 BYTE), 
"LOCATION_PATH" VARCHAR2(900 BYTE), 
"START_DATE" DATE, 
"END_DATE" DATE, 
"UPDATE_DATE" DATE, 
"DOWNLOAD_DATE" DATE, 
"IMPORT_DATE" DATE, 
"SOURCESYSTEM_CD" VARCHAR2(50 BYTE), 
"UPLOAD_ID" NUMBER(38,0), 
"ACTIVE_STATUS_CD" VARCHAR2(50 BYTE), 
"VISIT_BLOB" CLOB, 
"LENGTH_OF_STAY" NUMBER(38,0), 
 CONSTRAINT "VISIT_DIMENSION_PK" PRIMARY KEY ("ENCOUNTER_NUM", "PATIENT_NUM")
 USING INDEX
 TABLESPACE "INDX"  ENABLE
  ) SEGMENT CREATION IMMEDIATE
 TABLESPACE "TRANSMART" 
LOB ("VISIT_BLOB") STORE AS BASICFILE (
 TABLESPACE "TRANSMART" ENABLE STORAGE IN ROW CHUNK 8192 RETENTION 
 NOCACHE LOGGING ) ;

--
-- Type: INDEX; Owner: I2B2DEMODATA; Name: VD_UPLOADID_IDX
--
CREATE INDEX "I2B2DEMODATA"."VD_UPLOADID_IDX" ON "I2B2DEMODATA"."VISIT_DIMENSION" ("UPLOAD_ID")
TABLESPACE "INDX" ;

--
-- Type: INDEX; Owner: I2B2DEMODATA; Name: VISITDIM_STD_EDD_IDX
--
CREATE INDEX "I2B2DEMODATA"."VISITDIM_STD_EDD_IDX" ON "I2B2DEMODATA"."VISIT_DIMENSION" ("START_DATE", "END_DATE")
TABLESPACE "INDX" ;

--
-- Type: INDEX; Owner: I2B2DEMODATA; Name: VISITDIM_EN_PN_LP_IO_SD_IDX
--
CREATE INDEX "I2B2DEMODATA"."VISITDIM_EN_PN_LP_IO_SD_IDX" ON "I2B2DEMODATA"."VISIT_DIMENSION" ("ENCOUNTER_NUM", "PATIENT_NUM", "LOCATION_PATH", "INOUT_CD", "START_DATE")
TABLESPACE "INDX" ;

