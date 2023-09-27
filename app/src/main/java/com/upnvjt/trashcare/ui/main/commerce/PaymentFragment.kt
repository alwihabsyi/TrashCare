package com.upnvjt.trashcare.ui.main.commerce

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
import com.upnvjt.trashcare.data.tacommerce.Orders
import com.upnvjt.trashcare.databinding.FragmentPaymentBinding
import com.upnvjt.trashcare.ui.main.commerce.viewmodel.PaymentViewModel
import com.upnvjt.trashcare.util.Constants.storagePermission
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.checkPermissionStorage
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.permissionLaunch
import com.upnvjt.trashcare.util.pickPhoto
import com.upnvjt.trashcare.util.reduceFileImage
import com.upnvjt.trashcare.util.setUpCamera
import com.upnvjt.trashcare.util.setUpGallery
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.showPermissionSettingsAlert
import com.upnvjt.trashcare.util.toast
import com.upnvjt.trashcare.util.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    private var photoPath: String? = null
    private var imageFile: File? = null
    private val args by navArgs<PaymentFragmentArgs>()
    private var order: Orders? = null
    private val viewModel by viewModels<PaymentViewModel>()
    private var permissionGiven: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        order = args.order
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionSet()
        setActions()
        observer()
    }

    private fun permissionSet() {
        permissionGiven = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true
        } else {
            if (!checkPermissionStorage(requireContext())) permissionLaunch(storagePermission)
            else true
        }
    }

    private fun setActions() {
        binding.apply {
            ivBuktiPembayaran.setOnClickListener {
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

            btnKonfirmasi.setOnClickListener {
                if (imageFile == null || order == null) {
                    toast("Harap mengisi bukti pembayaran")
                    return@setOnClickListener
                }

                val setFile = reduceFileImage(imageFile as File)
                val photoUri = setFile.readBytes()
                viewModel.submitOrder(order!!, photoUri)
            }
        }
    }

    private fun observer() {
        viewModel.submitOrder.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    binding.btnKonfirmasi.text = ""
                    binding.progressBar.show()
                }

                is State.Success -> {
                    binding.progressBar.hide()
                    findNavController().navigate(PaymentFragmentDirections.actionPaymentFragmentToOrderProcessedFragment())
                }

                is State.Error -> {
                    binding.btnKonfirmasi.text = getString(R.string.konfirmasi)
                    binding.progressBar.hide()
                }
            }
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
                binding.ivBuktiPembayaran.setImageBitmap(BitmapFactory.decodeFile(file.path))
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
                binding.tvPilihBuktiPembayaran.hide()
                binding.ivBuktiPembayaran.setImageURI(uri)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}