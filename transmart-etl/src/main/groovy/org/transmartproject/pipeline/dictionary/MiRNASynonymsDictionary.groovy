/*************************************************************************
 * tranSMART - translational medicine data mart
 * 
 * Copyright 2012-2014 The TranSMART Foundation
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
  
package org.transmartproject.pipeline.dictionary

import groovy.sql.Sql
import groovy.util.logging.Slf4j
import org.apache.log4j.PropertyConfigurator
import org.transmartproject.pipeline.util.Util


/** Extracts synonyms data from tsv file and
 *  adds it to BIOMART.BIO_DATA_EXT_CODE.
 */

@Slf4j('logger')
class MiRNASynonymsDictionary {

    Sql sqlBiomart

    static main(args) {
        if (!args) {
            println "MiRNASynonymsDictionary <synonyms.tsv> [<organism. e.g. HOMO SAPIENS>]"
            System.exit(1)
        }

        def synonymsFileLocation = args[0]
        def organism = args.length > 1 ? args[1] : 'HOMO SAPIENS'
        PropertyConfigurator.configure("conf/log4j.properties");
        File synonymsFile = new File(synonymsFileLocation)
        MiRNASynonymsDictionary dict = new MiRNASynonymsDictionary()
        dict.loadData(synonymsFile, organism)

        print new Date()
        println " MiRNA synonyms load completed successfully"
    }

    void loadData(File synonymsFile, String organism) {
        if (!synonymsFile.exists()) {
            logger.error("File is not found: ${synonymsFile.getAbsolutePath()}")
            return
        }

        def synonymsMap = parseSynonyms(synonymsFile)
        logger.info("Start loading property file ...")
        Properties props = Util.loadConfiguration('')
        sqlBiomart = Util.createSqlFromPropertyFile(props, "biomart")
        try {
            def bioNameIdMap = getBioMarkerNameIdMap('MIRNA', organism)
            def existingSynonymsMap = getExistingSynonymsMap('MIRNA', organism)
            def noInDbBioNamesSet = synonymsMap.keySet() - bioNameIdMap.keySet()
            if (noInDbBioNamesSet) {
                logger.warn("Db missing such symbols: $noInDbBioNamesSet")
            }
            bioNameIdMap.each {nameIdEntry ->
                def bioName = nameIdEntry.key
                def synonyms = synonymsMap[bioName]
                def existingSynonyms = existingSynonymsMap[bioName]
                def newSysnonyms = synonyms - existingSynonyms
                if (newSysnonyms) {
                    def bioId = nameIdEntry.value

                    newSysnonyms.each {synonym ->
                        insertSynonym(bioId: bioId, bioName: bioName, synonym: synonym)
                    }
                }
            }
        } finally {
            sqlBiomart.close()
        }
    }

    Map<String, Set<String>> parseSynonyms(File synonymsFile) {
        if (!synonymsFile.exists()) {
            logger.error("File is not found: ${synonymsFile.getAbsolutePath()}")
            return [:]
        }

        Map<String, Set<String>> synonymsMap = [:].withDefault { [] as Set }
        int appSymbIndx, synIndx
        synonymsFile.eachLine {line, num ->
            def values = line.split(/\t/)*.replaceAll(/(^"|"$)/, '')
            if (num == 1) {
                def header = values*.toUpperCase()
                appSymbIndx = header.indexOf('APPROVED SYMBOL')
                assert appSymbIndx > -1, "Can't find 'APPROVED SYMBOL' column. Make sure file format is correct."

                synIndx = header.indexOf('SYNONYMS')
                assert synIndx > -1, "Can't find 'Synonyms' column. Make sure file format is correct."
            } else {
                def mirnaSymbol = values[appSymbIndx].toUpperCase()
                def synonyms = values[synIndx].trim().split(/\s*,\s*/)
                synonymsMap[mirnaSymbol] += synonyms as Set
            }
        }
        synonymsMap
    }

    protected void insertSynonym(entry) {
        logger.info "Add $entry.synonym synonym for $entry.bioName"

        sqlBiomart.executeInsert("""
              insert into BIO_DATA_EXT_CODE(BIO_DATA_ID, CODE, CODE_TYPE, BIO_DATA_TYPE, CODE_SOURCE)
              values(:bio_data_id, :code, :code_type, :bio_data_type, :code_source)
            """,
                [bio_data_id: entry.bioId,
                        code: entry.synonym,
                        code_type: 'SYNONYM',
                        bio_data_type: 'BIO_MARKER.MIRNA',
                        code_source: 'Alias'])
    }

    protected Map<String, Long> getBioMarkerNameIdMap(String markerType, String organism) {
        Map<String, Long> nameIdMap = [:]
        sqlBiomart.eachRow(
          """
          select BIO_MARKER_NAME, BIO_MARKER_ID
          from BIO_MARKER
          where BIO_MARKER_NAME is not null and ORGANISM = :organism and BIO_MARKER_TYPE = :type
          """,
          [organism: organism, type: markerType],
            {row ->
                if (nameIdMap[row.BIO_MARKER_NAME.toUpperCase()]) {
                    logger.warn("Bio marker with the same name detected: $row.BIO_MARKER_NAME")
                }
                nameIdMap[row.BIO_MARKER_NAME.toUpperCase()] = row.BIO_MARKER_ID
            })
        nameIdMap
    }

    protected Map<String, Set<String>> getExistingSynonymsMap(String markerType, String organism) {
        Map<String, Set<String>> resultsMap = [:].withDefault { [] as Set }
        sqlBiomart.eachRow(
                """
          select bm.BIO_MARKER_NAME, bdec.CODE
          from BIO_MARKER bm
          inner join BIO_DATA_EXT_CODE bdec on bdec.BIO_DATA_ID = bm.BIO_MARKER_ID
          where BIO_MARKER_NAME is not null and ORGANISM = :organism and BIO_MARKER_TYPE = :type
          """,
                [organism: organism, type: markerType],
                {row ->
                    resultsMap[row.BIO_MARKER_NAME.toUpperCase()] << row.CODE
                })
        resultsMap
    }
}
