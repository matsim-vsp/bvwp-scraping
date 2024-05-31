package org.tub.vsp.bvwp.computation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.container.base.rail.RailBaseDataContainer;
import org.tub.vsp.bvwp.data.type.Emission;

public class NkvCalculatorRail{

    private static final Logger log = LogManager.getLogger( NkvCalculatorRail.class );
    private final RailBaseDataContainer railBaseDataContainer;

    // moving towards replacing the stateless static functions by a modifiable instance approach:
    public NkvCalculatorRail(RailBaseDataContainer railBaseDataContainer) {
        this.railBaseDataContainer = railBaseDataContainer;
    }
    public Double calculateNkv( Modifications modifications ) {
        return calculateNkv( modifications, this.railBaseDataContainer );
    }

    //    public Double calculateCost_CO2( Modifications modifications ) {
//        return calculateCost_CO2( modifications, this.railBaseDataContainer );
//    }

    // old static methods beyond:
    @Deprecated // use instance approach
    public static Double calculateNkv(Modifications modifications, RailBaseDataContainer railBaseDataContainer) {
        assert modifications.mehrFzkm() == 0;
        assert modifications.nonCo2BenefitsFactor() == 1.;

        double baukosten = railBaseDataContainer.getCostBenefitAnalysis().getCost().overallCosts() * modifications.constructionCostFactor();
        double benefit = railBaseDataContainer.getCostBenefitAnalysis().getOverallBenefit().overall();
        double co2_infra_eur = railBaseDataContainer.getCostBenefitAnalysis().getNl().overall();
        double co2_betrieb_t = railBaseDataContainer.getPhysicalEffect().getEmissionsDataContainer().emissions().get(Emission.CO2);

        return ComputationKN.nkv_rail(modifications.co2Price(), baukosten, benefit, co2_infra_eur, co2_betrieb_t);
    }

//    @Deprecated // use instance approach
//    public static Double calculateCost_CO2( Modifications modifications, RailBaseDataContainer streetBaseDataContainer ) {
//        log.warn("modifications=" + modifications);
//        Amounts a = new Amounts();
//        Benefits b = benefitsFromBaseData(streetBaseDataContainer );
//
//        final double co2Costs = -b_co2( modifications, a, b );
//
//        log.warn( TEXT_RED + "project=" + streetBaseDataContainer.getProjectInformation().getProjectNumber() + "; co2Costs=" + co2Costs +
//        TEXT_BLACK );
//
//        return co2Costs;
//    }
}
