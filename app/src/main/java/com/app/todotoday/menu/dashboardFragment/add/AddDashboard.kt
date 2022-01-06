package com.app.todotoday.menu.dashboardFragment.add

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.todotoday.databinding.FragmentAddDashboardBinding
import com.app.todotoday.firestoreDB.DashboardsFS
import com.app.todotoday.sharedPreferences.SharedData.Companion.prefs
import com.google.firebase.firestore.FirebaseFirestore


class AddDashboard : Fragment() {

    private var _binding: FragmentAddDashboardBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    // private val worker = NotificationWorker



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddDashboardBinding.inflate(inflater,container,false)


        setUp()


        // Inflate the layout for this fragment
        return binding.root
    }


    private fun setUp(){

        binding.sendFAB.setOnClickListener{
            insertDataToDB()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun insertDataToDB() {

        val tittle = binding.tittleTaskET .text.toString()
        val description = binding.descriptionTaskET.text.toString()


        if(tittle.isNotEmpty()){

            // FireStore
            // Create Task object
            val dataDashboard = DashboardsFS("", tittle,  description)
            // Add Data to FireStore
            addData(dataDashboard)

            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()
            val action = AddDashboardDirections.actionAddDashboardToDashboardFragment()
            findNavController().navigate(action)
        }else{
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
        }
    }

    // Add Data to Cloud FireStore
    private fun addData(data: DashboardsFS){
        db.collection("users").document(prefs.getEmail())
            .collection("Dashboards").document().set(
                hashMapOf(
                    "Tittle" to data.tittle,
                    "Description" to data.description
                )
            )

    }

}