package com.konstantinnechvolod.nure_schedule

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.konstantinnechvolod.nure_schedule.find_auditory.Auditory
import com.konstantinnechvolod.nure_schedule.find_auditory.AuditoryResp
import com.konstantinnechvolod.nure_schedule.find_auditory.Controller
import kotlinx.android.synthetic.main.activity_find_aud_new.*
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
    private var date: String = "week"
    private var dateAndTime = Calendar.getInstance()
    var auditoryResp: AuditoryResp? = null

    private val isDatePickerVisible = MutableLiveData(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_aud_new)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        aud()

        isDatePickerVisible.observe(this, {
            if (it) {
                start_date_layout.visibility = View.VISIBLE
                end_date_layout.visibility = View.VISIBLE
            } else {
                start_date_layout.visibility = View.INVISIBLE
                end_date_layout.visibility = View.INVISIBLE
            }
        })
        mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        edittext_start.setOnClickListener {
            start()
        }

        edittext_end.setOnClickListener {
            end()
        }
        chip_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.week_chip -> {
                    isDatePickerVisible.value = false
                    date = "week"
                }
                R.id.month_chip -> {
                    isDatePickerVisible.value = false
                    date = "month"
                }
                R.id.custom_date_chip -> {
                    isDatePickerVisible.value = true
                    date = "custom_date"
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> true
        }
    }

    var tmp: String? = null

    private var startListener = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        tmp = dayOfMonth.toString() + "." + (monthOfYear + 1).toString() + "." + year.toString()
        edittext_start.setText(tmp)
        startDate = tmp!!
    }
    private var endListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        tmp = dayOfMonth.toString() + "." + (monthOfYear + 1).toString() + "." + year.toString()
        edittext_end.setText(tmp)
        endDate = tmp!!
    }

    fun start() {
        DatePickerDialog(this@FindAudActivity, startListener,
                dateAndTime[Calendar.YEAR],
                dateAndTime[Calendar.MONTH],
                dateAndTime[Calendar.DAY_OF_MONTH])
                .show()
    }

    fun end() {
        DatePickerDialog(this@FindAudActivity, endListener,
                dateAndTime[Calendar.YEAR],
                dateAndTime[Calendar.MONTH],
                dateAndTime[Calendar.DAY_OF_MONTH])
                .show()
    }

    fun find(v: View?) {
        if (edittext_auditory.text.isNullOrEmpty()) {
            auditory_input_layout.error = getString(R.string.error_no_auditory_entered)
            return
        }

        val audNumber = edittext_auditory!!.text.toString()
        val audID = getAudIdByName(audNumber)
        val editor = mSettings!!.edit()
        if (startDate != "" && endDate != "") {
            editor.putString(START_DATE, startDate)
            editor.putString(END_DATE, endDate)
        }
        editor.putString(APP_PREFERENCES_AUDITORY_ID, audID)
        editor.putString(APP_PREFERENCES_DATE_INTERVAL, date)
        editor.apply()
        finish()
    }

    var audList = ArrayList<Auditory>()
    var audId: String? = ""
    private fun aud() {
        //Reading source from local file
        val inputStream = this.resources.openRawResource(R.raw.auditory_id)
        val jsonString = readJsonFile(inputStream)
        val gson = Gson()
        auditoryResp = gson.fromJson(jsonString, AuditoryResp::class.java)
    }

    private fun getAudIdByName(shortName: String): String? {
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
        cistapi.university.enqueue(object : Callback<AuditoryResp?> {
            override fun onResponse(call: Call<AuditoryResp?>, response: Response<AuditoryResp?>) {
                for (i in response.body()?.university?.buildings?.indices!!) {
                    for (j in response.body()?.university?.buildings!![i]?.auditories?.indices!!) {

                        //Log.d("test", auditories.getShortName())// ;
                        audId = response.body()?.university?.buildings!![i]?.auditories!![j]?.shortName
                        //audIdList.add(audId);
                    }
                }
            }

            override fun onFailure(call: Call<AuditoryResp?>, t: Throwable) {
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