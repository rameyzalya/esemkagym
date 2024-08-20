package com.example.myesemka

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.myesemka.databinding.ActivityDailyCheckInCodeBinding
import com.example.myesemka.tools.ConnectionManager
import com.example.myesemka.tools.NetworkException
import com.example.myesemka.tools.SharedPreference.Companion.clearAuth
import com.example.myesemka.tools.SharedPreference.Companion.getAuth
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import org.json.JSONObject

class DailyCheckInCodeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityDailyCheckInCodeBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var connectionManager: ConnectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyCheckInCodeBinding.inflate(layoutInflater)

        setUpActionBar()
        setUpDrawer()
        setUpNavigation()
        connectionManager=ConnectionManager()
        loadCode()

        binding.btnLogout.setOnClickListener {
            clearAuth()
            startActivity(Intent(this, SignInActivity::class.java))
            finishAffinity()
        }

        setContentView(binding.root)
    }

    private fun loadCode() {
        lifecycleScope.launch {
            try {
                val getToken = getAuth()?.getString("token")
                val jsonCode = connectionManager.getCode(getToken)
                //ubah jadi json dan ambil nilai "code"
                val code = JSONObject(jsonCode).getString("code")
                binding.content.code.text = code
            } catch (e: NetworkException.AuthException) {
                handleAuthException()
            }
        }
        Log.d("TAG", "loadCode: ${getAuth()?.getString("token")}")
    }

    private fun handleAuthException() {
        val intent = Intent(this, SignInActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun setUpNavigation() {
        binding.navView.setCheckedItem(R.id.menu_daily_checkin_code)
        binding.navView.setNavigationItemSelectedListener(this)
    }


    private fun setUpActionBar() {
        setSupportActionBar(binding.content.appbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.content.appbar.titleToolbar.text = "Daily CheckIn"
    }

    //Handling drawe (menu)
    private fun setUpDrawer() {
        actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.drawerlayout, R.string.nav_open, R.string.nav_close)
        binding.drawerlayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    //Handling tombol drawer
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    //Handling klik item menu di dalam drawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_daily_checkin_code -> {

            }
            R.id.menu_manage_member -> {
                startActivity(Intent(this, ManageMemberActivity::class.java))
            }
            R.id.menu_report -> {
                val intent = Intent(this, ReportActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                clearAuth()
                startActivity(Intent(this, SignInActivity::class.java))
            }
        }
        binding.drawerlayout.closeDrawers()
        return true
    }
}