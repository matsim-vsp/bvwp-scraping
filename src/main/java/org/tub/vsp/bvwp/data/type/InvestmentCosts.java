package org.tub.vsp.bvwp.data.type;

import java.util.Objects;

public record InvestmentCosts(Double sum, Double barwert) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InvestmentCosts investmentCosts1 = (InvestmentCosts) o;

        if (!Objects.equals( sum, investmentCosts1.sum )) {
            return false;
        }
        return Objects.equals( barwert, investmentCosts1.barwert );
    }

    @Override
    public int hashCode() {
        int result = sum != null ? sum.hashCode() : 0;
        result = 31 * result + (barwert != null ? barwert.hashCode() : 0);
        return result;
    }
}
