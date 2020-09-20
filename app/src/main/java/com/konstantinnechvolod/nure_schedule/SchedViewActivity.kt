package com.konstantinnechvolod.nure_schedule

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class SchedViewActivity : AppCompatActivity() {
    private var startDate: String? = ""
    private var endDate: String? = ""
    private lateinit var mSettings: SharedPreferences
    private var REQUEST_URL = "https://cist.nure.ua/ias/app/tt/WEB_IAS_TT_GNR_RASP.GEN_AUD_RASP?ATypeDoc=1"

    //http://cist.nure.ua/ias/app/tt/WEB_IAS_TT_GNR_RASP.GEN_AUD_RASP?ATypeDoc=1&Aid_aud=5573655&ADateStart=01.09.2018&AdateEnd=30.09.2018
    private var date_interval: String? = "week" //возвращенное из FindAudActivity (week/month/custom_date)
    private var aud //возвращенный из FindAudActivity номер аудитории
            : String? = null
    private var Aid_aud = "&Aid_aud="
    private var ADateStart = "&ADateStart="
    private var ADateEnd = "&ADateEnd="
    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sched_view)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        webView = findViewById<View>(R.id.web_view) as WebView
        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
        }
        fab.hide()
        mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        if (mSettings.contains(APP_PREFERENCES_AUDITORY_ID)) {
            aud = mSettings.getString(APP_PREFERENCES_AUDITORY_ID, "")
            date_interval = mSettings.getString(APP_PREFERENCES_DATE_INTERVAL, "")
            if (date_interval == "custom_date") {
                startDate = mSettings.getString(START_DATE, "")
                endDate = mSettings.getString(END_DATE, "")
            }
        }
    }

    override fun onResume() {
        mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        if (mSettings.contains(APP_PREFERENCES_AUDITORY_ID)) {
            aud = mSettings.getString(APP_PREFERENCES_AUDITORY_ID, "")
            date_interval = mSettings.getString(APP_PREFERENCES_DATE_INTERVAL, "")
            if (date_interval == "custom_date") {
                startDate = mSettings.getString(START_DATE, "")
                endDate = mSettings.getString(END_DATE, "")
            }
        }
        setDefault()
        showSched()
        super.onResume()
    }

    private fun setDefault() {
        Aid_aud = "&Aid_aud="
        ADateStart = "&ADateStart="
        ADateEnd = "&ADateEnd="
        REQUEST_URL = "https://cist.nure.ua/ias/app/tt/WEB_IAS_TT_GNR_RASP.GEN_AUD_RASP?ATypeDoc=1"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return
        }
        date_interval = data.getStringExtra(DATE_RESULT)
        aud = data.getStringExtra(AUDITORY_RESULT)
        setDefault()
        showSched()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_sched_view, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            val intent = Intent(this, FindAudActivity::class.java)
            //startActivityForResult(intent,1);
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSched() {
        webView!!.settings.builtInZoomControls = true
        webView!!.settings.displayZoomControls = false
        getAuditoryID(aud)
        getDate(date_interval)
        createRequest(Aid_aud, ADateStart, ADateEnd)
        webView!!.loadUrl(REQUEST_URL)
    }

    private fun getAuditoryID(aud: String?) {
        Aid_aud = "&Aid_aud=$aud"
    }

    private fun getDate(date_interval: String?) {
        val sb = StringBuilder()
        when (date_interval) {
            "week" -> {
                // get current week
                val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
                val calendar = Calendar.getInstance()
                calendar.firstDayOfWeek = Calendar.MONDAY
                while (calendar[Calendar.DAY_OF_WEEK] > calendar.firstDayOfWeek) {
                    calendar.add(Calendar.DATE, -1) // Substract 1 day until first day of week.
                }
                ADateStart = sb.append(ADateStart).append(simpleDateFormat.format(calendar.time)).toString()
                sb.setLength(0)
                calendar.roll(Calendar.DAY_OF_WEEK, 6)
                ADateEnd = sb.append(ADateEnd).append(simpleDateFormat.format(calendar.time)).toString()
            }
            "month" -> {
                // get current month
                val currentMonth = Integer.toString(Calendar.getInstance()[Calendar.MONTH] + 1)
                val currentYear = Integer.toString(Calendar.getInstance()[Calendar.YEAR])
                ADateStart = sb.append(ADateStart).append("01.").append("$currentMonth.").append(currentYear).toString()
                sb.setLength(0)
                ADateEnd = sb.append(ADateEnd).append(Calendar.getInstance().getActualMaximum(Calendar.DATE).toString() + ".").append("$currentMonth.").append(currentYear).toString()
            }
            "custom_date" -> {
                ADateStart = sb.append(ADateStart).append(startDate).toString()
                sb.setLength(0)
                ADateEnd = sb.append(ADateEnd).append(endDate).toString()
            }
        }
        // получение конкретных дат, в зависимости от выбраного интервала
    }

    private fun createRequest(id_aud: String, ADateStart: String, ADateEnd: String) {
        // формирование запроса с имеющимися параметрами
        val stringBuilder = StringBuilder()
        stringBuilder.append(REQUEST_URL).append(Aid_aud).append(ADateStart).append(ADateEnd)
        REQUEST_URL = stringBuilder.toString()
        Log.d("getMonth", REQUEST_URL)
    }

    companion object {
        const val APP_PREFERENCES = "mySettings"
        const val APP_PREFERENCES_AUDITORY_ID = "auditory_id"
        const val APP_PREFERENCES_DATE_INTERVAL = "date_interval"
        const val START_DATE = "start_date"
        const val END_DATE = "end_date"
        private const val DATE_RESULT = "date_result"
        private const val AUDITORY_RESULT = "aud_result"
    }
}