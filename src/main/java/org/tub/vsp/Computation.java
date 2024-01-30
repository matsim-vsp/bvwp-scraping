package org.tub.vsp;

import org.apache.logging.log4j.core.util.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;

class Computation{
	// yyyyyy Einbau von Jahreswerten (z.B. Elektrifizierung über Zeit).  Voraussetzung: Ich kann die Diskontierung nachbauen.

	record Factors( double induz, double co2){}
	record Amounts ( double rz, double rz_induz, double rz_verl, double fzkm_all, double fzkm_induz, double fzkm_verl, double fzkm_reroute ){
		Amounts {
			Assertions.assertEquals( fzkm_all, fzkm_reroute + fzkm_induz + fzkm_verl );
		}
		Amounts ( double rz, double rz_induz, double rz_verl, double fzkm_all, double fzkm_induz, double fzkm_verl ){
			this( rz, rz_induz, rz_verl, fzkm_all, fzkm_induz, fzkm_verl, fzkm_all - fzkm_induz - fzkm_verl );
		}
	}
	record Benefits( double rz, double fzkm, double impl, double co2_betrieb, double co2_infra, double all ){}

	public static void main( String[] args ){

		Factors factors = new Factors( 5., 5. );
		Amounts amounts = new Amounts( -18.56, 1.46, 0.13, 131.53, 143.95, 9.75 );
		Benefits benefits = new Benefits( 2555.429, -785.233, 1025.464, -175.021, -151.319, 5305.683 );
		double baukosten = 2737.176;

		double nkv = nkv( factors, amounts, benefits, baukosten );

		System.out.println( "nkv=" + nkv );

	}
	private static double nkv( Factors f, Amounts a, Benefits b, double baukosten ) {
		double b_all = b.all;
		System.out.println("start: bb=" + b_all );

		// Zeitwert
		double zw = b.rz / a.rz;

		// Distanzkosten
		double distCost = b.fzkm / a.fzkm_all ;
		{
			double b_tmp = b_all;
			b_all -= a.fzkm_induz * distCost;
			b_all += a.fzkm_induz * distCost * f.induz;
			prn("rv_fzkm", b_all, b_tmp);
		}
		// rv_zeit
		{
			double b_tmp = b_all;
			b_all -= a.rz_induz * zw;
			b_all += a.rz_induz * zw * f.induz;
			prn( "rv_zeit", b_all, b_tmp );
		}
		// impl Nutzen

		// -- differentiate b_impl by induz and verl so that we can multiply only the induz part.
		// -- we do this by computing the relative b_RV shares and then use that to multiply b.impl.

		// (1) approximiere die b_rv:
		double b_rv_induz = a.rz_induz * zw + a.fzkm_induz * distCost;
		double b_rv_verl = a.rz_verl * zw + a.fzkm_verl * distCost;

		// (2)
		double b_impl_induz = b.impl * b_rv_induz / ( b_rv_induz + b_rv_verl );
		{
			double b_tmp = b_all;
			b_all -= b_impl_induz;
			b_all += b_impl_induz * f.induz;
			prn( "b_impl", b_all, b_tmp );
		}
		// co2 Bau
		{
			double b_tmp = b_all;
			b_all -= b.co2_infra;
			b_all += f.co2 * b.co2_infra;
			prn( "b_co2_infra", b_all, b_tmp );
		}
		// co2 Betrieb

		// -- differential co2 by induz and other so that only induz can be multiplied:
		double b_co2_induz = b.co2_betrieb * a.fzkm_induz / a.fzkm_all;
		double b_co2_verl = b.co2_betrieb * a.fzkm_verl / a.fzkm_all;
		double b_co2_reroute = b.co2_betrieb * a.fzkm_reroute / a.fzkm_all;

		{
			double b_tmp = b_all;
			b_all -= b_co2_induz + b_co2_verl + b_co2_reroute;
			b_all += f.co2 * (b_co2_induz * f.induz + b_co2_verl + b_co2_reroute);
			prn( "b_co2_betrieb", b_all, b_tmp );
		}

		// nkv:
		return b_all / baukosten;

	}
	private static void prn( String msg, double b_all, double b_tmp ){
		System.out.printf("%1$13s: Korrektur = %2$5.0f; bb = %3$5.0f" + System.lineSeparator(), msg, b_all - b_tmp, b_all );
	}

	@Test void test() {
		Amounts amounts = new Amounts( -18.56, 1.46, 0.13, 131.53, 143.95, 9.75 );
		Benefits benefits = new Benefits( 2555.429, -785.233, 1025.464, -175.021, -151.319, 5305.683 );
		double baukosten = 2737.176;
		{
			double nkv = nkv( new Factors( 1., 1. ), amounts, benefits, baukosten );
			System.out.println( nkv );
			Assertions.assertEquals( 1.938378, nkv, 0.001 );
		}
		{
			double nkv = nkv( new Factors( 5., 5. ), amounts, benefits, baukosten );
			System.out.println( nkv );
			Assertions.assertEquals( -0.089529, nkv, 0.001 );
		}
	}

}
