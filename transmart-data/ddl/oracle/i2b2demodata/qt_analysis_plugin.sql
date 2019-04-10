--
-- Type: TABLE; Owner: I2B2DEMODATA; Name: QT_ANALYSIS_PLUGIN
--
 CREATE TABLE "I2B2DEMODATA"."QT_ANALYSIS_PLUGIN"
  (	"PLUGIN_ID" NUMBER(10,0) NOT NULL ENABLE,
"PLUGIN_NAME" VARCHAR2(2000 BYTE),
"DESCRIPTION" VARCHAR2(2000 BYTE),
"VERSION_CD" VARCHAR2(50 BYTE),	--support for version
"PARAMETER_INFO" CLOB,		-- plugin parameter stored as xml
"PARAMETER_INFO_XSD" CLOB,
"COMMAND_LINE" CLOB,
"WORKING_FOLDER" CLOB,
"COMMANDOPTION_CD" CLOB,
"PLUGIN_ICON" CLOB,
"STATUS_CD" VARCHAR2(50 BYTE),	-- active,deleted,..
"USER_ID" VARCHAR2(50 BYTE),
"GROUP_ID" VARCHAR2(50 BYTE),
"CREATE_DATE" DATE,
"UPDATE_DATE" DATE,
 CONSTRAINT "ANALYSIS_PLUGIN_PK" PRIMARY KEY ("PLUGIN_ID")
 USING INDEX
 TABLESPACE "I2B2_INDEX"  ENABLE
  ) SEGMENT CREATION DEFERRED
 TABLESPACE "I2B2"
LOB ("PARAMETER_INFO") STORE AS SECUREFILE (
 TABLESPACE "I2B2" ENABLE STORAGE IN ROW CHUNK 8192
 NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES )
LOB ("PARAMETER_INFO_XSD") STORE AS SECUREFILE (
 TABLESPACE "I2B2" ENABLE STORAGE IN ROW CHUNK 8192
 NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES )
LOB ("COMMAND_LINE") STORE AS SECUREFILE (
 TABLESPACE "I2B2" ENABLE STORAGE IN ROW CHUNK 8192
 NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES )
LOB ("WORKING_FOLDER") STORE AS SECUREFILE (
 TABLESPACE "I2B2" ENABLE STORAGE IN ROW CHUNK 8192
 NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES )
LOB ("COMMANDOPTION_CD") STORE AS SECUREFILE (
 TABLESPACE "I2B2" ENABLE STORAGE IN ROW CHUNK 8192
 NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES )
LOB ("PLUGIN_ICON") STORE AS SECUREFILE (
 TABLESPACE "I2B2" ENABLE STORAGE IN ROW CHUNK 8192
 NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES ) ;
--
-- Type: INDEX; Owner: I2B2DEMODATA; Name: QT_APNAMEVERGRP_IDX
--
CREATE INDEX "I2B2DEMODATA"."QT_APNAMEVERGRP_IDX" ON "I2B2DEMODATA"."QT_ANALYSIS_PLUGIN" ("PLUGIN_NAME", "VERSION_CD", "GROUP_ID")
TABLESPACE "I2B2_INDEX" ;
