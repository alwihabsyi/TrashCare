package com.upnvjt.trashcare.ui.main.commerce

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.data.tacommerce.Orders
import com.upnvjt.trashcare.databinding.FragmentPaymentBinding
import com.upnvjt.trashcare.ui.main.commerce.viewmodel.PaymentViewModel
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.createTemporaryFile
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.pickPhoto
import com.upnvjt.trashcare.util.reduceFileImage
import com.upnvjt.trashcare.util.show
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

        setActions()
        observer()
    }

    private fun setActions() {
        binding.apply {
            ivBuktiPembayaran.setOnClickListener {
                pickPhoto(
                    onCameraClick = { startCamera() },
                    onGalleryClick = { startGallery() }
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

    @SuppressLint("QueryPermissionsNeeded")
    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity?.let { activity ->
            intent.resolveActivity(activity.packageManager)
            createTemporaryFile(requireActivity()).also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireActivity(),
                    "com.upnvjt.trashcare.ui.main.commerce",
                    it
                )
                photoPath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                launchCamera.launch(intent)
            }
        }
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

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launchGallery.launch(chooser)
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