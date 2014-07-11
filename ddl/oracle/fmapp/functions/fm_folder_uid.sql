--
-- Type: FUNCTION; Owner: FMAPP; Name: FM_FOLDER_UID
--
  CREATE OR REPLACE FUNCTION "FMAPP"."FM_FOLDER_UID" (
  FOLDER_NAME VARCHAR2
) RETURN VARCHAR2 AS
BEGIN
  -- $Id$
  -- Creates uid for bio_concept_code.

  RETURN 'FOL:' || FOLDER_NAME;
END FM_FOLDER_UID;
/
 
