
package com.konstantinnechvolod.nure_schedule.find_auditory;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Auditory {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("short_name")
    @Expose
    private String shortName;
    @SerializedName("floor")
    @Expose
    private String floor;
    @SerializedName("is_have_power")
    @Expose
    private String isHavePower;
    @SerializedName("auditory_types")
    @Expose
    private List<AuditoryType> auditoryTypes = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getIsHavePower() {
        return isHavePower;
    }

    public void setIsHavePower(String isHavePower) {
        this.isHavePower = isHavePower;
    }

    public List<AuditoryType> getAuditoryTypes() {
        return auditoryTypes;
    }

    public void setAuditoryTypes(List<AuditoryType> auditoryTypes) {
        this.auditoryTypes = auditoryTypes;
    }

}
