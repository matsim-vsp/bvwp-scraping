package org.tub.vsp.bvwp.data.container.analysis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.computation.ComputationKN;
import org.tub.vsp.bvwp.computation.NkvCalculator;
import org.tub.vsp.bvwp.data.Headers;
import org.tub.vsp.bvwp.data.container.base.StreetBaseDataContainer;
import org.tub.vsp.bvwp.computation.Modifications;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;

public class StreetAnalysisDataContainer {
    Logger logger = LogManager.getLogger(StreetAnalysisDataContainer.class );
    private final StreetBaseDataContainer streetBaseDataContainer;
    private final SequencedMap<String, Double> entries = new LinkedHashMap<>();
    private final List<String> remarks = new ArrayList<>();

    public StreetAnalysisDataContainer(StreetBaseDataContainer streetBaseDataContainer) {
        this.streetBaseDataContainer = streetBaseDataContainer;
        logger.info( this.streetBaseDataContainer.getUrl());
        this.addComputations(streetBaseDataContainer);
    }

    private void addComputations(StreetBaseDataContainer streetBaseDataContainer) {
//        double fzkm_induz = streetBaseDataContainer.getPhysicalEffect().getVehicleKilometers().induced();
        //        double mehrFzkm = 4. * fzkm_induz;
//        if (mehrFzkm == 0.) {
//            double fzkm = streetBaseDataContainer.getPhysicalEffect().getVehicleKilometers().overall();
//            mehrFzkm = 4. * fzkm;
//        }
        // yyyyyy die obige Rechnung muss schlussendlich genauer sein ... zusätzliche Spurkm / 60000 * 0.6 *
        // Jahresfahrleistung_AB
        // aber bräuchte dafür die Anzahl neuer Fahrspuren.  Und dafür wohl die Länge und die Projektkategorie
        // (woraus sich die Anzahl Fahrspuren ergibt)

        double mehrFzkm = streetBaseDataContainer.getProjectInformation().getLength() * 2 / ComputationKN.AB_length * 0.6 * ComputationKN.FZKM_AB;
        // (assumption 2 more lanes)

        if ( streetBaseDataContainer.getProjectInformation().getBautyp().contains( "4-streifig" ) ) {
            mehrFzkm *= 2;
            // (4 more lanes)
        }

        final double additionalFzkm = mehrFzkm - streetBaseDataContainer.getPhysicalEffect().getVehicleKilometers().overall();
        logger.info( "additionalFzkm=" + additionalFzkm);
//        entries.put("cost/km", streetBaseDataContainer.getCostBenefitAnalysis().getCost().overallCosts() / streetBaseDataContainer.getProjectInformation().getLength() );
//        entries.put("rz", streetBaseDataContainer.getPhysicalEffect().getTravelTimes().overall());
//        entries.put("rz/km", streetBaseDataContainer.getPhysicalEffect().getTravelTimes().overall() / streetBaseDataContainer.getProjectInformation().getLength() );
        entries.put( Headers.B_PER_KM, streetBaseDataContainer.getCostBenefitAnalysis().getOverallBenefit().overall() / streetBaseDataContainer.getProjectInformation().getLength() );
        entries.put( Headers.NKV_NO_CHANGE, NkvCalculator.calculateNkv(Modifications.NO_CHANGE, streetBaseDataContainer ) );
        entries.put( Headers.NKV_CO2, NkvCalculator.calculateNkv(Modifications.CO2_PRICE, streetBaseDataContainer ) );
        entries.put( Headers.NKV_INDUZ, NkvCalculator.calculateNkv(Modifications.createInducedWithAdditionalFzkm( additionalFzkm ), streetBaseDataContainer ) );
        entries.put( Headers.NKV_INDUZ_CO2, NkvCalculator.calculateNkv(Modifications.createInducedAndCo2WithMehrFzkm( additionalFzkm ), streetBaseDataContainer ) );
        entries.put( Headers.PKWKM_INDUZ_NEU, mehrFzkm );
        entries.put( Headers.B_CO2_NEU, NkvCalculator.calculateB_CO2( Modifications.createInducedAndCo2WithMehrFzkm( additionalFzkm ), streetBaseDataContainer ) );

        if ( streetBaseDataContainer.getProjectInformation().getProjectNumber().contains( "A1-G50-NI" ) ) {
            this.remarks.add("Eher geringer Benefit pro km ... erzeugt dann ueber die El pro km relativ viel Verkehr der per co2 stark negativ bewertet wird.");
        }

    }

    public StreetBaseDataContainer getStreetBaseDataContainer() {
        return streetBaseDataContainer;
    }

    public SequencedMap<String, Double> getColumns() {
        return entries;
    }
    public List<String> getRemarks(){
        return remarks;
    }
}
