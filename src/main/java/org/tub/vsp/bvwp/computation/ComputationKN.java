package org.tub.vsp.bvwp.computation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.tub.vsp.bvwp.BvwpUtils.assertNotNaN;

public class ComputationKN {
    // yyyyyy Einbau von Jahreswerten (z.B. Elektrifizierung über Zeit).  Voraussetzung: Ich kann die Diskontierung
    // nachbauen.

    private static final Logger log = LogManager.getLogger(ComputationKN.class);

    public static final double FZKM_AB = 221000;
    public static final double LANE_KM_AB = 60000.;

    enum CO2_PER_KM { typicalValues, fromPrins };
    private static final CO2_PER_KM co2PerKm = CO2_PER_KM.fromPrins;

    static final class Amounts {
        private final double rz;
        private final double rz_induz;
        private final double rz_verl;
        private final double pkwkm_all;
        private final double pkwkm_induz;
        private final double pkwkm_verl;
        private final double co2_pkw;
        private final double co2_lkw;
        private final double co2_kfz;
        private final double pkwkm_reroute;
        private final double co2_per_pkwkm;
        private final double lkwkm_all;
        public final double co2_per_lkwkm;

        Amounts( double pkwkm_all, double pkwkm_induz, double pkwkm_verl,
                 double pers_h, double pers_h_induz, double pers_h_verl,
                 double lkwkmAll,
                 double co2_pkw, double co2_lkw, double co2_kfz
               ) {

            this.rz = pers_h;
            this.rz_induz = pers_h_induz;
            this.rz_verl = pers_h_verl;

            this.pkwkm_all = pkwkm_all;
            this.pkwkm_induz = pkwkm_induz;
            this.pkwkm_verl = pkwkm_verl;
            this.pkwkm_reroute = pkwkm_all - pkwkm_induz - pkwkm_verl;

            lkwkm_all = lkwkmAll;

            this.co2_pkw = co2_pkw;
            this.co2_lkw = co2_lkw;
            this.co2_kfz = co2_kfz;


            switch ( co2PerKm ) {
                case typicalValues -> {
                    this.co2_per_pkwkm = 0.1 / 1000 * 1_000_000;
                    // 100g/km = 0.1kg/km converted to tons/km, but then the unit in bvwp-projekte is mio pkw-km.

                    this.co2_per_lkwkm = 2. / 1000 * 1_000_000;
                    // approx. 121g per transported ton x 20t/Lkw
                }
                case fromPrins -> {
                    this.co2_per_pkwkm = co2_pkw / pkwkm_all;
                    this.co2_per_lkwkm = co2_lkw / lkwkm_all;
                }
                default -> throw new IllegalStateException( "Unexpected value: " + co2PerKm );
            }

//            log.info( "co2 per pkwkm [t/mio-km=g/km]=" + (this.co2_pkw/this.pkwkm_all) + "; co2 per lkwkm=" + (this.co2_lkw/this.lkwkm_all) );
        }

        public double getPkwkm_induz() {
            return pkwkm_induz;
        }
    }

    static final class BenefitsAndInvestmentCosts{
        private final double fzkm;
        private final double rz;
        private final double impl;
        private final double co2_infra;
        private final double co2_betrieb;
        private final double all;
        private final double investmentCosts;

        BenefitsAndInvestmentCosts( double fzkm, double rz, double impl, double co2_infra, double co2_betrieb, double all, double investmentCosts ) {
            // yyyyyy ist alles noch ganz schön unklar benannt!!
            this.fzkm = fzkm;
            this.rz = rz;
            this.impl = impl;
            this.co2_infra = co2_infra;
            this.co2_betrieb = co2_betrieb;
            this.all = all;
            this.investmentCosts = investmentCosts;
        }
    }

