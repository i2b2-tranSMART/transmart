--
-- Type: TABLE; Owner: I2B2DEMODATA; Name: CONCEPT_DIMENSION
--
 CREATE TABLE "I2B2DEMODATA"."CONCEPT_DIMENSION"
     (	"CONCEPT_PATH" VARCHAR2(4000 BYTE) NOT NULL ENABLE,
"CONCEPT_CD" VARCHAR2(50 BYTE) NOT NULL ENABLE, --null in i2b2
"NAME_CHAR" VARCHAR2(2000 BYTE),
"CONCEPT_BLOB" CLOB,
"UPDATE_DATE" DATE,
"DOWNLOAD_DATE" DATE,
"IMPORT_DATE" DATE,
"SOURCESYSTEM_CD" VARCHAR2(50 BYTE),
"UPLOAD_ID" NUMBER,
"TABLE_NAME" VARCHAR2(255 BYTE), -- not in i2b2
 CONSTRAINT "CONCEPT_DIMENSION_PK" PRIMARY KEY ("CONCEPT_PATH")
  ) SEGMENT CREATION IMMEDIATE
COMPRESS BASIC NOLOGGING
 TABLESPACE "I2B2"
LOB ("CONCEPT_BLOB") STORE AS SECUREFILE (
 TABLESPACE "I2B2" ENABLE STORAGE IN ROW CHUNK 8192
 NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES ) ;
--
-- Type: INDEX; Owner: I2B2DEMODATA; Name: IDX_CONCEPT_DIM_1
--
CREATE INDEX "I2B2DEMODATA"."IDX_CONCEPT_DIM_1" ON "I2B2DEMODATA"."CONCEPT_DIMENSION" ("CONCEPT_CD") -- not in i2b2
TABLESPACE "I2B2_INDEX" ;
--
-- Type: INDEX; Owner: I2B2DEMODATA; Name: IDX_CONCEPT_DIM3
--
CREATE INDEX "I2B2DEMODATA"."IDX_CONCEPT_DIM3" ON "I2B2DEMODATA"."CONCEPT_DIMENSION" ("CONCEPT_PATH", "CONCEPT_CD")
TABLESPACE "I2B2_INDEX" ;

-- no trigger in i2b2

--
-- Type: TRIGGER; Owner: I2B2DEMODATA; Name: TRG_CONCEPT_DIMENSION_CD
--
---  CREATE OR REPLACE TRIGGER "I2B2DEMODATA"."TRG_CONCEPT_DIMENSION_CD"
---	 before insert on "CONCEPT_DIMENSION"
---	 for each row begin
---	 if inserting then
---	 if :NEW."CONCEPT_CD" is null then
---	 select CONCEPT_ID.nextval into :NEW."CONCEPT_CD" from dual;
---	 end if;
---	 end if;
---	 end;
---/
---ALTER TRIGGER "I2B2DEMODATA"."TRG_CONCEPT_DIMENSION_CD" ENABLE;
