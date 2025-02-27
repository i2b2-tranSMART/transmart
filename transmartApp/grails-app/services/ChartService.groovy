import groovy.util.logging.Slf4j
import org.apache.commons.math.stat.inference.TestUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartRenderingInfo
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.AxisLocation
import org.jfree.chart.axis.CategoryAxis
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.axis.ValueAxis
import org.jfree.chart.entity.StandardEntityCollection
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.plot.PiePlot
import org.jfree.chart.plot.PieLabelLinkStyle
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.category.BarRenderer
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer
import org.jfree.chart.renderer.category.CategoryItemRendererState
import org.jfree.chart.renderer.category.ScatterRenderer
import org.jfree.chart.renderer.category.StandardBarPainter
import org.jfree.chart.renderer.xy.StandardXYBarPainter
import org.jfree.chart.renderer.xy.XYBarRenderer
import org.jfree.chart.ui.RectangleInsets
import org.jfree.data.category.CategoryDataset
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.data.general.Dataset
import org.jfree.data.general.DefaultPieDataset
import org.jfree.data.statistics.BoxAndWhiskerCalculator
import org.jfree.data.statistics.BoxAndWhiskerItem
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset
import org.jfree.data.statistics.DefaultMultiValueCategoryDataset
import org.jfree.data.statistics.HistogramDataset
import org.jfree.data.statistics.MultiValueCategoryDataset
import org.jfree.graphics2d.svg.SVGGraphics2D
import org.jfree.util.ShapeUtilities
import org.springframework.beans.factory.annotation.Autowired
import org.transmartproject.core.dataquery.highdim.HighDimensionDataTypeResource
import org.transmartproject.core.dataquery.highdim.projections.Projection
import org.transmartproject.core.querytool.ConstraintByOmicsValue
import org.transmartproject.db.dataquery.highdim.HighDimensionResourceService

import java.awt.*
import java.awt.geom.Ellipse2D
import java.awt.geom.Rectangle2D
import java.util.List

/**
 * @author Florian Guitton <f.guitton@imperial.ac.uk>
 */
@Slf4j('logger')
class ChartService {

    static transactional = false

    @Autowired private HighDimensionQueryService highDimensionQueryService
    @Autowired private I2b2HelperService i2b2HelperService
    HighDimensionResourceService highDimensionResourceService

    Map<Object, Map> getSubsetsFromRequest(GrailsParameterMap params) {
	String result_instance_id1 = params.result_instance_id1 ?: null
	String result_instance_id2 = params.result_instance_id2 ?: null

	[1      : [exists: result_instance_id1 as boolean, instance: result_instance_id1],
	 2      : [exists: result_instance_id2 as boolean, instance: result_instance_id2],
	 commons: [:]]
    }

    Map<Object, Map> computeChartsForSubsets(Map<Object, Map> subsets) {

	// We intend to use some legacy functions that are used elsewhere
	// We need to use a printer for this
	StringWriter output = new StringWriter()
	PrintWriter writer = new PrintWriter(output)

        // We want to automatically clear the output buffer as we go
        output.metaClass.toStringAndFlush = {
            def tmp = buf.toString()
            buf.setLength(0)
            tmp
        }

	// We need to run some common statistics first
	// This must be changed for multiple (>2) cohort selection
	// We grab the intersection count for our two cohort
	if (subsets[1].exists && subsets[2].exists) {
	    subsets.commons.patientIntersectionCount = i2b2HelperService.getPatientSetIntersectionSize(
		subsets[1].instance, subsets[2].instance)
	}

	// subset shared diagrams
	Map<String, List<Double>> ageHistogramHandle = [:]
	Map<String, BoxAndWhiskerItem> agePlotHandle = [:]

	subsets.findAll { n, Map p -> p.exists }.each { n, Map p ->

	    // First we get the Query Definition
	    i2b2HelperService.renderQueryDefinition p.instance,
		'Query Summary for Subset ' + n, writer
	    p.query = output.toStringAndFlush()

	    // Let's fetch the patient count
	    p.patientCount = i2b2HelperService.getPatientSetSize(p.instance)
	    // Getting the age data
	    p.ageData = i2b2HelperService.getPatientDemographicValueDataForSubset(
		'AGE_IN_YEARS_NUM', p.instance) as List<Double>
		if (p.ageData) {
		p.ageStats = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(p.ageData)
		ageHistogramHandle['Subset ' + n] = p.ageData
		agePlotHandle['Subset ' + n] = p.ageStats
	    }

	    // Sex chart has to be generated for each subset
	    p.sexData = i2b2HelperService.getPatientDemographicDataForSubset('sex_cd', p.instance)
	    moveKeyToEndOfMap p.sexData, ''
	    p.sexPie = getSVGChart(type: 'pie', data: p.sexData, title: 'Sex')

	    // Same thing for Race chart
	    p.raceData = i2b2HelperService.getPatientDemographicDataForSubset('race_cd', p.instance)
	    moveKeyToEndOfMap p.raceData, ''
	    p.racePie = getSVGChart(type: 'pie', data: p.raceData, title: 'Race')
	}

	// Let's build our age diagrams now that we have all the points in
	subsets.commons.ageHisto = getSVGChart(type: 'histogram', data: ageHistogramHandle, title: 'Age')
	subsets.commons.agePlot = getSVGChart(type: 'boxplot', data: agePlotHandle, title: 'Age')

	subsets
    }

