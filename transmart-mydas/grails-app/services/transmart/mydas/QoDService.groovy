package transmart.mydas

import org.transmartproject.core.dataquery.highdim.chromoregion.RegionRow
import uk.ac.ebi.mydas.model.DasFeature
import uk.ac.ebi.mydas.model.DasType
import org.transmartproject.core.dataquery.highdim.vcf.VcfValues
import uk.ac.ebi.mydas.model.DasFeatureOrientation
import uk.ac.ebi.mydas.model.DasPhase
import javax.annotation.PostConstruct

/**
 * Created by j.hudecek on 5-3-14.
 */
class QoDService  extends  VcfServiceAbstract {

    @PostConstruct
    void init() {
        super.init()
        dasTypes = [projectionName];
    }

    @Override
    protected void getSpecificFeatures(RegionRow region, Object assays,  Map<String, String> params, Collection<DasType> dasTypes, Map<String, List<DasFeature>> featuresPerSegment) {
        constructSegmentFeaturesMap([region], getQDFeature, featuresPerSegment)
    }

    private def getQDFeature = { VcfValues val ->

        def linkMap = val.rsId == '.' ? [:]
                : [(new URL('http://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=' + val.rsId)): 'NCBI SNP Ref']

        [new DasFeature(
                // feature id - any unique id that represent this feature
                'qd-' + val.rsId,
                // feature label
                'Quality of Depth',
                // das type
                new DasType('qd', "", "", ""),
                // das method TODO: pls find out what is actually means
                dasMethod,
                // start pos
                val.position.toInteger(),
                // end pos
                val.position.toInteger(),
                // value - this is where Minor Allele Freq (MAF) value is placed
                val.qualityOfDepth,
                DasFeatureOrientation.ORIENTATION_NOT_APPLICABLE,
                DasPhase.PHASE_NOT_APPLICABLE,
                //notes
                getCommonNotes(val),
                //links
                linkMap,
                //targets
                [],
                //parents
                [],
                //parts
                []
        )]
    }
}
