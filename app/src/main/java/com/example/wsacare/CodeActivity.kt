package com.example.wsacare

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.wsacare.core.Constants
import com.example.wsacare.databinding.ActivityCodeBinding


class CodeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        haveWifi()
        clicks()
    }

    private fun haveWifi() {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            getCode()
            binding.internet.visibility = View.VISIBLE
            binding.noInternet.visibility = View.GONE
        } else {
            binding.internet.visibility = View.GONE
            binding.noInternet.visibility = View.VISIBLE
        }
    }

    private fun clicks() {
        binding.imgBack.setOnClickListener {
            val i = Intent(this@CodeActivity,InfoActivity::class.java)
            startActivity(i)
            finish()
        }
        binding.txtTryAgain.setOnClickListener { haveWifi() }
    }

    private fun getCode() {
        val queue = Volley.newRequestQueue(this)
        val request =
            JsonObjectRequest("${Constants.URL_BASE}/user_qr",
                {
                    binding.webView.loadUrl(it.getString("data"))
                    Log.e("getCode: ", it.toString())
                },
                {
                    Log.e("getCode: ", it.toString())
                }
            )
        queue.add(request)
    }
}