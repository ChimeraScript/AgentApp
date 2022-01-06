package com.app.todotoday.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.todotoday.databinding.FragmentAccountBinding
import com.app.todotoday.sharedPreferences.SharedData
import com.google.firebase.auth.FirebaseAuth


class AccountFragment : Fragment() {

    private var _binding : FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<AccountFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater,container,false)

        setUp()

        return binding.root
    }

    private fun setUp(){

        if (args.value == "signUp"){
            binding.signIn.visibility = View.INVISIBLE
            binding.signUp.visibility = View.VISIBLE
        }
        if (args.value == "signIn"){
            binding.signUp.visibility = View.INVISIBLE
            binding.signIn.visibility = View.VISIBLE
        }


        binding.signUp.setOnClickListener {
            if (binding.mailET.text.isNotEmpty() && binding.PasswordET.text.isNotEmpty()) {
                val passLength = binding.PasswordET.text.toString().length
                if (passLength>5) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        binding.mailET.text.toString(),
                        binding.PasswordET.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(it.result?.user?.email ?: "")
                        } else {
                            showAlert("An error has happened")
                        }
                    }
                }else{
                    showAlert("Password need min 6 chars")
                }

            }
        }

        binding.signIn.setOnClickListener {
            if (binding.mailET.text.isNotEmpty() && binding.PasswordET.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.mailET.text.toString(),
                    binding.PasswordET.text.toString()).addOnCompleteListener {

                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "")
                    } else {
                        showAlert("An error has happened")
                    }
                }
            }
        }
    }

    private fun showAlert(Text:String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage(Text)
        builder.setPositiveButton("Accept",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String){

        // Save data
        SharedData.prefs.saveEmail(email)
        SharedData.prefs.saveProviderName("BASIC")

        val action = AccountFragmentDirections.actionAccountFragmentToUserNameFragment()
        findNavController().navigate(action)
    }

}