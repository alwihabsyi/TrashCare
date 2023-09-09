package com.upnvjt.trashcare.ui.main.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.Identity
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.databinding.FragmentProfileBinding
import com.upnvjt.trashcare.ui.auth.AuthActivity
import com.upnvjt.trashcare.util.GoogleAuthUiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ProfileViewModel>()

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            requireActivity().applicationContext,
            Identity.getSignInClient(requireActivity().applicationContext)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.linearLogOut.setOnClickListener {
            lifecycleScope.launch {
                googleAuthUiClient.signOut()
            }
            viewModel.logout()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

    }
}