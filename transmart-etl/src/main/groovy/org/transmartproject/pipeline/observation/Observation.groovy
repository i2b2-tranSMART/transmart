/*************************************************************************
 * tranSMART - translational medicine data mart
 * 
 * Copyright 2008-2012 Janssen Research & Development, LLC.
 * 
 * This product includes software developed at Janssen Research & Development, LLC.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version, along with the following terms:
 *
 * 1.	You may convey a work based on this program in accordance with section 5,
 *      provided that you retain the above notices.
 * 2.	You may convey verbatim copies of this program code as you receive it,
 *      in any medium, provided that you retain the above notices.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************/
  

package org.transmartproject.pipeline.observation

import java.util.Properties;

import org.transmartproject.pipeline.util.Util

import groovy.sql.Sql
import groovy.util.logging.Slf4j
import org.transmartproject.pipeline.transmart.SearchKeyword
import org.transmartproject.pipeline.transmart.SearchKeywordTerm

import org.transmartproject.pipeline.transmart.BioDataExtCode
import org.transmartproject.pipeline.transmart.BioObservation

@Slf4j('logger')
class Observation {

    private static SearchKeyword searchKeyword
    private static SearchKeywordTerm searchKeywordTerm

    static main(args) {

//	PropertyConfigurator.configure("conf/log4j.properties");

	logger.info("Start loading property file loader.properties ...")
	Properties props = Util.loadConfiguration("conf/Observation.properties");

	Sql biomart = Util.createSqlFromPropertyFile(props, "biomart")
	Sql searchapp = Util.createSqlFromPropertyFile(props, "searchapp")

        searchKeyword = new SearchKeyword()
        searchKeyword.setSearchapp(searchapp)

        searchKeywordTerm = new SearchKeywordTerm()
        searchKeywordTerm.setSearchapp(searchapp)

	Observation obs = new Observation()
	Map [] obsMaps = obs.readObservationFile(props)

	BioObservation bioObs = new BioObservation()
	bioObs.setBiomart(biomart)
	bioObs.loadBioObservation(obsMaps[0])
		
	// obs_code -> bio_observation_id
	Map codeMap = bioObs.getBioObservationId()

	// if observation codes are from MeSH, then their synonyms will be looked from
	// MeSH Entry table
	if(props.get("default_obs_code_source").toString().toLowerCase().equals("mesh")){
	    obs.loadBioDataExtCode(biomart, props)
	} else{
	    obs.loadBioDataExtCode(biomart, props, obsMaps[1], codeMap)
	}

	obs.loadSearchKeyword(biomart, props)
//	obs.loadSearchKeywordTerm(searchapp, props)
    }

    Map [] readObservationFile(Properties props){

	File obsf = new File(props.get("observation_source_file"))
	boolean headerLine

	if(props.get("skip_header_line").toString().toLowerCase().equals("yes")){
	    headerLine = true
	} else{
	    headerLine = false
	}

	Map obs = [:]
	Map synonym = [:]

	if(obsf.size() > 0){
	    obsf.eachLine {

		if(headerLine){
		    headerLine = false
		} else{
		    String [] str = it.replace('"', '').split("\t")

		    /* Columns of the observation file should be tab-delimited and in the following order:
		     *  
		     *  1. observation name, 
		     *  2. observation code, 
		     *  3. observation description, 
		     *  4. observation type, 
		     *  5. observation code source, such as MeSH or ICD 
		     */
		    if(str.size() > 4){
			if(str[4].size() > 0) {
			    obs[str[0]] = str[1] + "\t" + str[2] + "\t" + str[3] + "\t" + str[4]

			    if(str.size() > 5 && (str[5].size() > 0)){
				//println str[1] + "\t" + str[3] + "\t" + str[4] + "\t" + str[5]
				synonym[str[1]] = str[3] + "\t" + str[4] + "\t" + str[5]
			    }
			}
			else {
			    obs[str[0]] = str[1] + "\t" + str[2] + "\t" + str[3] + "\t" + props.get("default_obs_code_source")

			    if(str.size() > 5 && (str[5].size() > 0)){
				//println str[1] + "\t" + str[3] + "\t" + props.get("default_obs_code_source") + "\t" + str[5]
				synonym[str[1]] = str[3] + "\t" + props.get("default_obs_code_source") + "\t" + str[5]
			    }
			}

		    } else{
			obs[str[0]] = str[1] + "\t" + str[2] + "\t" + str[3] + "\t" + props.get("default_obs_code_source")
		    }
		}
	    }
	}else{
	    logger.error("The file " + obsf.toString() + " is empty or not exist ...")
	}

	return [obs, synonym]
    }

