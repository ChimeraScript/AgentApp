package com.app.todotoday.menu.dashboardFragment.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.todotoday.databinding.FragmentEditDashboardBinding
import com.app.todotoday.firestoreDB.DashboardsFS
import com.app.todotoday.sharedPreferences.SharedData.Companion.prefs
import com.google.firebase.firestore.FirebaseFirestore


class UpdateDashboardFireBaseFragment : Fragment() {

    private var _binding: FragmentEditDashboardBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    private val args by navArgs<UpdateDashboardFireBaseFragmentArgs>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditDashboardBinding.inflate(inflater,container,false)


        setUp()


        // Inflate the layout for this fragment
        return binding.root
    }


    private fun setUp(){

        binding.UpdateTittleText.setText(args.dashboard.tittle)
        binding.UpdateDescriptionText.setText(args.dashboard.description)

        binding.Discard.setOnClickListener { deleteTask() }
        binding.UpdateSendFAB.setOnClickListener{ updateItem() }
    }

    private fun updateItem(){
        val tittle = binding.UpdateTittleText.text.toString()
        val description = binding.UpdateDescriptionText.text.toString()


        if(tittle.isNotEmpty()){

            // Update Dashboard
            db.collection("users").document(prefs.getEmail())
                .collection("Dashboards").document(args.dashboard.id).update(
                    "Tittle" , tittle,
                    "Description" , description
                )

            Toast.makeText(requireContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show()

            // Navigate Back
            val action = UpdateDashboardFireBaseFragmentDirections.actionUpdateDashboardFireBaseFragmentToDashboardFragment()
            findNavController().navigate(action)
        }else{
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun deleteTask(){

        val builder = AlertDialog.Builder(context)

        builder.setPositiveButton("yes") { _, _ ->
            deleteFireStoreData(args.dashboard)
            Toast.makeText(context, "Successfully removed!", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No"){ _, _ ->}
        builder.setTitle("Delete ${args.dashboard.tittle}?")
        builder.setMessage("${prefs.getName()} are you sure you want to delete ${args.dashboard.tittle}?")
        builder.create().show()
    }

    private fun deleteFireStoreData(data: DashboardsFS){
        //delete tasks
        db.collection("users").document(prefs.getEmail())
            .collection("Dashboards").document(data.id)
            .collection("Tasks").document().delete()

        //delete notes
        db.collection("users").document(prefs.getEmail())
            .collection("Dashboards").document(data.id)
            .collection("Notes").document().delete()

        //delete recurring tasks
        db.collection("users").document(prefs.getEmail())
            .collection("Dashboards").document(data.id)
            .collection("RecurringTasks").document().delete()

        db.collection("users").document(prefs.getEmail())
            .collection("Dashboards").document(data.id).delete()

        val action = UpdateDashboardFireBaseFragmentDirections.actionUpdateDashboardFireBaseFragmentToDashboardFragment()
        findNavController().navigate(action)
    }
}