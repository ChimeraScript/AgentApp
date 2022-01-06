package com.app.todotoday.menu.dashboardFragment.content.add

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.Data
import com.app.myapplication.fragments.Pickers.TimePickerFragment
import com.app.todotoday.databinding.FragmentAddRecurringTaskBinding
import com.app.todotoday.firestoreDB.RecurringTasksFS
import com.app.todotoday.sharedPreferences.SharedData
import com.app.todotoday.workmanager.NotificationWorker
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddRecurringTaskFragment : Fragment() {

    private var _binding : FragmentAddRecurringTaskBinding ? = null
    private val binding get() = _binding!!
    private val args by navArgs<AddNoteFragmentArgs>()
    private val db = FirebaseFirestore.getInstance()
    private val worker = NotificationWorker
    private var priority = true

    // List with dates
    private val list = arrayListOf<Long>()

    // days buttons
    private var mondayBtn = true
    private var tuesdayBtn = true
    private var wednesdayBtn = true
    private var thursdayBtn = true
    private var fridayBtn = true
    private var saturdayBtn = true
    private var sundayBtn = true

    // repetitions
    private var repetition = 0




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRecurringTaskBinding.inflate(inflater,container,false)

        setUp()

        return binding.root
    }


    private fun zzz(time: Long,differenceDays:Int){
        var repe=1
        var x :Long
        while (repe <= repetition){
            x = if (repe > 1){
                   ((repe-1) * 604800000)+(time + (differenceDays * 86400000))
            }else {
                time + (differenceDays * 86400000)
            }
            repe += 1
            list.add(x)
        }
    }

    private fun createNotification(
        time: kotlin.collections.ArrayList<Long>,
        tittle: String,
        description: String,
        creation:Int){

        for (i in time) {
            val duration = i - System.currentTimeMillis()

            // Workmananger
            try {
                val data = dataNotification(tittle, description, creation)
                worker.saveNotification(requireContext(), duration, data, creation.toString())
            }catch (e: ApiException){
                println("Error trying to create notification with $i")
            }
        }
    }

    private fun setUp(){
        repetitions()
        binding.timeTaskET.setOnClickListener{ showTimePickerDialog() }
        binding.priorityTaskTV.setOnClickListener{ priority() }
        binding.sendFAB.setOnClickListener{ insertDataToDB() }
    }

    @SuppressLint("SimpleDateFormat")
    private fun insertDataToDB() {

        val daysSelected = arrayListOf(mondayBtn,tuesdayBtn,wednesdayBtn,thursdayBtn,fridayBtn,saturdayBtn,sundayBtn)
        val tittle = binding.tittleTaskET.text.toString()
        val description = binding.descriptionTaskET.text.toString()
        val hour = binding.timeTaskET.text.toString()
        val priority = binding.priorityTaskTV.text.toString()

        if(tittle.isNotEmpty() && hour.isNotEmpty() && repetition != 0){
            if (!mondayBtn || !tuesdayBtn || !wednesdayBtn || !thursdayBtn || !fridayBtn || !saturdayBtn || !sundayBtn) {

                val sdfDay = SimpleDateFormat("dd/MM/yyyy")
                val netDate = Timestamp.now().toDate()
                val a = sdfDay.format(netDate)
                val date = "${a}-${hour}"
                val newDate = SimpleDateFormat("dd/MM/yyyy'-'HH:mm").parse(date)
                val z = newDate!!.time

                delayDate(daysSelected,z)

                // save  notification
                val creation = Timestamp.now().seconds.toInt()

                createNotification(list, tittle, description, creation)

                // FireStore
                // Create Task object
                val dataTask = RecurringTasksFS(
                    tittle = tittle,
                    description = description,
                    time = hour,
                    priority = priority,
                    Creation = creation.toString(),
                    monday = mondayBtn,
                    tuesday = tuesdayBtn,
                    wednesday = wednesdayBtn,
                    thursday = thursdayBtn,
                    friday = fridayBtn,
                    saturday = saturdayBtn,
                    sunday = sundayBtn,
                    repetition = repetition.toLong())
                // Add Data to FireStore
                addData(dataTask)

                Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()

                val action =
                    AddRecurringTaskFragmentDirections.actionAddRecurringTaskFragmentToInDashboardFragment(
                        args.dashboardId,
                        args.dashboardName)
                findNavController().navigate(action)
            }else{
                Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
            }
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
    private fun addData(data: RecurringTasksFS){
        db.collection("users").document(SharedData.prefs.getEmail())
            .collection("Dashboards").document(args.dashboardId)
            .collection("RecurringTasks").document().set(
                hashMapOf(
                    "Tittle" to data.tittle,
                    "Description" to data.description,
                    "Time" to data.time,
                    "Priority" to data.priority,
                    "Creation" to data.Creation,
                    "Monday" to mondayBtn,
                    "Tuesday" to tuesdayBtn,
                    "Wednesday" to wednesdayBtn,
                    "Thursday" to thursdayBtn,
                    "Friday" to fridayBtn,
                    "Saturday" to saturdayBtn,
                    "Sunday" to sundayBtn,
                    "Repetition" to repetition
                )
            )

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




    private fun repetitions(){

        binding.monday.setOnClickListener {
            mondayBtn = if(mondayBtn){
                binding.monday.setTextColor(Color.parseColor("#E9690C"))
                false
            }else{
                binding.monday.setTextColor(Color.parseColor("#000000"))
                true
            }
        }
        binding.tuesday.setOnClickListener {
            tuesdayBtn = if(tuesdayBtn){
                binding.tuesday.setTextColor(Color.parseColor("#E9690C"))
                false
            }else{
                binding.tuesday.setTextColor(Color.parseColor("#000000"))
                true
            }
        }
        binding.wednesday.setOnClickListener {
            wednesdayBtn = if(wednesdayBtn){
                binding.wednesday.setTextColor(Color.parseColor("#E9690C"))
                false
            }else{
                binding.wednesday.setTextColor(Color.parseColor("#000000"))
                true
            }
        }
        binding.thursday.setOnClickListener {
            thursdayBtn = if(thursdayBtn){
                binding.thursday.setTextColor(Color.parseColor("#E9690C"))
                false
            }else{
                binding.thursday.setTextColor(Color.parseColor("#000000"))
                true
            }
        }
        binding.friday.setOnClickListener {
            fridayBtn = if(fridayBtn){
                binding.friday.setTextColor(Color.parseColor("#E9690C"))
                false
            }else{
                binding.friday.setTextColor(Color.parseColor("#000000"))
                true
            }
        }
        binding.saturday.setOnClickListener {
            saturdayBtn = if(saturdayBtn){
                binding.saturday.setTextColor(Color.parseColor("#E9690C"))
                false
            }else{
                binding.saturday.setTextColor(Color.parseColor("#000000"))
                true
            }
        }
        binding.sunday.setOnClickListener {
            sundayBtn = if(sundayBtn){
                binding.sunday.setTextColor(Color.parseColor("#E9690C"))
                false
            }else{
                binding.sunday.setTextColor(Color.parseColor("#000000"))
                true
            }
        }

        binding.repetition1.setOnClickListener {
            if (repetition == 1){
                repetition = 0
                binding.repetition1.setTextColor(Color.parseColor("#000000"))
            }else {
                repetition = 1
                binding.repetition1.setTextColor(Color.parseColor("#E9690C"))
                binding.repetition2.setTextColor(Color.parseColor("#000000"))
                binding.repetition3.setTextColor(Color.parseColor("#000000"))
                binding.repetition4.setTextColor(Color.parseColor("#000000"))
                binding.repetition5.setTextColor(Color.parseColor("#000000"))
                binding.repetition6.setTextColor(Color.parseColor("#000000"))
                binding.repetition7.setTextColor(Color.parseColor("#000000"))
                binding.repetition8.setTextColor(Color.parseColor("#000000"))
                binding.repetition9.setTextColor(Color.parseColor("#000000"))
                binding.repetition10.setTextColor(Color.parseColor("#000000"))
            }
        }
        binding.repetition2.setOnClickListener {
            if (repetition == 2){
                repetition = 0
                binding.repetition2.setTextColor(Color.parseColor("#000000"))
            }else {
                repetition = 2
                binding.repetition2.setTextColor(Color.parseColor("#E9690C"))
                binding.repetition1.setTextColor(Color.parseColor("#000000"))
                binding.repetition3.setTextColor(Color.parseColor("#000000"))
                binding.repetition4.setTextColor(Color.parseColor("#000000"))
                binding.repetition5.setTextColor(Color.parseColor("#000000"))
                binding.repetition6.setTextColor(Color.parseColor("#000000"))
                binding.repetition7.setTextColor(Color.parseColor("#000000"))
                binding.repetition8.setTextColor(Color.parseColor("#000000"))
                binding.repetition9.setTextColor(Color.parseColor("#000000"))
                binding.repetition10.setTextColor(Color.parseColor("#000000"))
            }
        }
        binding.repetition3.setOnClickListener {
            if (repetition == 3){
                repetition = 0
                binding.repetition3.setTextColor(Color.parseColor("#000000"))
            }else {
                repetition = 3
                binding.repetition3.setTextColor(Color.parseColor("#E9690C"))
                binding.repetition6.setTextColor(Color.parseColor("#000000"))
                binding.repetition7.setTextColor(Color.parseColor("#000000"))
                binding.repetition8.setTextColor(Color.parseColor("#000000"))
                binding.repetition9.setTextColor(Color.parseColor("#000000"))
                binding.repetition10.setTextColor(Color.parseColor("#000000"))
                binding.repetition1.setTextColor(Color.parseColor("#000000"))
                binding.repetition2.setTextColor(Color.parseColor("#000000"))
                binding.repetition4.setTextColor(Color.parseColor("#000000"))
                binding.repetition5.setTextColor(Color.parseColor("#000000"))
            }
        }
        binding.repetition4.setOnClickListener {
            if (repetition == 4){
                repetition = 0
                binding.repetition4.setTextColor(Color.parseColor("#000000"))
            }else {
                repetition = 4
                binding.repetition4.setTextColor(Color.parseColor("#E9690C"))
                binding.repetition6.setTextColor(Color.parseColor("#000000"))
                binding.repetition7.setTextColor(Color.parseColor("#000000"))
                binding.repetition8.setTextColor(Color.parseColor("#000000"))
                binding.repetition9.setTextColor(Color.parseColor("#000000"))
                binding.repetition10.setTextColor(Color.parseColor("#000000"))
                binding.repetition1.setTextColor(Color.parseColor("#000000"))
                binding.repetition2.setTextColor(Color.parseColor("#000000"))
                binding.repetition3.setTextColor(Color.parseColor("#000000"))
                binding.repetition5.setTextColor(Color.parseColor("#000000"))
            }
        }
        binding.repetition5.setOnClickListener {
            if (repetition == 5){
                repetition = 0
                binding.repetition5.setTextColor(Color.parseColor("#000000"))
            }else {
                repetition = 5
                binding.repetition5.setTextColor(Color.parseColor("#E9690C"))
                binding.repetition6.setTextColor(Color.parseColor("#000000"))
                binding.repetition7.setTextColor(Color.parseColor("#000000"))
                binding.repetition8.setTextColor(Color.parseColor("#000000"))
                binding.repetition9.setTextColor(Color.parseColor("#000000"))
                binding.repetition10.setTextColor(Color.parseColor("#000000"))
                binding.repetition1.setTextColor(Color.parseColor("#000000"))
                binding.repetition2.setTextColor(Color.parseColor("#000000"))
                binding.repetition3.setTextColor(Color.parseColor("#000000"))
                binding.repetition4.setTextColor(Color.parseColor("#000000"))
            }
        }
        binding.repetition6.setOnClickListener {
            if (repetition == 6){
                repetition = 0
                binding.repetition6.setTextColor(Color.parseColor("#000000"))
            }else {
                repetition = 6
                binding.repetition6.setTextColor(Color.parseColor("#E9690C"))
                binding.repetition7.setTextColor(Color.parseColor("#000000"))
                binding.repetition8.setTextColor(Color.parseColor("#000000"))
                binding.repetition9.setTextColor(Color.parseColor("#000000"))
                binding.repetition10.setTextColor(Color.parseColor("#000000"))
                binding.repetition5.setTextColor(Color.parseColor("#000000"))
                binding.repetition1.setTextColor(Color.parseColor("#000000"))
                binding.repetition2.setTextColor(Color.parseColor("#000000"))
                binding.repetition3.setTextColor(Color.parseColor("#000000"))
                binding.repetition4.setTextColor(Color.parseColor("#000000"))
            }
        }
        binding.repetition7.setOnClickListener {
            if (repetition == 7){
                repetition = 0
                binding.repetition7.setTextColor(Color.parseColor("#000000"))
            }else {
                repetition = 7
                binding.repetition7.setTextColor(Color.parseColor("#E9690C"))
                binding.repetition8.setTextColor(Color.parseColor("#000000"))
                binding.repetition9.setTextColor(Color.parseColor("#000000"))
                binding.repetition10.setTextColor(Color.parseColor("#000000"))
                binding.repetition6.setTextColor(Color.parseColor("#000000"))
                binding.repetition5.setTextColor(Color.parseColor("#000000"))
                binding.repetition1.setTextColor(Color.parseColor("#000000"))
                binding.repetition2.setTextColor(Color.parseColor("#000000"))
                binding.repetition3.setTextColor(Color.parseColor("#000000"))
                binding.repetition4.setTextColor(Color.parseColor("#000000"))
            }
        }
        binding.repetition8.setOnClickListener {
            if (repetition == 8){
                repetition = 0
                binding.repetition8.setTextColor(Color.parseColor("#000000"))
            }else {
                repetition = 8
                binding.repetition8.setTextColor(Color.parseColor("#E9690C"))
                binding.repetition10.setTextColor(Color.parseColor("#000000"))
                binding.repetition9.setTextColor(Color.parseColor("#000000"))
                binding.repetition7.setTextColor(Color.parseColor("#000000"))
                binding.repetition6.setTextColor(Color.parseColor("#000000"))
                binding.repetition5.setTextColor(Color.parseColor("#000000"))
                binding.repetition1.setTextColor(Color.parseColor("#000000"))
                binding.repetition2.setTextColor(Color.parseColor("#000000"))
                binding.repetition3.setTextColor(Color.parseColor("#000000"))
                binding.repetition4.setTextColor(Color.parseColor("#000000"))
            }
        }
        binding.repetition9.setOnClickListener {
            if (repetition == 9){
                repetition = 0
                binding.repetition9.setTextColor(Color.parseColor("#000000"))
            }else {
                repetition = 9
                binding.repetition9.setTextColor(Color.parseColor("#E9690C"))
                binding.repetition10.setTextColor(Color.parseColor("#000000"))
                binding.repetition8.setTextColor(Color.parseColor("#000000"))
                binding.repetition7.setTextColor(Color.parseColor("#000000"))
                binding.repetition6.setTextColor(Color.parseColor("#000000"))
                binding.repetition5.setTextColor(Color.parseColor("#000000"))
                binding.repetition1.setTextColor(Color.parseColor("#000000"))
                binding.repetition2.setTextColor(Color.parseColor("#000000"))
                binding.repetition3.setTextColor(Color.parseColor("#000000"))
                binding.repetition4.setTextColor(Color.parseColor("#000000"))
            }
        }
        binding.repetition10.setOnClickListener {
            if (repetition == 10){
                repetition = 0
                binding.repetition10.setTextColor(Color.parseColor("#000000"))
            }else {
                repetition = 10
                binding.repetition10.setTextColor(Color.parseColor("#E9690C"))
                binding.repetition9.setTextColor(Color.parseColor("#000000"))
                binding.repetition8.setTextColor(Color.parseColor("#000000"))
                binding.repetition7.setTextColor(Color.parseColor("#000000"))
                binding.repetition6.setTextColor(Color.parseColor("#000000"))
                binding.repetition5.setTextColor(Color.parseColor("#000000"))
                binding.repetition1.setTextColor(Color.parseColor("#000000"))
                binding.repetition2.setTextColor(Color.parseColor("#000000"))
                binding.repetition3.setTextColor(Color.parseColor("#000000"))
                binding.repetition4.setTextColor(Color.parseColor("#000000"))
            }
        }
    }

    private fun delayDate(daySelected: List<Boolean>, time: Long){

        val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        var differenceDays = 0

        if (!daySelected[0]){
            when(day){
                1 -> differenceDays = 1
                2 -> differenceDays = 0
                3 -> differenceDays = 6
                4 -> differenceDays = 5
                5 -> differenceDays = 4
                6 -> differenceDays = 3
                7 -> differenceDays = 2
            }
            zzz(time,differenceDays)
        }
        if (!daySelected[1]){
            when(day){
                1 -> differenceDays = 2
                2 -> differenceDays = 1
                3 -> differenceDays = 0
                4 -> differenceDays = 6
                5 -> differenceDays = 5
                6 -> differenceDays = 4
                7 -> differenceDays = 3
            }
            zzz(time,differenceDays)
        }
        if (!daySelected[2]){
            when(day){
                1 -> differenceDays = 3
                2 -> differenceDays = 2
                3 -> differenceDays = 1
                4 -> differenceDays = 0
                5 -> differenceDays = 6
                6 -> differenceDays = 5
                7 -> differenceDays = 4
            }
            zzz(time,differenceDays)
        }
        if (!daySelected[3]){
            when(day){
                1 -> differenceDays = 4
                2 -> differenceDays = 3
                3 -> differenceDays = 2
                4 -> differenceDays = 1
                5 -> differenceDays = 0
                6 -> differenceDays = 6
                7 -> differenceDays = 5
            }
            zzz(time,differenceDays)
        }
        if (!daySelected[4]){
            when(day){
                1 -> differenceDays = 5
                2 -> differenceDays = 4
                3 -> differenceDays = 3
                4 -> differenceDays = 2
                5 -> differenceDays = 1
                6 -> differenceDays = 0
                7 -> differenceDays = 6
            }
            zzz(time,differenceDays)
        }
        if (!daySelected[5]){
            when(day){
                1 -> differenceDays = 6
                2 -> differenceDays = 5
                3 -> differenceDays = 4
                4 -> differenceDays = 3
                5 -> differenceDays = 2
                6 -> differenceDays = 1
                7 -> differenceDays = 0
            }
            zzz(time,differenceDays)
        }
        if (!daySelected[6]){
            when(day){
                1 -> differenceDays = 0
                2 -> differenceDays = 6
                3 -> differenceDays = 5
                4 -> differenceDays = 4
                5 -> differenceDays = 3
                6 -> differenceDays = 2
                7 -> differenceDays = 1
            }
            zzz(time,differenceDays)
        }
    }
}