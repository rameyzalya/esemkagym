package com.example.myesemka

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myesemka.Adapter.MemberAdapter
import com.example.myesemka.databinding.ActivityManageMemberBinding
import com.example.myesemka.tools.ConnectionManager
import com.example.myesemka.tools.NetworkException
import com.example.myesemka.tools.SharedPreference.Companion.getAuth
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class ManageMemberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageMemberBinding
    private lateinit var list: List<String>
    private lateinit var connectionManager: ConnectionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageMemberBinding.inflate(layoutInflater)
        connectionManager = ConnectionManager()
        loadMember("ACTIVE", null)
        setupActionBar()
        setupTab()
        setupSearch()
//        setupAdapter("Active")
        setContentView(binding.root)
    }

    private fun setupSearch() {
        //handling tombol search
        binding.btnSearch.setOnClickListener {
            val keyword = binding.etSearch.text.toString()
            loadMember(getStatus(), keyword)
        }
        //handle percarian dr keyboard
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val keyword: String = binding.etSearch.text.toString()
                loadMember(getStatus(), keyword)
                true
            } else {
                false
            }

        }
    }

    private fun getStatus(): String {
        //mengambil posisi tab dimana apakah active, inactive atau pending approval
        val tab = binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition)
        Log.d("TAG", "getStatusTab: ${tab?.text.toString()}")
        return when (tab?.text.toString()) {
            "Active Members" -> "ACTIVE"
            "Past Members" -> "INACTIVE"
            else -> "PENDING_APPROVAL"
        }
    }

    private fun loadMember(status: String, nama: String?) {
        lifecycleScope.launch {
            try {
                val token = getAuth()?.optString("token")
                val listMember = connectionManager.getMember(nama, status, token)
                Log.d("TAG", "loadMember: ${connectionManager.getMember(null, status, token)}")
                setupAdapter(listMember, status)
            } catch (e: Exception) {
                Log.d("TAG", "loadMember: ${e.message}")
            }
        }
    }


    private fun setupTab() {
        val listTab = listOf("Active Members", "Past Members", "Pending Approval")
        listTab.forEach { item ->
            val tab = binding.tabLayout.newTab()
            tab.text = item
            binding.tabLayout.addTab(tab)
        }
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    "Active Members" -> loadMember("ACTIVE", null)
                    "Past Members" -> loadMember("INACTIVE", null)
                    "Pending Approval" -> loadMember("PENDING_APPROVAL", null)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
        binding.tabLayout.selectTab((binding.tabLayout.getTabAt(0)))
    }

    private fun setupAdapter(data: JSONArray, status: String) {
        val adapterMember =
            MemberAdapter(data, status, { confirmMembership(it) }, { resumeMembership(it) })
        binding.rvMember.layoutManager = LinearLayoutManager(this)
        binding.rvMember.adapter = adapterMember
    }

    private fun confirmMembership(id: String) {
        val token = getAuth()?.getString("token")
        try {
            lifecycleScope.launch {
                connectionManager.approveMembership(id, token)
                withContext(Dispatchers.Main) {
                    AlertDialog.Builder(this@ManageMemberActivity)
                        .setTitle("Informasi")
                        .setMessage("Membership Approved")
                        .setNeutralButton("Ok") { _, _ -> }
                        .setOnDismissListener {
                            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
                        }
                        .create()
                        .show()
                }
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun handleError(e: Exception) {
        when (e) {
            is NetworkException.AuthException -> {
                Intent(this, SignInActivity::class.java).apply {
                    flags = FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK
                }.let { startActivity(it) }
            }

            else -> {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(e.message)
                    .setNeutralButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }
    }

    private fun resumeMembership(id: String) {
        val token = getAuth()?.getString("token")
        try {
            lifecycleScope.launch {
                connectionManager.resumeMembership(id, token)
                withContext(Dispatchers.Main) {
                    AlertDialog.Builder(this@ManageMemberActivity)
                        .setTitle("informasi")
                        .setMessage("Membership Resume")
                        .setNeutralButton("Ok") { _, _ -> }
                        .setOnDismissListener {
                            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
                        }
                        .create()
                        .show()
                }
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.appbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.appbar.titleToolbar.text = "Manage Members"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}