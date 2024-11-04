package org.tub.vsp.bvwp;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.Headers;
import tech.tablesaw.api.Table;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class BvwpUtils{
	private static Logger log = LogManager.getLogger( BvwpUtils.class );

	public static final String SEPARATOR = System.lineSeparator() + "===========================================";

//	private static final String CONSTRUCTION_COST_COLUMN = "BMF.Gesamtprojektkosten2022-Preis_2022-zusammengefasst_zu_NKA-Projekten";
	private static final String INVESTMENT_COST_COLUMN = "NKA.Summe bewertungsrelevanter Investitionskosten-Barwert_skalliert";

	private BvwpUtils(){} // do not instantiate

	public static String getPositivListe(){
	    return projectString( "BW", "A5" )
				   + projectString( "BW", "A6" )
				   + projectString( "BY", "A003" )
				   + projectString( "BY", "A008" ) // Ausbau München - Traunstein (- Salzburg)
				   + projectString( "BY", "A009" )
				   + projectString( "BY", "A092" )
				   + projectString( "BY", "A094" )
				   + projectString( "BY", "A099" )
				   + projectString( "HB", "A27" )
				   + projectString( "HE", "A3" )
				   + projectString( "HE", "A5" )
				   + projectString( "HE", "A45" )
				   + projectString( "HE", "A60" )
				   + projectString( "HE", "A67" )
				   + projectString( "HE", "A67" )
				   + projectString( "NI", "A2" )
				   + projectString( "NI", "A7" )
				   + projectString( "NI", "A27" )
				   + projectString( "NI", "A30" )
				   + projectString( "NW", "A1" )
				   + projectString( "NW", "A2" )
				   + projectString( "NW", "A3" )
				   + projectString( "NW", "A4" )
				   + projectString( "NW", "A30" )
				   + projectString( "NW", "A40" )
				   + projectString( "NW", "A42" )
				   + projectString( "NW", "A43" )
				   + projectString( "NW", "A45" )
				   + projectString( "NW", "A52" )
				   + projectString( "NW", "A57" )
				   + projectString( "NW", "A59" )
				   + projectString( "NW", "A559" )
				   + projectString( "RP", "A1" )
				   + projectString( "RP", "A1" )
				   + "(abcdef)"; // um das letzte "|" abzufangen
	}

	public static Table extractPriorityTable(Table table, String priority) {
		return table.where(table.stringColumn(Headers.EINSTUFUNG ).isEqualTo(priority ) );
	}

	public static String projectString(String bundesland, String road) {
	    return "(" + road + "-.*-" + bundesland + ".html" + ")|";
	}
	/**
	 * A function, that gets the path of a file and a column name. It returns a map that has as key the values of
	 * "PRINS.Projektnummer" and as value the values of the given column.
	 */
	public static Map<String, Double> getConstructionCostsFromTudFile( String pathToSharedSvn ) {
		Map<String, Double> resultMap = new HashMap<>();

		File csvFile = Paths.get(pathToSharedSvn, "/projects/unotrans/PRINS_BMF-Baukosten_aktualisiert_v02_rh-20240412.csv").toFile();

		try (BufferedReader br =
					 new BufferedReader(new InputStreamReader(new BOMInputStream(new FileInputStream(csvFile)), StandardCharsets.UTF_8))) {

			CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().withDelimiter(';').parse(br);

			// Get the index of the required columns
			int prinsProjektnummerIndex = csvParser.getHeaderMap().get("PRINS.Projektnummer");
			int columnIndex = csvParser.getHeaderMap().get( INVESTMENT_COST_COLUMN );

			// If the required columns are not found, return an empty map
			if (prinsProjektnummerIndex == -1 || columnIndex == -1) {
				throw new RuntimeException("The required columns are not found in the file.");
			}

			// Iterate over the records and extract the required values
			for (CSVRecord record : csvParser) {
				String prinsProjektnummer = record.get(prinsProjektnummerIndex);

				Double columnValue = Optional.of(record.get(columnIndex))
							     .filter(s -> !s.isEmpty())
//											 .map(Double::valueOf)
							     .map( JSoupUtils::parseDoubleOrElseNull)
							     .orElse(-1.);

				Double before = resultMap.put(prinsProjektnummer, columnValue);
				assert Objects.isNull(before) : "Duplicate PRINS.Projektnummer found: " + prinsProjektnummer;
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("The TUM construction costs file is not found.", e);
		} catch ( IOException e ) {
			throw new RuntimeException("An error occurred while reading the TUM construction costs file.", e);
		}

		return resultMap;
	}
	public static boolean assertNotNaN( String msg, double val ) {
		if ( Double.isNaN( val ) ) {
			log.warn( "not a regular number: " + msg + "=" + val );
			return true;
		}
		return false;
	}
}
