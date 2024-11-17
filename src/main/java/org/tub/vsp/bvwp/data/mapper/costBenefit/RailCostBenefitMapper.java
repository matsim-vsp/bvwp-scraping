package org.tub.vsp.bvwp.data.mapper.costBenefit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tub.vsp.bvwp.JSoupUtils;
import org.tub.vsp.bvwp.data.container.base.rail.RailBenefitFreightDataContainer;
import org.tub.vsp.bvwp.data.container.base.rail.RailBenefitPassengerDataContainer;
import org.tub.vsp.bvwp.data.container.base.rail.RailCostBenefitAnalysisDataContainer;
import org.tub.vsp.bvwp.data.type.Benefit;
import org.tub.vsp.bvwp.data.type.InvestmentCosts;

import java.util.Optional;

public class RailCostBenefitMapper {
    private static final Logger logger = LogManager.getLogger(RailCostBenefitMapper.class);

    public static RailCostBenefitAnalysisDataContainer mapDocument(Document document) {
        RailBenefitPassengerDataContainer passengerDataContainer = new RailBenefitPassengerDataContainer();
        RailBenefitFreightDataContainer freightDataContainer = new RailBenefitFreightDataContainer();
        RailCostBenefitAnalysisDataContainer result = new RailCostBenefitAnalysisDataContainer();

        Optional<Element> passengerBenefit = JSoupUtils.getTableByCssKeyAndPredicate(document, "table.table_webprins",
                RailCostBenefitMapper::isPassengerBenefitTable);

        Optional<Element> freightBenefit = JSoupUtils.getTableByCssKeyAndPredicate(document, "table.table_webprins",
                RailCostBenefitMapper::isFreightBenefitTable);

        Optional<Element> otherBenefit = JSoupUtils.getTableByCssKeyAndPredicate(document, "table.table_webprins",
                RailCostBenefitMapper::isOtherBenefitTable);

        Optional<Element> costTable = JSoupUtils.getTableByCssKeyAndPredicate(document, "table.table_kosten",
                RailCostBenefitMapper::isCostTable);

        passengerBenefit.ifPresent(table -> {
            setNb(table, passengerDataContainer);
            setNa(table, passengerDataContainer);
            setNs(table, passengerDataContainer);
            setNrz(table, passengerDataContainer);
            setNi(table, passengerDataContainer);
            setOverallCost(table, passengerDataContainer);
        });
        result.setPassengerBenefits(passengerDataContainer);

        freightBenefit.ifPresent(table -> {
            setNb(table, freightDataContainer);
            setNa(table, freightDataContainer);
            setNs(table, freightDataContainer);
            setNtz(table, freightDataContainer);
            setNi(table, freightDataContainer);
            setNz(table, freightDataContainer);
        });
        result.setFreightBenefits(freightDataContainer);

        otherBenefit.ifPresent(table -> {
            setNl(table, result);
            setOverallBenefit(table, result);
        });

        InvestmentCosts investmentCosts = costTable.map(CostBenefitMapperUtils::extractCosts ).orElse(null );
        result.setCost( investmentCosts );

        return result;
    }

    private static void setNb(Element table, RailBenefitPassengerDataContainer passengerTrafficDataContainer) {
        getFirstRowIndexWithBenefitKey(table, "NB").ifPresent(nb -> {
            Elements rows = table.select("tr");
            passengerTrafficDataContainer.setNbPkw(getBenefitFromRow(rows.get(nb + 1)));
            passengerTrafficDataContainer.setNbSpv(getBenefitFromRow(rows.get(nb + 2)));
            passengerTrafficDataContainer.setNbLuft(getBenefitFromRow(rows.get(nb + 3)));
        });
    }

