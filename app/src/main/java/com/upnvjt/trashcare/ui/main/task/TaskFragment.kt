package com.upnvjt.trashcare.ui.main.task

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjt.trashcare.data.tacampaign.TaskAdapter
import com.upnvjt.trashcare.data.tacampaign.TaskData
import com.upnvjt.trashcare.databinding.FragmentTaskBinding
import com.upnvjt.trashcare.ui.main.home.viewmodel.HomeViewModel
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskFragment : Fragment() {

    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()
    private val taskAdapter by lazy { TaskAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRv()
        observer()
    }

    private fun setUpRv() {
        binding.rvDailyTask.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun observer() {
        viewModel.task.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    binding.progressBar.show()
                }
                is State.Success -> {
                    binding.progressBar.hide()
                    setUpRvData(it.data!!)
                }
                is State.Error -> {
                    binding.progressBar.hide()
                    toast(it.message.toString())
                }
            }
        }
    }

    private fun setUpRvData(data: List<TaskData>) {
        taskAdapter.differ.submitList(data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}