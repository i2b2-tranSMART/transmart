package jobs.table.columns

import com.google.common.collect.ImmutableMap
import groovy.util.logging.Slf4j
import org.transmartproject.core.dataquery.DataRow
import org.transmartproject.core.dataquery.TabularResult
import org.transmartproject.core.dataquery.highdim.AssayColumn

@Slf4j('logger')
class HighDimensionSingleRowResultColumn extends AbstractColumn {

    private DataRow row

    private boolean sawRow = false

    private List<AssayColumn> assays

    @Override
    void beforeDataSourceIteration(String dataSourceName, Iterable dataSource) {
        assert dataSource instanceof TabularResult

        assays = ((TabularResult)dataSource).indicesList
    }

    @Override
    void onReadRow(String dataSourceName, row) {
        assert row instanceof DataRow

        if (sawRow) {
            logger.warn 'Further rows from {} ignored', dataSourceName
            return
        }

        sawRow = true

        this.row = row
    }

    @Override
    Map<String, Object> consumeResultingTableRows() {
        if (!row) {
	    return ImmutableMap.of()
	}

        ImmutableMap.Builder builder = ImmutableMap.builder()
        for (int i = 0; i < assays.size(); i++) {
            def value = row[i]
            // empty values are dropped
            if (value != null) {
                builder.put assays[i].patientInTrialId,value
            }
        }

        row = null
        builder.build()
    }
}
