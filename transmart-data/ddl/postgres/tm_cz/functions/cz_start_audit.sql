--
-- Name: cz_start_audit(character varying, character varying); Type: FUNCTION; Schema: tm_cz; Owner: -
--
CREATE FUNCTION cz_start_audit(jobname character varying, databasename character varying) RETURNS numeric
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

    jobId	numeric;

begin
    begin
	insert into tm_cz.cz_job_master
		    (start_date 
		    ,active
		    ,database_name
		    ,job_name
		    ,job_status) 
	values(
	    current_timestamp
	    ,'Y' 
	    ,databaseName
	    ,jobName
	    ,'Running')
	    returning job_id INTO jobId;
    end;
    
    return jobId;
    
exception 
    when others then
	perform tm_cz.cz_write_error(jobId,SQLERRML,SQLSTATE,SQLERRM,null,null);
	return -16;

end;

$$;

--
-- Name: cz_start_audit(character varying, character varying, bigint); Type: FUNCTION; Schema: tm_cz; Owner: -
--
CREATE FUNCTION cz_start_audit(jobname character varying, databasename character varying, jobid bigint) RETURNS numeric
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

begin
    begin
	insert into tm_cz.cz_job_master
		    (start_date 
		    ,active
		    ,database_name
		    ,job_name
		    ,job_status) 
	values(
	    current_timestamp
	    ,'Y'
	    ,databaseName
	    ,jobName
	    ,'Running')
	    returning job_id into jobid;
    end;
    
    return jobid;
    
exception 
    when others then
	perform tm_cz.cz_write_error(jobId,SQLERRML,SQLSTATE,SQLERRM,null,null);
	return -16;

end;

$$;

