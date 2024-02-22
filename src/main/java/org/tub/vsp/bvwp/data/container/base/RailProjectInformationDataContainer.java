package org.tub.vsp.bvwp.data.container.base;

import org.tub.vsp.bvwp.data.type.Priority;

import java.util.Objects;

public class RailProjectInformationDataContainer {
    private String projectNumber;
    private String title;
    private Priority priority;
    private String bautyp;
    private Double length;

    public String getProjectNumber() {
        return projectNumber;
    }

    public RailProjectInformationDataContainer setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public RailProjectInformationDataContainer setTitle(String title) {
        this.title = title;
        return this;
    }

    public Priority getPriority() {
        return priority;
    }

    public RailProjectInformationDataContainer setPriority(Priority priority) {
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

        RailProjectInformationDataContainer that = (RailProjectInformationDataContainer) o;

        if (!Objects.equals(projectNumber, that.projectNumber)) {
            return false;
        }
        if (!Objects.equals(title, that.title)) {
            return false;
        }
        return priority == that.priority;
    }

    @Override
    public int hashCode() {
        int result = projectNumber != null ? projectNumber.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (priority != null ? priority.hashCode() : 0);
        return result;
    }

    public RailProjectInformationDataContainer setBautyp(String bautyp) {
        this.bautyp = bautyp;
        return this;
    }

    public String getBautyp() {
        return bautyp;
    }

    public RailProjectInformationDataContainer setLength(Double length) {
        this.length = length;
        return this;
    }

    public Double getLength() {
        return length;
    }
}