    void loadBioDataExtCode(Sql biomart, Properties props, Map synonym, Map codeMap){
		
	if(props.get("skip_bio_data_ext_code").toString().toLowerCase().equals("yes")){
	    logger.info("Skip loading Observation's synonyms into BIO_DATA_EXT_CODE ...")
	}else{
	    logger.info("Start loading Observation's synonyms into BIO_DATA_EXT_CODE ...")

	    Map obsExtMap = [:]
	    String [] str 
	    synonym.each{ k, v ->
		str = v.split("\t")
		obsExtMap[str[2]] = "BIO_OBSERVATION\t" + codeMap[k]
	    }

	    BioDataExtCode bdec = new  BioDataExtCode()
	    bdec.setBiomart(biomart)
	    bdec.loadBioDataExtCode(obsExtMap)

	    logger.info("End loading Observation's synonyms into BIO_DATA_EXT_CODE ...")
	}
    }

    void loadBioDataExtCode(Sql biomart, Properties props){

        String qry
        String MeSHTable = props.get("mesh_heading_table")
        String MeSHSynonymTable = props.get("mesh_synonym_table")
        Boolean isPostgres = Util.isPostgres()

        if(props.get("skip_bio_data_ext_code").toString().toLowerCase().equals("yes")){
            logger.info("Skip loading Observation's MeSH synonyms into BIO_DATA_EXT_CODE ...")
        }else{
            logger.info("Start loading Observation's MeSH synonyms into BIO_DATA_EXT_CODE ...")
            logger.info("MeSHTable ${MeSHTable} MeSHSynonymTable ${MeSHSynonymTable}")

            if(isPostgres){
                qry = """ insert into bio_data_ext_code(bio_data_id, code, code_source, code_type, bio_data_type)
				 select o.bio_observation_id, s.entry, 'Alias', 'SYNONYM', 'BIO_OBSERVATION'
				 from bio_observation o, $MeSHTable m, $MeSHSynonymTable s
				 where o.obs_code = m.ui and m.mh=s.mh
				 except
				 select bio_data_id, code, 'Alias', 'SYNONYM', 'BIO_OBSERVATION'
				 from bio_data_ext_code"""
            } else {
                qry = """ insert into bio_data_ext_code(bio_data_id, code, code_source, code_type, bio_data_type)
				 select o.bio_observation_id, s.entry, 'Alias', 'SYNONYM', 'BIO_OBSERVATION'
				 from bio_observation o, $MeSHTable m, $MeSHSynonymTable s
				 where to_char(o.obs_code) = m.ui and m.mh=s.mh
				 minus
				 select bio_data_id, code, 'Alias', 'SYNONYM', 'BIO_OBSERVATION'
				 from bio_data_ext_code"""
            }

            biomart.execute(qry)

            logger.info("End loading Observation's MeSH synonyms into BIO_DATA_EXT_CODE ...")
        }
    }

