package com.app.todotoday.menu.infoFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.todotoday.databinding.FragmentInfoBinding
import com.app.todotoday.sharedPreferences.SharedData.Companion.prefs
import com.google.firebase.auth.FirebaseAuth

class InfoFragment : Fragment() {

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater,container,false)

        val email = prefs.getEmail()
        val name = prefs.getName()
        val provider = prefs.getProvider()
        setup(email,name,provider)

        return binding.root
    }

    private fun setup(email:String,name:String,provider:String){
        binding.EmailTV.text = email
        binding.ProviderTV.text = provider
        binding.userNameET.setText(name)

        binding.saveButton.setOnClickListener {

            val username =binding.userNameET.text.toString()
            prefs.saveName(username)

            val action = InfoFragmentDirections.actionInfoFragmentToTodayFragment()
            findNavController().navigate(action)
        }

        binding.logOutButton.setOnClickListener {

            prefs.clearAll()

            FirebaseAuth.getInstance().signOut()

            val action = InfoFragmentDirections.actionInfoFragmentToAuthFragment()
            findNavController().navigate(action)
        }

        binding.todayBtn.setOnClickListener {
            val action = InfoFragmentDirections.actionInfoFragmentToTodayFragment()
            findNavController().navigate(action)
        }

        binding.dashboardBtn.setOnClickListener {
            val action = InfoFragmentDirections.actionInfoFragmentToDashboardFragment()
            findNavController().navigate(action)
        }
    }
}