--
-- Name: protein_annotation_id; Type: SEQUENCE; Schema: deapp; Owner: -
--
CREATE SEQUENCE protein_annotation_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: de_protein_annotation; Type: TABLE; Schema: deapp; Owner: -
--
CREATE TABLE de_protein_annotation (
    id int DEFAULT nextval('protein_annotation_id'::regclass) NOT NULL,
    gpl_id character varying(50) NOT NULL,
    peptide character varying(200) NOT NULL,
    uniprot_id character varying(200),
    biomarker_id character varying(200),
    organism character varying(200),
    uniprot_name character varying(200),
    chromosome character varying(5),
    start_bp int,
    end_bp int
);

--
-- Name: de_protein_annotation_pkey; Type: CONSTRAINT; Schema: deapp; Owner: -
--
ALTER TABLE ONLY de_protein_annotation
    ADD CONSTRAINT de_protein_annotation_pkey PRIMARY KEY (id);

