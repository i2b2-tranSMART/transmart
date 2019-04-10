--
-- Type: SEQUENCE; Owner: BIOMART_USER; Name: APPLICATION_SETTINGS_ID_SEQ
--
CREATE SEQUENCE  "BIOMART_USER"."APPLICATION_SETTINGS_ID_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;

    --
-- Type: TYPE; Owner: BIOMART_USER; Name: CONCEPT_CD_TAB
--
CREATE OR REPLACE TYPE "BIOMART_USER"."CONCEPT_CD_TAB" IS TABLE OF VARCHAR2(50);

--
-- Type: TYPE; Owner: BIOMART_USER; Name: PATIENTS_TAB
--
CREATE OR REPLACE TYPE "BIOMART_USER"."PATIENTS_TAB" IS TABLE OF NUMBER(10,0);
