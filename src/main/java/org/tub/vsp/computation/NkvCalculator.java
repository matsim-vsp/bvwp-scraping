package org.tub.vsp.computation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.tub.vsp.data.container.base.PhysicalEffectDataContainer;
import org.tub.vsp.data.container.base.StreetBaseDataContainer;
import org.tub.vsp.data.type.Emission;

import java.util.Optional;

public class NkvCalculator {
    // yyyyyy Einbau von Jahreswerten (z.B. Elektrifizierung über Zeit).  Voraussetzung: Ich kann die Diskontierung
    // nachbauen.

    private static final Logger log = LogManager.getLogger(NkvCalculator.class);

    public static void main(String[] args) {
        log.warn("need to either only compute pkw, or include lkw into computation!");

        // A20 "Elbquerung":
        {
            log.info("=== A20 (Elbquerung):");
            Amounts amounts = new Amounts(-18.56, 1.46, 0.13, 131.53, 143.95, 9.75);
            Benefits benefits = new Benefits(-785.233, 2555.429, 1025.464, -151.319, -175.021, 5305.683);
            double baukosten = 2737.176;
            log.info("--- orig:");
            calculateNkv(Modifications.NO_CHANGE, amounts, benefits, baukosten);
            log.info("--- co2 price:");
            calculateNkv(Modifications.CO2_PRICE, amounts, benefits, baukosten);
            log.info("--- induz factor:");
            calculateNkv(Modifications.createInducedWithMehrFzkm(0), amounts, benefits, baukosten);
            log.info("--- both:");
            calculateNkv(Modifications.createInducedAndCo2WithMehrFzkm(0), amounts, benefits, baukosten);
            log.info("===");
        }
        // A8 Ausbau:
        {
            log.info("=== A8:");
            final Amounts amounts = new Amounts(-4.42, 0., 0., 2.31, 0., 0.);
            final Benefits benefits = new Benefits(-21.838, 532., 0., -6.473, -6.682, 1067.523);
            final double baukosten = 34.735;
            nkvOhneKR_induz(Modifications.NO_CHANGE, amounts, benefits, baukosten);
            nkvOhneKR_induz(Modifications.CO2_PRICE, amounts, benefits, baukosten);
            nkvOhneKR_induz(Modifications.createInducedWithMehrFzkm(8 - 2.31), amounts, benefits, baukosten
            );
            nkvOhneKR_induz(Modifications.createInducedAndCo2WithMehrFzkm(38 - 2.31), amounts, benefits, baukosten
            );
            log.info("===");
        }
        // A59 Ausbau bei Bonn rechtsrheinisch
        {
            log.info("=== A59:");
            final Amounts amounts = new Amounts(-1.33, 0., 0., 7.09, 0., 0.);
            final Benefits benefits = new Benefits(-88.090, 202.416, 0., -3.797, -29.699, 197.074);
            final double baukosten = 34.735;
            log.info("--- orig:");
            nkvOhneKR_induz(Modifications.NO_CHANGE, amounts, benefits, baukosten);
            log.info("--- induz offset:");
            nkvOhneKR_induz(Modifications.createInducedWithMehrFzkm(19.9 - amounts.fzkm_all()), amounts, benefits,
                    baukosten);
            log.info("--- co2 price:");
            nkvOhneKR_induz(Modifications.CO2_PRICE, amounts, benefits, baukosten);
            log.info("--- induz offset & co2 price:");
            nkvOhneKR_induz(Modifications.createInducedAndCo2WithMehrFzkm(19.9 - amounts.fzkm_all), amounts, benefits,
                    baukosten);
            log.info("===");
        }

    }

    public static Double calculateNkv(Modifications modifications, StreetBaseDataContainer streetBaseDataContainer) {
        Optional<Amounts> a = amountsFromStreetBaseData(streetBaseDataContainer);
        Optional<Benefits> b = benefitsFromStreetBaseData(streetBaseDataContainer);

        if (a.isEmpty() || b.isEmpty()) {
            return null;
        }

        Double baukosten = streetBaseDataContainer.getCostBenefitAnalysis()
                                                  .getCost()
                                                  .overallCosts();

        return calculateNkv(modifications, a.get(), b.get(), baukosten);
    }

    public static double calculateNkv(Modifications modifications, Amounts a, Benefits b, double baukosten) {
        double b_all = b.all;
        prn("start", b_all, 0.);

        // Zeitwert
        double zw = b.rz / a.rz;

        // Distanzkosten
        double distCost = b.fzkm / a.fzkm_all;
        {
            double b_tmp = b_all;
            b_all -= a.fzkm_induz * distCost;
            b_all += a.fzkm_induz * distCost * modifications.induzFactor();
            prn("rv_fzkm", b_all, b_tmp);
        }
        // rv_zeit
        {
            double b_tmp = b_all;
            b_all -= a.rz_induz * zw;
            b_all += a.rz_induz * zw * modifications.induzFactor();
            prn("rv_zeit", b_all, b_tmp);
        }
        // impl Nutzen

        // -- differentiate b_impl by induz and verl so that we can multiply only the induz part.
        // -- we do this by computing the relative b_RV shares and then use that to multiply b.impl.

        // (1) approximiere die b_rv:
        double b_rv_induz = a.rz_induz * zw + a.fzkm_induz * distCost;
        double b_rv_verl = a.rz_verl * zw + a.fzkm_verl * distCost;

        // (2)
        double b_impl_induz = b.impl * b_rv_induz / (b_rv_induz + b_rv_verl);
        if (b_rv_induz == 0.) {
            b_impl_induz = 0.;
        }
        {
            double b_tmp = b_all;
            b_all -= b_impl_induz;
            b_all += b_impl_induz * modifications.induzFactor();
            prn("b_impl", b_all, b_tmp);
        }

        return nkvOhneKR_induz(modifications, a, b, baukosten, b_all);

    }