    private void moveKeyToEndOfMap(Map map, String key) {
	if (map.containsKey(key)) {
	    def v = map[key]
	    map.remove key
	    map[key] = v
	}
    }

    Map<String, Map> getConceptsForSubsets(Map<Object, Map> subsets) {

	// We also retrieve all concepts involved in the query
	Map<String, Map> concepts = [:]

	i2b2HelperService.getDistinctConceptSet(subsets[1].instance, subsets[2].instance).collect {
	    i2b2HelperService.getConceptKeyForAnalysis it
	}.findAll() { String it -> !it.contains('SECURITY') }.each { String it ->
	    if (!i2b2HelperService.isHighDimensionalConceptKey(it)) {
		concepts[it] = getConceptAnalysis(it, null, subsets, null)
	    }
	}

	concepts
    }

    Map<String, Map> getHighDimensionalConceptsForSubsets(Map<Object, Map> subsets) {
        // We also retrieve all concepts involved in the query
	Map<String, Map> concepts = [:]
	highDimensionQueryService.getHighDimensionalConceptSet(subsets[1].instance, subsets[2].instance).findAll() { Map it ->
	    !it.concept_key.contains('SECURITY')
	}.each { Map it ->
	    String key = it.concept_key + it.omics_selector + ' - ' + it.omics_projection_type
	    if (!concepts.containsKey(key)) {
		concepts[key] = getConceptAnalysis(it.concept_key, it, subsets, null)
	    }
        }
        concepts
    }

