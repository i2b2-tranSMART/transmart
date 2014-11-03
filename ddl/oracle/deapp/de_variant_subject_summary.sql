--
-- Type: SEQUENCE; Owner: DEAPP; Name: DE_VARIANT_SUBJECT_SUMMARY_SEQ
--
CREATE SEQUENCE  "DEAPP"."DE_VARIANT_SUBJECT_SUMMARY_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;

--
-- Type: TABLE; Owner: DEAPP; Name: DE_VARIANT_SUBJECT_SUMMARY
--
 CREATE TABLE "DEAPP"."DE_VARIANT_SUBJECT_SUMMARY" 
  (	"VARIANT_SUBJECT_SUMMARY_ID" NUMBER NOT NULL ENABLE, 
"CHR" VARCHAR2(50 BYTE), 
"POS" NUMBER, 
"DATASET_ID" VARCHAR2(50 BYTE) NOT NULL ENABLE, 
"SUBJECT_ID" VARCHAR2(50 BYTE) NOT NULL ENABLE, 
"RS_ID" VARCHAR2(500 BYTE), 
"VARIANT" VARCHAR2(1000 BYTE), 
"VARIANT_FORMAT" VARCHAR2(100 BYTE), 
"VARIANT_TYPE" VARCHAR2(100 BYTE), 
"REFERENCE" NUMBER(1,0), 
"ALLELE1" NUMBER(*,0), 
"ALLELE2" NUMBER(*,0), 
"ASSAY_ID" NUMBER, 
 CONSTRAINT "VARIANT_SUBJECT_SUMMARY_ID" PRIMARY KEY ("VARIANT_SUBJECT_SUMMARY_ID")
 USING INDEX
 TABLESPACE "TRANSMART"  ENABLE
  ) SEGMENT CREATION IMMEDIATE
 TABLESPACE "TRANSMART" ;

--
-- Type: TRIGGER; Owner: DEAPP; Name: TRG_DE_VARIANT_SUBJ_SUMM_ID
--
  CREATE OR REPLACE TRIGGER "DEAPP"."TRG_DE_VARIANT_SUBJ_SUMM_ID" 
before insert on "DEAPP"."DE_VARIANT_SUBJECT_SUMMARY"
for each row begin
       	if inserting then
               	if :NEW."VARIANT_SUBJECT_SUMMARY_ID" is null then
                       	select DE_VARIANT_SUBJECT_SUMMARY_SEQ.nextval into :NEW."VARIANT_SUBJECT_SUMMARY_ID" from dual;
               	end if;
       	end if;
end;
/
ALTER TRIGGER "DEAPP"."TRG_DE_VARIANT_SUBJ_SUMM_ID" ENABLE;
 
--
-- Type: REF_CONSTRAINT; Owner: DEAPP; Name: VARIANT_SUBJECT_SUMMARY_FK
--
ALTER TABLE "DEAPP"."DE_VARIANT_SUBJECT_SUMMARY" ADD CONSTRAINT "VARIANT_SUBJECT_SUMMARY_FK" FOREIGN KEY ("DATASET_ID")
 REFERENCES "DEAPP"."DE_VARIANT_DATASET" ("DATASET_ID") ENABLE;

