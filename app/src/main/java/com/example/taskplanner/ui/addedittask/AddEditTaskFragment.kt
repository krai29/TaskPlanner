package com.example.taskplanner.ui.addedittask

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.taskplanner.R
import com.example.taskplanner.databinding.FragmentAddEditTaskBinding
import com.example.taskplanner.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {

    private val viewModel : AddEditTaskViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            etTaskName.setText(viewModel.taskName)
            cbTaskImportant.isChecked = viewModel.taskImportance
            cbTaskImportant.jumpDrawablesToCurrentState()
            tvTaskCreationTime.isVisible = viewModel.task!=null
            tvTaskCreationTime.text = "Created: ${viewModel.task?.createdDateFormatted}"

            etTaskName.addTextChangedListener {
                viewModel.taskName = it.toString()
            }

            cbTaskImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskImportance = isChecked
            }

            fabSaveTask.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect { event->
                when(event){
                    is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                        binding.etTaskName.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(),event.message,Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }
}