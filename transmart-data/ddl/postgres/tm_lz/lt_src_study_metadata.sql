--
-- Name: lt_src_study_metadata; Type: TABLE; Schema: tm_lz; Owner: -
--
CREATE TABLE lt_src_study_metadata (
    study_id character varying(100),
    title character varying(1000),
    description character varying(4000),
    design character varying(2000),
    start_date timestamp,
    completion_date timestamp,
    primary_investigator character varying(400),
    contact_field character varying(400),
    status character varying(100),
    overall_design character varying(2000),
    institution character varying(100),
    country character varying(50),
    biomarker_type character varying(255),
    target character varying(255),
    access_type character varying(100),
    study_owner character varying(510),
    study_phase character varying(100),
    blinding_procedure character varying(1000),
    studytype character varying(510),
    duration_of_study_weeks character varying(200),
    number_of_patients character varying(200),
    number_of_sites character varying(200),
    route_of_administration character varying(510),
    dosing_regimen character varying(3500),
    group_assignment character varying(510),
    type_of_control character varying(510),
    primary_end_points character varying(2000),
    secondary_end_points character varying(3500),
    inclusion_criteria character varying(4000),
    exclusion_criteria character varying(4000),
    subjects character varying(2000),
    gender_restriction_mfb character varying(510),
    min_age character varying(100),
    max_age character varying(100),
    secondary_ids character varying(510),
    development_partner character varying(100),
    geo_platform character varying(100),
    main_findings character varying(2000),
    search_area character varying(100),
    compound character varying(1000),
    disease character varying(1000),
    pubmed_ids character varying(4000),
    organism character varying(200),
    study_title character varying(500),
    study_date timestamp,
    study_institution character varying(500),
    study_country character varying(500),
    study_related_publication character varying(500),
    study_description character varying(2000),
    study_access_type character varying(500),
    study_objective character varying(2000),
    study_biomarker_type character varying(500),
    study_compound character varying(500),
    study_design_factors character varying(2000),
    study_nbr_subjects character varying(20),
    study_organism character varying(500)
);

