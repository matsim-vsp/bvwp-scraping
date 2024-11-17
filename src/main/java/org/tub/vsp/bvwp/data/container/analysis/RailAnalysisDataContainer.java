package org.tub.vsp.bvwp.data.container.analysis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.computation.ComputationKN;
import org.tub.vsp.bvwp.computation.Modifications;
import org.tub.vsp.bvwp.computation.NkvCalculatorRail;
import org.tub.vsp.bvwp.data.Headers;
import org.tub.vsp.bvwp.data.HeadersKN;
import org.tub.vsp.bvwp.data.container.base.rail.RailBaseDataContainer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;

import static org.tub.vsp.bvwp.computation.Modifications.*;


public class RailAnalysisDataContainer {
    Logger logger = LogManager.getLogger(RailAnalysisDataContainer.class);

    private final RailBaseDataContainer baseDataContainer;
    private double constructionCostFactor = 1;
    private final SequencedMap<String, Double> entries = new LinkedHashMap<>();
    private final List<String> remarks = new ArrayList<>();
//    private final double constructionCostTud;
    public RailAnalysisDataContainer(RailBaseDataContainer baseDataContainer) {
        this.baseDataContainer = baseDataContainer;
        this.addComputations();
    }

    //add analysis stuff here...
    private void addComputations() {

        double additionalLaneKm = baseDataContainer.getProjectInformation().getLength() * 2;
        // (assumption 2 more lanes)

//        switch( baseDataContainer.getProjectInformation().getBautyp() ){
//            case NB4 -> additionalLaneKm *= 2;
//            case NB6 -> additionalLaneKm *= 3;
//            case NB4_EW4 -> additionalLaneKm *= 1.5;
//        }

//        entries.put(Headers.VERKEHRSBELASTUNG_PLANFALL, baseDataContainer.getProjectInformation().getVerkehrsbelastungPlanfall() );

        if ( additionalLaneKm==0. ) {
            additionalLaneKm = 1.; // Knotenpunkt-Projekte; so that it becomes visible on logplot. kai, mar'24
        }

        entries.put( Headers.ADDTL_LANE_KM, additionalLaneKm );

        double addtlFzkmFromElasticity03 = additionalLaneKm / ComputationKN.LANE_KM_AB * 0.3 * ComputationKN.FZKM_AB;
//        final double addtlFzkmBeyondPrinsEl03 = addtlFzkmFromElasticity03 - baseDataContainer.getPhysicalEffect().getVehicleKilometers().overall();
        // (this is formulated such that addtlFzkmBeyondPrinsEl03=0 means the original additional Fzkm)

        entries.put(Headers.B_PER_KM, baseDataContainer.getCostBenefitAnalysis().getOverallBenefit().overall() / baseDataContainer.getProjectInformation().getLength() );

        entries.put( HeadersKN.NKV_ORIG, NkvCalculatorRail.calculateNkv( NO_CHANGE, baseDataContainer ) );

//        entries.put(Headers.NKV_CO2, NkvCalculatorRail.calculateNkv(new Modifications( co2Price796, 0., 1, 1, 1. ), baseDataContainer ) );
        entries.put(Headers.NKV_CO2_700_EN, NkvCalculatorRail.calculateNkv(new Modifications( co2Price796, 0., 1, 1, 1. ), baseDataContainer ) );
        entries.put( HeadersKN.NKV_CARBON700, NkvCalculatorRail.calculateNkv(new Modifications( co2Price796, 0., 1, 1, 1. ), baseDataContainer ) );
        entries.put(Headers.NKV_CO2_2000_EN, NkvCalculatorRail.calculateNkv( new Modifications( co2Price2000, 0, 1, 1, 1. ), baseDataContainer ) );
//        entries.put(Headers.NKV_EL03, NkvCalculatorRail.calculateNkv( new Modifications( co2PriceBVWP, addtlFzkmBeyondPrinsEl03, 1, 1. ), baseDataContainer ) );
//        entries.put(Headers.NKV_EL03_CARBON215_INVCOSTTUD, NkvCalculatorRail.calculateNkv( new Modifications( co2Price215, addtlFzkmBeyondPrinsEl03, constructionCostFactor, 1. ), baseDataContainer ) );
//        entries.put(Headers.NKV_EL03_CARBON700tpr0_INVCOSTTUD, NkvCalculatorRail.calculateNkv( new Modifications( co2Price700, addtlFzkmBeyondPrinsEl03, constructionCostFactor, 1.75 ), baseDataContainer ) );
//        entries.put(Headers.NKV_EL03_CARBON700tpr0, NkvCalculatorRail.calculateNkv( new Modifications( co2Price700, addtlFzkmBeyondPrinsEl03, 1., 1.75 ), baseDataContainer ) );
//        entries.put(Headers.NKV_EL03_CO2_INVCOST50, NkvCalculatorRail.calculateNkv( new Modifications( co2Price700, addtlFzkmBeyondPrinsEl03, constructionCostFactor ), baseDataContainer ) );

        entries.put(Headers.ADDTL_PKWKM_EL03, addtlFzkmFromElasticity03 );
//        entries.put(Headers.CO2_COST_ORIG, Math.max( 1., NkvCalculatorRail.calculateCost_CO2( NO_CHANGE, baseDataContainer ) ) );
//        entries.put(Headers.CO2_COST_EL03, Math.max( 1., NkvCalculatorRail.calculateCost_CO2( new Modifications( co2PriceBVWP, addtlFzkmBeyondPrinsEl03, 1, 1. ), baseDataContainer ) ) );
        // ("max(1,...)" so that they become visible on logplot.  find other solution!
//        entries.put(Headers.INVCOST_TUD, this.constructionCostTud );

        double AVERAGE_SPEED_OF_ADDITIONAL_TRAVEL = 50; // km/h
//        double addtlFzkmFromTtime = - baseDataContainer.getPhysicalEffect().getVehicleHours().overall() * AVERAGE_SPEED_OF_ADDITIONAL_TRAVEL;
//        entries.put( Headers.ADDTL_PKWKM_FROM_TTIME, addtlFzkmFromTtime );

//        entries.put( Headers.NKV_ELTTIME_CARBON215_INVCOSTTUD, NkvCalculatorRail.calculateNkv( new Modifications( co2Price215, addtlFzkmFromTtime, constructionCostFactor, 1. ), baseDataContainer ) );
//        entries.put( Headers.NKV_ELTTIME_CARBON700TPR0_INVCOSTTUD, NkvCalculatorRail.calculateNkv( new Modifications( co2Price700, addtlFzkmFromTtime, constructionCostFactor, 1.75 ), baseDataContainer ) );
//
//        entries.put( Headers.NKV_ELTTIME_CARBON2000_INVCOSTTUD, NkvCalculatorRail.calculateNkv( new Modifications( 2000 * INFLATION_Factor2022to2012, addtlFzkmFromTtime, constructionCostFactor, 1.75 ), baseDataContainer ) );

        if ( baseDataContainer.getProjectInformation().getProjectNumber().contains("A1-G50-NI" )) {
            this.remarks.add("Eher geringer Benefit pro km ... erzeugt dann ueber die El pro km relativ viel Verkehr " +
                                             "der per co2 stark negativ bewertet wird.");
        }

    }

    public RailBaseDataContainer getBaseDataContainer() {
        return baseDataContainer;
    }

    public SequencedMap<String, Double> getColumns() {
        return entries;
    }

    public List<String> getRemarks() {
        return remarks;
    }

}
