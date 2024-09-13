package org.tub.vsp.bvwp.users.ph;

import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.bvwp.data.container.base.street.StreetBaseDataContainer;
import org.tub.vsp.bvwp.data.container.base.street.StreetEnvironmentalDataContainer;
import org.tub.vsp.bvwp.data.type.EnvironmentalCriteria;
import org.tub.vsp.bvwp.scraping.StreetScraper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RunEnvironmentalCsvWriting {
    public static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(RunEnvironmentalCsvWriting.class);

    public static void main(String[] args) {
        List<StreetAnalysisDataContainer> allStreetBaseData =
                new StreetScraper().extractAllLocalBaseData("./data/street/all", "A", ".*", "")
                                   .stream()
                                   .map(s -> new StreetAnalysisDataContainer(s, 0))
                                   .toList();

        writeCsv(allStreetBaseData, "output/env_street_data.csv");
    }

    private static void writeCsv(List<StreetAnalysisDataContainer> streetAnalysisDataContainers, String fileName) {
        List<List<String>> csvData = new ArrayList<>();
        csvData.add(getHeaders());
        for (StreetAnalysisDataContainer streetAnalysisDataContainer : streetAnalysisDataContainers) {
            csvData.add(getEntry(streetAnalysisDataContainer.getStreetBaseDataContainer()));
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (List<String> row : csvData) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getHeaders() {
        List<String> headers = new ArrayList<>();
        headers.add("name");
        for (int i = 21; i < 30; i++) {
            if (i == 22) {
                headers.add(i + " bewertung");
                for (int j = 1; j <= 2; j++) {
                    headers.add(i + " " + j + ") abs");
                    headers.add(i + " " + j + ") betroffenheit");
                }
            } else if (i == 24) {
                headers.add(i + " bewertung");
                for (int j = 1; j <= 4; j++) {
                    headers.add(i + " " + j + ") abs");
                    headers.add(i + " " + j + ") betroffenheit");
                }
            } else {
                headers.add(i + " abs");
                headers.add(i + " betroffenheit");
                headers.add(i + " bewertung");
            }
        }
        return headers;
    }

    private static List<String> getEntry(StreetBaseDataContainer streetBaseDataContainer) {
        List<String> entries = new ArrayList<>();
        String projectNumber = streetBaseDataContainer.getProjectInformation().getProjectNumber();

        logger.info("While CSV writing. Processing project: {}", projectNumber);

        entries.add(projectNumber);

        StreetEnvironmentalDataContainer envCrit = streetBaseDataContainer.getEnvironmentalCriteria();

        if (envCrit == null) {
            logger.info("No environmental criteria.");
            for (int i = 0; i < getHeaders().size() - 1; i++) {
                entries.add(null);
            }
            return entries;
        }

        addEntries(entries, envCrit.getNaturschutzVorrangflaechen21());
        addEntries(entries, envCrit.getNatura2000Gebiete22());
        addEntries(entries, envCrit.getUnzerschnitteneKernraeume23());
        addEntries(entries, envCrit.getUnzerschnitteneGrossraeume24());
        addEntries(entries, envCrit.getFlaechenInanspruchnahme25());
        addEntries(entries, envCrit.getUeberschwemmungsgebiete26());
        addEntries(entries, envCrit.getWasserschutzgebiete27());
        addEntries(entries, envCrit.getVerkehrsarmeRaeume28());
        addEntries(entries, envCrit.getKulturLandschaftsschutz29());

        return entries;
    }

    private static void addEntries(List<String> result, EnvironmentalCriteria criteria) {
        Objects.requireNonNull(criteria);

        if (criteria.description().size() > 1) {
            result.add(Optional.ofNullable(criteria.bewertung()).map(Enum::toString).orElse(null));
            for (EnvironmentalCriteria.Description description : criteria.description()) {
                result.add(String.valueOf(description.absolute()));
                result.add(String.valueOf(description.betroffenheit()));
            }
        } else {
            result.add(String.valueOf(criteria.description().getFirst().absolute()));
            result.add(String.valueOf(criteria.description().getFirst().betroffenheit()));
            result.add(Optional.ofNullable(criteria.bewertung()).map(Enum::toString).orElse(null));
        }


    }
}
