package com.example.myesemka

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myesemka.SignUpActivity
import com.example.myesemka.databinding.ActivitySignInBinding
import com.example.myesemka.tools.ConnectionManager
import com.example.myesemka.tools.SharedPreference.Companion.getAuth
import com.example.myesemka.tools.SharedPreference.Companion.setAuth
import kotlinx.coroutines.launch
import org.json.JSONObject

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var connectionManager: ConnectionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isAlreadyLogin()) {
            getAuth()?.let { handleLoginSucces(it) }
        } else {
            connectionManager = ConnectionManager()

            binding.btnSignIn.setOnClickListener {
                login()
            }

            binding.emailtextinput.setText("admin@gmail.com")
            binding.pwtextinput.setText("admin")
            binding.btnSignUp.setOnClickListener {
                startActivity(Intent(this, SignUpActivity::class.java))
            }
        }
    }

    private fun isAlreadyLogin(): Boolean {
       return getAuth() != null
    }


    private fun login() {
        val email = binding.emailtextinput.text.toString()
        val password = binding.pwtextinput.text.toString()
        when {
            email.isNotEmpty() && password.isNotEmpty() -> {
                processLogin(email,password)
            }

            else -> {
                Toast.makeText(this, "email dan password tidak boleh kosong", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun processLogin(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val json = connectionManager.login(email, password)
                handleLoginSucces(json)
                setAuth(json)
            } catch (e:Exception) {
                handleError(e)
            }
        }
    }

    private fun handleError(e: Exception) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(e.message)
            .setNeutralButton("OK") { dialog,_ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun handleLoginSucces(json: JSONObject) {
        val user = json.getJSONObject("user")
        if (user.getBoolean("admin")) {
            startActivity(Intent(this, DailyCheckInCodeActivity::class.java))
        } else if (!user.isNull("joinedMemberAt") && !user.isNull("membershipEnd")){
            startActivity(Intent(this, CheckInMemberActivity::class.java))
        }else {
            startActivity(Intent(this, RegisteredActivity::class.java))
        }
        finish()
    }
}