    Map getConceptAnalysis(String concept, Map omics_params, Map<Object, Map> subsets, Map chartSize) {

        // We create our result holder and initiate it from subsets
	Map result = [:]
        subsets.each { k, v ->
            result[k] = [:]
            v.exists == null ?: (result[k].exists = v.exists)
            v.instance == null ?: (result[k].instance = v.instance)
        }

        // We retrieve the basics
        result.commons.conceptCode = i2b2HelperService.getConceptCodeFromKey(concept)
        result.commons.conceptKey = concept.substring(concept.substring(3).indexOf('\\') + 3)
        result.commons.conceptName = i2b2HelperService.getShortNameFromKey(concept)
        result.commons.conceptPath = concept
	result.commons.omics_params = omics_params ?: null

        if (i2b2HelperService.isValueConceptCode(result.commons.conceptCode)) {

            result.commons.type = 'value'

            // Let's prepare our subset shared diagrams, we will fill them later
	    Map<String, List<Double>> conceptHistogramHandle = [:]
	    Map<String, BoxAndWhiskerItem> conceptPlotHandle = [:]

	    result.findAll { n, p -> p.exists }.each { n, p ->

		if (p.instance) {
		    p.patientCount = i2b2HelperService.getPatientSetSize(p.instance)
		}
		else {
                    p.patientCount = i2b2HelperService.getPatientCountForConcept(concept)
		}

                // Getting the concept data
                p.conceptData = i2b2HelperService.getConceptDistributionDataForValueConceptFromCode(result.commons.conceptCode, p.instance).toList()
                p.conceptStats = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(p.conceptData)
                conceptHistogramHandle['Subset ' + n] = p.conceptData
                conceptPlotHandle['Subset ' + n] = p.conceptStats
            }

            // Let's build our concept diagrams now that we have all the points in
            result.commons.conceptHisto = getSVGChart(type: 'histogram', data: conceptHistogramHandle, size: chartSize)
            result.commons.conceptPlot = getSVGChart(type: 'boxplot', data: conceptPlotHandle, size: chartSize)

            // Let's calculate the T test if possible
            if (result[1].exists && result[2].exists) {

		if (result[1].conceptData.size() == 0 && result[2].conceptData.size() == 0) {
                    result.commons.testmessage = 'No t-test calculated: subset data is empty'
		}
		else if (result[1].conceptData.toArray() == result[2].conceptData.toArray()) {
                    result.commons.testmessage = 'No t-test calculated: these are the same subsets'
		}
		else if (result[1].conceptData.size() < 2 || result[2].conceptData.size() < 2) {
                    result.commons.testmessage = 'No t-test calculated: not enough data'
		}
                else {

		    double[] o = result[1].conceptData.toArray()
		    double[] t = result[2].conceptData.toArray()

                    result.commons.tstat = TestUtils.t(o, t).round(5)
                    result.commons.pvalue = String.format("%1.3e", TestUtils.tTest(o, t))
                    result.commons.significance = TestUtils.tTest(o, t, 0.05)

		    if (result.commons.significance) {
                        result.commons.testmessage = 't-test demonstrated results are significant at a 95% confidence level'
		    }
		    else {
                        result.commons.testmessage = 't-test demonstrated results are <b>not</b> significant at a 95% confidence level'
                    }
		}
            }
	}
	else if (i2b2HelperService.isHighDimensionalConceptCode(result.commons.conceptCode) && result.commons.omics_params) {

            result.commons.type = 'value'
            result.commons.conceptName = result.commons.omics_params.omics_selector + ' in ' + result.commons.conceptName

            // Let's prepare our subset shared diagrams, we will fill them later
	    Map conceptHistogramHandle = [:]
	    Map<String, BoxAndWhiskerItem> conceptPlotHandle = [:]

	    HighDimensionDataTypeResource resource =
		highDimensionResourceService.getHighDimDataTypeResourceFromConcept(concept)

	    result.findAll { n, p -> p.exists }.each { n, p ->

                // Getting the concept data
                p.conceptData =
                    resource.getDistribution(
                    new ConstraintByOmicsValue(projectionType: result.commons.omics_params.omics_projection_type,
                                               property: result.commons.omics_params.omics_property,
                                               selector: result.commons.omics_params.omics_selector),
                    concept,
                    (p.instance == '' ? null : p.instance as Long)).collect { k, v -> v }

		if (p.instance) {
		    p.patientCount = i2b2HelperService.getPatientSetSize(p.instance)
		}
		else {
                    p.patientCount = i2b2HelperService.getPatientCountForConcept(concept)
		}

                p.conceptStats = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(p.conceptData)
                conceptHistogramHandle['Subset ' + n] = p.conceptData
                conceptPlotHandle['Subset ' + n] = p.conceptStats
            }

            // Let's build our concept diagrams now that we have all the points in
	    result.commons.conceptHisto = getSVGChart(type: 'histogram', data: conceptHistogramHandle,
						      size: chartSize, ylabel: '',
						      xlabel: Projection.prettyNames.get(omics_params.omics_projection_type, omics_params.omics_projection_type),
						      bins: omics_params.omics_hist_bins ?: 10)
	    result.commons.conceptPlot = getSVGChart(type: 'boxplot-and-points',
						     data: conceptHistogramHandle, boxplotdata: conceptPlotHandle, size: chartSize)

            // Let's calculate the T test if possible
	    if (result[2].exists) {

		if (result[1].conceptData.size() == 0 && result[2].conceptData.size() == 0) {
                    result.commons.testmessage = 'No t-test calculated: subset data is empty'
		}
		else if (result[1].conceptData.toArray() == result[2].conceptData.toArray()) {
                    result.commons.testmessage = 'No t-test calculated: these are the same subsets'
		}
		else if (result[1].conceptData.size() < 2 || result[2].conceptData.size() < 2) {
                    result.commons.testmessage = 'No t-test calculated: not enough data'
		}
                else {
		    double[] o = result[1].conceptData.toArray()
		    double[] t = result[2].conceptData.toArray()

                    result.commons.tstat = TestUtils.t(o, t).round(5)
                    result.commons.pvalue = String.format("%1.3e", TestUtils.tTest(o, t))
                    result.commons.significance = TestUtils.tTest(o, t, 0.05)

		    if (result.commons.significance) {
                        result.commons.testmessage = 't-test demonstrated results are significant at a 95% confidence level'
		    }
		    else {
                        result.commons.testmessage = 't-test demonstrated results are <b>not</b> significant at a 95% confidence level'
                    }
		}
	    }
        }
        else {

            result.commons.type = 'traditional'

	    result.findAll { n, p -> p.exists }.each { n, p ->

		if (p.instance) {
		    p.patientCount = i2b2HelperService.getPatientSetSize(p.instance)
		}
		else {
                    p.patientCount = i2b2HelperService.getPatientCountForConcept(concept)
		}

                // Getting the concept data
		p.conceptData = i2b2HelperService.getConceptDistributionDataForConcept(concept, p.instance)
                p.conceptBar = getSVGChart(type: 'bar', data: p.conceptData,
					   size: [width: 500, height: p.conceptData.size() * 22 + 90],
					   ylabel: "Count", xlabel:"Concept")
            }

            // Let's calculate the χ² test if possible
	    if (result[2].exists) {

		boolean junction = false

                result[1].conceptData.each { k, v ->
                    junction = junction ?: (v > 0 && result[2].conceptData[k] > 0)
                }

		if (result[1].conceptData.size() == 0 && result[2].conceptData.size() == 0) {
                    result.commons.testmessage = 'No χ² test calculated: subsets are empty'
		}
		else if (!junction) {
                    result.commons.testmessage = 'No χ² test calculated: subsets are disjointed'
		}
		else if (result[1].conceptData == result[2].conceptData) {
                    result.commons.testmessage = 'No χ² test calculated: these are the same subsets'
		}
		else if (result[1].conceptData.size() != result[2].conceptData.size()) {
                    result.commons.testmessage = 'No χ² test calculated: subsets have different sizes'
		}
		else if (result[1].conceptData.size() < 2) {
                    result.commons.testmessage = 'No χ² test calculated: insufficient dimension'
		}
                else {

		    long[][] counts = [result[1].conceptData.values(), result[2].conceptData.values()]

                    result.commons.chisquare = TestUtils.chiSquare(counts).round(5)
                    result.commons.pvalue = String.format("%1.3e", TestUtils.chiSquareTest(counts))
                    result.commons.significance = TestUtils.chiSquareTest(counts, 0.05)

		    if (result.commons.significance) {
                        result.commons.testmessage = 'χ² test demonstrated results are significant at a 95% confidence level'
		    }
		    else {
                        result.commons.testmessage = 'χ² test demonstrated results are <b>not</b> significant at a 95% confidence level'
		    }
                }
            }
        }

	result
    }

