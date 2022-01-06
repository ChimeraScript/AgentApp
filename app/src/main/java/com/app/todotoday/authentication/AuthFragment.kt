package com.app.todotoday.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.todotoday.R
import com.app.todotoday.databinding.FragmentAuthBinding
import com.app.todotoday.sharedPreferences.SharedData.Companion.prefs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


@Suppress("DEPRECATION")
class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private val googleSignIn = 100


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater,container,false)

        //Setup
        setup()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setup() {

        binding.createAccount.setOnClickListener {
            val action = AuthFragmentDirections.actionAuthFragmentToAccountFragment("signUp")
            findNavController().navigate(action)
        }

        binding.Login.setOnClickListener {
            val action = AuthFragmentDirections.actionAuthFragmentToAccountFragment("signIn")
            findNavController().navigate(action)
        }

        binding.GoogleButton.setOnClickListener {

            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient: GoogleSignInClient = GoogleSignIn.getClient(requireContext(),googleConf)
            googleClient.signOut()


            startActivityForResult(googleClient.signInIntent,googleSignIn)

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
        prefs.saveEmail(email)
        prefs.saveProviderName("GOOGLE")

        val action = AuthFragmentDirections.actionAuthFragmentToUserNameFragment()
        findNavController().navigate(action)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == googleSignIn){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {

                val account = task.getResult(ApiException::class.java)

                if (account!=null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken,null)


                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {

                        if (it.isSuccessful) {
                            showHome(account.email?: "")
                        } else {
                            showAlert("An error has happened")
                        }
                    }
                }
            }catch (e: ApiException){
                showAlert("Error at login with Google")
            }
        }
    }
}