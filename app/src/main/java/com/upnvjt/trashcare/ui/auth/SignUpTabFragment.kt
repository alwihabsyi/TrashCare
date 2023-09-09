package com.upnvjt.trashcare.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.upnvjt.trashcare.data.User
import com.upnvjt.trashcare.databinding.FragmentSignupTabBinding
import com.upnvjt.trashcare.ui.auth.viewmodel.SignUpViewModel
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.Validation
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.string
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val TAG = "SignUpTabFragment"

@AndroidEntryPoint
class SignUpTabFragment : Fragment() {

    private var _binding: FragmentSignupTabBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SignUpViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignupTabBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnSignUp.setOnClickListener {
                if (validateFields()) {
                    val user = User(
                        etFirstname.string().trim(),
                        etLastname.string().trim(),
                        etUsername.string().trim(),
                        etEmail.string().trim()
                    )
                    val password = etPassword.string()
                    val passwordConfirmation = etConfirmPassword.string()
                    viewModel.signUpWithEmail(user, password, passwordConfirmation)
                } else {
                    toast("Harap isi semua field")
                }
            }
        }

        observer()
    }

    private fun observer() {
        viewModel.register.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    binding.btnSignUp.requestFocus()
                    binding.signUpProgressBar.show()
                    binding.tvBtnNext.hide()
                }
                is State.Success -> {
                    binding.signUpProgressBar.hide()
                    binding.tvBtnNext.show()
                    with(binding){
                        etEmail.text?.clear()
                        etUsername.text?.clear()
                        etPassword.text?.clear()
                        etConfirmPassword.text?.clear()
                    }

                    toast("Berhasil mendaftar, silahkan login")
                }
                is State.Error -> {
                    binding.signUpProgressBar.hide()
                    binding.tvBtnNext.show()
                    toast("Terjadi kesalahan")
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validation.collect { validation ->
                    if (validation.email is Validation.Failed){
                        withContext(Dispatchers.Main){
                            binding.etEmail.apply {
                                requestFocus()
                                toast(validation.email.message)
                            }
                        }
                    }

                    if (validation.password is Validation.Failed){
                        withContext(Dispatchers.Main){
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

    private fun validateFields(): Boolean {
        with(binding) {
            return etPassword.string().isNotEmpty() && etConfirmPassword.string().isNotEmpty()
                    && etEmail.string().isNotEmpty() && etUsername.string().isNotEmpty()
                    && etFirstname.string().isNotEmpty() && etLastname.string().isNotEmpty()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}