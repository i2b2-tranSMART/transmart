--
-- Name: czx_write_audit(numeric, character varying, character varying, character varying, numeric, numeric, character varying); Type: FUNCTION; Schema: tm_cz; Owner: -
--
CREATE FUNCTION czx_write_audit(jobid numeric, databasename character varying, procedurename character varying, stepdesc character varying, recordsmanipulated numeric, stepnumber numeric, stepstatus character varying) RETURNS numeric
    LANGUAGE plpgsql SECURITY DEFINER
AS $$
    /*************************************************************************
     * Copyright 2008-2012 Janssen Research & Development, LLC.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     ******************************************************************/

    declare

    lastTime timestamp;
    currTime timestamp;
    elapsedSecs	numeric;
    
begin

    select max(job_date)
      into lastTime
      from tm_cz.cz_job_audit
     where job_id = jobid;
    
    --	clock_timestamp() is the current system time
    
    select clock_timestamp() into currTime;

    elapsedSecs := coalesce(((date_part('day', currTime - lastTime) * 24 + 
			      Date_part('hour', currTime - lastTime)) * 60 +
			      date_part('minute', currTime - lastTime)) * 60 +
			      date_part('second', currTime - lastTime),0);

    begin
	insert into tm_cz.cz_job_audit
		    (job_id
		    ,database_name
 		    ,procedure_name
 		    ,step_desc
		    ,records_manipulated
		    ,step_number
		    ,step_status
		    ,job_date
		    ,time_elapsed_secs
		    )
	values(
 	    jobId
	    ,databaseName
	    ,procedureName
	    ,stepDesc
	    ,recordsManipulated
	    ,stepNumber
	    ,stepStatus
	    ,currTime
	    ,elapsedSecs);
    exception 
	when others then
	--raise notice 'proc failed state=%  errm=%', SQLSTATE, SQLERRM;
	    perform tm_cz.czx_write_error(jobid,'0'::character varying(10),SQLERRM::character varying(1000),SQLSTATE::character varying(1000),SQLERRM::character varying(1000));
	    return -16;
    end;
    
    raise notice 'step: %', stepDesc;
    
    return 1;

end;
$$;

