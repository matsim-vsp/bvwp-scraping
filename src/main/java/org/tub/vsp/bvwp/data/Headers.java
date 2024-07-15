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
	public static final String ADDTL_PKWKM = "zusätzliche Pkw-km";
	public static final String ADDTL_PKWKM_EL03_DIFF = "addtl_pkwkm_el03_diff";
	/**
	 * Der "induzierte" (Mehr-)Verkehr laut PRINS.
	 */
	public static final String ADDTL_PKWKM_INDUZ_ORIG = "addtl_pkwkm_induz";
	/**
	 * Mehrverkehr auf Pkw, den das Projekt erzeugt, laut unserer eigenen Abschätzung.  Wird immer mal mit anderen Elastizitäten gerechnet, so
	 * dass sich die Werte immer mal ändern.
	 */
	public static final String ADDTL_PKWKM_EL03 = ADDTL_PKWKM + " aus Elastizität 0,3";
	/**
	 * Mehrverkehr auf Pkw, den das Projekt erzeugt, laut PRINS.
	 */
	public static final String ADDTL_PKWKM_ORIG = ADDTL_PKWKM + " in PRINS";
	public static final String ADDTL_PKWKM_FROM_TTIME = ADDTL_PKWKM + " aus Reisezeitgewinnen";
	public static final String ADDTL_PKWKM_FROM_TTIME_DIFF = "addtl_pkwkm_from_ttime_diff";

	/**
	 * So etwas wie "Neubau", "Knotenpunkt(projekt)", Erweiterung auf 6 oder 8 Spuren.
	 *
	 * @see org.tub.vsp.bvwp.data.type.Bautyp
	 */
	public static final String BAUTYP = "Bautyp";
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
	public static final String INVCOST_ORIG = "Inv.kosten BVWP 2030";
	/**
	 * Investitionskosten nach Berechnung der TUD
	 */
	public static final String INVCOST_TUD = "Inv.kosten neu";
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
	public static final String NKV_ORIG = "NKV BVWP 2030";
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
	public static final String NKV_CARBON700ptpr0 = "NKV CO2-Kosten+";
	public static final String NKV_CARBON700_CAPPED5 = NKV_CARBON700ptpr0 + "_capped5";
	/**
	 * Neues NKV mit höherem Mehrverkehr, CO2-Preis 215, sowie höheren Investitionskosten.
	 */
	public static final String NKV_EL03_CARBON215_INVCOSTTUD = "NKV_el03_carbon215_invcostTud";
	public static final String NKV_EL03_CARBON215_INVCOSTTUD_CAPPED10 = NKV_EL03_CARBON215_INVCOSTTUD + "_capped10";
	/**
	 * min( 5, {@link #NKV_EL03_CARBON215_INVCOSTTUD})
	 */
	public static final String NKV_EL03_CARBON215_INVCOSTTUD_CAPPED5 = NKV_EL03_CARBON215_INVCOSTTUD + "_capped5";
	public static final String NKV_EL03_CARBON700ptpr0 = "NKV_el03_carbon700tpr0";
	public static final String NKV_EL03_CARBON700_CAPPED5 = NKV_EL03_CARBON700ptpr0 + "_capped5";
	public static final String NKV_EL03_CARBON700ptpr0_INVCOSTTUD = "NKV_el03_carbon700tpr0_invcostTud";
	public static final String NKV_EL03_CARBON700_INVCOSTTUD_CAPPED5 = NKV_EL03_CARBON700ptpr0_INVCOSTTUD + "_capped5";
	public static final String NKV_INDUZ_CO2_EN = "BCR_induzCo2";
	public static final String NKV_INDUZ_EN = "BCR_induz";
	public static final String NKV_NO_CHANGE_EN = "BCR";
	/**
	 * Originales NKV.
	 */
	/**
	 * Dies ist da, damit "bubble size" als Funktion der Einstufung geplottet werden kann.
	 */
	public static final String EINSTUFUNG_AS_NUMBER = "einstufungAsNumber";
	public static final String PROJECT_NAME = "Projektname";
	public static final String VERKEHRSBELASTUNG_PLANFALL = "DTV_Planfall";
	public static final String NKV_INVCOSTTUD = "NKV mit Inv.kosten+";
	public static final String NKV_ELTTIME = "NKV mit Straßenmehrverkehr+";
	public static final String NKV_ELTTIME_CARBON215_INVCOSTTUD = "NKV_elTtime_carbon215_invcostTud";
	public static final String NKV_ELTTIME_CARBON700ptpr0 = "NKV mit  Straßenmehrverkehr+/CO2-Preis+";
	public static final String NKV_ELTTIME_CARBON700ptpr0_INVCOSTTUD = "NKV mit  Inv.Kosten+/Straßenmehrverkehr+/CO2-Preis+";
	public static final String NKV_ELTTIME_CARBON700ptpr0_EMOB_INVCOSTTUD = "NKV mit Inv.Kosten+/Straßenmehrverkehr+/CO2-Preis+ & EMob";
	public static final String NKV_ELTTIME_CARBON2000ptpr0_EMOB_INVCOSTTUD = "NKV mit Inv.Kosten+/Straßenmehrverkehr+/CO2-Preis++ & EMob";
	public static final String NProCo2_ELTTIME_CARBON2000ptpr0_EMOB_INVCOSTTUD = "Nutzen_pro_CO2 mit Inv.Kosten+/Straßenmehrverkehr+/CO2-Preis++ & EMob";
	public static final String NKV_ELTTIME_CARBON2000_INVCOSTTUD = "NKV_elTtime_carbon2000_invcostTud";
	public static final String CO2_ELTTIME = "CO2[Mt] bei Straßenmehrverkehr+";

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
	public static String addCap( int cap, Table table, String key ){
		final String newColumnName = Headers.cappedOf( cap, key );
		if ( table. containsColumn( newColumnName ) ){
			return newColumnName;
		}
		DoubleColumn newColumn = DoubleColumn.create( newColumnName );
		for( Double number : table.doubleColumn( key ) ){
			number = Math.min( number, cap - Math.random() * 0.1 + 0.05 );
			newColumn.append( number );
		}
		table.addColumns( newColumn );
		return newColumnName;
	}

	private Headers(){} // do not instantiate
}
