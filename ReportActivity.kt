package com.example.myesemka

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myesemka.Adapter.AttendanceAdapter
import com.example.myesemka.databinding.ActivityReportBinding
import com.example.myesemka.tools.ConnectionManager
import com.example.myesemka.tools.SharedPreference.Companion.getAuth
import kotlinx.coroutines.launch
import org.json.JSONArray

class ReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportBinding
    private lateinit var connectionManager: ConnectionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        connectionManager = ConnectionManager()
        getAttendanceData()
        setupAppBar()
        setContentView(binding.root)
    }

    private fun setupAppBar() {
        setSupportActionBar(binding.appbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.appbar.titleToolbar.text = "Report"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun getAttendanceData() {
        val token = getAuth()?.getString("token")
        try {
            lifecycleScope.launch {
                //list
                val attendanceData = connectionManager.getAttendance(token)
                setUpAdapter(attendanceData)
                //fungsi untuk jumlah gender dan total orang
                countGender(attendanceData)
            }
        } catch (e: Exception) {

        }
    }

    private fun countGender(attendanceData: JSONArray) {
        var maleCount = 0
        var femaleCount = 0
        for (i in 0 until attendanceData.length()) {
            val item = attendanceData.getJSONObject(i)
            //ambil data JSONObject User
            val user = item.getJSONObject("user")
            //ambil data gender
            val gender = user.getString("gender")
            //pisahkan dan cek masing-masing gender
            if (gender == "MALE") {
                //jumlah male akan bertambah 1
                maleCount++
            } else if (gender == "FEMALE") {
                femaleCount++
            }
        }
        val totalCount = maleCount + femaleCount
        binding.number.text = totalCount.toString()

        //set jumlah total proggres
        binding.pbMale.max = totalCount
        binding.pbMale.progress = maleCount

        binding.pbFemale.max = totalCount
        binding.pbFemale.progress = femaleCount
    }


    private fun setUpAdapter(jsonArray: JSONArray) {
        val adapterAttendance = AttendanceAdapter(jsonArray)
        //Untuk Horizontal
        //binding.rvAttendance.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        //Untuk Vertikal
        binding.rvAttendance.layoutManager = LinearLayoutManager(this)
        binding.rvAttendance.adapter = adapterAttendance
    }


}