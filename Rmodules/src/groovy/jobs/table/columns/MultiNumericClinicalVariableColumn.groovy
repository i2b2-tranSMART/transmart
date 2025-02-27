package jobs.table.columns

import com.google.common.collect.ImmutableMap
import groovy.transform.CompileStatic
import org.transmartproject.core.dataquery.clinical.ClinicalVariableColumn
import org.transmartproject.core.dataquery.clinical.PatientRow

/**
 * Column that supports an arbitrary number of numeric clinical variables
 * and collects the values under a map where the keys are configurable
 * via the provided clinical variable -> string map.
 */
@CompileStatic
class MultiNumericClinicalVariableColumn extends AbstractColumn {

    // clinical variable -> name of the group
    Map<ClinicalVariableColumn, String> clinicalVariables

    private PatientRow lastRow

    @Override
    void onReadRow(String dataSourceName, row) {
        assert lastRow == null
        assert row instanceof PatientRow

        lastRow = (PatientRow) row
    }

    @Override
    Map<String, Object> consumeResultingTableRows() {
        if (lastRow == null) {
            return ImmutableMap.of()
        }

        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder()

        clinicalVariables.each { ClinicalVariableColumn col, String groupName ->
            def value = lastRow.getAt col
            if (value != null) {
                value = validateNumber col, value
                builder.put groupName, value
            }
        }

        PatientRow lastRowSaved = lastRow
        lastRow = null
        ImmutableMap.of(lastRowSaved.patient.inTrialId, builder.build()) as Map
    }
}
