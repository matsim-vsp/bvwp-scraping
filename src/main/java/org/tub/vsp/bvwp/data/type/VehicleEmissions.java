package org.tub.vsp.bvwp.data.type;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record VehicleEmissions(Double pkw, Double lkw, Double kfz) {
    private static final Logger logger = LogManager.getLogger(VehicleEmissions.class);

    public VehicleEmissions {
        double difference = Math.abs(pkw + lkw - kfz);
        if (difference > 0.001) {
            logger.warn("Emission values do not add up. Difference is {}.", difference);
        }
    }
}
