--
-- Name: bio_subject; Type: TABLE; Schema: biomart; Owner: -
--
CREATE TABLE bio_subject (
    bio_subject_id int NOT NULL,
    site_subject_id int,
    source character varying(200),
    source_code character varying(200),
    status character varying(200),
    organism character varying(200),
    bio_subject_type character varying(200) NOT NULL
);

--
-- Name: bio_subject_pk; Type: CONSTRAINT; Schema: biomart; Owner: -
--
ALTER TABLE ONLY bio_subject
    ADD CONSTRAINT bio_subject_pk PRIMARY KEY (bio_subject_id);

--
-- Name: tf_trg_bio_subject_id(); Type: FUNCTION; Schema: biomart; Owner: -
--
CREATE FUNCTION tf_trg_bio_subject_id() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
begin
    if new.bio_subject_id is null then
        select nextval('biomart.seq_bio_data_id') into new.bio_subject_id ;
    end if;
    return new;
end;
$$;

--
-- Name: trg_bio_subject_id; Type: TRIGGER; Schema: biomart; Owner: -
--
CREATE TRIGGER trg_bio_subject_id BEFORE INSERT ON bio_subject FOR EACH ROW EXECUTE PROCEDURE tf_trg_bio_subject_id();

