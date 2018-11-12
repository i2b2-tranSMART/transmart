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

package org.transmartproject.db.dataquery.highdim.assayconstraints

import org.junit.Before
import org.junit.Test
import org.transmartproject.core.dataquery.assay.Assay
import org.transmartproject.db.dataquery.highdim.AssayQuery
import org.transmartproject.db.dataquery.highdim.AssayTestData

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.transmartproject.db.test.Matchers.hasSameInterfaceProperties

class PatientIdListConstraintTests {

    AssayTestData testData = new AssayTestData()

    @Before
    void setUp() {
        testData.saveAll()
    }

    @Test
    void basicTest() {
        final def targetId = 'SUBJ_ID_2'

        def wantedPatients = testData.patients.findAll { it.inTrialId == targetId }
        def wantedAssays = testData.assays.findAll {
            it.patient in wantedPatients
        }

        def result = new AssayQuery([
                new PatientIdListCriteriaConstraint(
                        patientIdList: [targetId]
                )
        ]).list()

        assertThat result, containsInAnyOrder(
                wantedAssays.collect {
                    hasSameInterfaceProperties(Assay, it)
                })
    }

    @Test
    void testNonExistant() {
        def result = new AssayQuery([
                new PatientIdListCriteriaConstraint(
                        patientIdList: ['NONEXISTANT_PATIENT_567395367']
                )
        ]).list()

        assertThat result, empty()
    }

    @Test
    void testEmpty() {
        def result = new AssayQuery([
                new PatientIdListCriteriaConstraint(
                        patientIdList: []
                )
        ]).list()

        assertThat result, empty()
    }

}
