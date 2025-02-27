--
-- Name: de_rc_snp_info_seq; Type: SEQUENCE; Schema: deapp; Owner: -
--
CREATE SEQUENCE de_rc_snp_info_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: de_rc_snp_info; Type: TABLE; Schema: deapp; Owner: -
--
CREATE TABLE de_rc_snp_info (
    snp_info_id int DEFAULT nextval('de_rc_snp_info_seq'::regclass) NOT NULL,
    rs_id character varying(50),
    chrom character varying(4),
    pos int,
    ref character varying(1000),
    alt character varying(1000),
    gene_info character varying(1000),
    variation_class character varying(24),
    strand character varying(1),
    clinsig character varying(100),
    disease character varying(500),
    gmaf character varying(10),
    gene_biotype character varying(100),
    impact character varying(50),
    transcript_id character varying(100),
    functional_class character varying(100),
    effect character varying(100),
    exon_id character varying(100),
    amino_acid_change character varying(100),
    codon_change character varying(1000),
    hg_version character varying(10),
    gene_name character varying(50),
    entrez_id character varying(50),
    recombination_rate decimal(18,6),
    recombination_map decimal(18,6),
    regulome_score character varying(10),
    exon_intron character varying(50)
);

--
-- Name: de_rc_snp_info_chrom_pos_idx; Type: INDEX; Schema: deapp; Owner: -
--
CREATE INDEX de_rc_snp_info_chrom_pos_idx ON de_rc_snp_info USING btree (chrom, pos);

--
-- Name: de_rc_snp_info_entrez_id_idx; Type: INDEX; Schema: deapp; Owner: -
--
CREATE INDEX de_rc_snp_info_entrez_id_idx ON de_rc_snp_info USING btree (entrez_id);

--
-- Name: de_rc_snp_info_rs_id_idx; Type: INDEX; Schema: deapp; Owner: -
--
CREATE INDEX de_rc_snp_info_rs_id_idx ON de_rc_snp_info USING btree (rs_id);

