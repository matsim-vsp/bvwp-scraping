package org.tub.vsp.bvwp.data.container.base.street;

import java.util.Objects;

public class StreetBaseDataContainer {
    private String url;

    private StreetProjectInformationDataContainer projectInformation;
    private StreetPhysicalEffectDataContainer physicalEffect;
    private StreetCostBenefitAnalysisDataContainer costBenefitAnalysis;
    private StreetEnvironmentalDataContainer environmentalCriteria;

    public String getUrl() {
        return url;
    }

    public StreetBaseDataContainer setUrl(String url) {
        this.url = url;
        return this;
    }

    public StreetProjectInformationDataContainer getProjectInformation() {
        return projectInformation;
    }

    public StreetBaseDataContainer setProjectInformation(StreetProjectInformationDataContainer projectInformation) {
        this.projectInformation = projectInformation;
        return this;
    }

    public StreetPhysicalEffectDataContainer getPhysicalEffect() {
        return physicalEffect;
    }

    public StreetBaseDataContainer setPhysicalEffect(StreetPhysicalEffectDataContainer physicalEffect) {
        this.physicalEffect = physicalEffect;
        return this;
    }

    public StreetCostBenefitAnalysisDataContainer getCostBenefitAnalysis() {
        return costBenefitAnalysis;
    }

    public StreetBaseDataContainer setCostBenefitAnalysis(StreetCostBenefitAnalysisDataContainer costBenefitAnalysis) {
        this.costBenefitAnalysis = costBenefitAnalysis;
        return this;
    }

    public StreetEnvironmentalDataContainer getEnvironmentalCriteria() {
        return environmentalCriteria;
    }

    public StreetBaseDataContainer setEnvironmentalCriteria(StreetEnvironmentalDataContainer environmentalCriteria) {
        this.environmentalCriteria = environmentalCriteria;
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
        return Objects.equals(url, that.url) && Objects.equals(projectInformation, that.projectInformation) && Objects.equals(physicalEffect,
                that.physicalEffect) && Objects.equals(costBenefitAnalysis, that.costBenefitAnalysis) && Objects.equals(environmentalCriteria,
                that.environmentalCriteria);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(url);
        result = 31 * result + Objects.hashCode(projectInformation);
        result = 31 * result + Objects.hashCode(physicalEffect);
        result = 31 * result + Objects.hashCode(costBenefitAnalysis);
        result = 31 * result + Objects.hashCode(environmentalCriteria);
        return result;
    }
}
