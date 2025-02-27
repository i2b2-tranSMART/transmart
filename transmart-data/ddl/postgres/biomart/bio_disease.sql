--
-- Name: bio_disease; Type: TABLE; Schema: biomart; Owner: -
--
CREATE TABLE bio_disease (
    bio_disease_id int NOT NULL,
    disease character varying(510) NOT NULL,
    ccs_category character varying(510),
    icd10_code character varying(510),
    mesh_code character varying(510),
    icd9_code character varying(510),
    prefered_name character varying(510),
    etl_id_retired int,
    primary_source_cd character varying(30),
    etl_id character varying(50)
);

--
-- Name: diseasedim_pk; Type: CONSTRAINT; Schema: biomart; Owner: -
--
ALTER TABLE ONLY bio_disease
    ADD CONSTRAINT diseasedim_pk PRIMARY KEY (bio_disease_id);

--
-- Name: bio_disease_pk; Type: INDEX; Schema: biomart; Owner: -
--
CREATE UNIQUE INDEX bio_disease_pk ON bio_disease USING btree (bio_disease_id);

--
-- Name: tf_trg_bio_disease_id(); Type: FUNCTION; Schema: biomart; Owner: -
--
CREATE FUNCTION tf_trg_bio_disease_id() RETURNS trigger
    LANGUAGE plpgsql
AS $$
begin
    if new.bio_disease_id is null then
        select nextval('biomart.seq_bio_data_id') into new.bio_disease_id ;
    end if;
    return new;
end;
$$;

--
-- Name: trg_bio_disease_id; Type: TRIGGER; Schema: biomart; Owner: -
--
CREATE TRIGGER trg_bio_disease_id BEFORE INSERT ON bio_disease FOR EACH ROW EXECUTE PROCEDURE tf_trg_bio_disease_id();

