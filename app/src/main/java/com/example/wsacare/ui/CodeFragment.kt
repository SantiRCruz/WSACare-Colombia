package com.example.wsacare.ui

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.wsacare.R
import com.example.wsacare.core.Constants
import com.example.wsacare.databinding.FragmentCodeBinding


class CodeFragment : Fragment(R.layout.fragment_code) {
    private lateinit var binding: FragmentCodeBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCodeBinding.bind(view)



        getCode()
        clicks()
    }

    private fun clicks() {
        binding.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun getCode() {
        val queue = Volley.newRequestQueue(requireContext())
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