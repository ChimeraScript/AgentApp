package com.app.todotoday.splashScreen

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.todotoday.databinding.FragmentSplashScreenBinding
import com.app.todotoday.sharedPreferences.SharedData.Companion.prefs


class SplashScreen : Fragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashScreenBinding.inflate(inflater,container,false)


        startCount()


        // Inflate the layout for this fragment
        return binding.root
    }


    private fun startCount(){
        object: CountDownTimer(2300,1000){

            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {

                if (prefs.getName().isNotEmpty()){
                    val action = SplashScreenDirections.actionSplashScreenToWelcomeFragment()
                    findNavController().navigate(action)
                }else {
                    val action = SplashScreenDirections.actionSplashScreenToAuthFragment()
                    findNavController().navigate(action)
                }
            }
        }.start()
    }
}