package com.app.todotoday.menu.dashboardFragment.content.update

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import androidx.work.WorkManager
import com.app.myapplication.fragments.Pickers.TimePickerFragment
import com.app.todotoday.databinding.FragmentUpdateRecurringTaskBinding
import com.app.todotoday.firestoreDB.RecurringTasksFS
import com.app.todotoday.sharedPreferences.SharedData.Companion.prefs
import com.app.todotoday.workmanager.NotificationWorker
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class UpdateRecurringTaskFragment : Fragment() {

    private var _binding : FragmentUpdateRecurringTaskBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<UpdateRecurringTaskFragmentArgs>()
    private val db = FirebaseFirestore.getInstance()

    private val worker = NotificationWorker

    private var priority = true

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
        _binding = FragmentUpdateRecurringTaskBinding.inflate(inflater,container,false)

        setUp()

        return binding.root
    }

    private fun setUp(){

        repetitions()
        setText()
        binding.tittleTaskET.setText(args.recurringTask.tittle)
        binding.descriptionTaskET.setText(args.recurringTask.description)
        binding.timeTaskET.text = args.recurringTask.time
        binding.priorityTaskTV.text = args.recurringTask.priority

        when(args.recurringTask.repetition.toInt()){
            1 -> {repetition = 1
                binding.repetition1.setTextColor(Color.parseColor("#E9690C"))}
            2 -> {repetition = 2
                binding.repetition2.setTextColor(Color.parseColor("#E9690C"))}
            3 -> {repetition = 3
                binding.repetition3.setTextColor(Color.parseColor("#E9690C"))}
            4 -> {repetition = 4
                binding.repetition4.setTextColor(Color.parseColor("#E9690C"))}
            5 -> {repetition = 5
                binding.repetition5.setTextColor(Color.parseColor("#E9690C"))}
            6 -> {repetition = 6
                binding.repetition6.setTextColor(Color.parseColor("#E9690C"))}
            7 -> {repetition = 7
                binding.repetition7.setTextColor(Color.parseColor("#E9690C"))}
            8 -> {repetition = 8
                binding.repetition8.setTextColor(Color.parseColor("#E9690C"))}
            9 -> {repetition = 9
                binding.repetition9.setTextColor(Color.parseColor("#E9690C"))}
            10 -> {repetition = 10
                binding.repetition10.setTextColor(Color.parseColor("#E9690C"))}
        }


        binding.timeTaskET.setOnClickListener{ showTimePickerDialog() }
        binding.priorityTaskTV.setOnClickListener{ priority() }
        binding.Delete.setOnClickListener { deleteTask() }
        binding.sendFAB.setOnClickListener{ updateItem() }
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateItem(){
        val tittle = binding.tittleTaskET.text.toString()
        val description = binding.descriptionTaskET.text.toString()
        val hour = binding.timeTaskET.text.toString()
        val priority = binding.priorityTaskTV.text.toString()


        if(tittle.isNotEmpty() && description.isNotEmpty() && hour.isNotEmpty()){

            val sdfDay = SimpleDateFormat("dd/MM/yyyy")
            val netDate = Timestamp.now().toDate()
            val a = sdfDay.format(netDate)
            val date = "${a}-${hour}"
            val newDate = SimpleDateFormat("dd/MM/yyyy'-'HH:mm").parse(date)
            val z = newDate!!.time
            val duration = z - System.currentTimeMillis()


            // Update Current User
            db.collection("users").document(prefs.getEmail())
                .collection("Dashboards").document(args.dashboardId)
                .collection("RecurringTasks").document(args.recurringTask.id).update(
                    "Tittle" , tittle,
                    "Description" , description,
                    "Time" , hour,
                    "Priority" , priority,
                    "Monday" , mondayBtn,
                    "Tuesday" , tuesdayBtn,
                    "Wednesday" , wednesdayBtn,
                    "Thursday" , thursdayBtn,
                    "Friday" , fridayBtn,
                    "Saturday" , saturdayBtn,
                    "Sunday" , sundayBtn,
                    "Repetition" , repetition
                )



            // Workmananger - delete notification
            WorkManager.getInstance(requireContext()).cancelAllWorkByTag(args.recurringTask.Creation)


            // Workmananger - new notification
            val data = dataNotification(tittle, description, args.recurringTask.Creation.toInt())
            worker.saveNotification(requireContext(),duration,data,args.recurringTask.Creation)


            Toast.makeText(requireContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show()

            val action = UpdateRecurringTaskFragmentDirections.actionUpdateRecurringTaskFragmentToInDashboardFragment(args.dashboardId,args.dashboardName)
            findNavController().navigate(action)
        }else{
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setText(){
        if(!args.recurringTask.monday){
            mondayBtn = false
            binding.monday.setTextColor(Color.parseColor("#E9690C"))
        }
        if (!args.recurringTask.tuesday){
            tuesdayBtn = false
            binding.tuesday.setTextColor(Color.parseColor("#E9690C"))
        }
        if (!args.recurringTask.wednesday){
            wednesdayBtn = false
            binding.wednesday.setTextColor(Color.parseColor("#E9690C"))
        }
        if (!args.recurringTask.thursday){
            thursdayBtn = false
            binding.thursday.setTextColor(Color.parseColor("#E9690C"))
        }
        if (!args.recurringTask.friday){
            fridayBtn = false
            binding.friday.setTextColor(Color.parseColor("#E9690C"))
        }
        if (!args.recurringTask.saturday){
            saturdayBtn = false
            binding.saturday.setTextColor(Color.parseColor("#E9690C"))
        }
        if (!args.recurringTask.sunday){
            sundayBtn = false
            binding.sunday.setTextColor(Color.parseColor("#E9690C"))
        }
    }

    private fun dataNotification(tittle:String,description:String,noticationId:Int): Data {
        return Data.Builder()
            .putString("tittle",tittle)
            .putString("description",description)
            .putInt("Id",noticationId).build()
    }

    @SuppressLint("SimpleDateFormat")
    private fun deleteTask(){
        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton("yes") { _, _ ->
            deleteFireStoreData(args.recurringTask)
            Toast.makeText(context, "Successfully removed!", Toast.LENGTH_SHORT).show()
            val action = UpdateRecurringTaskFragmentDirections.actionUpdateRecurringTaskFragmentToInDashboardFragment(args.dashboardId,args.dashboardName)
            findNavController().navigate(action)
        }
        builder.setNegativeButton("No"){ _, _ ->}
        builder.setTitle("Delete ${args.recurringTask.tittle}?")
        builder.setMessage("${prefs.getName()} are you sure you want to delete ${args.recurringTask.tittle}?")
        builder.create().show()

        // Workmananger - delete notification
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag(args.recurringTask.Creation)


    }

    private fun deleteFireStoreData(data: RecurringTasksFS){
        db.collection("users").document(prefs.getEmail())
            .collection("Dashboards").document(args.dashboardId)
            .collection("RecurringTasks").document(data.id).delete()
    }


    // TimePicker
    private fun showTimePickerDialog(){
        val timePicker = TimePickerFragment{onTimeSelected(it)}
        activity?.let { timePicker.show(it.supportFragmentManager, "timePicker") }
    }
    private fun onTimeSelected(time: String){
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
}