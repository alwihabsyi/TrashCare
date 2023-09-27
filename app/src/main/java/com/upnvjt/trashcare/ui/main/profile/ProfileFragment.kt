package com.upnvjt.trashcare.ui.main.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.Identity
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.data.user.User
import com.upnvjt.trashcare.databinding.FragmentProfileBinding
import com.upnvjt.trashcare.ui.auth.AuthActivity
import com.upnvjt.trashcare.ui.main.home.viewmodel.HomeViewModel
import com.upnvjt.trashcare.ui.main.profile.EditProfileFragment.Companion.USER_PROFILE
import com.upnvjt.trashcare.ui.main.profile.commercehistory.CommerceHistoryActivity
import com.upnvjt.trashcare.util.GoogleAuthUiClient
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.glide
import com.upnvjt.trashcare.util.showBottomNavView
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()
    private var userData: User? = null

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
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

        setActions()
        observer()
    }

    private fun setActions() {
        binding.apply {
            btnLogout.setOnClickListener {
                lifecycleScope.launch {
                    googleAuthUiClient.signOut()
                }
                viewModel.logout()
                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

            btnCommerceHistory.setOnClickListener {
                val intent = Intent(requireContext(), CommerceHistoryActivity::class.java)
                startActivity(intent)
            }

            btnBookmark.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_bookmarkFragment)
            }

            btnSecurity.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_securityFragment)
            }

            tvEditProfile.setOnClickListener {
                userData?.let {
                    val b = Bundle().apply { putParcelable(USER_PROFILE, it) }
                    findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment, b)
                }
            }
        }
    }

    private fun observer() {
        viewModel.user.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {

                }
                is State.Success -> {
                    userData = it.data
                    userData?.let { data ->
                        val nama = if (data.lastname == null) data.firstname else "${data.firstname} ${data.lastname}"
                        binding.tvName.text = nama
                        if (data.imagePath != "") {
                            binding.ivProfil.glide(data.imagePath)
                        }
                    }
                }
                is State.Error -> {
                    toast(it.message.toString())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}