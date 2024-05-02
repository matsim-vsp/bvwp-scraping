package org.tub.vsp.bvwp.data;

import org.tub.vsp.bvwp.data.type.Einstufung;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

public final class Headers{
	/**
	 * Additional lane-km of the project.  Estimated from length of the construction project and project type (e.g. "Neubau" vs. "Erweiterung").
	 */
	public static final String ADDTL_LANE_KM = "addtl_lane_km";
	/**
	 * = {@link #ADDTL_PKWKM_EL03} - {@link #ADDTL_PKWKM_ORIG}
	 */
	public static final String ADDTL_PKWKM_EL03_DIFF = "addtl_pkwkm_el03_diff";
	/**
	 * Der "induzierte" (Mehr-)Verkehr laut PRINS.
	 */
	public static final String ADDTL_PKWKM_INDUZ_ORIG = "addtl_pkwkm_induz";
	/**
	 * Mehrverkehr auf Pkw, den das Projekt erzeugt, laut unserer eigenen Abschätzung.  Wird immer mal mit anderen Elastizitäten gerechnet, so
	 * dass sich die Werte immer mal ändern.
	 */
	public static final String ADDTL_PKWKM_EL03 = "addtl_pkwkm_el03";
	/**
	 * Mehrverkehr auf Pkw, den das Projekt erzeugt, laut PRINS.
	 */
	public static final String ADDTL_PKWKM_ORIG = "addtl_pkwkm_orig";
	public static final String ADDTL_PKWKM_FROM_TTIME = "addtl_pkwkm_from_ttime";