    /*
    public static void main(String[] args) {
        log.warn("need to either only compute pkw, or include lkw into computation!");

        // A20 "Elbquerung":
        {
            log.info("=== A20 (Elbquerung):");
            Amounts amounts = new Amounts(131.53, 143.95, 9.75, -18.56, 1.46, 0.13, 54_773.28, 48_689.94);
            Benefits benefits = new Benefits(-785.233, 2555.429, 1025.464, -151.319, -175.021, 5305.683);
            double baukosten = 2737.176;
            log.info("--- orig:");
            nkv(new Modifications(145, 0., 1 ), amounts, benefits, baukosten );
            log.info("--- co2 price:");
            nkv(new Modifications(5. * 145, 0., 1 ), amounts, benefits, baukosten );
            log.info("--- induz:");
            nkv(new Modifications(145, amounts.pkwkm_induz * 4., 1 ), amounts, benefits, baukosten );
            log.info("--- induz wo b_impl:");
            nkvOhneKR_induz(new Modifications(145, amounts.pkwkm_induz * 4., 1 ), amounts, benefits, baukosten,
                    benefits.all);
            log.info("--- both:");
            nkv(new Modifications(5. * 145, amounts.pkwkm_induz * 4., 1 ), amounts, benefits, baukosten );
            log.info("--- both wo b_impl:");
            nkvOhneKR_induz(new Modifications(5. * 145, amounts.pkwkm_induz * 4., 1 ), amounts, benefits, baukosten,
                    benefits.all);
            log.info("===");
        }
        // A8-G40-BW Ausbau:
        {
            log.info("=== A8-G40-BW:");
            final Amounts amounts = new Amounts(2.31, 0., 0., -4.42, 0., 0., 1_981.64, 2_037.89);
            final Benefits benefits = new Benefits(-21.838, 532., 0., -6.473, -6.682, 1067.523);
            final double baukosten = 34.735;
            nkvOhneKR_induz(new Modifications(145., 0., 1 ), amounts, benefits, baukosten, benefits.all );
            nkvOhneKR_induz(new Modifications(5. * 145., 0., 1 ), amounts, benefits, baukosten, benefits.all );
            nkvOhneKR_induz(new Modifications(145., 38 - 2.31, 1 ), amounts, benefits, baukosten, benefits.all );
            nkvOhneKR_induz(new Modifications(5. * 145., 38 - 2.31, 1 ), amounts, benefits, baukosten, benefits.all );
            log.info("===");
        }
        // A59 Ausbau bei Bonn rechtsrheinisch
        {
            log.info("=== A59:");
            final Amounts amounts = new Amounts(7.09, 0., 0., -1.33, 0., 0., 7_337.53, 7_707.91);
            final Benefits benefits = new Benefits(-88.090, 202.416, 0., -3.797, -29.699, 197.074);
            final double baukosten = 34.735;
            log.info("--- orig:");
            nkvOhneKR_induz(new Modifications(145., 0., 1 ), amounts, benefits, baukosten, benefits.all );
            log.info("--- induz offset:");
            nkvOhneKR_induz(new Modifications(145., 19.9 - amounts.pkwkm_all, 1 ), amounts, benefits, baukosten,
                    benefits.all);
            log.info("--- co2 price:");
            nkvOhneKR_induz(new Modifications(5. * 145., 0., 1 ), amounts, benefits, baukosten, benefits.all );
            log.info("--- induz offset & co2 price:");
            nkvOhneKR_induz(new Modifications(5. * 145., 19.9 - amounts.pkwkm_all, 1 ), amounts, benefits, baukosten,
                    benefits.all);
            log.info("===");
        }

    }
     */
    static double nkv( Modifications modifications, Amounts amounts, BenefitsAndInvestmentCosts benefits ) {
        double b_all = benefits.all;
        prn("start", b_all, 0.);

        double m_induzFactor = 1.;
        if (amounts.pkwkm_induz > 0.) {
            m_induzFactor = 1. + modifications.mehrFzkm() / amounts.pkwkm_induz;
        }

        // Zeitwert
//		double zw = benefits.rz / amounts.rz;
        double zw = -5.5 * 25;

        // Distanzkosten
//		double distCost = benefits.fzkm / amounts.pkwkm_all;
        double distCost = -0.24 * 25;
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
            prn("rv_zeit", b_all, b_tmp);
        }
        // impl Nutzen

        // -- differentiate b_impl by induz and verl so that we can multiply only the induz part.
        // -- we do this by computing the relative b_RV shares and then use that to multiply b.impl.

        // (1) approximiere die b_rv:
        double b_rv_induz = amounts.rz_induz * zw + amounts.pkwkm_induz * distCost;
        double b_rv_verl = amounts.rz_verl * zw + amounts.pkwkm_verl * distCost;

