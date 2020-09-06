package com.konstantinnechvolod.nure_schedule.find_auditory

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class University {
    @SerializedName("short_name")
    @Expose
    var shortName: String? = null

    @SerializedName("full_name")
    @Expose
    var fullName: String? = null

    @SerializedName("buildings")
    @Expose
    var buildings: List<Building?>? = null
}