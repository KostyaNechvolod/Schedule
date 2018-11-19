package com.konstantinnechvolod.nure_schedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class SchedViewActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_AUDITORY_ID = "auditory_id";
    public static final String APP_PREFERENCES_DATE_INTERVAL = "date_interval";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    private String startDate = "";
    private String endDate= "";
    private SharedPreferences mSettings;

    private String REQUEST_URL = "http://cist.nure.ua/ias/app/tt/WEB_IAS_TT_GNR_RASP.GEN_AUD_RASP?ATypeDoc=1";
    //http://cist.nure.ua/ias/app/tt/WEB_IAS_TT_GNR_RASP.GEN_AUD_RASP?ATypeDoc=1&Aid_aud=5573655&ADateStart=01.09.2018&AdateEnd=30.09.2018

    private String date_interval = "week"; //возвращенное из FindAudActivity (week/month/custom_date)
    private String aud; //возвращенный из FindAudActivity номер аудитории
    private String Aid_aud = "&Aid_aud=";
    private String ADateStart = "&ADateStart=";
    private String ADateEnd = "&ADateEnd=";
    private static final String DATE_RESULT = "date_result";
    private static final String AUDITORY_RESULT = "aud_result";

    private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sched_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        webView = (WebView) findViewById (R.id.web_view);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
        fab.hide();

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(mSettings.contains(APP_PREFERENCES_AUDITORY_ID)){
            aud = mSettings.getString(APP_PREFERENCES_AUDITORY_ID, "");
            date_interval = mSettings.getString(APP_PREFERENCES_DATE_INTERVAL, "");
            if(date_interval.equals("custom_date")){
                startDate = mSettings.getString(START_DATE,"");
                endDate = mSettings.getString(END_DATE,"");
            }
        }

    }

    @Override
    protected void onResume() {
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if(mSettings.contains(APP_PREFERENCES_AUDITORY_ID)){
            aud = mSettings.getString(APP_PREFERENCES_AUDITORY_ID, "");
            date_interval = mSettings.getString(APP_PREFERENCES_DATE_INTERVAL, "");
            if(date_interval.equals("custom_date")){
                startDate = mSettings.getString(START_DATE,"");
                endDate = mSettings.getString(END_DATE,"");
            }
        }
        setDefault();
        showSched();
        super.onResume();
    }


    private void setDefault(){
        Aid_aud = "&Aid_aud=";
        ADateStart = "&ADateStart=";
        ADateEnd = "&ADateEnd=";
        REQUEST_URL = "http://cist.nure.ua/ias/app/tt/WEB_IAS_TT_GNR_RASP.GEN_AUD_RASP?ATypeDoc=1";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(data == null){return;}
        date_interval = data.getStringExtra(DATE_RESULT);
        aud = data.getStringExtra(AUDITORY_RESULT);

        setDefault();
        showSched();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sched_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            final Intent intent = new Intent(this,FindAudActivity.class);
            //startActivityForResult(intent,1);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSched(){
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        getAuditoryID(aud);
        getDate(date_interval);
        createRequest(Aid_aud,ADateStart,ADateEnd);
        webView.loadUrl(REQUEST_URL);
    }

    private void getAuditoryID(String aud){

        Aid_aud = "&Aid_aud=" + aud;
    }

    private void getDate(String date_interval){
        StringBuilder sb = new StringBuilder();

        switch (date_interval) {
            case "week":
                // get current week
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Calendar calendar = Calendar.getInstance();
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                while (calendar.get(Calendar.DAY_OF_WEEK) > calendar.getFirstDayOfWeek()) {
                    calendar.add(Calendar.DATE, -1); // Substract 1 day until first day of week.
                }
                ADateStart = sb.append(ADateStart).append(simpleDateFormat.format(calendar.getTime())).toString();
                sb.setLength(0);
                calendar.roll(Calendar.DAY_OF_WEEK, 6);
                ADateEnd = sb.append(ADateEnd).append(simpleDateFormat.format(calendar.getTime())).toString();
                break;
            case "month":
                // get current month
                String currentMonth = Integer.toString(Calendar.getInstance().get(Calendar.MONTH) + 1);
                String currentYear = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
                ADateStart = sb.append(ADateStart).append("01.").append(currentMonth + ".").append(currentYear).toString();
                sb.setLength(0);
                ADateEnd = sb.append(ADateEnd).append(Calendar.getInstance().getActualMaximum(Calendar.DATE) + ".").append(currentMonth + ".").append(currentYear).toString();
                break;
            case "custom_date":
                ADateStart = sb.append(ADateStart).append(startDate).toString();
                sb.setLength(0);
                ADateEnd = sb.append(ADateEnd).append(endDate).toString();
                // set custom date
                break;
        }
        // получение конкретных дат, в зависимости от выбраного интервала
    }

    private void createRequest(String id_aud, String ADateStart, String ADateEnd){
        // формирование запроса с имеющимися параметрами
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(REQUEST_URL).append(Aid_aud).append(ADateStart).append(ADateEnd);
        REQUEST_URL = stringBuilder.toString();
        Log.d("getMonth", REQUEST_URL);
    }
}
