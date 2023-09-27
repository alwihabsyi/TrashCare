package com.upnvjt.trashcare.ui.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.tacampaign.TaskData
import com.upnvjt.trashcare.data.tacampaign.TaskStatus
import com.upnvjt.trashcare.data.tacampaign.dailyTask1
import com.upnvjt.trashcare.data.tacampaign.dailyTask2
import com.upnvjt.trashcare.data.tacampaign.dailyTask3
import com.upnvjt.trashcare.data.user.User
import com.upnvjt.trashcare.util.Constants
import com.upnvjt.trashcare.util.Constants.TASK
import com.upnvjt.trashcare.util.SignInResult
import com.upnvjt.trashcare.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _google = MutableLiveData<State<String>>()
    val google: LiveData<State<String>> = _google

    fun googleSignIn(result: SignInResult) {
        _google.value = State.Loading()
        firestore.collection(Constants.USER_COLLECTION).document(result.id!!).get()
            .addOnCompleteListener {
                if (it.result.exists()) {
                    _google.value = State.Success("Berhasil masuk")
                } else {
                    addNewUser(result.id, result.data!!)
                }
            }
    }

    private fun addNewUser(userId: String, user: User) {
        firestore.collection(Constants.USER_COLLECTION).document(userId).set(user)
            .addOnSuccessListener {
                setUserTaskData(userId)
            }.addOnFailureListener {
                _google.value = State.Error(it.message.toString())
            }
    }

    private fun setUserTaskData(userId: String) {
        firestore.runBatch {
            for (i in 1..3) {
                val ts = if (i == 1) dailyTask1 else if (i == 2) dailyTask2 else dailyTask3
                val title = "Daily Impact $i"
                val task = TaskData(
                    TaskStatus.Incomplete.taskStatus, title, ts, i
                )
                firestore.collection(Constants.USER_COLLECTION).document(userId).collection(TASK)
                    .document(task.id).set(task)
            }
        }.addOnSuccessListener {
            _google.value = State.Success("Berhasil masuk")
        }.addOnFailureListener {
            _google.value = State.Error(it.message.toString())
        }
    }

}