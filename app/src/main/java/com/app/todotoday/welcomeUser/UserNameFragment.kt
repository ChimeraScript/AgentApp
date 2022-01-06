package com.app.todotoday.welcomeUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.todotoday.databinding.FragmentUserNameBinding
import com.app.todotoday.sharedPreferences.SharedData.Companion.prefs
import com.google.firebase.firestore.FirebaseFirestore

class UserNameFragment : Fragment() {

    private var _binding: FragmentUserNameBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserNameBinding.inflate(inflater,container,false)

        initUI()

        // Inflate the layout for this fragment
        return binding.root
    }


    private fun initUI(){
        binding.sendButton.setOnClickListener{ checkValue() }
    }

    private fun checkValue(){

        if(binding.etName.text.isNotEmpty()){
            prefs.saveName(binding.etName.text.toString())
            saveFirebase()
            goToDetail()
        }else{
            // Empty editText
            showErrorName()
        }
    }

    private fun saveFirebase(){
        if (prefs.getName().isNotEmpty()){
            if (prefs.getEmail().isNotEmpty()&& prefs.getProvider().isNotEmpty()) {
                db.collection("users").document(prefs.getEmail()).set(
                    hashMapOf(
                        "Provider" to prefs.getProvider(),
                    )
                )
            }
        }
    }

    private fun showErrorName(){
        Toast.makeText(requireContext(), "Los campos no pueden estar vacios", Toast.LENGTH_SHORT).show()
    }
    private fun goToDetail() {
        val action = UserNameFragmentDirections.actionUserNameFragmentToWelcomeFragment()
        findNavController().navigate(action)
    }
}