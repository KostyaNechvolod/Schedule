package com.konstantinnechvolod.nure_schedule.find_auditory

import retrofit2.Call
import retrofit2.http.GET

interface CISTAPI {
    /*@GET("P_API_EVENTS_AUDITORY_JSON")
    Call<AUDITORYEVENTResp> getAuditoryEvent(@Query("p_id_auditory") int i);*/
    @get:GET("P_API_AUDITORIES_JSON?")
    val university: Call<AuditoryResp?>
}