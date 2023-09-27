package com.upnvjt.trashcare.ui.main.profile

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.data.user.User
import com.upnvjt.trashcare.databinding.FragmentEditProfileBinding
import com.upnvjt.trashcare.ui.main.profile.viewmodel.ProfileViewModel
import com.upnvjt.trashcare.util.Constants
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.glide
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.hideBottomNavView
import com.upnvjt.trashcare.util.permissionLaunch
import com.upnvjt.trashcare.util.pickPhoto
import com.upnvjt.trashcare.util.reduceFileImage
import com.upnvjt.trashcare.util.setUpCamera
import com.upnvjt.trashcare.util.setUpGallery
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.showPermissionSettingsAlert
import com.upnvjt.trashcare.util.string
import com.upnvjt.trashcare.util.toast
import com.upnvjt.trashcare.util.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<EditProfileFragmentArgs>()
    private var userData: User? = null
    private var permissionGiven: Boolean? = null
    private var photoPath: String? = null
    private var imageFile: File? = null
    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userData = args.user
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideBottomNavView()
        _binding = FragmentEditProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionSet()
        setupPage()
        setActions()
        observer()
    }

    private fun permissionSet() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionGiven = true
        }else {
            permissionLaunch(
                Constants.storagePermission,
                permissionGranted = {
                    permissionGiven = it
                }
            )
        }
    }

    private fun setupPage() {
        userData?.let {
            binding.apply {
                etFirstName.setText(it.firstname)
                etLastName.setText(it.lastname)
                etEmail.setText(it.email)
                etNomorTelepon.setText(it.phoneNo)
                if(it.imagePath != ""){
                    ivPhotoProfile.glide(it.imagePath)
                }
            }
        }
    }

    private fun setActions() {
        binding.apply {
            btnSimpanProfil.setOnClickListener {
                if (validateFields()) {
                    userData?.let {
                        val user = it.copy(
                            firstname = etFirstName.string().trim(),
                            lastname = etLastName.string().trim(),
                            phoneNo = etNomorTelepon.string().trim(),
                            email = etEmail.string().trim()
                        )

                        var photoUri: ByteArray? = null
                        if (imageFile != null){
                            val setFile = reduceFileImage(imageFile as File)
                            photoUri = setFile.readBytes()
                        }

                        viewModel.updateProfile(user, photoUri)
                    }
                } else {
                    toast("Harap isi semua field")
                }
            }

            ivPhotoProfile.setOnClickListener {
                takePhoto()
            }

            tvEditPhotoProfile.setOnClickListener {
                takePhoto()
            }

            btnAlamat.setOnClickListener {
                val intent = Intent(requireContext(), AddressActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun observer() {
        viewModel.updateProfile.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    binding.progressBar.show()
                    binding.btnSimpanProfil.text = ""
                }
                is State.Success -> {
                    binding.progressBar.hide()
                    toast("Berhasil update profil")
                    findNavController().navigateUp()
                }
                is State.Error -> {
                    binding.progressBar.hide()
                    binding.btnSimpanProfil.text = getString(R.string.simpan)
                    toast(it.message.toString())
                }
            }
        }
    }

    private fun takePhoto() {
        pickPhoto(
            onCameraClick = {
                permissionGiven?.let {
                    if (it) {
                        startCamera()
                    } else {
                        showPermissionSettingsAlert(requireContext())
                    }
                }
            },

            onGalleryClick = {
                val chooser = setUpGallery()
                launchGallery.launch(chooser)
            }
        )
    }

    private fun validateFields(): Boolean {
        with(binding) {
            return etEmail.string().isNotEmpty() && etNomorTelepon.string().isNotEmpty()
                    && etFirstName.string().isNotEmpty() && etLastName.string().isNotEmpty()
        }
    }



    private fun startCamera() {
        val intent = setUpCamera()
        photoPath = intent.path
        launchCamera.launch(intent.intent)
    }

    private val launchCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val myFile = File(photoPath!!)

            myFile.let { file ->
                imageFile = file
                binding.ivPhotoProfile.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, requireContext())
                imageFile = myFile
                binding.ivPhotoProfile.setImageURI(uri)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val USER_PROFILE = "user"
    }
}