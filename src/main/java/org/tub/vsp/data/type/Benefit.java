package org.tub.vsp.data.type;

import java.util.Objects;
import java.util.Optional;

public record Benefit(Double annual, Double overall) {

    public Benefit add(Benefit other) {
        double thisAnnual = Optional.ofNullable(annual)
                                    .orElse(0.0);
        double thisOverall = Optional.ofNullable(overall)
                                     .orElse(0.0);
        double otherAnnual = Optional.ofNullable(other)
                                     .map(Benefit::annual)
                                     .orElse(0.0);
        double otherOverall = Optional.ofNullable(other)
                                      .map(Benefit::overall)
                                      .orElse(0.0);

        return new Benefit(thisAnnual + otherAnnual, thisOverall + otherOverall);
    }

    public Benefit() {
        this(null, null);
    }

    public boolean equalsWithPrecision(Benefit other, int precision) {
        double thisAnnual = Optional.ofNullable(annual)
                                    .orElse(0.0);
        double thisOverall = Optional.ofNullable(overall)
                                     .orElse(0.0);
        double otherAnnual = Optional.ofNullable(other)
                                     .map(Benefit::annual)
                                     .orElse(0.0);
        double otherOverall = Optional.ofNullable(other)
                                      .map(Benefit::overall)
                                      .orElse(0.0);
        return Math.abs(thisAnnual - otherAnnual) < Math.pow(10., precision) && Math.abs(thisOverall - otherOverall) < Math.pow(10., precision);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Benefit benefit = (Benefit) o;

        if (!Objects.equals(annual, benefit.annual)) {
            return false;
        }
        return Objects.equals(overall, benefit.overall);
    }

    @Override
    public int hashCode() {
        int result = annual != null ? annual.hashCode() : 0;
        result = 31 * result + (overall != null ? overall.hashCode() : 0);
        return result;
    }
}
