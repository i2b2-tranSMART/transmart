/*************************************************************************
 * tranSMART - translational medicine data mart
 *
 * Copyright 2008-2012 Janssen Research & Development, LLC.
 *
 * This product includes software developed at Janssen Research & Development, LLC.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software  * Foundation, either version 3 of the License, or (at your option) any later version, along with the following terms:
 * 1.	You may convey a work based on this program in accordance with section 5, provided that you retain the above notices.
 * 2.	You may convey verbatim copies of this program code as you receive it, in any medium, provided that you retain the above notices.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS    * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 ******************************************************************/
package org.transmart.searchapp

class SearchKeyword {
    Long bioDataId
    String dataCategory
    String dataSource
    String displayDataCategory
    String keyword
    Long ownerAuthUserId
    String uniqueId

    static hasMany = [terms: SearchKeywordTerm]

    static mapping = {
	table 'SEARCHAPP.SEARCH_KEYWORD'
        version false
	id generator: 'sequence', params: [sequence: 'SEARCHAPP.SEQ_SEARCH_DATA_ID'], column: 'SEARCH_KEYWORD_ID'

        dataSource column: 'SOURCE_CODE'
	terms column: 'SEARCH_KEYWORD_ID' // TODO BB
    }

    static constraints = {
	bioDataId nullable: true
	dataCategory nullable: true, maxSize: 400
	dataSource nullable: true, maxSize: 200
	displayDataCategory nullable: true, maxSize: 400
	keyword maxSize: 400
	ownerAuthUserId nullable: true
	uniqueId nullable: true, maxSize: 1000
    }

    int hashCode() {
        // handle special case for TEXT SearchKeywords
	id == -1 ? keyword.hashCode() : id
    }

    boolean equals(other) {
        // handle special case for TEXT SearchKeywords
	id == -1 ? keyword == other?.keyword : id == other?.id
    }
}
