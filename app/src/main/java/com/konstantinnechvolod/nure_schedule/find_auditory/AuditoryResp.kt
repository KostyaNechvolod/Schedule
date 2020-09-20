package com.konstantinnechvolod.nure_schedule.find_auditory

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AuditoryResp {
    /*public void setUniversity(University university) {
        this.university = university;
    }*/
    @SerializedName("university")
    @Expose
    val university: University? = null
}