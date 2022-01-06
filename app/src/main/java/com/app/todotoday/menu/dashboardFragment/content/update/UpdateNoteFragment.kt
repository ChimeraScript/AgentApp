package com.app.todotoday.menu.dashboardFragment.content.update

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.Data
import androidx.work.WorkManager
import com.app.myapplication.fragments.Pickers.DatePickerFragment
import com.app.myapplication.fragments.Pickers.TimePickerFragment
import com.app.todotoday.databinding.FragmentUpdateBinding
import com.app.todotoday.firestoreDB.NotesFS
import com.app.todotoday.sharedPreferences.SharedData.Companion.prefs
import com.app.todotoday.workmanager.NotificationWorker
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class UpdateNoteFragment : Fragment() {

    private var _binding : FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<UpdateNoteFragmentArgs>()
    private val db = FirebaseFirestore.getInstance()

    private val worker = NotificationWorker

    private var priority = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater,container,false)

        setUp()

        return binding.root
    }

    private fun setUp(){

        binding.UpdateTittleText.setText(args.note.tittle)
        binding.UpdateDescriptionText.setText(args.note.description)
        binding.UpdateSelectDate.setText(args.note.date)
        binding.UpdateSelectHour.setText(args.note.time)
        binding.UpdatePriority.setText(args.note.priority)

        binding.UpdateSelectDate.setOnClickListener{ showDatePickerDialog() }
        binding.UpdateSelectHour.setOnClickListener{ showTimePickerDialog() }
        binding.UpdatePriority.setOnClickListener{ priority() }
        binding.Discard.setOnClickListener { deleteTask() }
        binding.UpdateSendFAB.setOnClickListener{ updateItem() }
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateItem(){
        val tittle = binding.UpdateTittleText.text.toString()
        val description = binding.UpdateDescriptionText.text.toString()
        val date = binding.UpdateSelectDate.text.toString()
        val time = binding.UpdateSelectHour.text.toString()
        val priority = binding.UpdatePriority.text.toString()


        if(inputCheck(tittle, description, date, time, priority)){

            val workTime = "${date}-${time}"
            val sdf = SimpleDateFormat("dd/MM/yyyy'-'HH:mm").parse(workTime)
            val newTime = sdf!!.time
            val duration = newTime - System.currentTimeMillis()


            // Update Current User
            db.collection("users").document(prefs.getEmail())
                .collection("Dashboards").document(args.dashboardId)
                .collection("Notes").document(args.note.id).update(
                    "Tittle" , tittle,
                    "Description" , description,
                    "Date" , date,
                    "Time" , time,
                    "Priority" , priority
                )



            // Workmananger - delete notification
            WorkManager.getInstance(requireContext()).cancelAllWorkByTag(args.note.Creation)


            // Workmananger - new notification
            val data = dataNotification(tittle, description, args.note.Creation.toInt())
            worker.saveNotification(requireContext(),duration,data,args.note.Creation)


            Toast.makeText(requireContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show()

            val action = UpdateNoteFragmentDirections.actionUpdateNoteFragmentToInDashboardFragment(args.dashboardId,args.dashboardName)
            findNavController().navigate(action)
        }else{
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dataNotification(tittle:String,description:String,noticationId:Int): Data {
        return Data.Builder()
            .putString("tittle",tittle)
            .putString("description",description)
            .putInt("Id",noticationId).build()
    }





    private fun inputCheck(tittle: String, description: String, date: String, hour: String, priority: String): Boolean{
        return !(TextUtils.isEmpty(tittle) && TextUtils.isEmpty(description) && TextUtils.isEmpty(date) && TextUtils.isEmpty(hour) && TextUtils.isEmpty(priority))
    }


    @SuppressLint("SimpleDateFormat")
    private fun deleteTask(){
        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton("yes") { _, _ ->
            deleteFireStoreData(args.note)
            Toast.makeText(context, "Successfully removed!", Toast.LENGTH_SHORT).show()
            val action = UpdateNoteFragmentDirections.actionUpdateNoteFragmentToInDashboardFragment(args.dashboardId,args.dashboardName)
            findNavController().navigate(action)
        }
        builder.setNegativeButton("No"){ _, _ ->}
        builder.setTitle("Delete ${args.note.tittle}?")
        builder.setMessage("${prefs.getName()}are you sure you want to delete ${args.note.tittle}?")
        builder.create().show()

        // Workmananger - delete notification
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag(args.note.Creation)


    }

    private fun deleteFireStoreData(data: NotesFS){
        db.collection("users").document(prefs.getEmail())
            .collection("Dashboards").document(args.dashboardId)
            .collection("Notes").document(data.id).delete()
    }


    // DatePicker
    private fun showDatePickerDialog(){
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        activity?.let { datePicker.show(it.supportFragmentManager, "DatePicker") }
    }
    private fun onDateSelected(day: Int, month: Int, year: Int) {
        //Correct the month
        var m:String= (month+1).toString()
        // add 0 to month number
        when(m){
            "1"->m="01"
            "2"->m="02"
            "3"->m="03"
            "4"->m="04"
            "5"->m="05"
            "6"->m="06"
            "7"->m="07"
            "8"->m="08"
            "9"->m="09"
        }
        // add 0 to day number
        var d = day.toString()
        when(d){
            "1"->d="01"
            "2"->d="02"
            "3"->d="03"
            "4"->d="04"
            "5"->d="05"
            "6"->d="06"
            "7"->d="07"
            "8"->d="08"
            "9"->d="09"
        }
        val newDate = "$d/$m/$year"
        binding.UpdateSelectDate.setText(newDate)
    }


    // TimePicker
    private fun showTimePickerDialog(){
        val timePicker = TimePickerFragment{onTimeSelected(it)}
        activity?.let { timePicker.show(it.supportFragmentManager, "timePicker") }
    }
    private fun onTimeSelected(time: String){
        binding.UpdateSelectHour.setText(time)
    }



    // Priority
    private fun priority(){
        if (priority) {
            binding.UpdatePriority1.visibility = View.VISIBLE
            binding.UpdatePriority2.visibility = View.VISIBLE
            binding.UpdatePriority3.visibility = View.VISIBLE
            binding.UpdatePriority1.setOnClickListener {priorityLow()}
            binding.UpdatePriority2.setOnClickListener {priorityMedium()}
            binding.UpdatePriority3.setOnClickListener {priorityHigh()}
        }else{
            hidePriority()
            priority = true
        }
    }


    @SuppressLint("SetTextI18n")
    private fun priorityLow() {
        binding.UpdatePriority.setText("Low")
        hidePriority()
        priority = true
    }

    @SuppressLint("SetTextI18n")
    private fun priorityMedium() {
        binding.UpdatePriority.setText("Medium")
        hidePriority()
        priority = true
    }

    @SuppressLint("SetTextI18n")
    private fun priorityHigh() {
        binding.UpdatePriority.setText("High")
        hidePriority()
        priority = true
    }

    private fun hidePriority(){
        binding.UpdatePriority1.visibility = View.GONE
        binding.UpdatePriority2.visibility = View.GONE
        binding.UpdatePriority3.visibility = View.GONE
    }
}