    private static void setNb(Element table, RailBenefitFreightDataContainer freightDataContainer) {
        getFirstRowIndexWithBenefitKey(table, "NB").ifPresent(nb -> {
            Elements rows = table.select("tr");
            freightDataContainer.setNbLkw(getBenefitFromRow(rows.get(nb + 1)));
            freightDataContainer.setNbSchiene(getBenefitFromRow(rows.get(nb + 2)));
            freightDataContainer.setNbSchiff(getBenefitFromRow(rows.get(nb + 3)));
        });
    }

    private static void setNa(Element table, RailBenefitPassengerDataContainer passengerTrafficDataContainer) {
        getFirstRowIndexWithBenefitKey(table, "NA").ifPresent(na -> {
            Elements rows = table.select("tr");
            passengerTrafficDataContainer.setNaPkw(getBenefitFromRow(rows.get(na + 1)));
            passengerTrafficDataContainer.setNaSpv(getBenefitFromRow(rows.get(na + 2)));
            passengerTrafficDataContainer.setNaLuft(getBenefitFromRow(rows.get(na + 3)));
        });
    }

    private static void setNa(Element table, RailBenefitFreightDataContainer freightDataContainer) {
        getFirstRowIndexWithBenefitKey(table, "NA").ifPresent(nb -> {
            Elements rows = table.select("tr");
            freightDataContainer.setNaLkw(getBenefitFromRow(rows.get(nb + 1)));
            freightDataContainer.setNaSchiene(getBenefitFromRow(rows.get(nb + 2)));
            freightDataContainer.setNaSchiff(getBenefitFromRow(rows.get(nb + 3)));
        });
    }

    private static void setNs(Element table, RailBenefitPassengerDataContainer passengerTrafficDataContainer) {
        getFirstRowIndexWithBenefitKey(table, "NS").ifPresent(ns -> {
            Elements rows = table.select("tr");
            passengerTrafficDataContainer.setNsPkw(getBenefitFromRow(rows.get(ns + 1)));
            passengerTrafficDataContainer.setNsSpv(getBenefitFromRow(rows.get(ns + 2)));
        });
    }

    private static void setNs(Element table, RailBenefitFreightDataContainer freightDataContainer) {
        getFirstRowIndexWithBenefitKey(table, "NS").ifPresent(nb -> {
            Elements rows = table.select("tr");
            freightDataContainer.setNsLkw(getBenefitFromRow(rows.get(nb + 1)));
            freightDataContainer.setNsSchiene(getBenefitFromRow(rows.get(nb + 2)));
            freightDataContainer.setNsSchiff(getBenefitFromRow(rows.get(nb + 3)));
        });
    }

    private static void setNrz(Element table, RailBenefitPassengerDataContainer passengerTrafficDataContainer) {
        getFirstRowIndexWithBenefitKey(table, "NRZ").ifPresent(nrz -> {
            Elements rows = table.select("tr");
            passengerTrafficDataContainer.setNrzVerbVerkehr(getBenefitFromRow(rows.get(nrz + 1)));
            passengerTrafficDataContainer.setNrzInduzVerkehr(getBenefitFromRow(rows.get(nrz + 2)));
            passengerTrafficDataContainer.setNrzVerlagerungPkwSpv(getBenefitFromRow(rows.get(nrz + 3)));
            passengerTrafficDataContainer.setNrzVerlagerungLuftSpv(getBenefitFromRow(rows.get(nrz + 4)));
        });
    }

    private static void setNtz(Element table, RailBenefitFreightDataContainer freightDataContainer) {
        getFirstRowIndexWithBenefitKey(table, "NTZ").ifPresent(nrz -> {
            Elements rows = table.select("tr");
            freightDataContainer.setNtzVerbVerkehr(getBenefitFromRow(rows.get(nrz + 1)));
            freightDataContainer.setNtzLkwSchiene(getBenefitFromRow(rows.get(nrz + 2)));
            freightDataContainer.setNtzSchiffSchiene(getBenefitFromRow(rows.get(nrz + 3)));
        });
    }

