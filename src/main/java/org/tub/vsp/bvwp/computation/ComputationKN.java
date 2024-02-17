package org.tub.vsp.bvwp.computation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ComputationKN{
	// yyyyyy Einbau von Jahreswerten (z.B. Elektrifizierung über Zeit).  Voraussetzung: Ich kann die Diskontierung nachbauen.
	
	private static final Logger log = LogManager.getLogger( ComputationKN.class );

	public static final double FZKM_AB = 221000;
	public static final double AB_length = 60000.;

	static final class Amounts{
		private final double rz;
		private final double rz_induz;
		private final double rz_verl;
		private final double pkwkm_all;
		private final double pkwkm_induz;
		private final double pkwkm_verl;
		private final double co2_kfz;
		private final double pkwkm_reroute;
		private final double co2_per_pkwkm;
		Amounts( double pkwkm_all, double pkwkm_induz, double pkwkm_verl, double pers_h, double pers_h_induz, double pers_h_verl, double co2_pkw, double co2_kfz ){

			this.rz = pers_h;
			this.rz_induz = pers_h_induz;
			this.rz_verl = pers_h_verl;
			this.pkwkm_all = pkwkm_all;
			this.pkwkm_induz = pkwkm_induz;
			this.pkwkm_verl = pkwkm_verl;
			this.co2_kfz = co2_kfz;
			this.pkwkm_reroute = pkwkm_all - pkwkm_induz - pkwkm_verl;
//			this.co2_per_pkwkm = co2_pkw / pkwkm_all;
			this.co2_per_pkwkm = 0.1 / 1000 * 1000_000; // 100g/km converted to tons/km, but then the unit in bvwp-projekte is mio pkw-km.
		}
	}

	static final class Benefits{
		private final double fzkm;
		private final double rz;
		private final double impl;
		private final double co2_infra;
		private final double co2_betrieb;
		private final double all;
		Benefits( double fzkm, double rz, double impl, double co2_infra, double co2_betrieb, double all ){
			// yyyyyy ist alles noch ganz schön unklar benannt!!
			this.fzkm = fzkm;
			this.rz = rz;
			this.impl = impl;
			this.co2_infra = co2_infra;
			this.co2_betrieb = co2_betrieb;
			this.all = all;
		}
	}

	public static void main( String[] args ){
		log.warn("need to either only compute pkw, or include lkw into computation!");

		// A20 "Elbquerung":
		{
			log.info("=== A20 (Elbquerung):");
			Amounts amounts = new Amounts( 131.53, 143.95, 9.75, -18.56, 1.46, 0.13, 54_773.28, 48_689.94 );
			Benefits benefits = new Benefits( -785.233, 2555.429, 1025.464, -151.319, -175.021, 5305.683 );
			double baukosten = 2737.176;
			log.info("--- orig:") ; nkv( new Modifications( 145, 0. ), amounts, benefits, baukosten );
			log.info("--- co2 price:") ; nkv( new Modifications( 5.*145, 0. ), amounts, benefits, baukosten );
			log.info("--- induz:") ; nkv( new Modifications( 145, amounts.pkwkm_induz*4. ), amounts, benefits, baukosten );
			log.info("--- induz wo b_impl:") ; nkvOhneKR_induz( new Modifications( 145, amounts.pkwkm_induz*4. ), amounts, benefits, baukosten, benefits.all );
			log.info("--- both:") ; nkv( new Modifications( 5.*145, amounts.pkwkm_induz*4. ), amounts, benefits, baukosten );
			log.info("--- both wo b_impl:") ; nkvOhneKR_induz( new Modifications( 5.*145, amounts.pkwkm_induz*4. ), amounts, benefits, baukosten, benefits.all );
			log.info("===");
		}
		// A8-G40-BW Ausbau:
		{
			log.info("=== A8-G40-BW:");
			final Amounts amounts = new Amounts( 2.31, 0., 0., -4.42, 0., 0., 1_981.64, 2_037.89	);
			final Benefits benefits = new Benefits( -21.838, 532., 0., -6.473, -6.682, 1067.523 );
			final double baukosten = 34.735;
			nkvOhneKR_induz( new Modifications( 145. , 0.), amounts, benefits, baukosten, benefits.all );
			nkvOhneKR_induz( new Modifications( 5.*145., 0. ), amounts, benefits, baukosten, benefits.all );
			nkvOhneKR_induz( new Modifications( 145., 38-2.31 ), amounts, benefits, baukosten, benefits.all );
			nkvOhneKR_induz( new Modifications( 5.*145., 38-2.31 ), amounts, benefits, baukosten, benefits.all );
			log.info("===");
		}
		// A59 Ausbau bei Bonn rechtsrheinisch
		{
			log.info("=== A59:");
			final Amounts amounts = new Amounts( 7.09, 0., 0., -1.33, 0., 0., 7_337.53, 7_707.91	 );
			final Benefits benefits = new Benefits( -88.090, 202.416, 0., -3.797, -29.699, 197.074 );
			final double baukosten = 34.735;
			log.info("--- orig:"); nkvOhneKR_induz( new Modifications(145. , 0.), amounts, benefits, baukosten, benefits.all );
			log.info("--- induz offset:"); nkvOhneKR_induz( new Modifications(145., 19.9- amounts.pkwkm_all ), amounts, benefits, baukosten, benefits.all );
			log.info("--- co2 price:"); nkvOhneKR_induz( new Modifications(5.*145., 0. ), amounts, benefits, baukosten, benefits.all );
			log.info("--- induz offset & co2 price:"); nkvOhneKR_induz( new Modifications(5.*145., 19.9 - amounts.pkwkm_all ), amounts, benefits, baukosten, benefits.all );
			log.info("===");
		}

	}
	static double nkv( Modifications modifications, Amounts amounts, Benefits benefits, double baukosten ) {
		double b_all = benefits.all;
		prn("start", b_all, 0. );

		double m_induzFactor = 1.;
		if ( amounts.pkwkm_induz > 0. ){
			m_induzFactor = 1. + modifications.mehrFzkm() / amounts.pkwkm_induz;
		}
		// (hatte ich früher direkt eingegeben; jetzt wird er ausgerechnet)

		// Zeitwert
//		double zw = benefits.rz / amounts.rz;
		double zw = - 5.5 * 25;

		// Distanzkosten
//		double distCost = benefits.fzkm / amounts.pkwkm_all;
		double distCost = - 0.24 * 25;
		{
			double b_tmp = b_all;
			b_all -= amounts.pkwkm_induz * distCost;
			b_all += amounts.pkwkm_induz * distCost * m_induzFactor;
			prn("rv_fzkm", b_all, b_tmp);
		}
		// rv_zeit
		{
			double b_tmp = b_all;
			b_all -= amounts.rz_induz * zw;
			b_all += amounts.rz_induz * zw * m_induzFactor;
			prn( "rv_zeit", b_all, b_tmp );
		}
		// impl Nutzen

		// -- differentiate b_impl by induz and verl so that we can multiply only the induz part.
		// -- we do this by computing the relative b_RV shares and then use that to multiply b.impl.

		// (1) approximiere die b_rv:
		double b_rv_induz = amounts.rz_induz * zw + amounts.pkwkm_induz * distCost;
		double b_rv_verl = amounts.rz_verl * zw + amounts.pkwkm_verl * distCost;

		// (2)
		double b_impl_induz = benefits.impl * b_rv_induz / ( b_rv_induz + b_rv_verl );
		if ( b_rv_induz==0. ) b_impl_induz = 0.;
		{
			double b_tmp = b_all;
			b_all -= b_impl_induz;
			b_all += b_impl_induz * m_induzFactor;
			prn( "b_impl", b_all, b_tmp );
		}

		return nkvOhneKR_induz( modifications, amounts, benefits, baukosten, b_all );

	}
	static double nkvOhneKR_induz( Modifications modifications, Amounts amounts, Benefits benefits, double baukosten, double b_all ){
		prn("incoming", b_all, b_all );
		// co2 Bau
		{
			double b_tmp = b_all;
			b_all -= benefits.co2_infra;
			b_all += modifications.co2Price()/145. * benefits.co2_infra;
			prn( "b_co2_infra", b_all, b_tmp );
		}

		// co2 Betrieb

		double b_per_co2 = benefits.co2_betrieb / amounts.co2_kfz;
		// (this divides all (negative) co2 benefits by all fzkm, including LKW.  We just need (discounted) benefits of ton co2, indep of where it comes from.)

		// -- b_co2 calculation is now done "by hand".  I.e. take pkwkm, multiply with emissions per km (obtained from co2_pkwkm / pkwkm), and then multiply b_per_co2:
		double b_co2_induz = amounts.pkwkm_induz * amounts.co2_per_pkwkm * b_per_co2;
		double b_co2_verl = amounts.pkwkm_verl * amounts.co2_per_pkwkm * b_per_co2;
		double b_co2_reroute = amounts.pkwkm_reroute * amounts.co2_per_pkwkm * b_per_co2;

		double bb_tmp = b_all;
		{
			double b_tmp = b_all;
			b_all -= b_co2_reroute;
			b_all += b_co2_reroute / 145. * modifications.co2Price();
			prn( "co2 after reRoute", b_all, b_tmp);
		}
		{
			double b_tmp = b_all;
			b_all -= b_co2_verl;
			b_all += b_co2_verl / 145. * modifications.co2Price();
			prn( "co2 after verl", b_all, b_tmp);
		}
		{
			double b_tmp = b_all;
			b_all -= b_co2_induz;
			b_all += b_co2_induz / 145. * modifications.co2Price();
			b_all += modifications.mehrFzkm() * amounts.co2_per_pkwkm * b_per_co2 * modifications.co2Price() / 145;
			prn( "co2 after induz", b_all, b_tmp);
		}
		prn( "b_co2_betrieb", b_all, bb_tmp );

		// nkv:
		final double nkv = b_all / baukosten;
		String colorString = ConsoleColors.TEXT_BLACK;
		if ( nkv < 1 ) colorString = ConsoleColors.TEXT_RED;
		log.info( "\t\t\t\t\tnkv=" + colorString + nkv + ConsoleColors.TEXT_BLACK );
		return nkv;
	}
	private static void prn( String msg, double b_all, double b_tmp ){
		log.info(String.format( "%1$20s: Korrektur = %2$5.0f; bb = %3$5.0f", msg, b_all - b_tmp, b_all ) );
	}

	@Test void test() {

		Amounts amounts = new Amounts( 131.53, 143.95, 9.75, -18.56, 1.46, 0.13, 54_773.28, 48_689.94 );
		Benefits benefits = new Benefits( -785.233, 2555.429, 1025.464, -151.319, -175.021, 5305.683 );
		double baukosten = 2737.176;
		{
			double nkv = nkv( new Modifications( 145., 0. ), amounts, benefits, baukosten );
			log.info( nkv );
			Assertions.assertEquals( 1.938378, nkv, 0.001 );
		}
		{
			double nkv = nkv( new Modifications( 5.*145., amounts.pkwkm_induz*4. ), amounts, benefits, baukosten );
			log.info( nkv );
			Assertions.assertEquals( -0.2963518828995041, nkv, 0.001 );
		}

		// this is, for backwards compatibility, the older computation where I had not split b_co2_betrieb into pkw and lkw:
		amounts = new Amounts( 131.53, 143.95, 9.75, -18.56, 1.46, 0.13, 48_689.94, 48_689.94 );
		{
			double nkv = nkv( new Modifications( 5.*145., amounts.pkwkm_induz*4. ), amounts, benefits, baukosten );
			log.info( nkv );
			Assertions.assertEquals( -0.089529, nkv, 0.001 );
		}
	}

	public static Double co2( Modifications modifications, Amounts amounts, Benefits benefits ){
		// yyyyyy for the time being this returns the benefits since they are easier to compute.  !!!!

		double co2 = 0.;

		// co2 Bau
		{
			co2 += benefits.co2_infra;
		}

		// co2 Betrieb

		double b_per_co2 = benefits.co2_betrieb / amounts.co2_kfz;
		// (this divides all (negative) co2 benefits by all fzkm, including LKW.  We just need (discounted) benefits of ton co2, indep of where it comes from.)

		// -- b_co2 calculation is now done "by hand".  I.e. take pkwkm, multiply with emissions per km (obtained from co2_pkwkm / pkwkm), and then multiply b_per_co2:
		double b_co2_induz = amounts.pkwkm_induz * amounts.co2_per_pkwkm * b_per_co2;
		double b_co2_verl = amounts.pkwkm_verl * amounts.co2_per_pkwkm * b_per_co2;
		double b_co2_reroute = amounts.pkwkm_reroute * amounts.co2_per_pkwkm * b_per_co2;

		{
			co2 += b_co2_reroute / 145. * modifications.co2Price()  ;
		}
		{
			co2 += b_co2_verl / 145. * modifications.co2Price() ;
		}
		{
			co2 += b_co2_induz / 145. * modifications.co2Price() ;
			co2 += modifications.mehrFzkm() * amounts.co2_per_pkwkm * b_per_co2 / 145. * modifications.co2Price();
		}

		return -co2;
	}
}
