package org.tub.vsp.bvwp.data.type;

import java.util.Objects;

public record Cost(Double cost, Double overallCosts) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Cost cost1 = (Cost) o;

        if (!Objects.equals(cost, cost1.cost)) {
            return false;
        }
        return Objects.equals(overallCosts, cost1.overallCosts);
    }

    @Override
    public int hashCode() {
        int result = cost != null ? cost.hashCode() : 0;
        result = 31 * result + (overallCosts != null ? overallCosts.hashCode() : 0);
        return result;
    }
}
