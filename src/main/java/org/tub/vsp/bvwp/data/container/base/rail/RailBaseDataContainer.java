package org.tub.vsp.bvwp.data.container.base.rail;

import java.util.Objects;

public class RailBaseDataContainer {
    private String url;

    RailProjectInformationDataContainer projectInformation;
    RailPhysicalEffectsDataContainer physicalEffect;
    RailCostBenefitAnalysisDataContainer costBenefitAnalysis;

    public String getUrl() {
        return url;
    }

    public RailBaseDataContainer setUrl(String url) {
        this.url = url;
        return this;
    }

    public RailProjectInformationDataContainer getProjectInformation() {
        return projectInformation;
    }

    public RailBaseDataContainer setProjectInformation(RailProjectInformationDataContainer projectInformation) {
        this.projectInformation = projectInformation;
        return this;
    }

    public RailPhysicalEffectsDataContainer getPhysicalEffect() {
        return physicalEffect;
    }

    public RailBaseDataContainer setPhysicalEffect( RailPhysicalEffectsDataContainer physicalEffect ) {
        this.physicalEffect = physicalEffect;
        return this;
    }

    public RailCostBenefitAnalysisDataContainer getCostBenefitAnalysis() {
        return costBenefitAnalysis;
    }

    public RailBaseDataContainer setCostBenefitAnalysis(RailCostBenefitAnalysisDataContainer costBenefitAnalysis) {
        this.costBenefitAnalysis = costBenefitAnalysis;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RailBaseDataContainer that = (RailBaseDataContainer) o;

        if (!Objects.equals(url, that.url)) {
            return false;
        }
        if (!Objects.equals(projectInformation, that.projectInformation)) {
            return false;
        }
        if (!Objects.equals(physicalEffect, that.physicalEffect)) {
            return false;
        }
        return Objects.equals(costBenefitAnalysis, that.costBenefitAnalysis);
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (projectInformation != null ? projectInformation.hashCode() : 0);
        result = 31 * result + (physicalEffect != null ? physicalEffect.hashCode() : 0);
        result = 31 * result + (costBenefitAnalysis != null ? costBenefitAnalysis.hashCode() : 0);
        return result;
    }
}
