package org.tub.vsp.bvwp.data.container.base;

import org.tub.vsp.bvwp.data.type.Bautyp;
import org.tub.vsp.bvwp.data.type.Priority;

import java.util.Objects;

public class ProjectInformationDataContainer {
    private String projectNumber;
    private String street;
    private Priority priority;
    private String bautyp;
    private Double length;
    private Double verkehrsbelastung2030;

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
    public ProjectInformationDataContainer setLength( Double length ){
        this.length = length;
        return this;
    }
    public Double getLength(){
        return length;
    }
    public ProjectInformationDataContainer setVerkehrsbelastung2030( Double verkehrsbelastung2030 ){
        this.verkehrsbelastung2030 = verkehrsbelastung2030;
        return this;
    }
    public Double getVerkehrsbelastung2030(){
        return verkehrsbelastung2030;
    }
}
