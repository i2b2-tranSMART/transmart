--
-- Name: observation_fact; Type: TABLE; Schema: i2b2demodata; Owner: -
--
CREATE TABLE observation_fact (
    encounter_num int NOT NULL,
    patient_num int NOT NULL,
    concept_cd character varying(250) NOT NULL,
    provider_id character varying(50) NOT NULL,
    start_date timestamp NOT NULL,
    modifier_cd character varying(100) default '@' NOT NULL,
    instance_num int default (1) NOT NULL,
    valtype_cd character varying(50),
    tval_char character varying(4000),
    nval_num decimal(18,5),
    valueflag_cd character varying(500),
    quantity_num decimal(18,5),
    units_cd character varying(500),
    end_date timestamp,
    location_cd character varying(100),
    observation_blob text,
    confidence_num decimal(18,5),
    update_date timestamp,
    download_date timestamp,
    import_date timestamp,
    sourcesystem_cd character varying(50),
    upload_id int,
    text_search_index serial,	-- postgres-only in i2b2
    sample_cd character varying(200)
);

--
-- Name: observation_fact_pk; Type: CONSTRAINT; Schema: i2b2demodata; Owner: -
--
ALTER TABLE ONLY observation_fact
    ADD CONSTRAINT observation_fact_pk PRIMARY KEY (patient_num, concept_cd, modifier_cd, start_date, encounter_num, instance_num, provider_id);

--
-- Name: OF_IDX_ClusteredConcept; Type: INDEX; Schema: i2b2demodata; Owner: -
--
CREATE INDEX OF_IDX_ClusteredConcept ON observation_fact USING btree (concept_cd);

--
-- Name: OF_IDX_ALLObservation_Fact; Type: INDEX; Schema: i2b2demodata; Owner: -
--
CREATE INDEX OF_IDX_ALLObservation_Fact ON observation_fact USING btree (patient_num, encounter_num, concept_cd, start_date, provider_id, modifier_cd,
	instance_num, valtype_cd, tval_char, nval_num, valueflag_cd, quantity_num, units_cd, end_date, location_cd, confidence_num);

--
-- Name: OF_IDX_Start_Date; Type: INDEX; Schema: i2b2demodata; Owner: -
--
CREATE INDEX fact_modifier_patient ON observation_fact USING btree (start_date, patient_num);

--
-- Name: OF_IDX_Start_Date; Type: INDEX; Schema: i2b2demodata; Owner: -
--
CREATE INDEX OF_IDX_Start_Date ON observation_fact USING btree (start_date, patient_num);

--
-- Name: OF_IDX_Modifier; Type: INDEX; Schema: i2b2demodata; Owner: -
--
CREATE INDEX OF_IDX_Modifier ON observation_fact USING btree (modifier_cd);

--
-- Name: OF_IDX_Encounter_Patient; Type: INDEX; Schema: i2b2demodata; Owner: -
--
CREATE INDEX OF_IDX_Encounter_Patient ON observation_fact USING btree (encounter_num, patient_num, instance_num);

--
-- Name: OF_IDX_UPLOADID; Type: INDEX; Schema: i2b2demodata; Owner: -
--
CREATE INDEX OF_IDX_UPLOADID ON observation_fact USING btree (upload_id);

--
-- Name: OF_IDX_SOURCESYSTEM_CD; Type: INDEX; Schema: i2b2demodata; Owner: -
--
CREATE INDEX OF_IDX_SOURCESYSTEM_CD ON observation_fact USING btree (sourcesystem_cd);

--
-- Name: OF_TEXT_SEARCH_UNIQUE; Type: INDEX; Schema: i2b2demodata; Owner: -
--
CREATE INDEX OF_TEXT_SEARCH_UNIQUE ON observation_fact USING btree (text_search_index);

--
-- Name: tf_trg_encounter_num(); Type: FUNCTION; Schema: i2b2demodata; Owner: -
--
CREATE FUNCTION tf_trg_encounter_num() RETURNS trigger
    LANGUAGE plpgsql
AS $$
begin
    if new.encounter_num is null then
	select nextval('i2b2demodata.seq_encounter_num') into new.encounter_num ;
    end if;
    return new;
end;
$$;

--
-- Name: trg_encounter_num; Type: TRIGGER; Schema: i2b2demodata; Owner: -
--
CREATE TRIGGER trg_encounter_num BEFORE INSERT ON observation_fact FOR EACH ROW EXECUTE PROCEDURE tf_trg_encounter_num();

--
-- Name: seq_encounter_num; Type: SEQUENCE; Schema: i2b2demodata; Owner: -
--
CREATE SEQUENCE seq_encounter_num
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

