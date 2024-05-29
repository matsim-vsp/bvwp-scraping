package org.tub.vsp.bvwp.computation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.container.base.rail.RailBaseDataContainer;
import org.tub.vsp.bvwp.data.container.base.street.StreetBaseDataContainer;
import org.tub.vsp.bvwp.data.container.base.street.StreetPhysicalEffectDataContainer;
import org.tub.vsp.bvwp.data.type.Emission;
import org.tub.vsp.bvwp.data.type.VehicleEmissions;

import java.util.Optional;

import static org.tub.vsp.bvwp.computation.ComputationKN.*;
import static org.tub.vsp.bvwp.computation.ConsoleColors.TEXT_BLACK;
import static org.tub.vsp.bvwp.computation.ConsoleColors.TEXT_RED;

public class NkvCalculatorRail{

    private static final Logger log = LogManager.getLogger( NkvCalculatorRail.class );
    private final RailBaseDataContainer railBaseDataContainer;

    // moving towards replacing the stateless static functions by a modifiable instance approach:
    public NkvCalculatorRail( RailBaseDataContainer streetBaseDataContainer ) {
        this.railBaseDataContainer = streetBaseDataContainer;
    }
    public Double calculateNkv( Modifications modifications ) {
        return calculateNkv( modifications, this.railBaseDataContainer );
    }
    public Double calculateCost_CO2( Modifications modifications ) {
        return calculateCost_CO2( modifications, this.railBaseDataContainer );
    }

    // old static methods beyond:
    @Deprecated // use instance approach
    public static Double calculateNkv( Modifications modifications, RailBaseDataContainer streetBaseDataContainer ) {
//        log.warn("modifications=" + modifications);
        Optional<Amounts> a = amountsFromBaseData(streetBaseDataContainer );
        Optional<Benefits> b = benefitsFromBaseData(streetBaseDataContainer );

        if (a.isEmpty()) {
            log.warn("amounts container is empty for project=" + streetBaseDataContainer.getUrl());
            return null;
        }
        if (b.isEmpty()) {
            log.warn("benefits container is empty for project=" + streetBaseDataContainer.getUrl());
            return null;
        }

        double baukosten = streetBaseDataContainer.getCostBenefitAnalysis().getCost().overallCosts() * modifications.constructionCostFactor();

        return nkvOhneKR_induz(modifications, a.get(), b.get(), baukosten, streetBaseDataContainer.getCostBenefitAnalysis().getOverallBenefit().overall());
    }


    private static Optional<Amounts> amountsFromBaseData( RailBaseDataContainer baseDataContainer ) {

        StreetPhysicalEffectDataContainer.Effect tt = baseDataContainer.getPhysicalEffect().getTravelTimes();

        StreetPhysicalEffectDataContainer.Effect vkm = baseDataContainer.getPhysicalEffect()
                                                                              .getVehicleKilometers();

        VehicleEmissions vehicleEmissions = baseDataContainer.getPhysicalEffect().getEmissionsDataContainer()
                                                                   .emissions().get(Emission.CO2);

        if (tt == null || vkm == null || vehicleEmissions == null) {
            return Optional.empty();
        }

//        new Amounts( 1., 1., 1., 1., 1., 1., 1., 1. );
        // uncomment to see argument names

        final Amounts amounts = new Amounts(
                vkm.overall(), Optional.ofNullable(vkm.induced()).orElse(0.), vkm.shifted(), // pkwkm
                tt.overall(), Optional.ofNullable(tt.induced()).orElse(0.), tt.shifted(), // pers_h
                vehicleEmissions.pkw(), vehicleEmissions.kfz() // co2
        );
        final Optional<Amounts> optional = Optional.of(amounts);
        if (optional.isEmpty()) {
            log.warn("here");
            throw new RuntimeException("stop");
        }
        return optional;
    }

    private static Optional<Benefits> benefitsFromBaseData( RailBaseDataContainer streetBaseDataContainer ) {
        // @formatter:off
        return Optional.ofNullable(streetBaseDataContainer).map(RailBaseDataContainer::getCostBenefitAnalysis)
                       .map(cb -> new Benefits(
                                       cb.getNbOperations().overall(), // fzkm
                                       cb.getNrz().overall(), // rz
                                       cb.getNi().overall(), // impl
                                       cb.getNl().overall(), // co2_infra
                                       cb.getNa().get(Emission.CO2).overall(), // co2_betrieb
                                       cb.getOverallBenefit().overall() // benefit
                       ));
        // @formatter:on
    }

    @Deprecated // use instance approach
    public static Double calculateCost_CO2( Modifications modifications, RailBaseDataContainer streetBaseDataContainer ) {
        log.warn("modifications=" + modifications);
        Optional<Amounts> a = amountsFromBaseData(streetBaseDataContainer );
        Optional<Benefits> b = benefitsFromBaseData(streetBaseDataContainer );

        if (a.isEmpty() || b.isEmpty()) {
            throw new RuntimeException( "co2 costs cannot be computed" );
        }
        final double co2Costs = -b_co2( modifications, a.get(), b.get() );

        log.warn( TEXT_RED + "project=" + streetBaseDataContainer.getProjectInformation().getProjectNumber() + "; co2Costs=" + co2Costs + TEXT_BLACK );

        return co2Costs;
    }
}
