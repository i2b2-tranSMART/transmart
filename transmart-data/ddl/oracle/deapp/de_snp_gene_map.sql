--
-- Type: TABLE; Owner: DEAPP; Name: DE_SNP_GENE_MAP
--
 CREATE TABLE "DEAPP"."DE_SNP_GENE_MAP"
  (	"SNP_ID" NUMBER(22,0),
"SNP_NAME" VARCHAR2(255 BYTE),
"ENTREZ_GENE_ID" NUMBER,
"ENTREZ_GENE_NAME" VARCHAR2(255 BYTE)
  ) SEGMENT CREATION IMMEDIATE
 TABLESPACE "TRANSMART" ;
--
-- Type: INDEX; Owner: DEAPP; Name: SNP_NAME_IDX1
--
CREATE INDEX "DEAPP"."SNP_NAME_IDX1" ON "DEAPP"."DE_SNP_GENE_MAP" ("SNP_NAME")
TABLESPACE "INDX" ;
--
-- Type: INDEX; Owner: DEAPP; Name: ENTREZ_IDX1
--
CREATE INDEX "DEAPP"."ENTREZ_IDX1" ON "DEAPP"."DE_SNP_GENE_MAP" ("ENTREZ_GENE_ID")
TABLESPACE "INDX" ;
--
-- Type: REF_CONSTRAINT; Owner: DEAPP; Name: FK_SNP_GENE_MAP_SNP_ID
--
ALTER TABLE "DEAPP"."DE_SNP_GENE_MAP" ADD CONSTRAINT "FK_SNP_GENE_MAP_SNP_ID" FOREIGN KEY ("SNP_ID")
 REFERENCES "DEAPP"."DE_SNP_INFO" ("SNP_INFO_ID") ENABLE;