    private static void setNi(Element table, RailBenefitPassengerDataContainer passengerTrafficDataContainer) {
        getFirstRowIndexWithBenefitKey(table, "NI").ifPresent(ni -> {
            Elements rows = table.select("tr");
            passengerTrafficDataContainer.setNiInduzVerkehr(getBenefitFromRow(rows.get(ni + 1)));
            passengerTrafficDataContainer.setNiVerlagerungPkwSpv(getBenefitFromRow(rows.get(ni + 2)));
            passengerTrafficDataContainer.setNiVerlagerungLuftSpv(getBenefitFromRow(rows.get(ni + 3)));
        });
    }

    private static void setNi(Element table, RailBenefitFreightDataContainer freightDataContainer) {
        getFirstRowIndexWithBenefitKey(table, "NI").ifPresent(ni -> {
            Elements rows = table.select("tr");
            freightDataContainer.setNiLkwSchiene(getBenefitFromRow(rows.get(ni + 1)));
            freightDataContainer.setNiSchiffSchiene(getBenefitFromRow(rows.get(ni + 2)));
        });
    }

    private static void setNz(Element table, RailBenefitFreightDataContainer freightDataContainer) {
        getFirstRowIndexWithBenefitKey(table, "NZ").ifPresent(nz -> {
            Elements rows = table.select("tr");
            freightDataContainer.setNzVerbVerkehr(getBenefitFromRow(rows.get(nz + 1)));
        });
    }

    private static void setNl(Element table, RailCostBenefitAnalysisDataContainer result) {
        getFirstRowIndexWithBenefitKey(table, "NL").ifPresent(nl -> {
            Elements rows = table.select("tr");
            result.setNl(getBenefitFromRow(rows.get(nl)));
        });
    }

    private static void setOverallBenefit(Element table, RailCostBenefitAnalysisDataContainer result) {
        Element row = JSoupUtils.firstRowWithKeyMatchesInCol(table, "Summe Nutzen", 0)
                                .orElseThrow();
        result.setOverallBenefit(getBenefitFromRow(row));
    }

    private static Optional<Integer> getFirstRowIndexWithBenefitKey(Element table, String key) {
        Optional<Integer> ni = JSoupUtils.getFirstRowIndexWithTextInCol(table, key, 1);
        if (ni.isEmpty()) {
            logger.warn("Could not find cost benefit for key {}.", key);
        }
        return ni;
    }

    private static void setOverallCost(Element table,
                                       RailBenefitPassengerDataContainer passengerTrafficDataContainer) {
        Element row = JSoupUtils.firstRowWithKeyContainedInCol(table, "Summe Nutzen Personenverkehr", 0)
                                .orElseThrow();

        passengerTrafficDataContainer.setOverallBenefit(getBenefitFromRow(row));

    }

    private static Benefit getBenefitFromRow(Element row) {
        Optional<Benefit> optionalBenefit = CostBenefitMapperUtils.extractBenefitFromRow(row);
        if (optionalBenefit.isEmpty()) {
            logger.warn("Could not find cost benefit for key {}.", "NB");
            return null;
        }
        return optionalBenefit.get();
    }

    private static boolean isPassengerBenefitTable(Element element) {
        return element.select("tr")
                      .get(0)
                      .text()
                      .contains("Nutzenkomponenten des Personenverkehrs");
    }

    private static boolean isFreightBenefitTable(Element element) {
        return element.select("tr")
                      .get(0)
                      .text()
                      .contains("Nutzenkomponenten des Güterverkehrs");
    }

    private static boolean isOtherBenefitTable(Element element) {
        return element.select("tr")
                      .get(0)
                      .text()
                      .contains("Sonstige Nutzenkomponenten");
    }

    private static boolean isCostTable(Element element) {
        if (element.select("tr").size() < 4) {
            return false;
        }
        return element.select("tr")
                      .get(3)
                      .text()
                      .contains("Summe bewertungsrelevante Investitionskosten");
    }
}
