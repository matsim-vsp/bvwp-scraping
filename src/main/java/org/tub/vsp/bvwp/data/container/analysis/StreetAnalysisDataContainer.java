package org.tub.vsp.bvwp.data.container.analysis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.computation.ComputationKN;
import org.tub.vsp.bvwp.computation.Modifications;
import org.tub.vsp.bvwp.computation.NkvCalculator;
import org.tub.vsp.bvwp.data.Headers;
import org.tub.vsp.bvwp.data.container.base.street.StreetBaseDataContainer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;

import static org.tub.vsp.bvwp.computation.Modifications.co2Price680;

public class StreetAnalysisDataContainer {
    Logger logger = LogManager.getLogger(StreetAnalysisDataContainer.class);
    private final StreetBaseDataContainer streetBaseDataContainer;
    private final double elasticity;
    private final double constructionCostFactor;
    private final SequencedMap<String, Double> entries = new LinkedHashMap<>();
    private final List<String> remarks = new ArrayList<>();

    public StreetAnalysisDataContainer( StreetBaseDataContainer streetBaseDataContainer, double elasticity, double constructionCostFactor ) {
        this.streetBaseDataContainer = streetBaseDataContainer;
        this.elasticity = elasticity;
        this.constructionCostFactor = constructionCostFactor;
        logger.info(this.streetBaseDataContainer.getUrl());
        this.addComputations();
    }

    private void addComputations() {
//        double fzkm_induz = streetBaseDataContainer.getPhysicalEffect().getVehicleKilometers().induced();
        //        double mehrFzkmFromElasticity = 4. * fzkm_induz;
//        if (mehrFzkmFromElasticity == 0.) {
//            double fzkm = streetBaseDataContainer.getPhysicalEffect().getVehicleKilometers().overall();
//            mehrFzkmFromElasticity = 4. * fzkm;
//        }
        // yyyyyy die obige Rechnung muss schlussendlich genauer sein ... zusätzliche Spurkm / 60000 * 0.6 *
        // Jahresfahrleistung_AB
        // aber bräuchte dafür die Anzahl neuer Fahrspuren.  Und dafür wohl die Länge und die Projektkategorie
        // (woraus sich die Anzahl Fahrspuren ergibt)

        double additionalLaneKm = streetBaseDataContainer.getProjectInformation().getLength() * 2;
        // (assumption 2 more lanes)

        switch (streetBaseDataContainer.getProjectInformation().getBautyp()) {
            case NB4:
                additionalLaneKm *= 2;
                break;
            case NB6:
                additionalLaneKm *= 3;
                break;
            case NB4_EW4:
                additionalLaneKm *= 1.5;
                break;
            case NB6_EW6:
                break;
            case EW4:
                break;
            case EW6:
                break;
            case EW8:
                break;
            case EW6_EW8:
                break;
//		    case EW8_EW9:
//			    break;
            case KNOTENPUNKT:
                break;
            case KNOTENPUNKT_EW4:
                break;
            case KNOTENPUNKT_EW6:
                break;
            case KNOTENPUNKT_NB4:
                break;
            case BLANK:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + streetBaseDataContainer.getProjectInformation()
                                                                                              .getBautyp());
        }

        final double INFLATION_Factor2022to2012 = 0.917; // Zinse Wert von 2020 auf BVWP Zeitpunkt 2012 ab.

        if ( additionalLaneKm==0. ) {
            additionalLaneKm = 1.; // Knotenpunkt-Projekte; so that it becomes visible on logplot. kai, mar'24
        }

        entries.put( Headers.ADDTL_LANE_KM, additionalLaneKm );

        double mehrFzkmFromElasticity = additionalLaneKm / ComputationKN.LANE_KM_AB * elasticity * ComputationKN.FZKM_AB;

        final double additionalFzkm = mehrFzkmFromElasticity - streetBaseDataContainer.getPhysicalEffect().getVehicleKilometers().overall();
//        logger.info("additionalFzkm=" + additionalFzkm);
//        entries.put("cost/km", streetBaseDataContainer.getCostBenefitAnalysis().getCost().overallCosts() /
//        streetBaseDataContainer.getProjectInformation().getLength() );
//        entries.put("rz", streetBaseDataContainer.getPhysicalEffect().getTravelTimes().overall());
//        entries.put("rz/km", streetBaseDataContainer.getPhysicalEffect().getTravelTimes().overall() /
//        streetBaseDataContainer.getProjectInformation().getLength() );
        entries.put(Headers.B_PER_KM, streetBaseDataContainer.getCostBenefitAnalysis().getOverallBenefit().overall() / streetBaseDataContainer.getProjectInformation().getLength());
        entries.put(Headers.NKV_ORIG, NkvCalculator.calculateNkv(Modifications.NO_CHANGE, streetBaseDataContainer));
        entries.put(Headers.NKV_CO2, NkvCalculator.calculateNkv(Modifications.CO2_PRICE_680, streetBaseDataContainer));
        entries.put(Headers.NKV_CO2_680_EN, NkvCalculator.calculateNkv(Modifications.CO2_PRICE_680, streetBaseDataContainer));
        entries.put(Headers.NKV_CO2_2000_EN, NkvCalculator.calculateNkv(Modifications.createCo2withoutInduzed(2000 * INFLATION_Factor2022to2012), streetBaseDataContainer));
        entries.put(Headers.NKV_INDUZ, NkvCalculator.calculateNkv(Modifications.createInducedWithAdditionalFzkm(additionalFzkm), streetBaseDataContainer));
        entries.put(Headers.NKV_INDUZ_CO2215_CONSTRUCTION,
                        NkvCalculator.calculateNkv( new Modifications( 215, additionalFzkm, constructionCostFactor ), streetBaseDataContainer ) );
        entries.put(Headers.NKV_INDUZ_CO2_CONSTRUCTION, NkvCalculator.calculateNkv( new Modifications( co2Price680, additionalFzkm, constructionCostFactor ), streetBaseDataContainer ) );
        entries.put(Headers.ADDTL_PKWKM_NEU, mehrFzkmFromElasticity );
        entries.put(Headers.CO2_COST_ORIG, Math.max( 1., NkvCalculator.calculateCost_CO2( Modifications.NO_CHANGE, streetBaseDataContainer ) ) );
        entries.put(Headers.CO2_COST_NEU, Math.max( 1., NkvCalculator.calculateCost_CO2(Modifications.createInducedAndCo2WithMehrFzkm(additionalFzkm ), streetBaseDataContainer ) ) );
        // ("max(1,...)" so that they become visible on logplot.  find other solution!
        entries.put(Headers.VERKEHRSBELASTUNG_PLANFALL, streetBaseDataContainer.getProjectInformation().getVerkehrsbelastungPlanfall() );

        if (streetBaseDataContainer.getProjectInformation().getProjectNumber().contains("A1-G50-NI")) {
            this.remarks.add("Eher geringer Benefit pro km ... erzeugt dann ueber die El pro km relativ viel Verkehr " +
                    "der per co2 stark negativ bewertet wird.");
        }

    }

    public StreetBaseDataContainer getStreetBaseDataContainer() {
        return streetBaseDataContainer;
    }

    public SequencedMap<String, Double> getColumns() {
        return entries;
    }

    public List<String> getRemarks() {
        return remarks;
    }
}
