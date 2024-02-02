package org.tub.vsp.data.container.base;

import org.tub.vsp.data.type.Severity;

import java.util.Objects;

public class ProjectInformationDataContainer {
    private String projectNumber;
    private String street;
    private Severity severity;

    public String getProjectNumber() {
        return projectNumber;
    }

    public ProjectInformationDataContainer setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public ProjectInformationDataContainer setStreet(String street) {
        this.street = street;
        return this;
    }

    public Severity getSeverity() {
        return severity;
    }

    public ProjectInformationDataContainer setSeverity(Severity severity) {
        this.severity = severity;
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

        ProjectInformationDataContainer that = (ProjectInformationDataContainer) o;

        if (!Objects.equals(projectNumber, that.projectNumber)) {
            return false;
        }
        if (!Objects.equals(street, that.street)) {
            return false;
        }
        return severity == that.severity;
    }

    @Override
    public int hashCode() {
        int result = projectNumber != null ? projectNumber.hashCode() : 0;
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + (severity != null ? severity.hashCode() : 0);
        return result;
    }
}
