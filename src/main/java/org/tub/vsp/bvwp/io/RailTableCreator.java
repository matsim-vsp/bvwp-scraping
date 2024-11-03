package org.tub.vsp.bvwp.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.Headers;
import org.tub.vsp.bvwp.data.container.analysis.RailAnalysisDataContainer;
import org.tub.vsp.bvwp.data.container.base.rail.RailBaseDataContainer;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RailTableCreator {
	private static final Logger logger = LogManager.getLogger(StreetCsvWriter.class);

	private final Table table;

	public RailTableCreator() {
		this.table = Table.create("defaultTable");
		// (I am somewhat abusing this class to also generate a TableSaw Table.  Probably would make sense later to
		// first genrate the Table object,
		// and then csv-write that.  kai, feb'24)
	}

	public Table computeTable(List<RailAnalysisDataContainer> analysisDataContainers) {
		addHeaders(analysisDataContainers);

		for (RailAnalysisDataContainer analysisDataContainer : analysisDataContainers) {
			getCsvRecord(analysisDataContainer, table);
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
	}

	private static List<Object> getCsvRecord( RailAnalysisDataContainer analysisDataContainer, Table table ) {
		RailBaseDataContainer baseDataContainer = analysisDataContainer.getBaseDataContainer();

		Record record = new Record(table);

		//general info
		record.add(Headers.PROJECT_NAME, baseDataContainer.getProjectInformation().getProjectNumber());
		record.add(Headers.LINK, baseDataContainer.getUrl());
		record.add(Headers.EINSTUFUNG, baseDataContainer.getProjectInformation().getPriority().name() );

		record.add(Headers.LENGTH, baseDataContainer.getProjectInformation().getLength());

		//overall benefit and cost
		record.add(Headers.B_OVERALL_ORIG, baseDataContainer.getCostBenefitAnalysis().getOverallBenefit().overall() );
		record.add(Headers.INVCOST_ORIG, baseDataContainer.getCostBenefitAnalysis().getCost().overallCosts());

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

	private void addHeaders(List<RailAnalysisDataContainer> analysisDataContainers) {
		//assert that all entries of new nkv have the same keys
		assert analysisDataContainers.stream()
					     .allMatch(a -> {
						     Set<String> thisKeys = a.getColumns().keySet();
						     Set<String> firstKeys = analysisDataContainers.getFirst().getColumns()
												   .keySet();
						     return thisKeys.containsAll(firstKeys) && thisKeys.size() == firstKeys.size();
					     }) : "Not all nkv have the same keys";

		table.addColumns(StringColumn.create(Headers.PROJECT_NAME));

		table.addColumns(StringColumn.create(Headers.LINK));
		table.addColumns(StringColumn.create(Headers.EINSTUFUNG));
		table.addColumns(StringColumn.create(Headers.BAUTYP));
		table.addColumns(DoubleColumn.create(Headers.LENGTH));

		table.addColumns(DoubleColumn.create(Headers.ADDTL_PKWKM_ORIG));
		table.addColumns(DoubleColumn.create(Headers.ADDTL_PKWKM_INDUZ_ORIG));
		table.addColumns(DoubleColumn.create(Headers.B_FZKM));

		table.addColumns(DoubleColumn.create(Headers.CO_2_EQUIVALENTS_EMISSIONS));
		//        headers.addDoubleColumn( Headers.B_CO_2_EQUIVALENTS_ANNUAL );
		table.addColumns(DoubleColumn.create(Headers.B_CO_2_EQUIVALENTS_ORIG));

//        for (String colName : EMISSION_COLUMNS.values()) {
//            headers.add(colName + "-emissions");
//            headers.add(colName + "-annual");
//            headers.add(colName + "-overall");
//        }

		table.addColumns(DoubleColumn.create(Headers.B_OVERALL_ORIG ) );
		table.addColumns(DoubleColumn.create(Headers.INVCOST_ORIG));

		for (String s : analysisDataContainers.getFirst()
						      .getColumns()
						      .keySet()) {
			table.addColumns(DoubleColumn.create(s));
		}

		for (int ii = 0; ii < 5; ii++) {
			table.addColumns(StringColumn.create("remark_" + ii));
		}

	}
}
