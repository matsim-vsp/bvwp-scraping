package org.tub.vsp.computation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.tub.vsp.data.container.base.PhysicalEffectDataContainer;
import org.tub.vsp.data.container.base.StreetBaseDataContainer;
import org.tub.vsp.data.type.Emission;

import java.util.Map;
import java.util.Optional;

import static org.tub.vsp.computation.ComputationKN.*;

public class NkvCalculator {

    private static final Logger log = LogManager.getLogger(NkvCalculator.class);


    public static Double calculateNkv(Modifications modifications, StreetBaseDataContainer streetBaseDataContainer) {
        log.warn("modifications=" + modifications );
        Optional<Amounts> a = amountsFromStreetBaseData(streetBaseDataContainer );
        Optional<Benefits> b = benefitsFromStreetBaseData(streetBaseDataContainer );

        if (a.isEmpty() || b.isEmpty()) {
            return null;
        }

        Double baukosten = streetBaseDataContainer.getCostBenefitAnalysis()
                                                  .getCost()
                                                  .overallCosts();

        return nkv(modifications, a.get(), b.get(), baukosten);
    }


    private static Optional<Amounts> amountsFromStreetBaseData( StreetBaseDataContainer streetBaseDataContainer ) {
        PhysicalEffectDataContainer.Effect tt = streetBaseDataContainer.getPhysicalEffect()
                                                                       .getTravelTimes();
        PhysicalEffectDataContainer.Effect vkm = streetBaseDataContainer.getPhysicalEffect()
                                                                        .getVehicleKilometers();
        final Map<Emission, Double> emissions = streetBaseDataContainer.getPhysicalEffect().getEmissionsDataContainer().emissions();
        for( Map.Entry<Emission, Double> entry : emissions.entrySet() ){
            log.warn( entry.getKey() + " " + entry.getValue() );
        }
        Double co2_kfz = emissions.get( Emission.CO2 );
        if ( co2_kfz==null ) {
            co2_kfz = 0.;
        }

        if (tt == null || vkm == null) {
            return Optional.empty();
        }

//        new Amounts( 1., 1., 1., 1., 1., 1., 1., 1. );
        // uncomment to see argument names

        return Optional.of(new Amounts(
                        vkm.overall(), vkm.induced(), vkm.shifted(), // pkwkm
                        tt.overall(), tt.induced(), tt.shifted(), // pers_h
                         co2_kfz, co2_kfz // co2 yyyyyy the first one would have to be only from pkw, but I think that this is currently not scraped
                          ));
    }

    private static Optional<Benefits> benefitsFromStreetBaseData( StreetBaseDataContainer streetBaseDataContainer ) {
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

}
