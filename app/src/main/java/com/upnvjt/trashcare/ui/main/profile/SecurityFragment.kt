package com.upnvjt.trashcare.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.databinding.FragmentSecurityBinding
import com.upnvjt.trashcare.ui.main.profile.viewmodel.ProfileViewModel
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.hideBottomNavView
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.string
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecurityFragment : Fragment() {

    private var _binding: FragmentSecurityBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideBottomNavView()
        _binding = FragmentSecurityBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActions()
        observer()
    }

    private fun setActions() {
        binding.btnUpdatePassword.setOnClickListener {
            if (!checkFields()) {
                toast("Harap isi semua field \n dan pastikan password berbeda")
                return@setOnClickListener
            }

            viewModel.updatePassword(binding.etPasswordLama.string(), binding.etPassword.string())
        }
    }

    private fun observer() {
        viewModel.updatePassword.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    binding.progressBar.show()
                    binding.btnUpdatePassword.text = ""
                }
                is State.Success -> {
                    binding.progressBar.hide()
                    binding.btnUpdatePassword.text = getString(R.string.update)
                    toast("Password berhasil diubah")
                }
                is State.Error -> {
                    binding.progressBar.hide()
                    binding.btnUpdatePassword.text = getString(R.string.update)
                    toast(it.message.toString())
                }
            }
        }
    }

    private fun checkFields(): Boolean {
        return binding.etPasswordLama.string().isNotEmpty() &&
                binding.etPassword.string().isNotEmpty() &&
                binding.etConfirmPassword.string().isNotEmpty() &&
                binding.etPasswordLama.string() != binding.etPassword.string() &&
                binding.etPassword.string() == binding.etConfirmPassword.string()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}