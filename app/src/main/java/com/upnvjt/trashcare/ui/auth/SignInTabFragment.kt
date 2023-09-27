package com.upnvjt.trashcare.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.databinding.FragmentSigninTabBinding
import com.upnvjt.trashcare.ui.auth.viewmodel.SignInViewModel
import com.upnvjt.trashcare.ui.main.MainActivity
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.Validation
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.setUpForgotPasswordDialog
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.string
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SignInTabFragment : Fragment() {

    private var _binding: FragmentSigninTabBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SignInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSigninTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnLogin.setOnClickListener {
                val email = etEmail.string().trim()
                val password = etPassword.string()

                viewModel.login(email, password)
            }

            tvForgotPassword.setOnClickListener {
                setUpForgotPasswordDialog { email ->
                    viewModel.resetPassword(email)
                }
            }
        }

        observer()
    }

    private fun observer() {
        viewModel.login.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    binding.signInProgressBar.show()
                    binding.btnLogin.text = ""
                }

                is State.Success -> {
                    binding.signInProgressBar.hide()
                    binding.btnLogin.text = getString(R.string.login)
                    Intent(requireContext(), MainActivity::class.java).also { intent ->
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }

                is State.Error -> {
                    binding.signInProgressBar.hide()
                    binding.btnLogin.text = getString(R.string.login)
                    toast(it.message.toString())
                }
            }
        }

        viewModel.resetPassword.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                }
                is State.Success -> {
                    Snackbar.make(requireView(), it.data.toString(), Snackbar.LENGTH_SHORT).show()
                }
                is State.Error -> {
                    Snackbar.make(requireView(), it.message.toString() , Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validation.collect { validation ->
                    if (validation.email is Validation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.etEmail.apply {
                                requestFocus()
                                toast(validation.email.message)
                            }
                        }
                    }

                    if (validation.password is Validation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.etPassword.apply {
                                requestFocus()
                                toast(validation.password.message)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}