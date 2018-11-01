
package com.konstantinnechvolod.nure_schedule.find_auditory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AUDITORYResp {

    @SerializedName("university")
    @Expose
    private University university;

    public University getUniversity() {
        return university;
    }

    /*public void setUniversity(University university) {
        this.university = university;
    }*/

}
