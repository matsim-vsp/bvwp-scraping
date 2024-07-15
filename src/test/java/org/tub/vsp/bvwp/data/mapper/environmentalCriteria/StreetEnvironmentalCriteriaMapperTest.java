package org.tub.vsp.bvwp.data.mapper.environmentalCriteria;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.bvwp.data.LocalFileAccessor;
import org.tub.vsp.bvwp.data.container.base.street.StreetEnvironmentalDataContainer;
import org.tub.vsp.bvwp.data.type.EnvironmentalCriteria;

import java.io.IOException;
import java.util.List;

class StreetEnvironmentalCriteriaMapperTest {
    @Test
    void test_a20() throws IOException {
        StreetEnvironmentalDataContainer result = StreetEnvironmentalCriteriaMapper.mapDocument(LocalFileAccessor.getLocalDocument("a20.html"));

        Assertions.assertEquals(result.getNaturschutzVorrangflaechen21().bewertung(), EnvironmentalCriteria.UmweltBewertung.MITTEL);
        Assertions.assertEquals(result.getNaturschutzVorrangflaechen21().description(), List.of(new EnvironmentalCriteria.Description(1.6, 0.01)));

        Assertions.assertEquals(result.getNatura2000Gebiete22().bewertung(), EnvironmentalCriteria.UmweltBewertung.HOCH);
        Assertions.assertEquals(result.getNatura2000Gebiete22().description(), List.of(new EnvironmentalCriteria.Description(4., 0.0),
                new EnvironmentalCriteria.Description(0., 0.0)));

        Assertions.assertEquals(result.getUnzerschnitteneKernraeume23().bewertung(), EnvironmentalCriteria.UmweltBewertung.HOCH);
        Assertions.assertEquals(result.getUnzerschnitteneKernraeume23().description(), List.of(new EnvironmentalCriteria.Description(15.9, 0.1)));

        Assertions.assertEquals(result.getUnzerschnitteneGrossraeume24().bewertung(), EnvironmentalCriteria.UmweltBewertung.HOCH);
        Assertions.assertEquals(result.getUnzerschnitteneGrossraeume24().description(), List.of(new EnvironmentalCriteria.Description(5.9, 0.04),
                new EnvironmentalCriteria.Description(14.0, 0.09),
                new EnvironmentalCriteria.Description(0.0, 0.0),
                new EnvironmentalCriteria.Description(0.0, 0.0)));

        Assertions.assertNull(result.getFlaechenInanspruchnahme25().bewertung());
        Assertions.assertEquals(result.getFlaechenInanspruchnahme25().description(), List.of(new EnvironmentalCriteria.Description(870.9, 0.0)));

        Assertions.assertEquals(result.getUeberschwemmungsgebiete26().bewertung(), EnvironmentalCriteria.UmweltBewertung.GERING);
        Assertions.assertEquals(result.getUeberschwemmungsgebiete26().description(), List.of(new EnvironmentalCriteria.Description(0.0, 0.0)));

        Assertions.assertEquals(result.getWasserschutzgebiete27().bewertung(), EnvironmentalCriteria.UmweltBewertung.HOCH);
        Assertions.assertEquals(result.getWasserschutzgebiete27().description(), List.of(new EnvironmentalCriteria.Description(0.5, 0.0)));

        Assertions.assertEquals(result.getVerkehrsarmeRaeume28().bewertung(), EnvironmentalCriteria.UmweltBewertung.HOCH);
        Assertions.assertEquals(result.getVerkehrsarmeRaeume28().description(), List.of(new EnvironmentalCriteria.Description(18941.4, 0.0)));

        Assertions.assertEquals(result.getKulturLandschaftsschutz29().bewertung(), EnvironmentalCriteria.UmweltBewertung.HOCH);
        Assertions.assertEquals(result.getKulturLandschaftsschutz29().description(), List.of(new EnvironmentalCriteria.Description(76.7, 0.49)));
    }


}