        // (2)
        double b_impl_induz = benefits.impl * b_rv_induz / (b_rv_induz + b_rv_verl);
        if (b_rv_induz == 0.) {
            b_impl_induz = 0.;
        }
        {
            double b_tmp = b_all;
            b_all -= b_impl_induz;
            b_all += b_impl_induz * m_induzFactor;
            prn("b_impl", b_all, b_tmp);
        }

        return nkvOhneKR_induz(modifications, amounts, benefits, b_all );

    }

    static double nkvOhneKR_induz( Modifications modifications, Amounts amounts, BenefitsAndInvestmentCosts benefits, double b_all ) {
        prn("incoming", b_all, b_all);

        // ### preparations:

        double b_per_co2 = benefits.co2_betrieb / amounts.co2_kfz;
        // (this divides all (negative) co2 benefits by all emissions, including LKW.  We just need (discounted) benefits
        // of ton co2, indep of where it comes from.)

        // -- b_co2 calculation is now done "by hand".  I.e. take pkwkm, multiply with emissions per km (obtained
        // from co2_pkwkm / pkwkm), and then multiply b_per_co2:
        double b_co2_induz = amounts.pkwkm_induz * amounts.co2_per_pkwkm * b_per_co2;
        if ( amounts.pkwkm_induz==0. ) {
            b_co2_induz=0.;
        }
        double b_co2_verl = amounts.pkwkm_verl * amounts.co2_per_pkwkm * b_per_co2;
        if ( amounts.pkwkm_verl==0. ) {
            b_co2_verl=0.;
        }
        double b_co2_reroute = amounts.pkwkm_reroute * amounts.co2_per_pkwkm * b_per_co2;
        if ( amounts.pkwkm_reroute==0. ) {
            b_co2_reroute=0.;
        }

        double b_co2_lkw = amounts.lkwkm_all * amounts.co2_per_lkwkm * b_per_co2;
        if ( amounts.lkwkm_all==0 ) {
            // in this case, co2_per_lkwkm is infty, and the multiplication is NaN.  But in reality, the value is just zero.
            b_co2_lkw = 0;
        }
        if ( assertNotNaN( "b_co2_lkw", b_co2_lkw ) ) {
            log.info( "lkwkm_all=" + amounts.lkwkm_all + "; co2_per_lkwkm=" + amounts.co2_per_lkwkm + "; b_per_co2=" + b_per_co2 );
        }

        // ### first deduct the CO2 components so that we can afterwards re-scale the other material according to changed discount rate:
        {
            double b_tmp = b_all;

            // --- for infra:
            assertNotNaN( "co2_infra", benefits.co2_infra );
            b_all -= benefits.co2_infra;

            // --- for operations:
            assertNotNaN( "b_co2_reroute", b_co2_reroute );
            b_all -= b_co2_reroute;
            assertNotNaN( "b_co2_verl", b_co2_verl );
            b_all -= b_co2_verl;
            assertNotNaN( "b_co2_induz", b_co2_induz );
            b_all -= b_co2_induz;
            assertNotNaN( "b_co2_lkw", b_co2_lkw );
            b_all -= b_co2_lkw;

            prn( "b after deductions:", b_all, b_tmp );
        }
        // ### then re-add the CO2 components with the new values:

        // co2 Bau
        {
            double b_tmp = b_all;
            b_all += modifications.co2Price() / 145. * benefits.co2_infra;
            prn("b after co2 infra:", b_all, b_tmp);
        }

        // co2 Betrieb
        final double operationsCorrFactor = modifications.co2Price() / 145. * modifications.discountCorrFact() * modifications.emobCorrFact();

        {
            double b_tmp = b_all;
            b_all += b_co2_reroute / 145. * modifications.co2Price() * modifications.discountCorrFact() * modifications.emobCorrFact();
            prn("b after co2 reroute:", b_all, b_tmp);
        }
        {
            double b_tmp = b_all;
            b_all += b_co2_verl / 145. * modifications.co2Price() * modifications.discountCorrFact() * modifications.emobCorrFact();
            prn("b after co2 verl:", b_all, b_tmp);
        }
        {
            double b_tmp = b_all;
            b_all += b_co2_induz / 145. * modifications.co2Price() * modifications.discountCorrFact() * modifications.emobCorrFact() ;
//            b_all += modifications.mehrFzkm() * amounts.co2_per_pkwkm * b_per_co2 * modifications.co2Price() / 145 * modifications.discountCorrFact() * modifications.emobCorrFact() ;
            b_all += modifications.mehrFzkm() * 100 * b_per_co2 * modifications.co2Price() / 145 * modifications.discountCorrFact() * modifications.emobCorrFact() ;
            // 100 t / 1 mio km = 100g/km
            prn("b after co2 induz", b_all, b_tmp);
        }
        {
            double b_tmp = b_all;
            b_all += b_co2_lkw / 145. * modifications.co2Price() * modifications.discountCorrFact() * modifications.emobCorrFact() ;
            prn("b after co2 lkw", b_all, b_tmp);
        }
//        prn("b_co2_betrieb", b_all, bb_tmp);

        // ### investmentCosts:
        final double investmentCosts = benefits.investmentCosts * modifications.investmentCostFactor();

        // ### finally compute the nkv and return it:
        final double nkv = b_all / investmentCosts;
        if ( Double.isNaN( nkv ) ){
            String colorString = ConsoleColors.TEXT_BLACK;
            if( Double.isNaN( nkv ) ){
                colorString = ConsoleColors.TEXT_RED;
            }
            log.info( "\t\t\t\t\tnkv=" + colorString + nkv + ConsoleColors.TEXT_BLACK  + "; b_all=" + b_all + "; investmentCosts=" + investmentCosts );
        }
        return nkv;
    }

    private static void prn(String msg, double b_all, double b_tmp) {
        if ( Double.isNaN( b_all ) || Double.isNaN( b_tmp ) ){
            log.info( String.format( "%1$20s: before = %2$5.0f; corr = %3$5.0f; after = %4$5.0f", msg, b_tmp, b_all - b_tmp, b_all ) );
        }
    }

    static Double b_co2(Modifications modifications, Amounts amounts, BenefitsAndInvestmentCosts benefits ) {
        // yyyyyy for the time being this returns the benefits since they are easier to compute.  !!!!

        double co2 = 0.;

        // co2 Bau
        {
			co2 += benefits.co2_infra / 145. * modifications.co2Price() ;
        }

        // co2 Betrieb

        double b_per_co2 = benefits.co2_betrieb / amounts.co2_kfz;
        // (this divides all (negative) co2 benefits by all fzkm, including LKW.  We just need (discounted) benefits
        // of ton co2, indep of where it comes from.)

        // -- b_co2 calculation is now done "by hand".  I.e. take pkwkm, multiply with emissions per km (obtained
        // from co2_pkwkm / pkwkm), and then multiply b_per_co2:
        final double b_co2_induz = amounts.pkwkm_induz * amounts.co2_per_pkwkm * b_per_co2;
        final double b_co2_verl = amounts.pkwkm_verl * amounts.co2_per_pkwkm * b_per_co2;
        final double b_co2_reroute = amounts.pkwkm_reroute * amounts.co2_per_pkwkm * b_per_co2;
        final double b_co2_addtlInduz = modifications.mehrFzkm() * amounts.co2_per_pkwkm * b_per_co2;

        {
            co2 += b_co2_reroute / 145. * modifications.co2Price() * modifications.emobCorrFact();
        }
        {
            co2 += b_co2_verl / 145. * modifications.co2Price() * modifications.emobCorrFact();
        }
        {
            co2 += b_co2_induz / 145. * modifications.co2Price() * modifications.emobCorrFact();

            co2 += b_co2_addtlInduz / 145. * modifications.co2Price() * modifications.emobCorrFact();
            // mehrFzkm are those which are on top of PRINS
        }

		return co2;

		// Note that this really says nothing about old vs new co2 price, or old vs new addl traffic.  That all depends on the settings in "modifications".
    }

    /**
     * @param co2_infra_eur benefit in €/a
     * @param co2_betrieb_t emissions in t/a
     * @return new nkv
     */
    static double nkv_rail(double co2_price, double baukosten, double benefit, double co2_infra_eur, double co2_betrieb_t) {
        benefit -= co2_infra_eur;
        benefit -= co2_betrieb_t;

        benefit += co2_price / 145. * co2_infra_eur;
        benefit += co2_price * co2_betrieb_t;

        return benefit / baukosten;
    }
}
