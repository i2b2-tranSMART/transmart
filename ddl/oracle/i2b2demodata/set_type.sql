--
-- Type: TABLE; Owner: I2B2DEMODATA; Name: SET_TYPE
--
 CREATE TABLE "I2B2DEMODATA"."SET_TYPE" 
  (	"ID" NUMBER(38,0) NOT NULL ENABLE, 
"NAME" VARCHAR2(500 BYTE), 
"CREATE_DATE" TIMESTAMP (6), 
 CONSTRAINT "PK_ST_ID" PRIMARY KEY ("ID")
 USING INDEX
 TABLESPACE "INDX"  ENABLE
  ) SEGMENT CREATION IMMEDIATE
 TABLESPACE "TRANSMART" ;

--
-- Type: SEQUENCE; Owner: I2B2DEMODATA; Name: SQ_UP_PATDIM_PATIENTNUM
--
CREATE SEQUENCE  "I2B2DEMODATA"."SQ_UP_PATDIM_PATIENTNUM"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;

--
-- Type: TRIGGER; Owner: I2B2DEMODATA; Name: TRG_SET_TYPE_ID
--
  CREATE OR REPLACE TRIGGER "I2B2DEMODATA"."TRG_SET_TYPE_ID" 
   before insert on "I2B2DEMODATA"."SET_TYPE" 
   for each row 
begin  
   if inserting then 
      if :NEW."ID" is null then 
         select SQ_UP_PATDIM_PATIENTNUM.nextval into :NEW."ID" from dual; 
      end if; 
   end if; 
end;
/
ALTER TRIGGER "I2B2DEMODATA"."TRG_SET_TYPE_ID" ENABLE;
 
