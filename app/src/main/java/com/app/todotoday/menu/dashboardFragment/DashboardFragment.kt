package com.app.todotoday.menu.dashboardFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.todotoday.databinding.FragmentDashboardBinding
import com.app.todotoday.firestoreDB.DashboardsFS
import com.app.todotoday.firestoreDB.FireStoreViewModel


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModelFB by lazy { ViewModelProvider(this).get(FireStoreViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater,container,false)


        setUp()


        // Inflate the layout for this fragment
        return binding.root
    }


    private fun setUp(){

        // RecyclerView

        val adapter = DashboardAdapter()
        binding.dashboardRecyclerView.adapter = adapter
        binding.dashboardRecyclerView.layoutManager = LinearLayoutManager(context)



        viewModelFB.fetchDashboardData().observe(viewLifecycleOwner,{
            adapter.setDataFB(it as ArrayList<DashboardsFS>)
        })


        binding.addFAB.setOnClickListener {
            val action = DashboardFragmentDirections.actionDashboardFragmentToAddDashboard()
            findNavController().navigate(action)
        }


        binding.todayBtn.setOnClickListener {
            val action = DashboardFragmentDirections.actionDashboardFragmentToTodayFragment()
            findNavController().navigate(action)
        }

        binding.InfoBtn.setOnClickListener {
            val action = DashboardFragmentDirections.actionDashboardFragmentToInfoFragment()
            findNavController().navigate(action)
        }
    }
}