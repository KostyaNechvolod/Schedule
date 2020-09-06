package com.konstantinnechvolod.nure_schedule

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.konstantinnechvolod.nure_schedule.find_auditory.AUDITORYResp
import com.konstantinnechvolod.nure_schedule.find_auditory.Auditory
import com.konstantinnechvolod.nure_schedule.find_auditory.Controller
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

class FindAudActivity : AppCompatActivity() {
    private var startDate = ""
    private var endDate = ""
    var mSettings: SharedPreferences? = null
    private var date: String? = null
    var radioGroup: RadioGroup? = null
    var editAudNumber: EditText? = null
    private lateinit var start: TextView
    private lateinit var end: TextView
    private var dateAndTime = Calendar.getInstance()
    var auditoryResp: AUDITORYResp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_aud)
        aud()
        editAudNumber = findViewById(R.id.editText)
        start = findViewById(R.id.start)
        end = findViewById(R.id.end)
        start.visibility = View.INVISIBLE
        end.visibility = View.INVISIBLE
        mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        radioGroup = findViewById<View>(R.id.radioGroup) as RadioGroup
        radioGroup!!.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_week -> {
                    date = "week"
                    start.visibility = View.INVISIBLE
                    end.visibility = View.INVISIBLE
                }
                R.id.radio_month -> {
                    date = "month"
                    start.visibility = View.INVISIBLE
                    end.visibility = View.INVISIBLE
                }
                R.id.radio_user_date -> {
                    date = "custom_date"
                    start.visibility = View.VISIBLE
                    end.visibility = View.VISIBLE
                }
                else -> {
                }
            }
        }
    }

    var tmp: String? = null
    var startListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        tmp = Integer.toString(dayOfMonth) + "." + Integer.toString(monthOfYear + 1) + "." + Integer.toString(year)
        start!!.text = tmp
        startDate = tmp!!
    }
    var endListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        tmp = Integer.toString(dayOfMonth) + "." + Integer.toString(monthOfYear + 1) + "." + Integer.toString(year)
        end!!.text = tmp
        endDate = tmp!!
    }

    fun start(v: View?) {
        DatePickerDialog(this@FindAudActivity, startListener,
                dateAndTime[Calendar.YEAR],
                dateAndTime[Calendar.MONTH],
                dateAndTime[Calendar.DAY_OF_MONTH])
                .show()
    }

    fun end(v: View?) {
        DatePickerDialog(this@FindAudActivity, endListener,
                dateAndTime[Calendar.YEAR],
                dateAndTime[Calendar.MONTH],
                dateAndTime[Calendar.DAY_OF_MONTH])
                .show()
    }

    fun find(v: View?) {
        val audNuber = editAudNumber!!.text.toString()
        val audID = audId(audNuber)
        Log.d("TestId", audID)
        val editor = mSettings!!.edit()
        if (startDate !== "" && endDate !== "") {
            editor.putString(START_DATE, startDate)
            editor.putString(END_DATE, endDate)
        }
        editor.putString(APP_PREFERENCES_AUDITORY_ID, audID)
        editor.putString(APP_PREFERENCES_DATE_INTERVAL, date)
        editor.apply()
        //Toast.makeText(getApplicationContext(),audID,Toast.LENGTH_SHORT).show();
        finish()
        /*Intent intent = new Intent();
        intent.putExtra(DATE_RESULT, date);
        intent.putExtra(AUDITORY_RESULT, audNuber);
        setResult(RESULT_OK, intent);
        finish();*/
    }

    var audList = ArrayList<Auditory>()
    var audId: String? = ""
    private fun aud() {
        //Reading source from local file
        val inputStream = this.resources.openRawResource(R.raw.auditory_id)
        val jsonString = readJsonFile(inputStream)
        val gson = Gson()
        auditoryResp = gson.fromJson(jsonString, AUDITORYResp::class.java)
    }

    private fun audId(shortName: String): String? {
        var audId: String? = ""
        for (i in auditoryResp?.university?.buildings?.indices!!) {
            for (j in auditoryResp?.university?.buildings!![i]?.auditories?.indices!!) {
                if (shortName == auditoryResp?.university?.buildings!![i]?.auditories!![j]?.shortName) {
                    audId = auditoryResp?.university?.buildings!![i]?.auditories!![j]?.id
                }
            }
        }
        return audId
    }

    private fun readJsonFile(inputStream: InputStream): String {
// TODO Auto-generated method stub
        val outputStream = ByteArrayOutputStream()
        val bufferByte = ByteArray(1024)
        var length: Int
        try {
            while (inputStream.read(bufferByte).also { length = it } != -1) {
                outputStream.write(bufferByte, 0, length)
            }
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
        }
        return outputStream.toString()
    }

    private fun audRetrofit() {
        val cistapi = Controller.api
        cistapi.university.enqueue(object : Callback<AUDITORYResp?> {
            override fun onResponse(call: Call<AUDITORYResp?>, response: Response<AUDITORYResp?>) {
                for (i in response.body()?.university?.buildings?.indices!!) {
                    for (j in response.body()?.university?.buildings!![i]?.auditories?.indices!!) {

                        //Log.d("test", auditories.getShortName())// ;
                        audId = response.body()?.university?.buildings!![i]?.auditories!![j]?.shortName
                        //audIdList.add(audId);
                        Log.d("test1", audId)
                    }
                }
            }

            override fun onFailure(call: Call<AUDITORYResp?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    inner class Auditories(var id: String, var shortName: String, var floor: String)
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