--
-- Name: de_pathway_gene; Type: TABLE; Schema: deapp; Owner: -
--
CREATE TABLE de_pathway_gene (
    id int NOT NULL,
    pathway_id int,
    gene_symbol character varying(200),
    gene_id character varying(200)
);

--
-- Name: de_pathway_gene_pkey; Type: CONSTRAINT; Schema: deapp; Owner: -
--
ALTER TABLE ONLY de_pathway_gene
    ADD CONSTRAINT de_pathway_gene_pkey PRIMARY KEY (id);

--
-- Name: tf_trg_de_pathway_gene_id(); Type: FUNCTION; Schema: deapp; Owner: -
--
CREATE FUNCTION tf_trg_de_pathway_gene_id() RETURNS trigger
    LANGUAGE plpgsql
AS $$
begin
    if new.id is null then
	select nextval('deapp.seq_data_id') into new.id ;
    end if;
    return new;
end;
$$;

--
-- Name: trg_de_pathway_gene_id; Type: TRIGGER; Schema: deapp; Owner: -
--
CREATE TRIGGER trg_de_pathway_gene_id BEFORE INSERT ON de_pathway_gene FOR EACH ROW EXECUTE PROCEDURE tf_trg_de_pathway_gene_id();

--
-- Name: seq_data_id; Type: SEQUENCE; Schema: deapp; Owner: -
--
CREATE SEQUENCE seq_data_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 20;

