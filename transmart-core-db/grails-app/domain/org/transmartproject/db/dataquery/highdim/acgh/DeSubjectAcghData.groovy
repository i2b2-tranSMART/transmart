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

package org.transmartproject.db.dataquery.highdim.acgh

import groovy.transform.EqualsAndHashCode
import org.transmartproject.core.dataquery.highdim.acgh.AcghValues
import org.transmartproject.core.dataquery.highdim.acgh.CopyNumberState
import org.transmartproject.db.dataquery.highdim.DeSubjectSampleMapping
import org.transmartproject.db.dataquery.highdim.chromoregion.DeChromosomalRegion
import org.transmartproject.db.i2b2data.PatientDimension

@EqualsAndHashCode(includes = [ 'assay', 'region' ])
class DeSubjectAcghData implements AcghValues, Serializable {

    Double chipCopyNumberValue
    Short  flag
    Double probabilityOfAmplification
    Double probabilityOfGain
    Double probabilityOfLoss
    Double probabilityOfNormal
    Double segmentCopyNumberValue
    String trialName

    DeChromosomalRegion jRegion // see comment in mapping

    /* unused; should be the same as assay.patient */
    PatientDimension patient

    static belongsTo = [region: DeChromosomalRegion,
			assay:  DeSubjectSampleMapping,
			patient: PatientDimension]

    static transients = ['copyNumberState']

    static mapping = {
        table   schema:    'deapp'
        id      composite: [ 'assay', 'region' ]
        version false
        sort    assay:  'asc'

        chipCopyNumberValue        column: 'chip'
        probabilityOfAmplification column: 'probamp'
        probabilityOfGain          column: 'probgain'
        probabilityOfLoss          column: 'probloss'
        probabilityOfNormal        column: 'probnorm'
        segmentCopyNumberValue     column: 'segmented'

        /* references */
//        region   column: 'region_id'
//        assay    column: 'assay_id'
//        patient  column: 'patient_id'

        // this duplicate mapping is needed due to a Criteria bug.
        // see https://forum.hibernate.org/viewtopic.php?f=1&t=1012372
        jRegion  column: 'region_id', insertable: false, updateable: false
    }

    static constraints = {
        chipCopyNumberValue        nullable: true, scale:   17
        flag                       nullable: true
        patient                    nullable: true
        probabilityOfAmplification nullable: true, scale:   17
        probabilityOfGain          nullable: true, scale:   17
        probabilityOfLoss          nullable: true, scale:   17
        probabilityOfNormal        nullable: true, scale:   17
        segmentCopyNumberValue     nullable: true, scale:   17
        trialName                  nullable: true, maxSize: 50
    }

    CopyNumberState getCopyNumberState() {
        CopyNumberState.forInteger(flag.intValue())
    }
}
