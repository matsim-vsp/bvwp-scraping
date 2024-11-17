package org.tub.vsp.bvwp.computation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.container.base.street.StreetBaseDataContainer;
import org.tub.vsp.bvwp.data.container.base.street.StreetPhysicalEffectDataContainer;
import org.tub.vsp.bvwp.data.type.Emission;
import org.tub.vsp.bvwp.data.type.VehicleEmissions;

import java.util.Optional;

import static org.tub.vsp.bvwp.computation.ComputationKN.*;
import static org.tub.vsp.bvwp.computation.ConsoleColors.*;

public class NkvCalculator {

    private static final Logger log = LogManager.getLogger(NkvCalculator.class);
    private final StreetBaseDataContainer streetBaseDataContainer;

    // moving towards replacing the stateless static functions by a modifiable instance approach:
    public NkvCalculator( StreetBaseDataContainer streetBaseDataContainer ) {
        this.streetBaseDataContainer = streetBaseDataContainer;
    }
    public Double calculateNkv( Modifications modifications ) {
        return calculateNkv( modifications, this.streetBaseDataContainer );
    }
    public Double calculateCo2_t( Modifications modifications ) {
        return calculateCo2_t( modifications, this.streetBaseDataContainer );
    }

    // old static methods beyond:
    private static Double calculateNkv(Modifications modifications, StreetBaseDataContainer streetBaseDataContainer) {
//        log.warn("modifications=" + modifications);
        Optional<Amounts> a = amountsFromStreetBaseData(streetBaseDataContainer);
        Optional<BenefitsAndInvestmentCosts> b = benefitsFromStreetBaseData(streetBaseDataContainer );

        if (a.isEmpty()) {
            log.warn("amounts container is empty for project=" + streetBaseDataContainer.getUrl());
//            return null;
            throw new RuntimeException();
        }
        if (b.isEmpty()) {
            log.warn("benefits container is empty for project=" + streetBaseDataContainer.getUrl());
//            return null;
            throw new RuntimeException();
        }

        return nkvOhneKR_induz(modifications, a.get(), b.get(), b.get().all );
    }


    private static Optional<Amounts> amountsFromStreetBaseData(StreetBaseDataContainer streetBaseDataContainer) {

        StreetPhysicalEffectDataContainer.PEffect tt = streetBaseDataContainer.getPhysicalEffect().getTravelTimes();

        StreetPhysicalEffectDataContainer.PEffect pVehicleKilometers = streetBaseDataContainer.getPhysicalEffect().getPVehicleKilometers();

        Double lVehicleKilometers = streetBaseDataContainer.getPhysicalEffect().getLVehicleKilometers();

        VehicleEmissions vehicleEmissions = streetBaseDataContainer.getPhysicalEffect().getEmissionsDataContainer().emissions().get(Emission.CO2);

        if (tt == null || pVehicleKilometers == null || vehicleEmissions == null) {
            return Optional.empty();
        }

//        new Amounts( 1., 1., 1., 1., 1., 1., 1., 1. );
        // uncomment to see argument names

        final Amounts amounts = new Amounts(
                pVehicleKilometers.overall(), Optional.ofNullable(pVehicleKilometers.induced()).orElse(0.), pVehicleKilometers.shifted(), // pkwkm
                tt.overall(), Optional.ofNullable(tt.induced()).orElse(0.), tt.shifted(), // pers_h
                        lVehicleKilometers, // lkwkm
                        vehicleEmissions.pkw(), vehicleEmissions.lkw(), vehicleEmissions.kfz() // co2
        );
        final Optional<Amounts> optional = Optional.of(amounts);
        if (optional.isEmpty()) {
            log.warn("here");
            throw new RuntimeException("stop");
        }
        return optional;
    }

    private static Optional<BenefitsAndInvestmentCosts> benefitsFromStreetBaseData( StreetBaseDataContainer streetBaseDataContainer ) {
        // @formatter:off
        return Optional.ofNullable(streetBaseDataContainer).map(StreetBaseDataContainer::getCostBenefitAnalysis)
                       .map(cb -> new BenefitsAndInvestmentCosts(
                                       cb.getNbOperations().overall(), // fzkm
                                       cb.getNrz().overall(), // rz
                                       cb.getNi().overall(), // impl
                                       cb.getNl().overall(), // co2_infra
                                       cb.getNa().get(Emission.CO2).overall(), // co2_betrieb
                                       cb.getOverallBenefit().overall(), // benefit
                                       cb.getCost().barwert()
                       ));
        // @formatter:on
    }

    @Deprecated // use instance approach
    public static Double calculateCo2_t( Modifications modifications, StreetBaseDataContainer streetBaseDataContainer ) {
        log.warn("modifications=" + modifications);
        Optional<Amounts> a = amountsFromStreetBaseData(streetBaseDataContainer);
        Optional<BenefitsAndInvestmentCosts> b = benefitsFromStreetBaseData(streetBaseDataContainer );

        if (a.isEmpty() || b.isEmpty()) {
            throw new RuntimeException( "co2 costs cannot be computed" );
        }
        final double result = -b_co2( modifications, a.get(), b.get() ) / 145.;

        log.warn( TEXT_RED + "project=" + streetBaseDataContainer.getProjectInformation().getProjectNumber() + "; result=" + result + TEXT_BLACK );

        return result;
    }
}
