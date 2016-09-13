--
-- check for null values
--

set search_path = tm_cz, pg_catalog;

DROP FUNCTION IF EXISTS tm_cz.i2b2_process_rnaseq_data(character varying, character varying, character varying, character varying, character varying, numeric, numeric);

\i ../../../ddl/postgres/tm_cz/functions/i2b2_process_rnaseq_data.sql

ALTER FUNCTION i2b2_process_rnaseq_data(character varying, character varying, character varying, character varying, character varying, numeric, numeric) SET search_path TO tm_cz, tm_lz, tm_wz, deapp, i2b2demodata, pg_temp;



