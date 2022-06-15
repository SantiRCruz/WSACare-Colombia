package com.example.wsacare.ui

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.wsacare.InfoActivity
import com.example.wsacare.R
import com.example.wsacare.core.Constants
import com.example.wsacare.databinding.DialogAlertBinding
import com.example.wsacare.databinding.DialogConfirmBinding
import com.example.wsacare.databinding.FragmentCheckListBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat


class CheckListFragment : Fragment(R.layout.fragment_check_list) {
    private lateinit var binding: FragmentCheckListBinding
    private var uri: Uri? = null
    private var dataSymptoms = mutableListOf<Int>(2,1)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                pickFromGallery()
            } else {
                Snackbar.make(
                    binding.root,
                    "You have to enable the permission",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data?.data
                uri = data
                binding.btnAdd.setImageURI(data)
                binding.imgClose.visibility = View.VISIBLE
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCheckListBinding.bind(view)

        obtainActualDate()
        obtainList()
        clicks()
        animations()

    }
    private fun animations() {
        binding.txt.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.down_up
            )
        )
        binding.linearLayout.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.down_up1
            )
        )
        binding.btnAdd.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.down_up1
            )
        )
        binding.txt1.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.down_up2
            )
        )
        binding.btnConfirm.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.down_up2
            )
        )
    }

    private fun obtainActualDate() {
        val date = System.currentTimeMillis()
        val sdf = SimpleDateFormat("EEE, MMM d, ''yy")
        binding.txtDate.text = sdf.format(date)
    }

    private val checkStateArray = BooleanArray(9)
    private fun clicks() {
        binding.btnAdd.setOnClickListener { requestPermission() }
        binding.btnConfirm.setOnClickListener { alertDialog() }
        binding.imgBack.setOnClickListener { findNavController().popBackStack() }
        binding.imgClose.setOnClickListener { resetImage() }

        binding.c1.setOnClickListener {
            checkStateArray[0] = (it as CheckBox).isChecked
        }
        binding.c2.setOnClickListener {
            checkStateArray[1] = (it as CheckBox).isChecked
        }
        binding.c3.setOnClickListener {
            checkStateArray[2] = (it as CheckBox).isChecked
        }
        binding.c4.setOnClickListener {
            checkStateArray[3] = (it as CheckBox).isChecked
        }
        binding.c5.setOnClickListener {
            checkStateArray[4] = (it as CheckBox).isChecked
        }
        binding.c6.setOnClickListener {
            checkStateArray[5] = (it as CheckBox).isChecked
        }
        binding.c7.setOnClickListener {
            checkStateArray[6] = (it as CheckBox).isChecked
        }
        binding.c8.setOnClickListener {
            checkStateArray[7] = (it as CheckBox).isChecked
        }
        binding.c9.setOnClickListener {
            checkStateArray[8] = (it as CheckBox).isChecked
        }
    }

    private fun alertDialog() {
        val dialogBinding = DialogConfirmBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()


        dialogBinding.btnNo.setOnClickListener { alertDialog.dismiss() }
        dialogBinding.btnYes.setOnClickListener {
            validateData()
            alertDialog.dismiss()
        }
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun validateData() {
        val intArray = ArrayList<Int>()
        for (i in checkStateArray.indices) {
            if (checkStateArray[i]) {
                intArray.add(i)
            }
        }
//        sendData(intArray)
    }

//    private fun sendData(intArray: ArrayList<Int>) {
//        val jsonArray = JSONArray()
//        for (i in intArray) {
//            jsonArray.put(i)
//        }
//        val queue = Volley.newRequestQueue(requireContext())
//        val request = JsonObjectRequest(
//            Request.Method.POST, "${Constants.URL_BASE}/day_symptoms", jsonArray,
//            {
//                /*Log.e("signIn: ", it.getJSONArray(0).toString())*/
//            },
//            {
//
//                Log.e("signIn: ", it.message.toString())
//            }
//        )
//        queue.add(request)
//    }

    private fun validateOneSymptom(): Boolean {
        return if (dataSymptoms.isNullOrEmpty()){
            Snackbar.make(
                binding.root,
                "At least you have to select one symptom",
                Snackbar.LENGTH_SHORT
            ).show()
            false
        }else{
            true
        }

    }

    private fun resetImage() {
        binding.imgClose.visibility = View.GONE
        binding.btnAdd.setImageResource(R.drawable.ic_baseline_add_box_24)
        uri = null
    }

    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> pickFromGallery()
            else -> requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun pickFromGallery() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT)
        i.type = "image/*"
        galleryResult.launch(i)

    }

    private fun obtainList() {
        val queue = Volley.newRequestQueue(requireContext())
        val request = JsonObjectRequest("${Constants.URL_BASE}/symptom_list",
            {
                val jsonArray = it.getJSONArray("data")
                for (i in 0 until jsonArray.length()) {
                    val json = jsonArray.getJSONObject(i)
                    when (i) {
                        0 -> {
                            binding.txtC1.text = json.getString("title")
                        }
                        1 -> {
                            binding.txtC2.text = json.getString("title")
                        }
                        2 -> {
                            binding.txtC3.text = json.getString("title")
                        }
                        3 -> {
                            binding.txtC4.text = json.getString("title")
                        }
                        4 -> {
                            binding.txtC5.text = json.getString("title")
                        }
                        5 -> {
                            binding.txtC6.text = json.getString("title")
                        }
                        6 -> {
                            binding.txtC7.text = json.getString("title")
                        }
                        7 -> {
                            binding.txtC8.text = json.getString("title")
                        }
                        8 -> {
                            binding.txtC9.text = json.getString("title")
                        }
                    }
                }

                Log.e("obtainList: ", it.toString())
            },
            {
                Log.e("obtainList: ", it.toString())
            }
        )
        queue.add(request)

    }
}