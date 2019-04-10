--
-- Type: TABLE; Owner: TM_WZ; Name: WT_SUBJECT_RNASEQ_CALCS
--
 CREATE TABLE "TM_WZ"."WT_SUBJECT_RNASEQ_CALCS"
     (	--WL-- "TRIAL_NAME" VARCHAR2(200),
       "REGION_ID NUMBER(38,0),
       "MEAN_READCOUNT" NUMBER,
       "MEDIAN_READCOUNT" NUMBER,
       "STDDEV_READCOUNT" NUMBER
  ) SEGMENT CREATION IMMEDIATE
 TABLESPACE "TRANSMART" ;
--
-- Type: INDEX; Owner: TM_WZ; Name: WT_SUBJECT_RNASEQ_CALCS_I1
--
CREATE INDEX "TM_WZ"."WT_SUBJECT_RNASEQ_CALCS_I1" ON "TM_WZ"."WT_SUBJECT_RNASEQ_CALCS" ("REGION_ID")
TABLESPACE "INDX" ;
