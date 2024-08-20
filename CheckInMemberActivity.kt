package com.example.myesemka

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myesemka.Adapter.CheckinAdapter
import com.example.myesemka.databinding.ActivityCheckInMemberBinding
import com.example.myesemka.databinding.ActivityManageMemberBinding
import com.example.myesemka.tools.ConnectionManager
import com.example.myesemka.tools.SharedPreference.Companion.getAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CheckInMemberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckInMemberBinding
    private lateinit var connectionManager: ConnectionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckInMemberBinding.inflate(layoutInflater)
        connectionManager = ConnectionManager()
        setUpDate()
        setupAppBar()
        loadAttendence()
        binding.buttonCheckin.setOnClickListener {
            checkInAndOutMember(binding.textInput.text.toString())
        }
        setContentView(binding.root)
    }

    private fun setupAppBar() {
        setSupportActionBar(binding.appbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.appbar.titleToolbar.text = "Daily CheckIn"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun checkInAndOutMember(inputCode: String) {
        val token = getAuth()?.getString("token")
        if (binding.buttonCheckin.text == "Check In") {
            try {
                lifecycleScope.launch {
                  connectionManager.checkInMember(inputCode, token)
                    withContext(Dispatchers.Main) {
                        AlertDialog.Builder (this@CheckInMemberActivity)
                            .setTitle("Information")
                            .setMessage("Check In Success")
                            .setNeutralButton("Ok") {_,_ ->}
                            .setOnDismissListener {
                                loadAttendence()
                            }
                            .create()
                            .show()
                    }
                }
            } catch (e:Exception) {
                handleError(e)
            }
        } else {
            try {
                lifecycleScope.launch {
                    connectionManager.checkoutMember(token)
                    withContext(Dispatchers.Main) {
                        AlertDialog.Builder(this@CheckInMemberActivity)
                            .setTitle("Information")
                            .setMessage("Check Out Success")
                            .setNeutralButton("Ok") {_,_ ->}
                            .setOnDismissListener {
                                loadAttendence()
                            }
                            .create()
                            .show()
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }

    }

    private fun loadAttendence() {
        try {
            var token = getAuth()?.getString("token")
            lifecycleScope.launch {
                val response = connectionManager.getAttendance(token)
                setUpAdapter(response)
            }
        } catch (e: Exception) {
            handleError(e)
        }

    }

    private fun handleError(e: Exception) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(e.message ?: "Unknown Error")
            .setNeutralButton("Ok") {dialog,_->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun setUpAdapter(response: JSONArray) {
        binding.rvList.layoutManager = LinearLayoutManager (this@CheckInMemberActivity)
        binding.rvList.adapter = CheckinAdapter(response)
        Log.d("TAG", "datamembers $response")
        Log.d("TAG", "datamemberss ${response.length()}")
        for (i in 0 until response.length()){
            val data = response.getJSONObject(i)
            Log.d("TAG", "datamember $data")
            val checkIn = LocalDateTime.parse(data.getString("checkIn"))
            if (checkIn.toLocalDate()== LocalDate.now()) {
                if (data.isNull("checkOut")) {
                    binding.textInput.visibility = View.GONE
                    binding.buttonCheckin.text = "Check Out"
                } else {
                    binding.textInput.visibility = View.VISIBLE
                    binding.buttonCheckin.text = "Check In"
                    binding.buttonCheckin.isEnabled = false
                    binding.textInput.isEnabled = false
                }
            }
        }
    }

    private fun setUpDate() {
        val date = LocalDate.now()
        binding.date.text = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    }
}