package com.upnvjt.trashcare.data.tacampaign

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.databinding.ItemTaskBinding
import com.upnvjt.trashcare.ui.main.task.TaskDetailActivity
import com.upnvjt.trashcare.ui.main.task.TaskDetailActivity.Companion.TASK_ID

class TaskAdapter(var fromHome: Boolean = false) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    inner class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TaskData) {
            binding.apply {
                tvTaskTitle.text = item.id
                taskStatus.text =
                    if (item.taskStatus == TaskStatus.Incomplete.taskStatus) "Task Incomplete" else "Task Complete"

                taskStatus.isChecked = item.taskStatus != TaskStatus.Incomplete.taskStatus

                if (fromHome) {
                    val params = (cardView.layoutParams as ViewGroup.MarginLayoutParams)
                    params.marginStart = 0
                    params.topMargin = 0
                    params.bottomMargin = 5
                }

                if (item.taskStatus == TaskStatus.Incomplete.taskStatus) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        taskStatus.setTextColor(itemView.context.getColor(R.color.red))
                    } else {
                        taskStatus.setTextColor(itemView.context.resources.getColor(R.color.red))
                    }
                }
            }
        }

    }

    private val diffUtil = object : DiffUtil.ItemCallback<TaskData>() {
        override fun areItemsTheSame(oldItem: TaskData, newItem: TaskData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskData, newItem: TaskData): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            ItemTaskBinding.inflate(
                LayoutInflater.from(parent.context), null, false
            )
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = differ.currentList[position]

        holder.bind(item)

        val context = holder.itemView.context
        holder.binding.btnTacampaign.setOnClickListener {
            Intent(context, TaskDetailActivity::class.java).also {
                it.putExtra(TASK_ID, item)
                context.startActivity(it)
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

}