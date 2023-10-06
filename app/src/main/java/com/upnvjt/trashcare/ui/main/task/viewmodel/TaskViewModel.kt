package com.upnvjt.trashcare.ui.main.task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.upnvjt.trashcare.data.tacampaign.TaskData
import com.upnvjt.trashcare.data.tacampaign.TaskStatus
import com.upnvjt.trashcare.util.Constants.TASK
import com.upnvjt.trashcare.util.Constants.USER_COLLECTION
import com.upnvjt.trashcare.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _submitTask = MutableLiveData<State<TaskData>>()
    val submitTask: LiveData<State<TaskData>> = _submitTask

    fun submitTask(taskData: TaskData, photo: ByteArray) {
        _submitTask.value = State.Loading()
        var image = ""
        viewModelScope.launch {
            try {
                async {
                    val id = UUID.randomUUID().toString()
                    launch {
                        val imageStorage = storage.reference.child("task/images/$id")
                        val result = imageStorage.putBytes(photo).await()
                        val downloadUrl = result.storage.downloadUrl.await().toString()
                        image = downloadUrl
                    }
                }.await()
            } catch (e: Exception) {
                _submitTask.value = State.Error(e.message.toString())
            }

            val task = taskData.copy(
                photoUrl = image, taskStatus = TaskStatus.Submitted.taskStatus, userId = auth.uid, dateSubmitted = Date()
            )
            submitToDatabase(task)
        }
    }

    private fun submitToDatabase(task: TaskData) {
        firestore.runBatch {
            firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(TASK)
                .document(task.id).set(task)

            firestore.collection(TASK).document(task.taskId)
                .set(task)
        }.addOnSuccessListener {
            _submitTask.value = State.Success(task)
        }.addOnFailureListener {
            _submitTask.value = State.Error(it.message.toString())
        }
    }
}