package jobs

import groovy.transform.CompileStatic
import jobs.steps.Step
import jobs.steps.ValueGroupDumpDataStep
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@CompileStatic
@Component
@Scope('job')
class KMeansClustering extends HighDimensionalOnlyJob {

    @Override
    protected Step createDumpHighDimensionDataStep(Closure resultsHolder) {
        new ValueGroupDumpDataStep(
                temporaryDirectory: temporaryDirectory,
                resultsHolder: resultsHolder,
                params: params)
    }

    @Override
    protected List<String> getRStatements() {
        String source = 'source(\'$pluginDirectory/Heatmap/KMeansHeatmap.R\')'

        // TODO What about clusters.number = 2, probes.aggregate = false?
        String createHeatmap = '''KMeansHeatmap.loader(
                            input.filename   = '$inputFileName',
                            aggregate.probes = '$divIndependentVariableprobesAggregation' == 'true',
                            clusters.number  = as.integer('$txtClusters'),
                            ${ txtMaxDrawNumber ? ", maxDrawNumber  = as.integer('$txtMaxDrawNumber')" : ''},
                            calculateZscore = '$calculateZscore'
                            )'''

        [ source, createHeatmap ]
    }

    @Override
    protected String getForwardPath() {
        "/RKMeans/heatmapOut?jobName=$name"
    }
}
