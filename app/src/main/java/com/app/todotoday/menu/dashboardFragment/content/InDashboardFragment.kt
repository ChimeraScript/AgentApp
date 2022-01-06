package com.app.todotoday.menu.dashboardFragment.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.todotoday.databinding.FragmentInDashboardBinding
import com.app.todotoday.firestoreDB.FireStoreViewModel


class InDashboardFragment : Fragment() {

    private var _binding : FragmentInDashboardBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<InDashboardFragmentArgs>()

    private val viewModelFB by lazy { ViewModelProvider(this).get(FireStoreViewModel::class.java) }

    private var openFAB = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInDashboardBinding.inflate(inflater,container,false)

        setUp()
        showTask()

        // Inflate the layout for this fragment
        return binding.root
    }


    private fun setUp(){

        binding.dashboardName.text = args.dashboardName

        binding.tasksBtn.setOnClickListener {

            showTask()

            binding.notesTittle.visibility = View.INVISIBLE
            binding.notesRV.visibility = View.INVISIBLE

            binding.recurringTaskTittle.visibility = View.INVISIBLE
            binding.recurringTaskRV.visibility = View.INVISIBLE

            binding.tasksTittle.visibility = View.VISIBLE
            binding.taskRV.visibility = View.VISIBLE
        }


        binding.notesBtn.setOnClickListener {

            // Notes RecyclerView
            val notesAdapter =NotesAdapter(args.dashboardId,args.dashboardName)
            binding.notesRV.adapter = notesAdapter
            binding.notesRV.layoutManager = LinearLayoutManager(context)

            binding.shimmerViewContainer.visibility = View.VISIBLE
            binding.shimmerViewContainer.startShimmer()
            viewModelFB.fetchNoteData(args.dashboardId).observe(viewLifecycleOwner,{
                binding.shimmerViewContainer.hideShimmer() // don't work
                binding.shimmerViewContainer.stopShimmer()
                binding.shimmerViewContainer.visibility=View.GONE
                notesAdapter.setData(it)
            })

            binding.tasksTittle.visibility = View.INVISIBLE
            binding.taskRV.visibility = View.INVISIBLE

            binding.recurringTaskTittle.visibility = View.INVISIBLE
            binding.recurringTaskRV.visibility = View.INVISIBLE

            binding.notesTittle.visibility = View.VISIBLE
            binding.notesRV.visibility = View.VISIBLE
        }


        binding.recurringTaskBtn.setOnClickListener {

            // RecurringTask RecyclerView
            val recurringTaskAdapter =RecurringTaskAdapter(args.dashboardId,args.dashboardName)
            binding.recurringTaskRV.adapter = recurringTaskAdapter
            binding.recurringTaskRV.layoutManager = LinearLayoutManager(context)

            binding.shimmerViewContainer.visibility = View.VISIBLE
            binding.shimmerViewContainer.startShimmer()
            viewModelFB.fetchRecurringTaskData(args.dashboardId).observe(viewLifecycleOwner,{
                binding.shimmerViewContainer.hideShimmer() // don't work
                binding.shimmerViewContainer.stopShimmer()
                binding.shimmerViewContainer.visibility=View.GONE
                recurringTaskAdapter.setData(it)
            })

            binding.tasksTittle.visibility = View.INVISIBLE
            binding.taskRV.visibility = View.INVISIBLE

            binding.notesTittle.visibility = View.INVISIBLE
            binding.notesRV.visibility = View.INVISIBLE

            binding.recurringTaskTittle.visibility = View.VISIBLE
            binding.recurringTaskRV.visibility = View.VISIBLE
        }


        binding.dashboardBtn.setOnClickListener {
            val action = InDashboardFragmentDirections.actionInDashboardFragmentToDashboardFragment()
            findNavController().navigate(action)
        }


        binding.InfoBtn.setOnClickListener {
            val action = InDashboardFragmentDirections.actionInDashboardFragmentToInfoFragment()
            findNavController().navigate(action)
        }


        binding.todayBtn.setOnClickListener {
            val action = InDashboardFragmentDirections.actionInDashboardFragmentToTodayFragment()
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

                openFAB=true

                noteFAB()
                taskFAB()
                recurringTaskFAB()
            }
        }
    }


    private fun noteFAB() = binding.noteFAB.setOnClickListener {
        openFAB=false
        val action = InDashboardFragmentDirections.actionInDashboardFragmentToAddNoteFragment(args.dashboardId,args.dashboardName)
        findNavController().navigate(action)
    }


    private fun taskFAB() = binding.TaskFAB.setOnClickListener {
        openFAB=false
        val action = InDashboardFragmentDirections.actionInDashboardFragmentToAddTasksFragment(args.dashboardId,args.dashboardName)
        findNavController().navigate(action)
    }


    private fun recurringTaskFAB() = binding.recurringTaskFAB.setOnClickListener {
        openFAB=false
        val action = InDashboardFragmentDirections.actionInDashboardFragmentToAddRecurringTaskFragment(args.dashboardId,args.dashboardName)
        findNavController().navigate(action)
    }

    private fun showTask(){
        // Tasks RecyclerView
        val taskAdapter = TaskAdapter(args.dashboardId,args.dashboardName)
        binding.taskRV.adapter = taskAdapter
        binding.taskRV.layoutManager = LinearLayoutManager(context)

        binding.shimmerViewContainer.startShimmer()
        binding.shimmerViewContainer.visibility = View.VISIBLE
        viewModelFB.fetchTaskData(args.dashboardId).observe(viewLifecycleOwner,{
            binding.shimmerViewContainer.hideShimmer() // don't work
            binding.shimmerViewContainer.stopShimmer()
            binding.shimmerViewContainer.visibility=View.GONE
            taskAdapter.setData(it)
        })
    }
}