    void loadSearchKeyword(Sql biomart, Properties props){

        Boolean isPostgres = Util.isPostgres()
        String qry
        String qrysyn

        if(props.get("skip_search_keyword").toString().toLowerCase().equals("yes")){
            logger.info("Skip loading Observation data into SEARCH_KEYWORD ...")
        }else{
            logger.info("Start loading Observation data into SEARCH_KEYWORD ...")

            String qryold = """ insert into SEARCH_KEYWORD (KEYWORD, BIO_DATA_ID, UNIQUE_ID, DATA_CATEGORY, DISPLAY_DATA_CATEGORY)
					select obs_name, bio_observation_id, 'OBS:'||obs_code, to_nchar('OBSERVATION'), to_nchar('Observation')
					from biomart.bio_observation 
					where obs_code not in 
                                            (select replace(UNIQUE_ID, 'OBS:', '')
                                             from search_keyword
                                             where DATA_CATEGORY='OBSERVATION')
                          """
            if(isPostgres) {
                qry = """ select distinct obs_name, bio_observation_id, obs_code, obs_code_source
			  from biomart.bio_observation
                      """
                //bio_observation_id
                qrysyn = """ select code from biomart.bio_data_ext_code
                             where code_source = 'Alias' and code_type ='SYNONYM' and bio_data_type = 'BIO_OBSERVATION'
                                 and bio_data_id = ?
                         """
            } else {
                qry = """ select distinct bio_observation_id, obs_name, obs_code, obs_code_source
			  from biomart.bio_observation""" 
                qrysyn = """ select code from biomart.bio_data_ext_code
                             where code_source = 'Alias' and code_type ='SYNONYM' and bio_data_type = 'BIO_OBSERVATION'
                                 and bio_data_id = ?
                         """
            }
            biomart.eachRow(qry)
            {
                long observationId = it.bio_observation_id
                // Check if it exists with OBS: prefix and insert into SEARCH_KEYWORD if not 
                searchKeyword.insertSearchKeyword(it.obs_name, observationId,
                                                  'OBS:'+it.obs_code,
                                                  it.obs_code_source, 'OBSERVATION', 'Observation')
                // Determine the id of the keyword that was just inserted
                long searchKeywordID = searchKeyword.getSearchKeywordId(it.obs_name, 'OBSERVATION')
                // Insert into SEARCH_KEYWORD_TERM
                // check if exists and inserts if not:
                if(searchKeywordID){
                    searchKeywordTerm.insertSearchKeywordTerm(it.obs_name, searchKeywordID, 1)
                    biomart.eachRow(qrysyn,[it.bio_observation_id]) 
                    {
                        searchKeywordTerm.insertSearchKeywordTerm(it.code, searchKeywordID, 2)
                    }
                }

            }

            logger.info("End loading Observation data into SEARCH_KEYWORD ...")
        }
    }

    void loadSearchKeywordTerm(Sql searchapp, Properties props){

	if(props.get("skip_search_keyword_term").toString().toLowerCase().equals("yes")){
	    logger.info("Skip loading Observation data into SEARCH_KEYWORD_TERM ...")
	}else{
	    logger.info("Start loading Observation data into SEARCH_KEYWORD_TERM ...")

	    // Observation name
	    String qry = """ insert into search_keyword_term (KEYWORD_TERM, SEARCH_KEYWORD_ID, RANK,TERM_LENGTH)
				 select to_char(upper(keyword)), search_keyword_id, 1, length(keyword)
				 from search_keyword where DATA_CATEGORY='OBSERVATION'
				 minus
				 select KEYWORD_TERM, SEARCH_KEYWORD_ID, RANK,TERM_LENGTH
                                 from searchapp.search_keyword_term where rank=1
			 """
	    searchapp.execute(qry)

	    // Observation Synonyms from MeSH
	    String qrys = """ insert into search_keyword_term (KEYWORD_TERM, SEARCH_KEYWORD_ID, RANK,TERM_LENGTH)
				  select upper(e.code), s.search_keyword_id, 2, length(s.keyword)
				  from search_keyword s, biomart.bio_data_ext_code e, biomart.bio_observation d
				  where s.bio_data_id=e.bio_data_id and e.bio_data_id=d.bio_observation_id
				  minus
				  select keyword_term, search_keyword_id, rank, term_length
				  from searchapp.search_keyword_term
				  where rank=2
			  """
	    searchapp.execute(qrys)

	    logger.info "End loading Observation data into SEARCH_KEYWORD_TERM ... "
	}
    }
}
