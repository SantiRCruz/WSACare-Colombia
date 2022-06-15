package com.example.wsacare

import android.app.ActivityOptions
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.wsacare.core.Constants
import com.example.wsacare.databinding.ActivityMainBinding
import com.example.wsacare.databinding.DialogAlertBinding
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var json: JSONObject
    private var wifi = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        clicks()
        animations()

    }

    private fun animations() {
        binding.tilEmail.startAnimation(AnimationUtils.loadAnimation(this,R.anim.down_up))
        binding.textView2.startAnimation(AnimationUtils.loadAnimation(this,R.anim.down_up3))
        binding.tilPassword.startAnimation(AnimationUtils.loadAnimation(this,R.anim.down_up1))
        binding.btnSignUp.startAnimation(AnimationUtils.loadAnimation(this,R.anim.down_up2))
    }

    private fun clicks() {
        binding.btnSignUp.setOnClickListener { validateData() }
    }

    private fun validateData() {
        val results = arrayListOf(validateEmail(), validatePassword())
        if (false in results)
            return

        signIn()
    }

    private fun signIn() {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            val queue = Volley.newRequestQueue(applicationContext)
            val request = JsonObjectRequest(Request.Method.POST, "${Constants.URL_BASE}/signin/",
                JSONObject().apply {
                    put("login", binding.edtEmail.text.toString())
                    put("password", binding.edtPassword.text.toString())
                },
                {
                    if (it.getBoolean("success")) {
                        json = it.getJSONObject("data")
                        Constants.ID_USER = json.getString("id")
                        Constants.NAME = json.getString("name")
//                    saveImportantData()

                        val i = Intent(this@MainActivity, InfoActivity::class.java)
                        startActivity(i,ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                        finish()
                    } else {
                        alertDialogError()
                    }
                    Log.e("signIn: ", it.getJSONObject("data").toString())
                },
                {
                    if (it.message == "java.net.UnknownHostException: Unable to resolve host wsa2021.mad.hakta.pro : No address associated with hostname") {
                        wifi = 1
                        alertDialogError()
                    } else {
                        wifi = 2
                        alertDialogError()
                    }
                    Log.e("signIn: ", it.message.toString())
                }
            )
            queue.add(request)
        } else {
            wifi = 1
            alertDialogError()
        }

    }

    private fun saveImportantData() {
//        val shared = getSharedPreferences(Constants.USER,MODE_PRIVATE).edit()
//        shared.putString("id", json.getString("id"))
//        shared.putString("login", json.getString("login"))
//        shared.putString("name", json.getString("name"))
//        shared.putString("token", json.getString("token"))
    }

    private fun alertDialogError() {
        val dialogBinding = DialogAlertBinding.inflate(LayoutInflater.from(this))
        val alertDialog = AlertDialog.Builder(this).apply {
            setView(dialogBinding.root)
        }.create()
        if (wifi==1)
            dialogBinding.textView4.text = "No Internet Connection"
        else if(wifi==2)
            dialogBinding.textView4.text = "We can’t find account with this credentials"
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()

    }

    private fun validatePassword(): Boolean {
        return if (binding.edtPassword.text.toString().isNullOrEmpty()) {
            binding.tilPassword.error = "The field can't be empty"
            false
        } else {
            binding.tilPassword.error = null
            true
        }
    }

    private fun validateEmail(): Boolean {
        return if (binding.edtEmail.text.toString().isNullOrEmpty()) {
            binding.tilEmail.error = "The field can't be empty"
            false
        } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(binding.edtEmail.text.toString())
                .matches()
        ) {
            binding.tilEmail.error = "The field is not a Email"
            false
        } else {
            binding.tilEmail.error = null
            true
        }

    }


}