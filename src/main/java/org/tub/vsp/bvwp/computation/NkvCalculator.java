package org.tub.vsp.bvwp.computation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.container.base.PhysicalEffectDataContainer;
import org.tub.vsp.bvwp.data.container.base.StreetBaseDataContainer;
import org.tub.vsp.bvwp.data.type.Emission;
import org.tub.vsp.bvwp.data.type.VehicleEmissions;

import java.util.Map;
import java.util.Optional;

import static org.tub.vsp.bvwp.computation.ComputationKN.*;

public class NkvCalculator {

    private static final Logger log = LogManager.getLogger(NkvCalculator.class);


    public static Double calculateNkv(Modifications modifications, StreetBaseDataContainer streetBaseDataContainer) {
//        log.warn("modifications=" + modifications);
        Optional<Amounts> a = amountsFromStreetBaseData(streetBaseDataContainer);
        Optional<Benefits> b = benefitsFromStreetBaseData(streetBaseDataContainer);

        if ( a.isEmpty() ) {
            log.warn("amounts container is empty for project=" + streetBaseDataContainer.getUrl() );
            return null ;
        }
        if ( b.isEmpty() ) {
            log.warn("benefits container is empty for project=" + streetBaseDataContainer.getUrl() );
            return null ;
        }

        Double baukosten = streetBaseDataContainer.getCostBenefitAnalysis().getCost().overallCosts();

        return nkvOhneKR_induz(modifications, a.get(), b.get(), baukosten, streetBaseDataContainer.getCostBenefitAnalysis().getOverallBenefit().overall() );
    }


    private static Optional<Amounts> amountsFromStreetBaseData(StreetBaseDataContainer streetBaseDataContainer) {

        PhysicalEffectDataContainer.Effect tt = streetBaseDataContainer.getPhysicalEffect().getTravelTimes();

        PhysicalEffectDataContainer.Effect vkm = streetBaseDataContainer.getPhysicalEffect().getVehicleKilometers();

        VehicleEmissions vehicleEmissions = streetBaseDataContainer.getPhysicalEffect().getEmissionsDataContainer().emissions().get(Emission.CO2 );

        if (tt == null || vkm == null || vehicleEmissions == null) {
            return Optional.empty();
        }

//        new Amounts( 1., 1., 1., 1., 1., 1., 1., 1. );
        // uncomment to see argument names

        final Amounts amounts = new Amounts(
                        vkm.overall(), vkm.induced().orElse(0.), vkm.shifted(), // pkwkm
                        tt.overall(), tt.induced().orElse(0.), tt.shifted(), // pers_h
                        vehicleEmissions.pkw(), vehicleEmissions.kfz() // co2
        );
        final Optional<Amounts> optional = Optional.of( amounts );
        if ( optional.isEmpty() ) {
            log.warn("here");
            throw new RuntimeException("stop");
        }
        return optional;
    }

    private static Optional<Benefits> benefitsFromStreetBaseData(StreetBaseDataContainer streetBaseDataContainer) {
        // @formatter:off
        return Optional.ofNullable(streetBaseDataContainer).map(StreetBaseDataContainer::getCostBenefitAnalysis)
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

    public static Double calculateB_CO2( Modifications modifications, StreetBaseDataContainer streetBaseDataContainer ){
        log.warn("modifications=" + modifications);
        Optional<Amounts> a = amountsFromStreetBaseData(streetBaseDataContainer);
        Optional<Benefits> b = benefitsFromStreetBaseData( streetBaseDataContainer );

        if (a.isEmpty() || b.isEmpty() ) {
            return null;
        }
        return ComputationKN.b_co2( modifications, a.get(), b.get() );
    }
}
