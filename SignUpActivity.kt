package com.example.myesemka

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myesemka.databinding.ActivitySignInBinding
import com.example.myesemka.databinding.ActivitySignUpBinding
import com.example.myesemka.tools.ConnectionManager
import kotlinx.coroutines.launch
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var connectionManager: ConnectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        connectionManager = ConnectionManager ()

        binding.btnSignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        binding.btnSignUp.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        val email = binding.registerEmail.text.toString()
        val password = binding.registerPw.text.toString()
        val nama = binding.registerNama.text.toString()
        val gender = if (binding.rbMale.isChecked) "MALE" else "FEMALE"
        when {
            email.isNotEmpty() && password.isNotEmpty() && nama.isNotEmpty() -> {
                processSignUp(email,password,nama,gender)
            }

            else -> {
                Toast.makeText(this, "email dan password tidak boleh kosong", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun processSignUp(email: String, password: String, nama: String, gender: String) {
        lifecycleScope.launch {
            try {
                connectionManager.signup(email, password, nama, gender)
                startActivity(Intent(this@SignUpActivity, RegisteredActivity::class.java))
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun handleError(e: Exception) {
        AlertDialog.Builder(this)
            .setTitle("error")
            .setMessage(e.message)
            .setNeutralButton("OK") { dialog,_ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

}