package org.tub.vsp.data.container.analysis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.computation.ComputationKN;
import org.tub.vsp.computation.Modifications;
import org.tub.vsp.computation.NkvCalculator;
import org.tub.vsp.data.container.base.StreetBaseDataContainer;

import java.util.LinkedHashMap;
import java.util.SequencedMap;

public class StreetAnalysisDataContainer {
    Logger logger = LogManager.getLogger(StreetAnalysisDataContainer.class );
    private final StreetBaseDataContainer streetBaseDataContainer;
    private final SequencedMap<String, Double> nkvByChange = new LinkedHashMap<>();

    public StreetAnalysisDataContainer(StreetBaseDataContainer streetBaseDataContainer) {
        this.streetBaseDataContainer = streetBaseDataContainer;
        logger.warn( this.streetBaseDataContainer.getUrl().toString() );
        this.addComputations(streetBaseDataContainer);
    }

    private StreetAnalysisDataContainer addNkvByChange(String change, Double nkv) {
        nkvByChange.put(change, nkv);
        return this;
    }

    private void addComputations(StreetBaseDataContainer streetBaseDataContainer) {
        double fzkm_induz = streetBaseDataContainer.getPhysicalEffect().getVehicleKilometers().induced();
        double mehrFzkm = 4. * fzkm_induz;
        if ( mehrFzkm==0. ) {
            double fzkm = streetBaseDataContainer.getPhysicalEffect().getVehicleKilometers().overall();
            mehrFzkm = 4. * fzkm;
        }
        // yyyyyy die obige Rechnung muss schlussendlich genauer sein ... zusätzliche Spurkm / 60000 * 0.6 * Jahresfahrleistung_AB
        // aber bräuchte dafür die Anzahl neuer Fahrspuren.  Und dafür wohl die Länge und die Projektkategorie (woraus sich die Anzahl Fahrspuren ergibt)
        double test = 1. / ComputationKN.AB_length * 0.6 * ComputationKN.FZKM_AB;

        this.addNkvByChange("noChange", NkvCalculator.calculateNkv(Modifications.NO_CHANGE, streetBaseDataContainer))
            .addNkvByChange("co2", NkvCalculator.calculateNkv(Modifications.CO2_PRICE, streetBaseDataContainer))
            .addNkvByChange("induz", NkvCalculator.calculateNkv(Modifications.createInducedWithMehrFzkm( mehrFzkm ), streetBaseDataContainer))
            .addNkvByChange("induzCo2", NkvCalculator.calculateNkv(Modifications.createInducedAndCo2WithMehrFzkm( mehrFzkm ), streetBaseDataContainer));
    }

    public StreetBaseDataContainer getStreetBaseDataContainer() {
        return streetBaseDataContainer;
    }

    public SequencedMap<String, Double> getNkvByChange() {
        return nkvByChange;
    }
}
