--
-- Name: biomarker_gene_uid(character varying); Type: FUNCTION; Schema: tm_cz; Owner: -
--
CREATE FUNCTION biomarker_gene_uid(gene_id character varying) RETURNS character varying
    LANGUAGE plpgsql
AS $$
begin
    -- Creates uid for biomarker_gene.

    return 'GENE:' || coalesce(gene_ID, 'ERROR');
end;

$$;

