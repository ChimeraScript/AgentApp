package com.app.todotoday.menu.todayFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.todotoday.databinding.FragmentTodayBinding
import com.app.todotoday.firestoreDB.FireStoreViewModel
import com.app.todotoday.firestoreDB.TasksFS
import java.util.*


class TodayFragment : Fragment(){

    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!
    private val viewModelFB by lazy { ViewModelProvider(this).get(FireStoreViewModel::class.java) }
    private var openFAB = false
    private var date: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodayBinding.inflate(inflater,container,false)

        setUp()

        // Inflate the layout for this fragment
        return binding.root
    }


    private fun setUp(){
        val calendar = Calendar.getInstance()
        val d = calendar.get(Calendar.DAY_OF_MONTH)
        val m = calendar.get(Calendar.MONTH)
        val y = calendar.get(Calendar.YEAR)
        recyclerDate(d,m,y)


        binding.datePickerTimeline.setInitialDate(y, m, d-3)
        binding.datePickerTimeline.setOnDateSelectedListener { year, month, day, _ ->

            recyclerDate(day,month,year)

        }

        binding.dashboardBtn.setOnClickListener {
            val action = TodayFragmentDirections.actionTodayFragmentToDashboardFragment()
            findNavController().navigate(action)
        }

        binding.InfoBtn.setOnClickListener {
            val action = TodayFragmentDirections.actionTodayFragmentToInfoFragment()
            findNavController().navigate(action)
        }

        binding.MenuFAB.setOnClickListener{
            if (openFAB){

                binding.noteText.visibility = View.INVISIBLE
                binding.taskText.visibility = View.INVISIBLE
                binding.recurringTaskText.visibility = View.INVISIBLE

                binding.noteFAB.visibility = View.INVISIBLE
                binding.TaskFAB.visibility = View.INVISIBLE
                binding.recurringTaskFAB.visibility = View.INVISIBLE


                openFAB=false
            } else{

                binding.noteText.visibility = View.VISIBLE
                binding.taskText.visibility = View.VISIBLE
                binding.recurringTaskText.visibility = View.VISIBLE

                binding.noteFAB.visibility = View.VISIBLE
                binding.TaskFAB.visibility = View.VISIBLE
                binding.recurringTaskFAB.visibility = View.VISIBLE

                noteFAB()
                taskFAB()
                recurringTaskFAB()

                openFAB=true
            }
        }
    }

    private fun noteFAB() = binding.noteFAB.setOnClickListener {
        val action = TodayFragmentDirections.actionTodayFragmentToDashboardFragment()
        findNavController().navigate(action)
    }
    private fun taskFAB() = binding.TaskFAB.setOnClickListener {
        val action = TodayFragmentDirections.actionTodayFragmentToDashboardFragment()
        findNavController().navigate(action)
    }
    private fun recurringTaskFAB() = binding.recurringTaskFAB.setOnClickListener {
        val action = TodayFragmentDirections.actionTodayFragmentToDashboardFragment()
        findNavController().navigate(action)
    }

    private fun recyclerDate(day:Int,month:Int,year:Int){
        // error month's number
        var aM:String= (month+1).toString()
        // add 0 to month number
        when(aM){
            "1"->aM="01"
            "2"->aM="02"
            "3"->aM="03"
            "4"->aM="04"
            "5"->aM="05"
            "6"->aM="06"
            "7"->aM="07"
            "8"->aM="08"
            "9"->aM="09"
        }
        // add 0 to day number
        var aD = day.toString()
        when(aD){
            "1"->aD="01"
            "2"->aD="02"
            "3"->aD="03"
            "4"->aD="04"
            "5"->aD="05"
            "6"->aD="06"
            "7"->aD="07"
            "8"->aD="08"
            "9"->aD="09"
        }
        date = "$aD/$aM/$year"
        binding.dateText.text = date


        val adapter = TodayAdapter()
        binding.recyclerView .adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)


        viewModelFB.fetchDashboardTasks(date).observe(viewLifecycleOwner,{
            adapter.setDataFB(it as ArrayList<TasksFS>)
        })

    }
}