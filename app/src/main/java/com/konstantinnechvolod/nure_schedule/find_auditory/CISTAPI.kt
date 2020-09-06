package com.konstantinnechvolod.nure_schedule.find_auditory;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryName;

public interface CISTAPI {
    @GET("P_API_AUDITORIES_JSON?")
    Call<AUDITORYResp> getUniversity();

    /*@GET("P_API_EVENTS_AUDITORY_JSON")
    Call<AUDITORYEVENTResp> getAuditoryEvent(@Query("p_id_auditory") int i);*/
}
