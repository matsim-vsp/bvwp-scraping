package org.tub.vsp.data.type;

import java.util.Map;

public enum Emission {
    CO2, CO, NOX, HC, PM, SO2, CO2_OVERALL_EQUIVALENTS;

    public static final Map<Emission, String> STRING_IDENTIFIER_BY_EMISSION_WITHOUT_CO2_OVERALL = Map.of(
            CO2, "Kohlendioxid-Emissionen (CO2)",
            CO, "Kohlenmonoxid-Emissionen (CO)",
            NOX, "Stickoxid-Emissionen (NOx)",
            HC, "Kohlenwasserstoff-Emissionen (HC)",
            PM, "Feinstaub-Emissionen (PM)",
            SO2, "Schwefeldioxid-Emissionen (SO2)"
    );

    public static final String STRING_IDENTIFIER_CO2_OVERALL_EQUIVALENTS = "Äquivalenten aus Lebenszyklusemissionen";
}
