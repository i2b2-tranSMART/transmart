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
 * (at your option) any later version, along with the following terms:
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
  

package org.transmartproject.pipeline.transmart

import groovy.sql.Sql;
import groovy.util.logging.Slf4j

@Slf4j('logger')
class BioAssayFeatureGroup {

    Sql biomart
    String testsDataTable

    void loadBioAssayFeatureGroup(){
	logger.info "Start loading new probes into BIO_ASSAY_FEATURE_GROUP ..."
	String qry = """ insert into bio_assay_feature_group(feature_group_name, feature_group_type)
                         select probeset, 'PROBESET' from ${testsDataTable} 
                         minus
			 select to_char(feature_group_name), to_char(feature_group_type) 
			 from bio_assay_feature_group """

	biomart.execute(qry)
	logger.info "End loading new probes into BIO_ASSAY_FEATURE_GROUP ..."
    }

    void setTestsDataTable(String testsDataTable){
	this.testsDataTable = testsDataTable
    }

    void setBiomart(Sql biomart){
	this.biomart = biomart
    }
}
