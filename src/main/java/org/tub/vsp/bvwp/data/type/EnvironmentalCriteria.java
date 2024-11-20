package org.tub.vsp.bvwp.data.type;

import java.util.List;

public record EnvironmentalCriteria(List<Description> description, UmweltBewertung bewertung) {
    public record Description(double absolute, double betroffenheit) {
    }

    public enum UmweltBewertung {
        HOCH, MITTEL, GERING, PLANFESTGESTELLT
    }
}
