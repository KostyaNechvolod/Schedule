package com.konstantinnechvolod.nure_schedule;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.konstantinnechvolod.nure_schedule.find_auditory.AUDITORYResp;
import com.konstantinnechvolod.nure_schedule.find_auditory.Auditory;
import com.konstantinnechvolod.nure_schedule.find_auditory.CISTAPI;
import com.konstantinnechvolod.nure_schedule.find_auditory.Controller;
import com.konstantinnechvolod.nure_schedule.find_auditory.University;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindAudActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_AUDITORY_ID = "auditory_id";
    public static final String APP_PREFERENCES_DATE_INTERVAL = "date_interval";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    private String startDate = "";
    private String endDate = "";

    SharedPreferences mSettings;

    private String date;
    private static final String DATE_RESULT = "date_result";
    private static final String AUDITORY_RESULT = "aud_result";

    RadioGroup radioGroup;
    EditText editAudNumber;
    TextView start;
    TextView end;

    Calendar dateAndTime=Calendar.getInstance();

    AUDITORYResp auditoryResp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_aud);

        aud();

        editAudNumber = findViewById(R.id.editText);
        start = findViewById(R.id.start);
        end = findViewById(R.id.end);

        start.setVisibility(View.INVISIBLE);
        end.setVisibility(View.INVISIBLE);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radio_week:
                        date = "week";
                        start.setVisibility(View.INVISIBLE);
                        end.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radio_month:
                        date = "month";
                        start.setVisibility(View.INVISIBLE);
                        end.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radio_user_date:
                        date = "custom_date";

                        start.setVisibility(View.VISIBLE);
                        end.setVisibility(View.VISIBLE);

                    default:
                        break;
                }
            }
        });

    }

    String tmp;

    DatePickerDialog.OnDateSetListener startListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            tmp = Integer.toString(dayOfMonth) + "." + Integer.toString(monthOfYear + 1) + "." + Integer.toString(year);
            start.setText(tmp);
            startDate = tmp;
        }
    };

    DatePickerDialog.OnDateSetListener endListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            tmp = Integer.toString(dayOfMonth) + "." + Integer.toString(monthOfYear + 1) + "." + Integer.toString(year);
            end.setText(tmp);
            endDate = tmp;
        }
    };

    public void start(View v){
        new DatePickerDialog(FindAudActivity.this,startListener,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();

    }

    public void end(View v){
        new DatePickerDialog(FindAudActivity.this,endListener,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public void find(View v){
        String audNuber = editAudNumber.getText().toString();
        String audID = audId(audNuber);
        Log.d("TestId", audID);


        SharedPreferences.Editor editor = mSettings.edit();
        if(startDate != "" && endDate != ""){
            editor.putString(START_DATE, startDate);
            editor.putString(END_DATE, endDate);
        }
        editor.putString(APP_PREFERENCES_AUDITORY_ID, audID);
        editor.putString(APP_PREFERENCES_DATE_INTERVAL, date);
        editor.apply();
        //Toast.makeText(getApplicationContext(),audID,Toast.LENGTH_SHORT).show();
        finish();
        /*Intent intent = new Intent();
        intent.putExtra(DATE_RESULT, date);
        intent.putExtra(AUDITORY_RESULT, audNuber);
        setResult(RESULT_OK, intent);
        finish();*/
    }

    public ArrayList<Auditory> audList = new ArrayList<>();

    String audId = "";

    private void aud(){
        //Reading source from local file
        InputStream inputStream = this.getResources().openRawResource(R.raw.auditory_id);
        String jsonString = readJsonFile(inputStream);

        Gson gson = new Gson();
        auditoryResp = gson.fromJson(jsonString, AUDITORYResp.class);
    }

    private String audId(String shortName){
        String audId = "";
        for(int i=0; i< auditoryResp.getUniversity().getBuildings().size(); i++){
            for(int j=0; j<auditoryResp.getUniversity().getBuildings().get(i).getAuditories().size(); j++){
                if(shortName.equals(auditoryResp.getUniversity().getBuildings().get(i).getAuditories().get(j).getShortName())){
                    audId = auditoryResp.getUniversity().getBuildings().get(i).getAuditories().get(j).getId();
                }
            }
        }
        return audId;
    }

    private String readJsonFile(InputStream inputStream) {
// TODO Auto-generated method stub
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte bufferByte[] = new byte[1024];
        int length;
        try {
            while ((length = inputStream.read(bufferByte)) != -1) {
                outputStream.write(bufferByte, 0, length);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }

    private void audRetrofit(){

        CISTAPI cistapi = Controller.getApi();
        cistapi.getUniversity().enqueue(new Callback<AUDITORYResp>() {
            @Override
            public void onResponse(Call<AUDITORYResp> call, Response<AUDITORYResp> response) {
                for(int i=0; i < response.body().getUniversity().getBuildings().size(); i++){
                    for (int j = 0; j< response.body().getUniversity().getBuildings().get(i).getAuditories().size();j++){

                        //Log.d("test", auditories.getShortName())// ;
                        audId = response.body().getUniversity().getBuildings().get(i).getAuditories().get(j).getShortName();
                        //audIdList.add(audId);
                        Log.d("test1", audId);
                    }

                }
            }

            @Override
            public void onFailure(Call<AUDITORYResp> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), t.toString(),Toast.LENGTH_LONG).show();
                Log.d("test", t.getLocalizedMessage());
            }
        });
    }

    public class Auditories {
        private String id;
        private String shortName;
        private String floor;

        public Auditories(String id, String shortName, String floor) {
            this.id = id;
            this.shortName = shortName;
            this.floor = floor;
        }

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
    }

}
