package com.example.wsacare.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.wsacare.CodeActivity
import com.example.wsacare.R
import com.example.wsacare.core.Constants
import com.example.wsacare.databinding.FragmentInfoBinding
import com.example.wsacare.models.DataHistory
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat

class InfoFragment : Fragment(R.layout.fragment_info) {
    private lateinit var binding: FragmentInfoBinding
    private lateinit var data: String
    private lateinit var name: String
    private var status = false
    private var date = ""
    private val dataHistory = mutableListOf<DataHistory>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInfoBinding.bind(view)

        binding.txtNameOut.text = "${Constants.NAME} ,"

        getCases()
        getHistory()
        getCovidStats()
        obtainActualDate()
        getShareInfo()
        clicks()
        animations()

    }

    private fun getCovidStats() {
        val queue = Volley.newRequestQueue(requireContext())
        val request =
            JsonObjectRequest("${Constants.URL_BASE}/covid_stats",
                {
                    val json = it.getJSONObject("data")
                    val world = json.getJSONObject("world")
                    binding.txtWorldInfected.text = world.getString("infected")
                    val currentCity = json.getJSONObject("current_city")
                    binding.txtDeaths.text = currentCity.getString("death")
                    val worldBefore = json.getJSONObject("world_before")
                    binding.txtRecovered.text = worldBefore.getString("recovered")
                    val cityBefore = json.getJSONObject("city_before")
                    binding.txtVaccinated.text = cityBefore.getString("vaccinated")
                    Log.e("getCovidStats: ", it.toString())
                },
                {
                    Log.e("getCovidStats: ", it.toString())
                }
            )
        queue.add(request)
    }

    private fun animations() {
        binding.bgReports.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.down_up
            )
        )
        binding.cardWithOutReport.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.down_up1
            )
        )
        binding.cardWithReport.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.down_up1
            )
        )
        binding.bgWithReport.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.down_up2
            )
        )
        binding.bgWithOutReport.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.down_up2
            )
        )
    }

    private fun clicks() {
        binding.imgUpload.setOnClickListener {
            if (status)
                Snackbar.make(
                    binding.root,
                    "As of $date, there is a possibility that i have a covid",
                    Snackbar.LENGTH_SHORT
                ).show()
            else
                Snackbar.make(
                    binding.root,
                    "As of $date, it is likely that I am healthy (but is not certain)",
                    Snackbar.LENGTH_SHORT
                ).show()

        }
        binding.imgCode.setOnClickListener {
            val i = Intent(requireActivity(), CodeActivity::class.java)
            requireActivity().startActivity(i)
        }
        binding.btnSignUp.setOnClickListener { findNavController().navigate(R.id.action_infoFragment_to_checkListFragment) }
        binding.cardContacts.setOnClickListener {  }
    }

    private fun obtainActualDate() {
        val date = System.currentTimeMillis()
        val sdf = SimpleDateFormat("EEE, MMM d, ''yy")
        binding.txtActualDate.text = sdf.format(date)
    }

    private fun getShareInfo() {
        val shared =
            requireActivity().getSharedPreferences(Constants.USER, AppCompatActivity.MODE_PRIVATE)
        data = shared.getString("id", "")!!
        name = shared.getString("name", "")!!
    }

    private fun getCases() {
        val queue = Volley.newRequestQueue(requireContext())
        val request = JsonObjectRequest("${Constants.URL_BASE}/cases",
            {
                val numCases = it.getInt("data")
                if (numCases == 0) {
                    binding.txtNumCases.text = "No case"
                } else {
                    binding.txtNumCases.text = numCases.toString()
                    binding.bgReports.setCardBackgroundColor(Color.RED)
                }

                Log.e("getCases: ", it.toString())
            },
            {
                Log.e("getCases: ", it.toString())
            }
        )
        queue.add(request)
    }

    private fun getHistory() {
        val queue = Volley.newRequestQueue(requireContext())
        val request =
            JsonObjectRequest("${Constants.URL_BASE}/symptoms_history?user_id=${Constants.ID_USER}",
                {
                    if (it.getBoolean("success")) {
                        binding.cardWithReport.visibility = View.VISIBLE
                        binding.bgWithReport.visibility = View.VISIBLE
                        binding.txtName.text = Constants.NAME
                        val jsonArray = it.getJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            val json = jsonArray.getJSONObject(i)
                            dataHistory.add(
                                DataHistory(
                                    json.getString("date"),
                                    json.getInt("probability_infection")
                                )
                            )
                        }
                        val finalData = dataHistory[jsonArray.length() - 1]
                        binding.txtDate.text = finalData.date
                        date = finalData.date
                        if (finalData.probability_infection >= 50) {
                            status = true
                            binding.txtCase.text = "CALL DOCTOR"
                            binding.bgCase.setBackgroundResource(R.color.dark_red)
                            binding.txtProbability.text = "You may be infected with a virus"
                        } else {
                            status = false
                            binding.txtCase.text = "CLEAR"
                            binding.bgCase.setBackgroundResource(R.color.green)
                            binding.txtProbability.text =
                                "* Wear mask. Keep 2m distance. Wash hands."
                        }

                        ///////
                        Log.e("dataSize: ", dataHistory.size.toString())

                        for (i in 0 until dataHistory.size) {
                            Log.e("dataSize:", i.toString())
                            when (i) {
                                0 -> {
                                    binding.v1.setBackgroundResource(R.color.blue)
                                    if (dataHistory[i].probability_infection >= 60) {
                                        binding.i1.setImageResource(R.drawable.circle_red)
                                        if (i == dataHistory.size - 1) {
                                            binding.i1.setImageResource(R.drawable.circle_red_big)
                                        }
                                    } else {
                                        binding.i1.setImageResource(R.drawable.circle_blue)
                                        if (i == dataHistory.size - 1) {
                                            binding.i1.setImageResource(R.drawable.circle_blue_big)
                                        }
                                    }
                                }
                                1 -> {
                                    binding.v2.setBackgroundResource(R.color.blue)
                                    if (dataHistory[i].probability_infection >= 60) {
                                        binding.i2.setImageResource(R.drawable.circle_red)
                                        if (i == dataHistory.size - 1) {
                                            binding.i2.setImageResource(R.drawable.circle_red_big)
                                            binding.v2.setBackgroundResource(R.color.gray)

                                        }
                                    } else {
                                        binding.i2.setImageResource(R.drawable.circle_blue)
                                        if (i == dataHistory.size - 1) {
                                            binding.i2.setImageResource(R.drawable.circle_blue_big)
                                            binding.v2.setBackgroundResource(R.color.gray)

                                        }
                                    }
                                }
                                2 -> {
                                    binding.v3.setBackgroundResource(R.color.blue)
                                    if (dataHistory[i].probability_infection >= 60) {
                                        binding.i3.setImageResource(R.drawable.circle_red)
                                        if (i == dataHistory.size - 1) {
                                            binding.i3.setImageResource(R.drawable.circle_red_big)
                                            binding.v3.setBackgroundResource(R.color.gray)
                                        }
                                    } else {
                                        binding.i3.setImageResource(R.drawable.circle_blue)
                                        if (i == dataHistory.size - 1) {
                                            binding.i3.setImageResource(R.drawable.circle_blue_big)
                                            binding.v3.setBackgroundResource(R.color.gray)
                                        }
                                    }
                                }
                                3 -> {
                                    binding.v4.setBackgroundResource(R.color.blue)
                                    if (dataHistory[i].probability_infection >= 60) {
                                        binding.i4.setImageResource(R.drawable.circle_red)
                                        if (i == dataHistory.size - 1) {
                                            binding.i4.setImageResource(R.drawable.circle_red_big)
                                            binding.v4.setBackgroundResource(R.color.gray)
                                        }
                                    } else {
                                        binding.i4.setImageResource(R.drawable.circle_blue)
                                        if (i == dataHistory.size - 1) {
                                            binding.i4.setImageResource(R.drawable.circle_blue_big)
                                            binding.v4.setBackgroundResource(R.color.gray)
                                        }
                                    }
                                }
                                4 -> {
                                    binding.v5.setBackgroundResource(R.color.blue)
                                    if (dataHistory[i].probability_infection >= 60) {
                                        binding.i5.setImageResource(R.drawable.circle_red)
                                        if (i == dataHistory.size - 1) {
                                            binding.i5.setImageResource(R.drawable.circle_red_big)
                                            binding.v5.setBackgroundResource(R.color.gray)
                                        }
                                    } else {
                                        binding.i5.setImageResource(R.drawable.circle_blue)
                                        if (i == dataHistory.size - 1) {
                                            binding.i5.setImageResource(R.drawable.circle_blue_big)
                                            binding.v5.setBackgroundResource(R.color.gray)
                                        }
                                    }
                                }
                                5 -> {
                                    binding.v6.setBackgroundResource(R.color.blue)
                                    if (dataHistory[i].probability_infection >= 60) {
                                        binding.i6.setImageResource(R.drawable.circle_red)
                                        if (i == dataHistory.size - 1) {
                                            binding.i6.setImageResource(R.drawable.circle_red_big)
                                            binding.v6.setBackgroundResource(R.color.gray)

                                        }
                                    } else {
                                        binding.i6.setImageResource(R.drawable.circle_blue)
                                        if (i == dataHistory.size - 1) {
                                            binding.i6.setImageResource(R.drawable.circle_blue_big)
                                            binding.v6.setBackgroundResource(R.color.gray)

                                        }
                                    }
                                }
                                6 -> {
                                    if (dataHistory[i].probability_infection >= 60) {
                                        binding.i7.setImageResource(R.drawable.circle_red)
                                        if (i == dataHistory.size - 1) {
                                            binding.i7.setImageResource(R.drawable.circle_red_big)

                                        }
                                    } else {
                                        binding.i7.setImageResource(R.drawable.circle_blue)
                                        if (i == dataHistory.size - 1) {
                                            binding.i7.setImageResource(R.drawable.circle_blue_big)

                                        }
                                    }
                                }
                            }
                        }


                    } else {
                        binding.cardWithOutReport.visibility = View.VISIBLE
                        binding.bgWithOutReport.visibility = View.VISIBLE
                    }
                    Log.e("getHistory: ", it.toString())
                },
                {
                    binding.cardWithOutReport.visibility = View.VISIBLE
                    binding.bgWithOutReport.visibility = View.VISIBLE
                    Log.e("getHistory: ", it.toString())
                }
            )
        queue.add(request)
    }


}