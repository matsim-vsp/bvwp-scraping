package org.tub.vsp.bvwp.data.container.base.street;

import org.tub.vsp.bvwp.data.type.Bautyp;
import org.tub.vsp.bvwp.data.type.Einstufung;

import java.util.Objects;

public class StreetProjectInformationDataContainer {
    private String projectNumber;
    private String street;
    private Einstufung priority;
    private Bautyp bautyp;
    private Double length;
    private Double verkehrsbelastung2030;

    public String getProjectNumber() {
        return projectNumber;
    }

    public StreetProjectInformationDataContainer setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public StreetProjectInformationDataContainer setStreet(String street) {
        this.street = street;
        return this;
    }

    public Einstufung getPriority() {
        return priority;
    }

    public StreetProjectInformationDataContainer setEinstufung( Einstufung priority ) {
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

        StreetProjectInformationDataContainer that = (StreetProjectInformationDataContainer) o;

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

    public StreetProjectInformationDataContainer setBautyp(Bautyp bautyp) {
        this.bautyp = bautyp;
        return this;
    }

    public Bautyp getBautyp() {
        return bautyp;
    }

    public StreetProjectInformationDataContainer setLength(Double length) {
        this.length = length;
        return this;
    }

    public Double getLength() {
        return length;
    }
    public StreetProjectInformationDataContainer setVerkehrsbelastungPlanfall( Double verkehrsbelastung2030 ){
        this.verkehrsbelastung2030 = verkehrsbelastung2030;
        return this;
    }

    public Double getVerkehrsbelastungPlanfall() {
        return verkehrsbelastung2030;
    }
}
