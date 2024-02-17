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
        log.warn("modifications=" + modifications);
        Optional<Amounts> a = amountsFromStreetBaseData(streetBaseDataContainer);
        Optional<Benefits> b = benefitsFromStreetBaseData(streetBaseDataContainer);

        if (a.isEmpty() || b.isEmpty()) {
            return null;
        }

        Double baukosten = streetBaseDataContainer.getCostBenefitAnalysis().getCost().overallCosts();

        return nkvOhneKR_induz(modifications, a.get(), b.get(), baukosten, streetBaseDataContainer.getCostBenefitAnalysis().getOverallBenefit().overall() );
    }


    private static Optional<Amounts> amountsFromStreetBaseData(StreetBaseDataContainer streetBaseDataContainer) {
        PhysicalEffectDataContainer.Effect tt = streetBaseDataContainer.getPhysicalEffect()
                                                                       .getTravelTimes();
        PhysicalEffectDataContainer.Effect vkm = streetBaseDataContainer.getPhysicalEffect()
                                                                        .getVehicleKilometers();
        final Map<Emission, VehicleEmissions> emissions = streetBaseDataContainer.getPhysicalEffect()
                                                                                 .getEmissionsDataContainer()
                                                                                 .emissions();

        VehicleEmissions vehicleEmissions = emissions.get(Emission.CO2);

        if (tt == null || vkm == null || vehicleEmissions == null) {
            return Optional.empty();
        }

//        new Amounts( 1., 1., 1., 1., 1., 1., 1., 1. );
        // uncomment to see argument names

        return Optional.of(new Amounts(
                vkm.overall(), vkm.induced(), vkm.shifted(), // pkwkm
                tt.overall(), tt.induced(), tt.shifted(), // pers_h
                vehicleEmissions.pkw(), vehicleEmissions.kfz() // co2 yyyyyy the first one would have to be only from
                // pkw, but I think that this is currently not scraped
        ));
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

    public static Double calculateCO2( Modifications modifications, StreetBaseDataContainer streetBaseDataContainer ){
        log.warn("modifications=" + modifications);
        Optional<Amounts> a = amountsFromStreetBaseData(streetBaseDataContainer);
        Optional<Benefits> b = benefitsFromStreetBaseData( streetBaseDataContainer );

        if (a.isEmpty() || b.isEmpty() ) {
            return null;
        }
        return ComputationKN.co2( modifications, a.get(), b.get() );
    }
}
