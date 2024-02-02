package org.tub.vsp.data.container.analysis;

import org.tub.vsp.computation.Modifications;
import org.tub.vsp.computation.NkvCalculator;
import org.tub.vsp.data.container.base.StreetBaseDataContainer;

import java.util.LinkedHashMap;
import java.util.SequencedMap;

public class StreetAnalysisDataContainer {
    private final StreetBaseDataContainer streetBaseDataContainer;
    private final SequencedMap<String, Double> nkvByChange = new LinkedHashMap<>();

    public StreetAnalysisDataContainer(StreetBaseDataContainer streetBaseDataContainer) {
        this.streetBaseDataContainer = streetBaseDataContainer;
        this.addComputations(streetBaseDataContainer);
    }

    private StreetAnalysisDataContainer addNkvByChange(String change, Double nkv) {
        nkvByChange.put(change, nkv);
        return this;
    }

    private void addComputations(StreetBaseDataContainer streetBaseDataContainer) {
        this.addNkvByChange("noChange", NkvCalculator.calculateNkv(Modifications.NO_CHANGE,
                    streetBaseDataContainer))
            .addNkvByChange("co2", NkvCalculator.calculateNkv(Modifications.CO2_PRICE,
                    streetBaseDataContainer))
            .addNkvByChange("induz", NkvCalculator.calculateNkv(Modifications.createInducedWithMehrFzkm(0),
                    streetBaseDataContainer))
            .addNkvByChange("induzCo2", NkvCalculator.calculateNkv(Modifications.createInducedAndCo2WithMehrFzkm(0),
                    streetBaseDataContainer));
    }

    public StreetBaseDataContainer getStreetBaseDataContainer() {
        return streetBaseDataContainer;
    }

    public SequencedMap<String, Double> getNkvByChange() {
        return nkvByChange;
    }
}
