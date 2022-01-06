package com.app.todotoday.welcomeUser

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.todotoday.databinding.FragmentWelcomeBinding
import com.app.todotoday.sharedPreferences.SharedData.Companion.prefs
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater,container,false)

        iniUI()
        getToken()
        startCount()
        

        // Inflate the layout for this fragment
        return binding.root
    }


    private fun getToken(){
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)
            ?.addOnCompleteListener {
                if (it.isSuccessful){
                    db.collection("users").document(prefs.getEmail()).update(
                        "UserToken" , it.result?.token
                    )
                }
            }
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            db.collection("users").document(prefs.getEmail()).update(
                "UserTokenMsg" , token
            )
        })
    }

    @SuppressLint("SetTextI18n")
    private fun iniUI(){
        val userName = prefs.getName()
        binding.helloText.text = "Welcome $userName"
    }

    private fun startCount(){
        object: CountDownTimer(1200,1200){
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                val action = WelcomeFragmentDirections.actionWelcomeFragmentToTodayFragment()
                findNavController().navigate(action)
            }
        }.start()
    }
}