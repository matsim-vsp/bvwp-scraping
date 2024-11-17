package org.tub.vsp.bvwp.users.kn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.Headers;
import org.tub.vsp.bvwp.data.HeadersKN;
import org.tub.vsp.bvwp.data.container.analysis.RailAnalysisDataContainer;
import org.tub.vsp.bvwp.io.RailTableCreator;
import org.tub.vsp.bvwp.plot.MultiPlotUtils;
import org.tub.vsp.bvwp.scraping.RailScraper;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.display.Browser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RunLocalRailScrapingKN{
    private static final Logger logger = LogManager.getLogger( RunLocalRailScrapingKN.class );

    public static void main(String[] args) throws IOException {
        RailScraper scraper = new RailScraper();

        logger.info("Starting scraping");
        List<RailAnalysisDataContainer> allRailData = scraper
                                                                      .extractAllLocalBaseData("./data/rail/all", "", "^2.*", "")
                                                                      .stream()
                                                                      .map(RailAnalysisDataContainer::new)
                                                                      .toList();

        Table table = new RailTableCreator().computeTable(allRailData );

        // ===

        Figures1RailKN figures1 = new Figures1RailKN( table, Headers.addCap( 5, table, HeadersKN.NKV_ORIG ) );
//        Figures2KN figures2 = new Figures2KN( table );

        List<Figure> plots1 = new ArrayList<>();
        List<Figure> plots2 = new ArrayList<>();

        // ===

//        plots1.add( figures1.nkv_orig() );
//        plots1.add( figures1.nkv_carbon700() );

        plots1.add( figures1.invCost_orig());
        plots1.add( figures1.invCost_orig_cumulative());

//        plots1.add( figures1.fzkmFromTtime_vs_fzkmOrig() );
//        plots1.add( figures1.fzkmFromTtimeSum_vs_fzkmOrig() );

//        plots1.add( figures1.nkv_el03() );
//        plots1.add( figures1.nkv_carbon700() );
//        plots1.add( figures1.nkv_el03_carbon700() );
//
//        plots1.add( figures1.elasticities() );
//        plots1.add( figures1.fzkmDiff() );
//        plots1.add( figures1.carbon() );
//        plots1.add( figures1.nkv_el03_diff() );
//        plots1.add( figures1.dtv() );
//        plots1.add( figures1.fzkmNew() );

        // ===

        StringBuilder page = new StringBuilder( MultiPlotUtils.pageTop() + System.lineSeparator() );
        for( int ii=0; ii<plots1.size(); ii++ ) {
            page.append( plots1.get( ii ).asJavascript( "plot" + (ii + 1) ) ).append( System.lineSeparator() );
        }
        for( int ii=0; ii<plots2.size(); ii++ ) {
            final char c = (char) (ii + 65); // generate A, B, ... to be backwards compatible with what we had so far.  kai, mar'24
            page.append( plots2.get( ii ).asJavascript( "plot" + c ) ).append( System.lineSeparator() );
        }
        page.append( MultiPlotUtils.pageBottom );

        File outputFile = Paths.get("multiplot.html" ).toFile();

        try ( FileWriter fileWriter = new FileWriter(outputFile)) {
            fileWriter.write( page.toString() );
        }

        new Browser().browse(outputFile );

        // ===


    }
}
