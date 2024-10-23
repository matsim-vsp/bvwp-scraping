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

import static org.tub.vsp.bvwp.computation.Modifications.*;

public class StreetAnalysisDataContainer {
    Logger logger = LogManager.getLogger(StreetAnalysisDataContainer.class);
    private final StreetBaseDataContainer streetBaseData;
    private double constructionCostFactor;
    private final SequencedMap<String, Double> entries = new LinkedHashMap<>();
    private final List<String> remarks = new ArrayList<>();
    private final double constructionCostTud;

    public StreetAnalysisDataContainer(StreetBaseDataContainer streetBaseDataContainer, double investmentCostNew ) {
        this.streetBaseData = streetBaseDataContainer;
        this.constructionCostTud = investmentCostNew;
        constructionCostFactor = investmentCostNew / streetBaseData.getCostBenefitAnalysis().getCost().overallCosts();
        if ( constructionCostFactor < 0. ) {
            constructionCostFactor = 1.;
        }
        this.addComputations();

        logger.info(this.streetBaseData.getUrl());
    }

    private void addComputations() {
//        double fzkm_induz = streetBaseDataContainer.getPhysicalEffect().getVehicleKilometers().induced();
        //        double addtlFzkmFromElasticity03 = 4. * fzkm_induz;
//        if (addtlFzkmFromElasticity03 == 0.) {
//            double fzkm = streetBaseDataContainer.getPhysicalEffect().getVehicleKilometers().overall();
//            addtlFzkmFromElasticity03 = 4. * fzkm;
//        }
        // yyyyyy die obige Rechnung muss schlussendlich genauer sein ... zusätzliche Spurkm / 60000 * 0.6 *
        // Jahresfahrleistung_AB
        // aber bräuchte dafür die Anzahl neuer Fahrspuren.  Und dafür wohl die Länge und die Projektkategorie
        // (woraus sich die Anzahl Fahrspuren ergibt)

        double additionalLaneKm = streetBaseData.getProjectInformation().getLength() * 2;
        // (assumption 2 more lanes)

	    switch( streetBaseData.getProjectInformation().getBautyp() ){
		    case NB4 -> additionalLaneKm *= 2;
		    case NB6 -> additionalLaneKm *= 3;
		    case NB4_EW4 -> additionalLaneKm *= 1.5;
	    }

        entries.put(Headers.VERKEHRSBELASTUNG_PLANFALL, streetBaseData.getProjectInformation().getVerkehrsbelastungPlanfall() );

        if ( additionalLaneKm==0. ) {
            additionalLaneKm = 1.; // Knotenpunkt-Projekte; so that it becomes visible on logplot. kai, mar'24
        }

        entries.put( Headers.ADDTL_LANE_KM, additionalLaneKm );

        double addtlFzkmFromElasticity03 = additionalLaneKm / ComputationKN.LANE_KM_AB * 0.3 * ComputationKN.FZKM_AB;
        final double addtlFzkmBeyondPrinsEl03 = addtlFzkmFromElasticity03 - streetBaseData.getPhysicalEffect().getVehicleKilometers().overall();
        // (this is formulated such that addtlFzkmBeyondPrinsEl03=0 means the original additional Fzkm)

//        logger.info("addtlFzkmBeyondPrinsEl03=" + addtlFzkmBeyondPrinsEl03);
//        entries.put("cost/km", streetBaseDataContainer.getCostBenefitAnalysis().getCost().overallCosts() /
//        streetBaseDataContainer.getProjectInformation().getLength() );
//        entries.put("rz", streetBaseDataContainer.getPhysicalEffect().getTravelTimes().overall());
//        entries.put("rz/km", streetBaseDataContainer.getPhysicalEffect().getTravelTimes().overall() /
//        streetBaseDataContainer.getProjectInformation().getLength() );
        entries.put(Headers.B_PER_KM, streetBaseData.getCostBenefitAnalysis().getOverallBenefit().overall() / streetBaseData.getProjectInformation().getLength() );

//        if ( !streetBaseData.getProjectInformation().getProjectNumber().contains( "A20" ) ) {
//            return;
//        }

//        final double discountCorrFact = 1.14;
        // ptpr0 = stpr 1%.  Diskontierung 1% statt 1.7% über Jahre 0 bis 42 bedeutet 1.14 x so viel Nutzen.  Vgl. prins.mw.  Könnte man ordentlicher machen.

        // Nach neuerer Überlegung lassen wir alles andere bei 1.7% , und schieben nur co2 um diesen Faktor nach oben.

        final double emobCorrFact = 0.1;

        NkvCalculator nkvCalculator = new NkvCalculator( streetBaseData );

        entries.put(Headers.NKV_ORIG, nkvCalculator.calculateNkv( NO_CHANGE ) );
        entries.put(Headers.NKV_CO2, nkvCalculator.calculateNkv( new Modifications( co2Price796, 0., 1, 1, 1. ) ) );
        entries.put(Headers.NKV_CO2_700_EN, nkvCalculator.calculateNkv( new Modifications( co2Price796, 0., 1, 1, 1. ) ) );
        entries.put(Headers.NKV_CO2_2000_EN, nkvCalculator.calculateNkv( new Modifications( co2Price2000, 0, 1, 1, 1. ) ) );
        entries.put(Headers.NKV_EL03, nkvCalculator.calculateNkv( new Modifications( co2PriceBVWP, addtlFzkmBeyondPrinsEl03, 1, 1, 1. ) ) );
//        entries.put(Headers.NKV_EL03_CARBON215_INVCOSTTUD, nkvCalculator.calculateNkv( new Modifications( co2Price215, addtlFzkmBeyondPrinsEl03, constructionCostFactor ) ) );
//        entries.put(Headers.NKV_EL03_CARBON700ptpr0_INVCOSTTUD, nkvCalculator.calculateNkv( new Modifications( discountCorr*co2Price700, addtlFzkmBeyondPrinsEl03, constructionCostFactor ) ) );
//        entries.put(Headers.NKV_EL03_CARBON700ptpr0, nkvCalculator.calculateNkv( new Modifications( discountCorr*co2Price700, addtlFzkmBeyondPrinsEl03, 1. ) ) );
//        entries.put(Headers.NKV_EL03_CARBON215_INVCOSTTUD, NkvCalculator.calculateNkv( new Modifications( co2Price215, addtlFzkmBeyondPrinsEl03, constructionCostFactor, 1. ), streetBaseData ) );
//        entries.put(Headers.NKV_EL03_CARBON700tpr0_INVCOSTTUD, NkvCalculator.calculateNkv( new Modifications( co2Price700, addtlFzkmBeyondPrinsEl03, constructionCostFactor, 1.75 ), streetBaseData ) );
//        entries.put(Headers.NKV_EL03_CARBON700tpr0, NkvCalculator.calculateNkv( new Modifications( co2Price700, addtlFzkmBeyondPrinsEl03, 1., 1.75 ), streetBaseData ) );
//        entries.put(Headers.NKV_EL03_CO2_INVCOST50, NkvCalculator.calculateNkv( new Modifications( co2Price700, addtlFzkmBeyondPrinsEl03, constructionCostFactor ), streetBaseData ) );

        //Für EWGT Paper KMT
        //Nur Investitionskosten ändern sich
        entries.put(Headers.NKV_INVCOSTTUD_EN, NkvCalculator.calculateNkv( new Modifications( co2PriceBVWP, 0, constructionCostFactor, 1., 1. ), streetBaseData ) );
        entries.put(Headers.NKV_INVCOST150_EN, NkvCalculator.calculateNkv( new Modifications( co2PriceBVWP, 0, 1.5, 1. , 1.), streetBaseData ) );
        entries.put(Headers.NKV_INVCOST200_EN, NkvCalculator.calculateNkv( new Modifications( co2PriceBVWP, 0, 2, 1. , 1.), streetBaseData ) );

        //Investitionskosten und CO2 Preis ändern sich
        entries.put(Headers.NKV_CO2_700_INVCOSTTUD_EN, NkvCalculator.calculateNkv( new Modifications( co2Price796, 0, constructionCostFactor, 1. , 1.), streetBaseData ) );
        entries.put(Headers.NKV_CO2_700_INVCOST150_EN, NkvCalculator.calculateNkv( new Modifications( co2Price796, 0, 1.5, 1. , 1.), streetBaseData ) );
        entries.put(Headers.NKV_CO2_700_INVCOST200_EN, NkvCalculator.calculateNkv( new Modifications( co2Price796, 0, 2, 1. , 1.), streetBaseData ) );
        entries.put(Headers.NKV_CO2_2000_INVCOSTTUD_EN, NkvCalculator.calculateNkv( new Modifications( co2Price2000, 0, constructionCostFactor, 1. , 1.), streetBaseData ) );
        entries.put(Headers.NKV_CO2_2000_INVCOST150_EN, NkvCalculator.calculateNkv( new Modifications( co2Price2000, 0, 1.5, 1. , 1.), streetBaseData ) );
        entries.put(Headers.NKV_CO2_2000_INVCOST200_EN, NkvCalculator.calculateNkv( new Modifications( co2Price2000, 0, 2, 1. , 1.), streetBaseData ) );

//
        entries.put(Headers.ADDTL_PKWKM_EL03, addtlFzkmFromElasticity03 );
//        entries.put(Headers.CO2_COST_ORIG, Math.max( 1., nkvCalculator.calculateCost_CO2( NO_CHANGE, streetBaseData ) ) );
//        entries.put(Headers.CO2_COST_EL03, Math.max( 1., nkvCalculator.calculateCo2_t( new Modifications( co2PriceBVWP, addtlFzkmBeyondPrinsEl03, 1 ) ) ) );
//         ("max(1,...)" so that they become visible on logplot.  find other solution!
        entries.put(Headers.INVCOST_TUD, this.constructionCostTud );

        double AVERAGE_SPEED_OF_ADDITIONAL_TRAVEL = 50; // km/h
        double addtlFzkmFromTtime = - streetBaseData.getPhysicalEffect().getVehicleHours().overall() * AVERAGE_SPEED_OF_ADDITIONAL_TRAVEL;
        entries.put( Headers.ADDTL_PKWKM_FROM_TTIME, addtlFzkmFromTtime );

        // Beiträge einzeln:
        entries.put( Headers.NKV_INVCOSTTUD, nkvCalculator.calculateNkv( new Modifications( co2PriceBVWP, 0., constructionCostFactor, 1, 1. ) ) );
        entries.put( Headers.NKV_ELTTIME, nkvCalculator.calculateNkv( new Modifications( co2PriceBVWP, addtlFzkmFromTtime, 1, 1, 1. ) ) );
        entries.put(Headers.NKV_CARBON700, nkvCalculator.calculateNkv( new Modifications( co2Price796, 0., 1, 1, 1. ) ) );

        // Induz. Strassenmehrverkehr und CO2-Preis kombiniert:
        entries.put( Headers.NKV_ELTTIME_CARBON700, nkvCalculator.calculateNkv( new Modifications( co2Price796, addtlFzkmFromTtime, 1., 1., 1. ) ) );

        // ... + Investitionskosten:
        entries.put( Headers.NKV_ELTTIME_CARBON700_INVCOSTTUD, nkvCalculator.calculateNkv( new Modifications( co2Price796, addtlFzkmFromTtime, constructionCostFactor, 1., 1. ) ) );

        // ... + eMob:
        entries.put( Headers.NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD, nkvCalculator.calculateNkv( new Modifications( co2Price796, addtlFzkmFromTtime, constructionCostFactor, 1, emobCorrFact ) ) );

        entries.put( Headers.NKV_ELTTIME_CARBON215_INVCOSTTUD, nkvCalculator.calculateNkv( new Modifications( co2Price215, addtlFzkmFromTtime, constructionCostFactor, 1, 1. ) ) );
        entries.put( Headers.NKV_ELTTIME_CARBON2000_EMOB_INVCOSTTUD, nkvCalculator.calculateNkv( new Modifications( co2Price2000, addtlFzkmFromTtime, constructionCostFactor, 1, emobCorrFact ) ) );

        entries.put(Headers.CO2_ORIG, nkvCalculator.calculateCo2_t( new Modifications( co2PriceBVWP, 0., 1, 1, 1. ) ) );

        entries.put(Headers.CO2_ELTTIME, nkvCalculator.calculateCo2_t( new Modifications( co2PriceBVWP, addtlFzkmFromTtime, 1, 1, 1. ) ) );

        entries.put(Headers.CO2_ELTTIME_EMOB, nkvCalculator.calculateCo2_t( new Modifications( co2PriceBVWP, addtlFzkmFromTtime, 1, 1, emobCorrFact ) ) );

        entries.put( Headers.NKV_ELTTIME_CARBON2000_INVCOSTTUD, nkvCalculator.calculateNkv( new Modifications( co2Price2000, addtlFzkmFromTtime, constructionCostFactor, 1, 1. ) ) );

        if ( streetBaseData.getProjectInformation().getProjectNumber().contains("A1-G50-NI" )) {
            this.remarks.add("Eher geringer Benefit pro km ... erzeugt dann ueber die El pro km relativ viel Verkehr " +
                    "der per co2 stark negativ bewertet wird.");
        }
//        logger.info("exit");
//        System.exit(-1);

    }

    public StreetBaseDataContainer getStreetBaseDataContainer() {
        return streetBaseData;
    }

    public SequencedMap<String, Double> getColumns() {
        return entries;
    }

    public List<String> getRemarks() {
        return remarks;
    }
}