	/**
	 * So etwas wie "Neubau", "Knotenpunkt(projekt)", Erweiterung auf 6 oder 8 Spuren.
	 *
	 * @see org.tub.vsp.bvwp.data.type.Bautyp
	 */
	public static final String BAUTYP = "bautyp";
	/**
	 * Die (vermutlich negativen) Nutzenbeiträge ("benefits") durch CO2 sowie durch Emissionen, die auf CO2 umgerechnet werden können.  (??)
	 */
	public static final String B_CO_2_EQUIVALENTS_ORIG = "b_co2_equivalents_orig";
	/**
	 * Die (vermutlich negativen) Nutzenbeiträge ("benefits") durch zusätzliche Fahrzeug-km.
	 */
	public static final String B_FZKM = "b_fzkm_orig";
	/**
	 * Gesamter Projektnutzen.
	 */
	public static final String B_OVERALL = "b_overall_orig";
	/**
	 * (Vermutlich) Projektnutzen pro Fahrstreifen-km.
	 */
	public static final String B_PER_KM = "b_orig_per_km";
	/**
	 * CO2-Kosten nach Neuberechnung (immer mal anders).
	 */
	public static final String CO2_COST_EL03 = "cost_co2_el03";
	/**
	 * Originale CO2-Kosten.
	 */
	public static final String CO2_COST_ORIG = "cost_co2_orig";
	/**
	 * Originale Investitionskosten.
	 */
	public static final String INVCOST_ORIG = "investmentCost_orig";
	/**
	 * Investitionskosten nach Berechnung der TUD
	 */
	public static final String INVCOST_TUD = "investmentCost_tud";
	/**
	 * Umrechnung von Emissionen (welchen??) in CO2-Äquivalente.
	 */
	public static final String CO_2_EQUIVALENTS_EMISSIONS = "co2_equivalents_emissions_orig";
	/**
	 * So etwas wie "VB-E", "VB", ...
	 *
	 * @see Einstufung
	 */
	public static final String EINSTUFUNG = "Einstufung";
	/**
	 * Länge des Projektes.
	 */
	public static final String LENGTH = "length";
	/**
	 * URL des Projektes.
	 */
	public static final String LINK = "URL";
	/**
	 * min( 5 , {@link #NKV_ORIG} )
	 */
	public static final String NKV_ORIG_CAPPED5 = "NKV_orig_capped5";
	/**
	 * NKV bei erhöhtem CO2-Preis (welchem?).
	 */
	public static final String NKV_CO2 = "NKV_co2";
	public static final String NKV_CO2_2000_EN = "BCR_co2_2000";
	public static final String NKV_CO2_700_EN = "BCR_co2_680";
	/**
	 * {@link #NKV_EL03_CARBON215_INVCOSTTUD} - {@link #NKV_ORIG}
	 */
	public static final String NKV_EL03_DIFF = "NKV_el03_Diff";
	/**
	 * Neues NKV bei erhöhter Abschätzung für Mehrverkehr.
	 */
	public static final String NKV_EL03 = "NKV_el03";
	public static final String NKV_EL03_CAPPED5 = NKV_EL03 + "_capped5";
	public static final String NKV_CARBON700 = "NKV_carbon700";
	public static final String NKV_CARBON700_CAPPED5 = NKV_CARBON700 + "_capped5";
	/**
	 * Neues NKV mit höherem Mehrverkehr, CO2-Preis 215, sowie höheren Investitionskosten.
	 */
	public static final String NKV_EL03_CARBON215_INVCOSTTUD = "NKV_el03_carbon215_invcostTud";
	public static final String NKV_EL03_CARBON215_INVCOSTTUD_CAPPED10 = NKV_EL03_CARBON215_INVCOSTTUD + "_capped10";
	/**
	 * min( 5, {@link #NKV_EL03_CARBON215_INVCOSTTUD})
	 */
	public static final String NKV_EL03_CARBON215_INVCOSTTUD_CAPPED5 = NKV_EL03_CARBON215_INVCOSTTUD + "_capped5";
	public static final String NKV_EL03_CARBON700 = "NKV_el03_carbon700";
	public static final String NKV_EL03_CARBON700_CAPPED5 = NKV_EL03_CARBON700 + "_capped5";
	public static final String NKV_EL03_CARBON700_INVCOSTTUD = "NKV_el03_carbon700_invcostTud";
	public static final String NKV_EL03_CARBON700_INVCOSTTUD_CAPPED5 = NKV_EL03_CARBON700_INVCOSTTUD + "_capped5";
	public static final String NKV_INDUZ_CO2_EN = "BCR_induzCo2";
	public static final String NKV_INDUZ_EN = "BCR_induz";
	public static final String NKV_NO_CHANGE_EN = "BCR";
	/**
	 * Originales NKV.
	 */
	public static final String NKV_ORIG = "NKV_orig";
	/**
	 * Dies ist da, damit "bubble size" als Funktion der Einstufung geplottet werden kann.
	 */
	public static final String EINSTUFUNG_AS_NUMBER = "einstufungAsNumber";
	public static final String PROJECT_NAME = "project _name";
	public static final String VERKEHRSBELASTUNG_PLANFALL = "DTV_Planfall";
	public static final String NKV_ELTTIME_CARBON215_INVCOSTTUD = "NKV_elFromTtime_carbon215_invcostTud";
	public static final String NKV_ELTTIME_CARBON700_INVCOSTTUD = "NKV_elFromTtime_carbon700_invcostTud";

	public static String capped5Of( String str ) {
		int cap=5;
		return cappedOf( cap, str );
	}
	public static String cappedOf( int cap, String str ){
		return str + "_capped" + cap;
	}

	public static void addCap5( Table table, String key ) {
		int cap=5;
		addCap( cap, table, key );
	}
	public static void addCap( int cap, Table table, String key ){
		DoubleColumn newColumn = DoubleColumn.create( Headers.cappedOf( cap, key ) );
		for( Double number : table.doubleColumn( key ) ){
			number = Math.min( number, cap - Math.random() * 0.1 + 0.05 );
			newColumn.append( number );
		}
		table.addColumns( newColumn );
	}

	private Headers(){} // do not instantiate
}
