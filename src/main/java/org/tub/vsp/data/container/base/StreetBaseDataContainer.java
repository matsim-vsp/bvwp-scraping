package org.tub.vsp.data.container.base;

import java.util.Objects;

public class StreetBaseDataContainer {
    private String url;

    private ProjectInformationDataContainer projectInformation;
    private PhysicalEffectDataContainer physicalEffect;
    private CostBenefitAnalysisDataContainer costBenefitAnalysis;

    public String getUrl() {
        return url;
    }

    public StreetBaseDataContainer setUrl(String url) {
        this.url = url;
        return this;
    }

    public ProjectInformationDataContainer getProjectInformation() {
        return projectInformation;
    }

    public StreetBaseDataContainer setProjectInformation(ProjectInformationDataContainer projectInformation) {
        this.projectInformation = projectInformation;
        return this;
    }

    public PhysicalEffectDataContainer getPhysicalEffect() {
        return physicalEffect;
    }

    public StreetBaseDataContainer setPhysicalEffect(PhysicalEffectDataContainer physicalEffect) {
        this.physicalEffect = physicalEffect;
        return this;
    }

    public CostBenefitAnalysisDataContainer getCostBenefitAnalysis() {
        return costBenefitAnalysis;
    }

    public StreetBaseDataContainer setCostBenefitAnalysis(CostBenefitAnalysisDataContainer costBenefitAnalysis) {
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

        StreetBaseDataContainer that = (StreetBaseDataContainer) o;

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