    private getSVGChart(Map args) {

        // Retrieving function parameters
        def type = args.type ?: null
	Map data = args.data ?: [:]
	Map boxplotdata = args.boxplotdata ?: [:]
	Map size = args.size ?: [:]
	String title = args.title ?: ''
	String xlabel = args.xlabel ?: ''
	String ylabel = args.ylabel ?: ''
	int bins = 10
	if (args.containsKey('bins')) {
            try {
                bins = args.bins as Integer
            }
	    catch (ignored) {
		logger.error 'Could not parse provided argument to integer: {}', args.bins
	    }
        }

        // We retrieve the dimension if provided
	int width = size?.width ?: 300
	int height = size?.height ?: 300

	if (type == 'pie') {
	    width = 400;
	    height = 400;
	}

        // If no data is being sent we return an empty string
	if (!data) {
	    return ''
	}

	int nValues = 0
	int nKeys = 0

        // We initialize a couple of objects that we are going to need
        Dataset set = null
        JFreeChart chart = null
        Color transparent = new Color(255, 255, 255, 0)

        Color subset1SeriesColor = new Color(110, 155, 73, 150)
        Color subset2SeriesColor = new Color(110, 158, 200, 150)
        Color subset1SeriesOutlineColor = new Color(110, 155, 73)
        Color subset2SeriesOutlineColor = new Color(17, 86, 146)

        SVGGraphics2D renderer = new SVGGraphics2D(width, height)

        // If not already defined, we add a method for defaulting parameters
	if (!JFreeChart.metaClass.getMetaMethod('setChartParameters', [])) {
            JFreeChart.metaClass.setChartParameters = {

                padding = RectangleInsets.ZERO_INSETS
                backgroundPaint = transparent
                plot.outlineVisible = false
                plot?.backgroundPaint = transparent

                if (plot instanceof CategoryPlot || plot instanceof XYPlot) {

                    float[] dashArray = [2.0F, 2.0F] as float[]

		    plot?.domainGridlinePaint = Color.LIGHT_GRAY
                    plot?.domainGridlineStroke = new BasicStroke(1F, 0, 2, 0.0F, dashArray, 0.0F);
		    plot?.rangeGridlinePaint = Color.LIGHT_GRAY
                    plot?.rangeGridlineStroke = new BasicStroke(1F, 0, 2, 0.0F, dashArray, 0.0F);
		    plot?.renderer?.setSeriesPaint(0, subset1SeriesColor)
                    plot?.renderer?.setSeriesPaint(1, subset2SeriesColor)
                    plot?.renderer?.setSeriesOutlinePaint(0, subset1SeriesOutlineColor)
                    plot?.renderer?.setSeriesOutlinePaint(1, subset2SeriesOutlineColor)
                    if (plot?.renderer instanceof BarRenderer) {

                        plot?.renderer?.drawBarOutline = true
                        plot?.renderer?.shadowsVisible = false
                        plot?.renderer?.barPainter = new StandardBarPainter()
                    }
                    if (plot?.renderer instanceof XYBarRenderer) {

                        plot?.renderer?.drawBarOutline = true
                        plot?.renderer?.shadowsVisible = false
                        plot?.renderer?.barPainter = new StandardXYBarPainter()
                    }
                }
            }
	}

        // Depending on the type of chart we proceed
        switch (type) {
            case 'histogram':

		// requires data values to set min/max
		// can be called with empty values

                data.each { k, v ->
		    if (k) {
			nKeys++
		    }
                    nValues += v.size()
                }
		if (nKeys == 0) {
		    return ''
		}
		if (nValues == 0) {
		    return ''
		}

                def min = null
                def max = null

                set = new HistogramDataset()
                data.each { k, v ->
                    if(v.size()){
                        min = min != null ? (v.min() != null && min > v.min() ? v.min() : min) : v.min()
                        max = max != null ? (v.max() != null && max < v.max() ? v.max() : max) : v.max()
                    }
                }.each { k, v ->
		    if (k && v.size()) {
			set.addSeries(k, (double[]) v.toArray(), bins, min, max)
		    }
                }

                chart = ChartFactory.createHistogram(title, xlabel, ylabel, set, PlotOrientation.VERTICAL, true, true, false)
                chart.setChartParameters()

                // If the first series (index 0) is related to 'Subset 2' i.s.o. 'Subset 1'
                // (e.g. because 'Subset 1' is empty or if no data is avaialable for the given concept)
                // adjust the default coloring scheme
                if (set.getSeriesCount()>0 && set.getSeriesKey(0) ==~ /.* 2/) {
                    chart.plot.renderer.setSeriesPaint(0, subset2SeriesColor)
                    chart.plot.renderer.setSeriesOutlinePaint(0, subset2SeriesOutlineColor)
                }
                chart.legend.visible = false

                break

            case 'boxplot':

		// value is BoxAndWhiskerItem which has NaN if no values were given

                data.each { k, v ->
                    if (k) {
                        nKeys++
			if (!v.getMean().isNaN()) {
			    nValues++
			}
                    }
		}
		if (nKeys == 0) {
		    return ''
		}
		if (nValues == 0) {
		    return ''
		}

                set = new DefaultBoxAndWhiskerCategoryDataset()

                data.each { k, v ->
                    // ignore data (BoxAndWhiskerItem) which is a result of calculations with an empty data set
		    if (k && !allStatsAreNaN(v)) {
			set.add(v, k, k)
		    }
                }

                chart = ChartFactory.createBoxAndWhiskerChart(title, xlabel, ylabel, set, false)
                chart.setChartParameters()
                // If the first series (index 0) is related to 'Subset 2' i.s.o. 'Subset 1'
                // (e.g. because 'Subset 1' is empty or if no data is avaialable for the given concept)
                // adjust the default coloring scheme
                if (set.getRowCount()>0 && set.getRowKey(0) ==~ /.* 2/) {
                    chart.plot.renderer.setSeriesPaint(0, subset2SeriesColor)
                    chart.plot.renderer.setSeriesOutlinePaint(0, subset2SeriesOutlineColor)
                }
		chart.plot.renderer.maximumBarWidth = 0.09
		chart.plot.renderer.setSeriesOutlinePaint(0, subset1SeriesOutlineColor)
		chart.plot.renderer.setSeriesOutlinePaint(1, subset2SeriesOutlineColor)
		chart.plot.renderer.setUseOutlinePaintForWhiskers(true)
		chart.plot.renderer.setSeriesPaint(0, new Color(110, 155, 73))
		chart.plot.renderer.setSeriesPaint(1, new Color(110, 158, 200))

                break

            case 'boxplot-and-points':

                set = new DefaultBoxAndWhiskerCategoryDataset()
                def set2 = new DefaultMultiValueCategoryDataset()
                String rowname = new String('Row 0')
                boxplotdata.each { k, v ->
		    if (k) {
			set.add(v, rowname, k)
		    }
                }
                data.each { k, v ->
		    if (k) {
			set2.add(v, rowname, k)
		    }
                }

                final CategoryAxis xAxis = new CategoryAxis(xlabel)
                final NumberAxis yAxis = new NumberAxis(ylabel)
                yAxis.setAutoRangeIncludesZero(false)
                final BoxAndWhiskerRenderer boxAndWhiskerRenderer = new BoxAndWhiskerRenderer()
		boxAndWhiskerRenderer.setDefaultToolTipGenerator(new BoxAndWhiskerToolTipGenerator())
                final CategoryPlot catplot = new CategoryPlot(set, xAxis, yAxis, boxAndWhiskerRenderer)

                // add the points
                catplot.setDataset(1, set2)
		ScatterRenderer pointsWithJitterRenderer = createScatterWithJitterRenderer(20)
                pointsWithJitterRenderer.setSeriesShape(0, createScatterShape(data))
                catplot.setRenderer(1, pointsWithJitterRenderer)

                chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, catplot, false)

                ChartFactory.chartTheme.apply(chart)
                chart.setChartParameters()
                chart.plot.renderer.maximumBarWidth = 0.09
                break

            case 'pie':

		// fails if given a null key (e.g. missing gender values)
            
                data.each { k, v ->
		    if (k) {
			nKeys++
		    }
		}
		if (nKeys == 0) {
		    return ''
                }

                set = new DefaultPieDataset()
                data.each { k, v ->
                    // Allow values for key '' to be passed on
		    if (k != null) {
			set.setValue(k, v)
		    }
                }

                chart = ChartFactory.createPieChart(title, set, false, false, false)
                chart.setChartParameters()

                chart.title.font.size = 13
                chart.title.padding = new RectangleInsets(30, 0, 0, 0)
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setLabelBackgroundPaint(null);
		plot.setLabelOutlinePaint(null);
		plot.setLabelShadowPaint(null);
		plot.setMaximumLabelWidth(0.25);
		plot.setShadowPaint(transparent);
		plot.setInteriorGap(0.25);
		plot.setLabelLinkStyle(PieLabelLinkStyle.STANDARD);

                data.eachWithIndex { o, i ->
                    if(o.key){
			chart.plot.setSectionPaint(
			    o.key, new Color(213, 18, 42, (255 / (data.size() + 1) * (data.size() - i)).toInteger()))
                    }
                }

                break

            case 'bar':

		// skip any null keys
                data.each { k, v ->
		    if (k) {
			nKeys++
		    }
		}
		if (nKeys == 0) {
		    return ''
                }

                set = new DefaultCategoryDataset()
                data.each { k, v ->
                    // Allow values for key '' to be passed on
		    if (k != null) {
			set.setValue v, '', k
		    }
                }

		chart = ChartFactory.createBarChart(title, xlabel, ylabel, set, PlotOrientation.HORIZONTAL,
						    false, true, false)
                chart.setChartParameters()

		def categoryPlot = chart.getCategoryPlot()
                categoryPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT)
                categoryPlot.renderer.setSeriesPaint(0, new Color(128, 193, 119))
                categoryPlot.renderer.setSeriesOutlinePaint(0, new Color(84, 151, 12))
		CategoryAxis axis = new CategoryAxis();
		axis.setMaximumCategoryLabelLines(2);
		axis.configure();
		categoryPlot.setDomainAxis(axis);

