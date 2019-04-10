--
-- Type: TABLE; Owner: I2B2DEMODATA; Name: JMS_ROLES
--
 CREATE TABLE "I2B2DEMODATA"."JMS_ROLES"
  (	"ROLEID" VARCHAR2(32 BYTE) NOT NULL ENABLE,
"USERID" VARCHAR2(32 BYTE) NOT NULL ENABLE,
 PRIMARY KEY ("USERID", "ROLEID")
 USING INDEX
 TABLESPACE "I2B2_INDEX"  ENABLE
  ) SEGMENT CREATION IMMEDIATE
 TABLESPACE "I2B2" ;
