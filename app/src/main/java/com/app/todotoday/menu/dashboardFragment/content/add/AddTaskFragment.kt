package com.app.todotoday.menu.dashboardFragment.content.add

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.Data
import com.app.myapplication.fragments.Pickers.DatePickerFragment
import com.app.myapplication.fragments.Pickers.TimePickerFragment
import com.app.todotoday.databinding.FragmentAddBinding
import com.app.todotoday.firestoreDB.TasksFS
import com.app.todotoday.sharedPreferences.SharedData.Companion.prefs
import com.app.todotoday.workmanager.NotificationWorker
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class AddTaskFragment : Fragment() {

    private var _binding : FragmentAddBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<AddTaskFragmentArgs>()

    private val db = FirebaseFirestore.getInstance()

    private val worker = NotificationWorker

    private var priority = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater,container,false)

        setUp()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setUp(){
        binding.dateTaskET.setOnClickListener{ showDatePickerDialog() }
        binding.timeTaskET.setOnClickListener{ showTimePickerDialog() }
        binding.priorityTaskTV.setOnClickListener{ priority() }

        binding.sendFAB.setOnClickListener{
            insertDataToDB()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun insertDataToDB() {
        val tittle = binding.tittleTaskET.text.toString()
        val description = binding.descriptionTaskET.text.toString()
        val date = binding.dateTaskET.text.toString()
        val hour = binding.timeTaskET.text.toString()
        val priority = binding.priorityTaskTV.text.toString()


        if(tittle.isNotEmpty() && date.isNotEmpty() && hour.isNotEmpty()){

            // save  notification
            val notificationId = Timestamp.now().seconds.toInt()

            val time = "$date-$hour"
            val sdf = SimpleDateFormat("dd/MM/yyyy'-'HH:mm").parse(time)
            val workDate = sdf!!.time
            val duration = workDate - System.currentTimeMillis()



            // Workmananger
            val data = dataNotification(tittle, description, notificationId)
            worker.saveNotification(requireContext(),duration,data,notificationId.toString())


            // FireStore
            // Create Task object
            val dataTask = TasksFS(tittle=tittle, description = description, date = date, time = hour, priority = priority, Creation = notificationId.toString())
            // Add Data to FireStore
            addData(dataTask)

            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()

            val action = AddTaskFragmentDirections.actionAddTasksFragmentToInDashboardFragment(args.dashboardId,args.dashboardName)
            findNavController().navigate(action)
        }else{
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
        }
    }


    private fun dataNotification(tittle:String,description:String,noticationId:Int): Data {
        return Data.Builder()
            .putString("tittle",tittle)
            .putString("description",description)
            .putInt("Id",noticationId).build()
    }



    // Add Data to Cloud FireStore
    @SuppressLint("SimpleDateFormat")
    private fun addData(data: TasksFS){
        db.collection("users").document(prefs.getEmail())
            .collection("Dashboards").document(args.dashboardId)
            .collection("Tasks").document().set(
                hashMapOf(
                    "Tittle" to data.tittle,
                    "Description" to data.description,
                    "Date" to data.date,
                    "Time" to data.time,
                    "Priority" to data.priority,
                    "Creation" to data.Creation
                )
            )

    }

    // DatePicker
    private fun showDatePickerDialog(){
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        activity?.let { datePicker.show(it.supportFragmentManager ,"DatePicker") }
    }
    private fun onDateSelected(day: Int, month: Int, year: Int){
        // error month's number
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
        val newDate="$d/$m/$year"
        binding.dateTaskET.text = newDate
    }

    // TimePicker
    private fun showTimePickerDialog(){
        val timePicker = TimePickerFragment{onTimeSelected(it)}
        activity?.let { timePicker.show(it.supportFragmentManager,"timePicker") }
    }
    private fun onTimeSelected(time:String){
        binding.timeTaskET.text = time
    }

    // Priority
    private fun priority(){
        if (priority) {
            binding.priorityLowTV.visibility = View.VISIBLE
            binding.priorityMediumTV.visibility = View.VISIBLE
            binding.priorityHighTV.visibility = View.VISIBLE
            binding.priorityLowTV.setOnClickListener {priorityLow()}
            binding.priorityMediumTV.setOnClickListener {priorityMedium()}
            binding.priorityHighTV.setOnClickListener {priorityHigh()}
        }else{
            hidePriority()
            priority = true
        }
    }
    @SuppressLint("SetTextI18n")
    private fun priorityLow() {
        binding.priorityTaskTV.text = "Low"
        hidePriority()
        priority = true
    }
    @SuppressLint("SetTextI18n")
    private fun priorityMedium() {
        binding.priorityTaskTV.text = "Medium"
        hidePriority()
        priority = true
    }
    @SuppressLint("SetTextI18n")
    private fun priorityHigh() {
        binding.priorityTaskTV.text = "High"
        hidePriority()
        priority = true
    }
    private fun hidePriority(){
        binding.priorityLowTV.visibility = View.GONE
        binding.priorityMediumTV.visibility = View.GONE
        binding.priorityHighTV.visibility = View.GONE
    }
}