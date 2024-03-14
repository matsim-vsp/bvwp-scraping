package org.tub.vsp.bvwp.data;

import org.tub.vsp.bvwp.data.type.Einstufung;

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
	public static final String CO2_COST_NEU = "cost_co2_new";
	/**
	 * Originale CO2-Kosten.
	 */
	public static final String CO2_COST_ORIG = "cost_co2_orig";
	/**
	 * Originale Investitionskosten.
	 */
	public static final String COST_OVERALL = "investment_cost_orig";
	/**
	 * Investitionskosten nach Neuberechnung.  Immer mal anders.
	 */
	public static final String COST_OVERALL_INCREASED = "investment_cost_increased";
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
	public static final String NKV_CO2_680_EN = "BCR_co2_680";
	/**
	 * {@link #NKV_EL03_CO2_215_CONSTRUCTION} - {@link #NKV_ORIG}
	 */
	public static final String NKV_EL03_DIFF = "NKV_el03_Diff";
	/**
	 * Neues NKV bei erhöhter Abschätzung für Mehrverkehr.
	 */
	public static final String NKV_EL03 = "NKV_el03";
	/**
	 * Neues NKV mit höherem Mehrverkehr, CO2-Preis 215, sowie höheren Investitionskosten.
	 */
	public static final String NKV_EL03_CO2_215_CONSTRUCTION = "NKV_el03Co2_215Invkosten";
	/**
	 * min( 5, {@link #NKV_EL03_CO2_215_CONSTRUCTION})
	 */
	public static final String NKV_EL03_CO2_215_CONSTRUCTION_CAPPED5 = "NKV_el03Co2_215Invkosten_capped5";
	/**
	 * Neues NKV mit höherem Mehrverkehr, höherem CO2-Preis, höheren Investitionskosten.
	 */
	public static final String NKV_EL03_CO2_CONSTRUCTION = "NKV_el03Co2Invkosten";
	/**
	 * min( 5, {@link #NKV_EL03_CO2_CONSTRUCTION} )
	 */
	public static final String NKV_EL03_CO2_CONSTRUCTION_CAPPED5 = "NKV_el03Co2Invkosten_capped5";
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
	public static final String VERKEHRSBELASTUNG_PLANFALL = "Verkehrsbelastung_Planfall";
	private Headers(){} // do not instantiate
}
