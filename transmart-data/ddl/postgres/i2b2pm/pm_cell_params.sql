--
-- Name: pm_cell_params; Type: TABLE; Schema: i2b2pm; Owner: -
--
CREATE TABLE pm_cell_params (
    id int,
    datatype_cd character varying(50),
    cell_id character varying(50) NOT NULL,
    project_path character varying(255) NOT NULL,
    param_name_cd character varying(50) NOT NULL,
    value text,
    can_override int,
    change_date timestamp,
    entry_date timestamp,
    changeby_char character varying(50),
    status_cd character varying(50)
);
--
-- Name: tf_trg_pm_cell_params_inc(); Type: FUNCTION; Schema: i2b2pm; Owner: -
--
CREATE FUNCTION tf_trg_pm_cell_params_inc() RETURNS trigger
    LANGUAGE plpgsql
AS $$
begin
    if new.id is null then
	select nextval('i2b2pm.pm_params') into new.id ;
    end if;
    return new;
end;
$$;

--
-- Name: trg_pm_cell_params_inc; Type: TRIGGER; Schema: i2b2pm; Owner: -
--
CREATE TRIGGER trg_pm_cell_params_inc BEFORE INSERT ON pm_cell_params FOR EACH ROW EXECUTE PROCEDURE tf_trg_pm_cell_params_inc();
--
-- Name: pm_cell_params_pk; Type: CONSTRAINT; Schema: i2b2pm; Owner: -
--
ALTER TABLE ONLY pm_cell_params
    ADD CONSTRAINT pm_cell_params_pk PRIMARY KEY (id);
