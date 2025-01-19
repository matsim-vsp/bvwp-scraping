package org.tub.vsp.bvwp.data;

public class HeadersKN {
	// !!! NOTE: Ich mache jetzt nur noch NKVs mit dem originalen Barwert; Korrekturen dazu immer nur kurz vor dem Plotten/Rausschreiben.  kai, nov'24 !!
	// Die Headers landen trotzdem hier!
	public static final String INVCOST_BARWERT_ORIG = "Inv.kosten BVWP 2030 Barwert";
	/**
	 * Originale Investitionskosten ("Summe bewertungsrelevanter Investitionskosten").
	 */
	public static final String INVCOST_SUM_ORIG = "Inv.kosten BVWP 2030 Summe";

	// == orig ... still in Headers!

	// == einzelne
	public static final String NKV_CARBON700 = "NKV mit CO2-Kosten+";
	public static final String NKV_ELTTIME_HIGH = "NKV mit induz. Verkehr+ hoch";
	public static final String NKV_ELTTIME_LOW = "NKV mit induz. Verkehr+ niedrig";
	public static final String NKV_INVCOST73 = "NKV mit Investitionskosten+73%";

	// == kombinierte:

	public static final String NKV_CARBON700_EMOB = NKV_CARBON700 + "/EMob";
	// (emob kompensiert carbon)
	public static final String NKV_ELTTIME_CARBON700 = NKV_ELTTIME_LOW+ "/CO2-Preis+";
	// (Vorbereitung auf T&E Szenario)
	public static final String NKV_ELTTIME_CARBON700_INVCOST73 = NKV_ELTTIME_CARBON700 + "/Inv.Kosten+73%";
	// (dramatische Wirkung T&E Szenario)
	public static final String NKV_ELTTIME_CARBON700_EMOB = NKV_ELTTIME_CARBON700 + "/EMob+";
	// (Vorbereitung)
	public static final String NKV_ELTTIME_CARBON700_EMOB_INVCOST73 = NKV_ELTTIME_CARBON700_EMOB + "/InvKosten+73%";
	// (... aber auch dies durch emob wieder eingefangen)

	// == BMDV
	public static final String NKV_CARBON700_EMOB_INVCOST73 = NKV_CARBON700_EMOB + "/Inv.Kosten+73%";
	public static final String NKV_BMDV = NKV_CARBON700_EMOB_INVCOST73;

	// == Sensitivität BMDV:
	public static final String NKV_CARBON700_EMOB_INVCOST73_ELTTIME = NKV_CARBON700_EMOB_INVCOST73 + "/induz. Verkehr+";
	// (mehr induzierter Verkehr ... wollen wir glaube ich gar nicht)

	public static final String NKV_CARBON700_INVCOST73 = NKV_CARBON700 + "/Inv.Kosten+73%";
	// (emob weggelassen)

	public static final String NKV_CARBON700_EMOB_INVCOST73_10pctLessTraffic = NKV_CARBON700_EMOB_INVCOST73 + "/Prognose-";
	public static final String INVCOST_PLUS_120_PCT = "Baukosten 2024";
	// (niedrigere Prognose)


	private HeadersKN(){} // do not instantiate
}
