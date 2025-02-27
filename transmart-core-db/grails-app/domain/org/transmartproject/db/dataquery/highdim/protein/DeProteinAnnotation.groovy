/*
 * Copyright © 2013-2014 The Hyve B.V.
 *
 * This file is part of transmart-core-db.
 *
 * Transmart-core-db is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * transmart-core-db.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.transmartproject.db.dataquery.highdim.protein

import org.transmartproject.db.dataquery.highdim.DeGplInfo

class DeProteinAnnotation {

    String   chromosome
    Long     endBp
    String   gplId
    String   peptide
    Long     startBp
    String   uniprotId
    String   uniprotName

    // irrelevant
    //String biomarkerId
    //String organism

    static hasMany = [dataRows: DeSubjectProteinData]

    static belongsTo = [platform: DeGplInfo]

    static mappedBy = [dataRows: 'annotation']

    static mapping = {
        table    schema:    'deapp'
        id       generator: 'assigned'
        version  false

        gplId    insertable: false, updateable: false
        platform column:    'gpl_id'
    }

    static constraints = {
        chromosome  nullable: true
        endBp       nullable: true
        peptide     maxSize:  800
        startBp     nullable: true
        uniprotId   nullable: true, maxSize: 200
        uniprotName nullable: true, maxSize: 200

        //biomarkerId nullable: true, maxSize: 400
        //organism    nullable: true, maxSize: 800
    }
}
