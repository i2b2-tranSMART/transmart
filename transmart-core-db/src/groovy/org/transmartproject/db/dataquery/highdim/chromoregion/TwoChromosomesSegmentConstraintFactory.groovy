package org.transmartproject.db.dataquery.highdim.chromoregion

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.transmartproject.core.dataquery.highdim.dataconstraints.DataConstraint
import org.transmartproject.db.dataquery.highdim.dataconstraints.CriteriaDataConstraint
import org.transmartproject.db.dataquery.highdim.dataconstraints.DisjunctionDataConstraint
import org.transmartproject.db.dataquery.highdim.parameterproducers.AbstractMethodBasedParameterFactory
import org.transmartproject.db.dataquery.highdim.parameterproducers.ProducerFor

/**
 * @author j.hudecek
 */
@CompileStatic
@Component
@Scope('prototype')
class TwoChromosomesSegmentConstraintFactory extends AbstractMethodBasedParameterFactory {

    String setSegmentPrefix(String value) {
        chromosomeSegmentConstraintFactoryUp.segmentPrefix = value
    }

    String setSegmentChromosomeColumn(String value) {
        chromosomeSegmentConstraintFactoryUp.segmentChromosomeColumn = value
    }

    String setSegmentStartColumn(String value) {
        chromosomeSegmentConstraintFactoryUp.segmentStartColumn = value
    }

    String setSegmentEndColumn(String value) {
        chromosomeSegmentConstraintFactoryUp.segmentEndColumn = value
    }

    String setSegmentTwoPrefix(String value) {
        chromosomeSegmentConstraintFactoryDown.segmentPrefix = value
    }

    String setSegmentTwoChromosomeColumn(String value) {
        chromosomeSegmentConstraintFactoryDown.segmentChromosomeColumn = value
    }

    String setSegmentTwoStartColumn(String value) {
        chromosomeSegmentConstraintFactoryDown.segmentStartColumn = value
    }

    String setSegmentTwoEndColumn(String value) {
        chromosomeSegmentConstraintFactoryDown.segmentEndColumn = value
    }

    @Autowired
    ChromosomeSegmentConstraintFactory chromosomeSegmentConstraintFactoryUp

    @Autowired
    ChromosomeSegmentConstraintFactory chromosomeSegmentConstraintFactoryDown

    @ProducerFor(DataConstraint.CHROMOSOME_SEGMENT_CONSTRAINT)
    DisjunctionDataConstraint createTwoChromosomeSegmentConstraints(Map<String, Object> params) {

        ChromosomeSegmentConstraint chr = chromosomeSegmentConstraintFactoryUp.createChromosomeSegmentConstraint(params)
        ChromosomeSegmentConstraint chr2 = chromosomeSegmentConstraintFactoryDown.createChromosomeSegmentConstraint(params)

        new DisjunctionDataConstraint(constraints: [chr, chr2] as List<CriteriaDataConstraint> )
    }
}
