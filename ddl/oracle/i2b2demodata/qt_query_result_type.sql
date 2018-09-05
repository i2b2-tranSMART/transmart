--
-- Type: TABLE; Owner: I2B2DEMODATA; Name: QT_QUERY_RESULT_TYPE
--
 CREATE TABLE "I2B2DEMODATA"."QT_QUERY_RESULT_TYPE" 
  (	"RESULT_TYPE_ID" NUMBER(3,0) NOT NULL ENABLE, 
"NAME" VARCHAR2(100 BYTE), 
"DESCRIPTION" VARCHAR2(200 BYTE), 
"DISPLAY_TYPE_ID" VARCHAR2(500 BYTE), 
"VISUAL_ATTRIBUTE_TYPE_ID" VARCHAR2(3 BYTE), 
 CONSTRAINT "QT_QUERY_RESULT_TYPE_PKEY" PRIMARY KEY ("RESULT_TYPE_ID")
 USING INDEX
 TABLESPACE "INDX"  ENABLE
  ) SEGMENT CREATION IMMEDIATE
 TABLESPACE "TRANSMART" ;

--
-- Type: SEQUENCE; Owner: I2B2DEMODATA; Name: QT_SQ_QR_QRID
--
CREATE SEQUENCE  "I2B2DEMODATA"."QT_SQ_QR_QRID"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;

--
-- Type: TRIGGER; Owner: I2B2DEMODATA; Name: TRG_QT_QRT_RT_ID
--
  CREATE OR REPLACE TRIGGER "I2B2DEMODATA"."TRG_QT_QRT_RT_ID" 
   before insert on "I2B2DEMODATA"."QT_QUERY_RESULT_TYPE" 
   for each row 
begin  
   if inserting then 
      if :NEW."RESULT_TYPE_ID" is null then 
         select QT_SQ_QR_QRID.nextval into :NEW."RESULT_TYPE_ID" from dual; 
      end if; 
   end if; 
end;
/
ALTER TRIGGER "I2B2DEMODATA"."TRG_QT_QRT_RT_ID" ENABLE;
 
