package org.tub.vsp.bvwp;

import com.google.gson.reflect.TypeToken;
import org.tub.vsp.bvwp.data.container.base.StreetBaseDataContainer;
import org.tub.vsp.bvwp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.bvwp.io.JsonIo;
import org.tub.vsp.bvwp.io.StreetCsvWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RunCsvWriting {
    public static void main(String[] args) throws IOException {
        JsonIo jsonIo = new JsonIo();
        Type listType = new TypeToken<ArrayList<StreetBaseDataContainer>>() {
        }.getType();
        List<StreetBaseDataContainer> baseDataContainers = jsonIo.readJsonList("output/street_data.json", listType);

        StreetCsvWriter csvWriter = new StreetCsvWriter("output/street_data.csv");
        csvWriter.writeCsv(baseDataContainers.stream()
                                             .map(StreetAnalysisDataContainer::new)
                                             .toList());
    }
}
