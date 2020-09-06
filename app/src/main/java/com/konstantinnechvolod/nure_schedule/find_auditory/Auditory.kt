package com.konstantinnechvolod.nure_schedule.find_auditory

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Auditory {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("short_name")
    @Expose
    var shortName: String? = null

    @SerializedName("floor")
    @Expose
    var floor: String? = null

    @SerializedName("is_have_power")
    @Expose
    var isHavePower: String? = null

    @SerializedName("auditory_types")
    @Expose(deserialize = false, serialize = false)
    var auditoryTypes: List<AuditoryType>? = null
}