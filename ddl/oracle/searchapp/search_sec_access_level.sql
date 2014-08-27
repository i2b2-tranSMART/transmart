--
-- Type: TABLE; Owner: SEARCHAPP; Name: SEARCH_SEC_ACCESS_LEVEL
--
 CREATE TABLE "SEARCHAPP"."SEARCH_SEC_ACCESS_LEVEL" 
  (	"SEARCH_SEC_ACCESS_LEVEL_ID" NUMBER(18,0) NOT NULL ENABLE, 
"ACCESS_LEVEL_NAME" NVARCHAR2(200), 
"ACCESS_LEVEL_VALUE" NUMBER(18,0), 
 CONSTRAINT "SEARCH_SEC_AC_LEVEL_PK" PRIMARY KEY ("SEARCH_SEC_ACCESS_LEVEL_ID")
 USING INDEX
 TABLESPACE "TRANSMART"  ENABLE
  ) SEGMENT CREATION IMMEDIATE
 TABLESPACE "TRANSMART" ;

--
-- Type: TRIGGER; Owner: SEARCHAPP; Name: TRG_SEARCH_SEC_ACC_LEVEL_ID
--
  CREATE OR REPLACE TRIGGER "SEARCHAPP"."TRG_SEARCH_SEC_ACC_LEVEL_ID" before insert on "SEARCH_SEC_ACCESS_LEVEL"    for each row begin     if inserting then       if :NEW."SEARCH_SEC_ACCESS_LEVEL_ID" is null then          select SEQ_SEARCH_DATA_ID.nextval into :NEW."SEARCH_SEC_ACCESS_LEVEL_ID" from dual;       end if;    end if; end;










/
ALTER TRIGGER "SEARCHAPP"."TRG_SEARCH_SEC_ACC_LEVEL_ID" ENABLE;
 
