package org.tub.vsp.bvwp.io;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.Headers;
import org.tub.vsp.bvwp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.bvwp.data.container.base.street.StreetBaseDataContainer;
import org.tub.vsp.bvwp.data.container.base.street.StreetCostBenefitAnalysisDataContainer;
import org.tub.vsp.bvwp.data.type.Benefit;
import org.tub.vsp.bvwp.data.type.Cost;
import org.tub.vsp.bvwp.data.type.Emission;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class StreetCsvWriter {
    private static final Logger logger = LogManager.getLogger(StreetCsvWriter.class);
    private final String outputPath;

    private static final Map<Emission, String> EMISSION_COLUMNS = initMap();
    private final Table table;

    private static Map<Emission, String> initMap() {
        Map<Emission, String> map = new LinkedHashMap<>();
        map.put(Emission.CO2, "co2");
        map.put(Emission.CO, "co");
        map.put(Emission.NOX, "nox");
        map.put(Emission.PM, "pm");
        map.put(Emission.HC, "hc");
        map.put(Emission.SO2, "so2");
        return map;
    }

    public StreetCsvWriter(String outputPath) {
        this.outputPath = outputPath;
        this.table = Table.create("defaultTable");
        // (I am somewhat abusing this class to also generate a TableSaw Table.  Probably would make sense later to
        // first genrate the Table object,
        // and then csv-write that.  kai, feb'24)
    }

    public Table writeCsv(List<StreetAnalysisDataContainer> analysisDataContainers) {
        logger.info("Writing csv.");
        List<String> headers = getHeaders(analysisDataContainers, table);

        //make sure that the directory exists
        Path path = Paths.get(outputPath);
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            logger.error("Could not create directory for file {}", path);
            throw new RuntimeException(e);
        }

        try (
                BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputPath));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.Builder.create()
                                                                                .setNullString("")
                                                                                .setHeader(headers.toArray(new String[0]))
                                                                                .setDelimiter(';')
                                                                                .build())
        ) {
            for (StreetAnalysisDataContainer analysisDataContainer : analysisDataContainers) {
                logger.info("Writing csv record for {}", analysisDataContainer.getStreetBaseDataContainer().getUrl());
                final List<Object> csvRecord = getCsvRecord(analysisDataContainer, table);
                csvPrinter.printRecord(csvRecord);
                {
                    StringBuilder strb = new StringBuilder();
                    for (int ii = 2; ii < csvRecord.size(); ii++) {
                        strb.append(csvRecord.get(ii)).append(";");
                    }
                    System.out.println(strb);
                }
            }
            csvPrinter.flush();
            logger.info("Finished writing csv.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return table;
    }

    private static class Record {
        private final Row row;
        private final List<Object> record = new ArrayList<>();
        private final Table table;
        int ii = 0;

        private Record(Table table) {
            this.row = table.appendRow();
            this.table = table;
        }

        //        private Record add( String str ) {
//            row.setString( ii, str );
//            record.add( str );
//            ii++;
//            return this;
//        }
        private Record add(String key, String str) {

//            if ( !table.columnNames().contains( key ) ) {
//                table.addColumns( StringColumn.create( key ) );
//            }
            // not working (I think)

            row.setString(key, str);
            record.add(str);
            ii++;
            return this;
        }

        //        private Record add( Double dbl ) {
//            if ( dbl != null ){
//                row.setDouble( ii, dbl );
//            } else {
//                row.setDouble( ii, Double.NaN );
//            }
//            record.add( dbl );
//            ii++;
//            return this;
//        }
        private Record add(String key, Double dbl) {

//            if ( !table.columnNames().contains( key ) ) {
//                table.addColumns( DoubleColumn.create( key ) );
//            }
            // not working (I think)

            if (dbl != null) {
                row.setDouble(key, dbl);
            } else {
                row.setDouble(key, Double.NaN);
            }
            record.add(dbl);
            ii++;
            return this;
        }
//        private Record addAll( Iterable<?> objs ) {
//            for( Object obj : objs ){
//                if ( obj==null ) {
//                    this.add( Double.NaN );
//                } else if ( obj instanceof  String ) {
//                    this.add( (String) obj );
//                } else if ( obj instanceof  Double ) {
//                    this.add( (Double) obj );
//                } else {
//                    throw new RuntimeException( "not implemented; obj=" + obj );
//                }
//            }
//            return this;
//        }
    }

    private static List<Object> getCsvRecord(StreetAnalysisDataContainer analysisDataContainer, Table table) {
        StreetBaseDataContainer baseDataContainer = analysisDataContainer.getStreetBaseDataContainer();

        Record record = new Record(table);

        //general info
        record.add(Headers.PROJECT_NAME, baseDataContainer.getProjectInformation().getProjectNumber());
        record.add(Headers.LINK, baseDataContainer.getUrl());
        record.add(Headers.EINSTUFUNG, baseDataContainer.getProjectInformation().getPriority().name() );
        record.add(Headers.BAUTYP, baseDataContainer.getProjectInformation().getBautyp().name());

        record.add(Headers.LENGTH, baseDataContainer.getProjectInformation().getLength());

        record.add(Headers.ADDTL_PKWKM_ORIG, baseDataContainer.getPhysicalEffect().getVehicleKilometers().overall());
        record.add(Headers.ADDTL_PKWKM_INDUZ_ORIG, Optional.ofNullable(baseDataContainer.getPhysicalEffect().getVehicleKilometers().induced() ).orElse(0. ) );

        record.add(Headers.B_PER_KM, baseDataContainer.getCostBenefitAnalysis().getNbOperations().overall());

        //co2 equivalents
        record.add(Headers.CO_2_EQUIVALENTS_EMISSIONS, baseDataContainer.getPhysicalEffect().getEmissionsDataContainer().co2Overall());
//        record.add(Headers.B_CO_2_EQUIVALENTS_ANNUAL, baseDataContainer.getCostBenefitAnalysis()
//                                                                       .getCo2EquivalentBenefit()
//                                                                       .annual()); // yyyy not needed
        record.add(Headers.B_CO_2_EQUIVALENTS_ORIG, baseDataContainer.getCostBenefitAnalysis().getCo2EquivalentBenefit().overall());

        //emissions
//        for (Emission emission : EMISSION_COLUMNS.keySet()) {
//            addEmissionsAnnualOverallBenefit(baseDataContainer, record, emission);
//        }

        //overall benefit and cost
        record.add(Headers.B_OVERALL, Optional.ofNullable(baseDataContainer.getCostBenefitAnalysis())
                                              .map(StreetCostBenefitAnalysisDataContainer::getOverallBenefit)
                                              .map(Benefit::overall)
                                              .orElse(null));
        record.add(Headers.COST_OVERALL, Optional.ofNullable(baseDataContainer.getCostBenefitAnalysis())
                                                 .map(StreetCostBenefitAnalysisDataContainer::getCost)
                                                 .map(Cost::overallCosts)
                                                 .orElse(null));
        // (yy warum diese aufwändige Syntax?  kai, feb'24)
        // --> da sowohl getCostBenefitAnalysis, getCost als auch overallCosts null zurückgeben können, wenn die
        // Daten nicht vorhanden sind. So spart man sich null checks (paul, feb'24)

        for (Map.Entry<String, Double> entry : analysisDataContainer.getColumns().entrySet()) {
            record.add(entry.getKey(), entry.getValue());
        }

        int ii = 0;
        for (String remark : analysisDataContainer.getRemarks()) {
            record.add("remark_" + ii, "\"" + remark + "\"");
            ii++;
        }

        return record.record;
    }

    private static class HeadersAdder {
        private final Table table;
        private final List<String> headers = new ArrayList<>();

        private HeadersAdder(Table table) {
            this.table = table;
        }

        private HeadersAdder addStringColumn(String name) {
            headers.add(name);
            table.addColumns(StringColumn.create(name));
            return this;
        }

        private HeadersAdder addDoubleColumn(String name) {
            headers.add(name);
            table.addColumns(DoubleColumn.create(name));
            return this;
        }
    }

    private static List<String> getHeaders(List<StreetAnalysisDataContainer> analysisDataContainers, Table table) {
        //assert that all entries of new nkv have the same keys
        assert analysisDataContainers.stream()
                                     .allMatch(a -> {
                                         Set<String> thisKeys = a.getColumns().keySet();
                                         Set<String> firstKeys = analysisDataContainers.getFirst().getColumns()
                                                                                       .keySet();
                                         return thisKeys.containsAll(firstKeys) && thisKeys.size() == firstKeys.size();
                                     }) : "Not all nkv have the same keys";

        HeadersAdder headers = new HeadersAdder(table);
        headers.addStringColumn(Headers.PROJECT_NAME);

        headers.addStringColumn(Headers.LINK);
        headers.addStringColumn( Headers.EINSTUFUNG );
        headers.addStringColumn(Headers.BAUTYP);
        headers.addDoubleColumn(Headers.LENGTH);

        headers.addDoubleColumn( Headers.ADDTL_PKWKM_ORIG );
        headers.addDoubleColumn(Headers.ADDTL_PKWKM_INDUZ_ORIG );
//        headers.addDoubleColumn( Headers.PKWKM_INDUZ_NEU ); // added by automagic
        headers.addDoubleColumn(Headers.B_FZKM);

        headers.addDoubleColumn(Headers.CO_2_EQUIVALENTS_EMISSIONS);
//        headers.addDoubleColumn( Headers.B_CO_2_EQUIVALENTS_ANNUAL );
        headers.addDoubleColumn( Headers.B_CO_2_EQUIVALENTS_ORIG );

//        for (String colName : EMISSION_COLUMNS.values()) {
//            headers.add(colName + "-emissions");
//            headers.add(colName + "-annual");
//            headers.add(colName + "-overall");
//        }

        headers.addDoubleColumn(Headers.B_OVERALL);
        headers.addDoubleColumn(Headers.COST_OVERALL);

        for (String s : analysisDataContainers.getFirst()
                                              .getColumns()
                                              .keySet()) {
            headers.addDoubleColumn(s);
        }

        for (int ii = 0; ii < 5; ii++) {
            headers.addStringColumn("remark_" + ii);
        }

        return headers.headers;
    }

    private static void addEmissionsAnnualOverallBenefit(StreetBaseDataContainer baseDataContainer,
                                                         List<Object> record, Emission emission) {
        record.add(baseDataContainer.getPhysicalEffect()
                                    .getKfzEmission(emission));
        record.add(Optional.ofNullable(baseDataContainer.getCostBenefitAnalysis())
                           .map(StreetCostBenefitAnalysisDataContainer::getNa)
                           .map(m -> m.get(emission))
                           .map(Benefit::annual)
                           .orElse(null));
        record.add(Optional.ofNullable(baseDataContainer.getCostBenefitAnalysis())
                           .map(StreetCostBenefitAnalysisDataContainer::getNa)
                           .map(m -> m.get(emission))
                           .map(Benefit::overall)
                           .orElse(null));
    }
}
