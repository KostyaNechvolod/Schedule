package com.konstantinnechvolod.nure_schedule.find_auditory

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AuditoryType {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("short_name")
    @Expose
    var shortName: String? = null
}