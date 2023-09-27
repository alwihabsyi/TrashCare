package com.upnvjt.trashcare.ui.auth

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.databinding.ActivityAuthBinding
import com.upnvjt.trashcare.ui.auth.viewmodel.AuthViewModel
import com.upnvjt.trashcare.ui.main.MainActivity
import com.upnvjt.trashcare.util.GoogleAuthUiClient
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.setUpTabLayout
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AuthViewModel>()
    private lateinit var auth: FirebaseAuth

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            Identity.getSignInClient(applicationContext)
        )
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            lifecycleScope.launch {
                val signInResult = googleAuthUiClient.signInWithIntent(it.data ?: return@launch)
                signInResult.data?.let {
                    viewModel.googleSignIn(signInResult)
                }
            }
        }else {
            binding.progressBar.hide()
            binding.btnGoogle.text = getString(R.string.masuk_dengan_google)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        setUpTabLayout(
            binding.tabLayout,
            binding.viewPagerAuth,
            AuthViewPagerAdapter(supportFragmentManager, lifecycle)
        )

        binding.btnGoogle.setOnClickListener {
            googleSignIn()
        }

        observer()
    }

    private fun observer() {
        viewModel.google.observe(this) {
            when (it) {
                is State.Loading -> {
                }

                is State.Success -> {
                    binding.progressBar.hide()
                    binding.btnGoogle.text = getString(R.string.masuk_dengan_google)
                    Intent(this, MainActivity::class.java).also { intent ->
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }

                is State.Error -> {
                    binding.progressBar.hide()
                    binding.btnGoogle.text = getString(R.string.masuk_dengan_google)
                    toast(it.data.toString())
                }
            }
        }
    }

    private fun googleSignIn() {
        lifecycleScope.launch {
            binding.btnGoogle.text = ""
            binding.progressBar.show()
            val signInIntentSender = googleAuthUiClient.signIn()
            launcher.launch(
                IntentSenderRequest.Builder(
                    signInIntentSender ?: return@launch
                ).build()
            )
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            Intent(this, MainActivity::class.java).also { intent ->
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}