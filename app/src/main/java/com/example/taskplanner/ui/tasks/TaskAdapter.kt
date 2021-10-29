package com.example.taskplanner.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskplanner.data.Task
import com.example.taskplanner.databinding.ItemTaskBinding

class TaskAdapter : ListAdapter<Task,TaskAdapter.TasksViewHolder>(DiffCallbak()) {

    class TasksViewHolder(
        private val binding : ItemTaskBinding) : RecyclerView.ViewHolder(binding.root){

            fun bind(task: Task){
                binding.apply {
                    cbTask.isChecked = task.completed
                    tvTaskTitle.text = task.name
                    tvTaskTitle.paint.isStrikeThruText = task.completed
                    ivTaskImportance.isVisible = task.importance
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),parent,false)
        return TasksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class DiffCallbak : DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem == newItem


    }
}