                break
        }

	chart.draw renderer, new Rectangle(0, 0, width, height),
	    new ChartRenderingInfo(new StandardEntityCollection())

	String result = renderer.getSVGDocument()

        // We need to remove some of the perturbing DOM injected by JFreeChart
        result = (result =~ /<\?xml(.*)\?>/).replaceAll('')
        result = (result =~ /<!DOCTYPE(.*?)>/).replaceAll('')
	result = (result =~ /xmlns(.*?)="(.*?)"(\s*)/).replaceAll('')
        result
    }

    /**
     * Create a Shape object for a circle with radius depending on the amount of data points
     * @param data Map where the values are lists of data points
     */
    private Ellipse2D.Double createScatterShape(Map data) {
        int amount = 0
        data.each { k, v ->
            amount = v.size() > amount ? v.size() : amount
        }
        // radius is at least 3 (at 300 or more data points) and at most 6 (0 data points)
        double radius = Math.max(3.0, -0.01 * amount + 6.0)
	new Ellipse2D.Double(0.0, 0.0, radius, radius)
    }

    /**
     * Create a renderer for a scatterplot with jitter on the category axis
     * @param jitter The amount of jitter. Category axis values for points will be perturbed by (Math.rand() - 0.5) * jitter
     */
    private ScatterRenderer createScatterWithJitterRenderer(double jitter) {
	new ScatterRenderer() {
	    void drawItem(Graphics2D g2, CategoryItemRendererState state,
                          Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis,
			  ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {

                // do nothing if item is not visible
                if (!getItemVisible(row, column)) {
                    return
                }

                int visibleRow = state.getVisibleSeriesIndex(row)
                if (visibleRow < 0) {
                    return
                }

                int visibleRowCount = state.getVisibleSeriesCount()

                PlotOrientation orientation = plot.getOrientation()

		MultiValueCategoryDataset d = dataset
                Comparable rowKey = d.getRowKey(row)
                Comparable columnKey = d.getColumnKey(column)
				List values = d.getValues(rowKey, columnKey)
                if (values == null) {
                    return
                }

                int valueCount = values.size()
                for (int i = 0; i < valueCount; i++) {
                    // current data point...
                    double x1
		    if (getUseSeriesOffset()) {
                        x1 = domainAxis.getCategorySeriesMiddle(column,
								dataset.columnCount, visibleRow, visibleRowCount,
								getItemMargin(), dataArea, plot.domainAxisEdge)
                    }
                    else {
                        x1 = domainAxis.getCategoryMiddle(column, getColumnCount(),
							  dataArea, plot.domainAxisEdge)
                    }
                    // add the jitter here
                    x1 += (Math.random() - 0.5) * jitter
		    Number n = values.get(i)
                    double value = n.doubleValue()
		    double y1 = rangeAxis.valueToJava2D(value, dataArea, plot.rangeAxisEdge)

                    Shape shape = getItemShape(row, column)
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        shape = ShapeUtilities.createTranslatedShape(shape, y1, x1)
                    }
                    else if (orientation == PlotOrientation.VERTICAL) {
                        shape = ShapeUtilities.createTranslatedShape(shape, x1, y1)
                    }
                    if (getItemShapeFilled(row, column)) {
			if (getUseFillPaint()) {
			    g2.setPaint getItemFillPaint(row, column)
                        }
                        else {
			    g2.setPaint getItemPaint(row, column)
                        }
			g2.fill shape
                    }

		    if (getDrawOutlines()) {
			if (getUseOutlinePaint()) {
			    g2.setPaint getItemOutlinePaint(row, column)
                        }
                        else {
			    g2.setPaint getItemPaint(row, column)
                        }
			g2.setStroke getItemOutlineStroke(row, column)
			g2.draw shape
                    }
                }
            }
        }
    }

    private boolean allStatsAreNaN(BoxAndWhiskerItem item) {
	Double.isNaN(item.mean) && Double.isNaN(item.median) &&
	    Double.isNaN(item.q1) && Double.isNaN(item.q3)
    }
}
