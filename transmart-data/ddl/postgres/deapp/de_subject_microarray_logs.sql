--
-- Name: de_subject_microarray_logs; Type: TABLE; Schema: deapp; Owner: -
--
CREATE TABLE de_subject_microarray_logs (
    probeset character varying(50),
    raw_intensity double precision,
    pvalue double precision,
    refseq character varying(50),
    gene_symbol character varying(50),
    assay_id int,
    patient_id int,
    subject_id character varying(100),
    trial_name character varying(15),
    timepoint character varying(30),
    log_intensity double precision
);

