package jobs.steps.helpers

import groovy.util.logging.Slf4j
import jobs.table.Column
import jobs.table.columns.ConstantValueColumn
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.transmartproject.core.exceptions.InvalidArgumentsException

/**
 * A configurator supporting:
 *
 * - categorical variables, binned or not
 * - low dimensional data, binned or not
 *   (no binning allowed only if !forceNumericBinning)
 * - high dimensional data, multirow or not (depending on multiRow),
 *   binning or not (no binning allowed only if !forceNumericBinning)
 *
 * Does not support multiple numeric variables.
 */
@Component
@Scope('prototype')
@Qualifier('general')
@Slf4j('logger')
class OptionalBinningColumnConfigurator extends ColumnConfigurator {

    @Autowired
    BinningColumnConfigurator binningConfigurator

    String projection

    String keyForConceptPaths,
           keyForDataType,        /* CLINICAL for clinical data */
           keyForSearchKeywordId, /* only applicable for high dim data */
           keyForIsCategorical    /* optional; see isCategorical() */

    boolean multiRow = false

    /* if numeric variables must be binned */
    boolean forceNumericBinning = true

    protected Class<? extends ColumnConfigurator> numericColumnConfigurationClass = NumericColumnConfigurator

    private ColumnConfigurator innerConfigurator

    @Autowired
    ApplicationContext appCtx

    private void setupInnerConfigurator(String conceptPaths) {

        boolean emptyConcept = !conceptPaths
        if (emptyConcept) {
            //when required we will never reach here
            logger.debug 'Not required and no value for {}, assuming constant value column', keyForConceptPaths

            innerConfigurator = appCtx.getBean SimpleAddColumnConfigurator
            innerConfigurator.column = new ConstantValueColumn(
                    header:             getHeader(),
                    missingValueAction: missingValueAction)
            innerConfigurator.addColumn()

        }
        else if (categorical) {
            logger.debug 'Found pipe character in {}, assuming categorical data', keyForConceptPaths

            innerConfigurator = appCtx.getBean CategoricalColumnConfigurator

            innerConfigurator.header             = getHeader()
            innerConfigurator.keyForConceptPaths = keyForConceptPaths
        }
        else {
            logger.debug 'Did not find pipe character in {}, assuming continuous data', keyForConceptPaths

            innerConfigurator = appCtx.getBean numericColumnConfigurationClass

            innerConfigurator.header                = getHeader()
            innerConfigurator.projection            = projection
            innerConfigurator.keyForConceptPath     = keyForConceptPaths
            innerConfigurator.keyForDataType        = keyForDataType
            innerConfigurator.keyForSearchKeywordId = keyForSearchKeywordId
            innerConfigurator.multiRow              = multiRow
        }

        binningConfigurator.innerConfigurator = innerConfigurator
    }

    @Override
    protected void doAddColumn(Closure<Column> decorateColumn) {

        String conceptPaths = getConceptPaths()
        setupInnerConfigurator conceptPaths

        if (conceptPaths) {

            if (!binningConfigurator.binningEnabled &&
                    !(innerConfigurator instanceof CategoricalColumnConfigurator) &&
                    forceNumericBinning) {
                throw new InvalidArgumentsException('Numeric variables must be binned for column ' + header)
            }
            
            //configure binning only if has variable
            binningConfigurator.addColumn decorateColumn
        }
    }

    boolean isCategorical() {
        keyForIsCategorical ?
	    getStringParam(keyForIsCategorical).equalsIgnoreCase('true') : isMultiVariable()
    }

    protected boolean isMultiVariable() {
        getStringParam(keyForConceptPaths).contains '|'
    }

    boolean isCategoricalOrBinned() {
        isCategorical() || binningConfigurator.binningEnabled
    }
    /**
     * Sets parameter keys based on optional base key part
     */
    void setKeys(String keyPart = '') {
	String cap = keyPart.capitalize()
        keyForConceptPaths    = keyPart + 'Variable'
        keyForDataType        = 'div' + cap + 'VariableType'
        keyForSearchKeywordId = 'div' + cap + 'VariablePathway'
    }

    String getConceptPaths() {
        //if required this will fail on empty conceptPaths
        getStringParam keyForConceptPaths, required
    }    
}
