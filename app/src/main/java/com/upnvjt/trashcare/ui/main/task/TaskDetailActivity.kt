package com.upnvjt.trashcare.ui.main.task

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.data.tacampaign.TaskData
import com.upnvjt.trashcare.data.tacampaign.TaskStatus
import com.upnvjt.trashcare.databinding.ActivityTaskDetailBinding
import com.upnvjt.trashcare.ui.main.task.viewmodel.TaskViewModel
import com.upnvjt.trashcare.util.Constants.storagePermission
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.hide
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

@Suppress("DEPRECATION")
@AndroidEntryPoint
class TaskDetailActivity : AppCompatActivity() {

    private var _binding: ActivityTaskDetailBinding? = null
    private val binding get() = _binding!!
    private var taskData: TaskData? = null
    private var permissionGiven: Boolean? = null
    private var photoPath: String? = null
    private var imageFile: File? = null
    private val viewModel by viewModels<TaskViewModel>()

    private val permission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val isGranted = permissions[Manifest.permission.CAMERA] ?: false
            val readData = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false

            if (isGranted && readData) {
                permissionGiven = true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionSet()
        setPage()
        setActions()
        observer()
    }

    private fun permissionSet() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionGiven = true
        } else {
            permission.launch(storagePermission)
        }
    }

    @SuppressLint("InflateParams")
    private fun setPage() {
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TASK_ID, TaskData::class.java)
        } else {
            intent.getParcelableExtra(TASK_ID)
        }

        data?.let {
            when (it.taskStatus) {
                TaskStatus.Incomplete.taskStatus -> {
                    taskData = it
                    binding.tvDetailTask.text = it.id

                    for (i in it.tugas) {
                        val inflater =
                            LayoutInflater.from(this).inflate(R.layout.item_task_detail, null)
                        binding.taskLayout.addView(inflater, binding.taskLayout.childCount)
                    }

                    val count = binding.taskLayout.childCount
                    for (c in 0 until count) {
                        val v = binding.taskLayout.getChildAt(c)
                        val tvTugas = v.findViewById<TextView>(R.id.tv_tugas)
                        tvTugas.text = it.tugas[c]
                    }
                }
                TaskStatus.Submitted.taskStatus -> {
                    binding.updateTaskContainer.hide()
                    binding.btnSubmitTask.hide()
                    binding.infoWaitingContainer.show()
                }
                else -> {
                    binding.updateTaskContainer.hide()
                    binding.btnSubmitTask.hide()
                    binding.cvInfoTaCoins.show()
                    binding.infoFinishContainer.show()
                }
            }
        }
    }

    private fun observer() {
        viewModel.submitTask.observe(this) {
            when (it) {
                is State.Loading -> {
                    binding.btnSubmitTask.text = ""
                    binding.progressBar.show()
                }
                is State.Success -> {
                    binding.progressBar.hide()
                    binding.updateTaskContainer.hide()
                    binding.btnSubmitTask.hide()
                    binding.infoWaitingContainer.show()
                    binding.cvInfoTaCoins.show()
                }
                is State.Error -> {
                    binding.progressBar.hide()
                    binding.btnSubmitTask.text = getString(R.string.selesaikan_misi)
                    toast(it.message.toString())
                }
            }
        }
    }

    private fun setActions() {
        binding.apply {
            cvUploadTask.setOnClickListener {
                pickPhoto(
                    onCameraClick = {
                        permissionGiven?.let {
                            if (it) {
                                startCamera()
                            } else {
                                showPermissionSettingsAlert(this@TaskDetailActivity)
                            }
                        }
                    },

                    onGalleryClick = {
                        val chooser = setUpGallery()
                        launchGallery.launch(chooser)
                    }
                )
            }

            cbCompleted.setOnClickListener {
                btnSubmitTask.isEnabled = cbCompleted.isChecked
            }

            btnSubmitTask.setOnClickListener {
                if (imageFile == null) {
                    toast("Foto bukti tidak boleh kosong")
                    return@setOnClickListener
                }

                taskData?.let {
                    val setFile = reduceFileImage(imageFile as File)
                    val photoUri = setFile.readBytes()

                    viewModel.submitTask(it, photoUri)
                }
            }

            btnBack.setOnClickListener {
                finish()
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
        if (it.resultCode == RESULT_OK) {
            val myFile = File(photoPath!!)

            myFile.let { file ->
                imageFile = file
                binding.ivBukti.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this)
                imageFile = myFile
                binding.ivBukti.setImageURI(uri)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TASK_ID = "task_id"
    }
}