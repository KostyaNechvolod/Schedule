
package com.konstantinnechvolod.nure_schedule.find_auditory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuditoryType {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("short_name")
    @Expose
    private String shortName;

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

}
