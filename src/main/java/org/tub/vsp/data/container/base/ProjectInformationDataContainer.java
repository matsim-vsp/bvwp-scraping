package org.tub.vsp.data.container.base;

import org.tub.vsp.data.type.Priority;

import java.util.Objects;

public class ProjectInformationDataContainer {
    private String projectNumber;
    private String street;
    private Priority priority;
    private String bautyp;

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

    public Priority getPriority() {
        return priority;
    }

    public ProjectInformationDataContainer setPriority( Priority priority ) {
        this.priority = priority;
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
        return priority == that.priority;
    }

    @Override
    public int hashCode() {
        int result = projectNumber != null ? projectNumber.hashCode() : 0;
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + (priority != null ? priority.hashCode() : 0);
        return result;
    }
    public ProjectInformationDataContainer setBautyp( String bautyp ){
        this.bautyp = bautyp;
        return this;
    }
    public String getBautyp(){
        return bautyp;
    }
}
