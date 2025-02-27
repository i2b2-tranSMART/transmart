--
-- Type: TABLE; Owner: BIOMART; Name: BIO_TAXONOMY
--
 CREATE TABLE "BIOMART"."BIO_TAXONOMY" 
  (	"BIO_TAXONOMY_ID" NUMBER(18,0) NOT NULL ENABLE, 
"TAXON_NAME" NVARCHAR2(200) NOT NULL ENABLE, 
"TAXON_LABEL" NVARCHAR2(200) NOT NULL ENABLE, 
"NCBI_TAX_ID" NVARCHAR2(200), 
 CONSTRAINT "BIO_TAXON_PK" PRIMARY KEY ("BIO_TAXONOMY_ID")
 USING INDEX
 TABLESPACE "INDX"  ENABLE
  ) SEGMENT CREATION IMMEDIATE
 TABLESPACE "TRANSMART" ;

--
-- Type: TRIGGER; Owner: BIOMART; Name: TRG_BIO_TAXON_ID
--
  CREATE OR REPLACE TRIGGER "BIOMART"."TRG_BIO_TAXON_ID" before
  INSERT ON "BIO_TAXONOMY" FOR EACH row BEGIN IF inserting THEN IF :NEW."BIO_TAXONOMY_ID" IS NULL THEN
  SELECT SEQ_BIO_DATA_ID.nextval INTO :NEW."BIO_TAXONOMY_ID" FROM dual;
END IF;
END IF;
END;

/
ALTER TRIGGER "BIOMART"."TRG_BIO_TAXON_ID" ENABLE;
 