    public static Double nkvOhneKR_induz(Modifications modifications, StreetBaseDataContainer streetBaseDataContainer) {
        Optional<Amounts> a = amountsFromStreetBaseData(streetBaseDataContainer);
        Optional<Benefits> b = benefitsFromStreetBaseData(streetBaseDataContainer);

        if (a.isEmpty() || b.isEmpty()) {
            return null;
        }

        Double baukosten = streetBaseDataContainer.getCostBenefitAnalysis()
                                                  .getCost()
                                                  .overallCosts();

        return nkvOhneKR_induz(modifications, a.get(), b.get(), baukosten);
    }

    private static double nkvOhneKR_induz(Modifications modifications, Amounts a, Benefits b, double baukosten) {
        return nkvOhneKR_induz(modifications, a, b, baukosten, b.all());
    }

    public static double nkvOhneKR_induz(Modifications modifications, Amounts a, Benefits b, double baukosten,
                                         double b_all) {
        prn("incoming", b_all, b_all);
        // co2 Bau
        {
            double b_tmp = b_all;
            b_all -= b.co2_infra;
            b_all += modifications.co2Price() / 145. * b.co2_infra;
            prn("b_co2_infra", b_all, b_tmp);
        }
        // co2 Betrieb

        // -- differential co2 by induz and other so that only induz can be multiplied:
        double b_co2_induz = b.co2_betrieb * a.fzkm_induz / a.fzkm_all;
        double b_co2_verl = b.co2_betrieb * a.fzkm_verl / a.fzkm_all;
        double b_co2_reroute = b.co2_betrieb * a.fzkm_reroute / a.fzkm_all;

        double co2_mult = b.co2_betrieb / a.fzkm_all;

        {
            double b_tmp = b_all;
            b_all -= b_co2_induz + b_co2_verl + b_co2_reroute;
            b_all += modifications.co2Price() / 145. * (b_co2_induz * modifications.induzFactor() + b_co2_verl + b_co2_reroute + co2_mult * modifications.mehrFzkm());
            prn("b_co2_betrieb", b_all, b_tmp);
        }

        // nkv:
        final double nkv = b_all / baukosten;
        String colorString = ConsoleColors.TEXT_BLACK;
        if (nkv < 1) {
            colorString = ConsoleColors.TEXT_RED;
        }
        log.info("\t\t\t\t\tnkv=" + colorString + nkv + ConsoleColors.TEXT_BLACK);
        return nkv;
    }

    private static void prn(String msg, double b_all, double b_tmp) {
        log.info(String.format("%1$13s: Korrektur = %2$5.0f; bb = %3$5.0f", msg, b_all - b_tmp, b_all));
    }

    private static Optional<Amounts> amountsFromStreetBaseData(StreetBaseDataContainer streetBaseDataContainer) {
        PhysicalEffectDataContainer.Effect tt = streetBaseDataContainer.getPhysicalEffect()
                                                                       .getTravelTimes();
        PhysicalEffectDataContainer.Effect vkm = streetBaseDataContainer.getPhysicalEffect()
                                                                        .getVehicleKilometers();

        if (tt == null || vkm == null) {
            return Optional.empty();
        }

        return Optional.of(new Amounts(tt.overall(), tt.induced(), tt.shifted(), vkm.overall(), vkm.induced(),
                vkm.shifted()));
    }

    private static Optional<Benefits> benefitsFromStreetBaseData(StreetBaseDataContainer streetBaseDataContainer) {
        // @formatter:off
        return Optional.ofNullable(streetBaseDataContainer).map(StreetBaseDataContainer::getCostBenefitAnalysis)
                       .map(cb -> new Benefits(cb.getNbOperations().overall(), cb.getNrz().overall(), cb.getNi().overall(),
                cb.getNl().overall(), cb.getNa().get(Emission.CO2).overall(), cb.getOverallBenefit().overall()));
        // @formatter:on
    }

    record Amounts(double rz, double rz_induz, double rz_verl, double fzkm_all, double fzkm_induz, double fzkm_verl,
                   double fzkm_reroute) {
        Amounts {
            Assertions.assertEquals(fzkm_all, fzkm_reroute + fzkm_induz + fzkm_verl);
        }

        Amounts(double rz, double rz_induz, double rz_verl, double fzkm_all, double fzkm_induz, double fzkm_verl) {
            this(rz, rz_induz, rz_verl, fzkm_all, fzkm_induz, fzkm_verl, fzkm_all - fzkm_induz - fzkm_verl);
        }
    }

    record Benefits(double fzkm, double rz, double impl, double co2_infra, double co2_betrieb, double all) {
    }

}
