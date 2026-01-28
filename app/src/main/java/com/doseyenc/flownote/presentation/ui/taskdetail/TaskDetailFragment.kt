package com.doseyenc.flownote.presentation.ui.taskdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.doseyenc.flownote.R
import com.doseyenc.flownote.databinding.FragmentTaskDetailBinding
import com.doseyenc.flownote.domain.model.Task
import com.doseyenc.flownote.presentation.viewstate.ViewState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskDetailFragment : Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupToolbar()
        observeViewState()
        observeEvents()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is TaskDetailEvent.Edit -> {
                            findNavController().navigate(
                                TaskDetailFragmentDirections
                                    .actionTaskDetailFragmentToTaskAddEditFragment(
                                        taskId = event.taskId
                                    )
                            )
                        }

                        is TaskDetailEvent.Deleted -> {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun renderState(state: ViewState<Task>) = with(binding) {
        when (state) {
            is ViewState.Loading -> {
                progressBar.visibility = View.VISIBLE
                contentLayout.visibility = View.GONE
            }

            is ViewState.Success -> {
                progressBar.visibility = View.GONE
                contentLayout.visibility = View.VISIBLE
                displayTask(state.data)
            }

            is ViewState.Error -> {
                progressBar.visibility = View.GONE
                contentLayout.visibility = View.GONE
                Snackbar.make(
                    root,
                    state.message.ifBlank { getString(R.string.error_generic) },
                    Snackbar.LENGTH_LONG
                ).show()
            }

            is ViewState.Empty -> {
                progressBar.visibility = View.GONE
                contentLayout.visibility = View.GONE
            }
        }
    }

    private fun displayTask(task: Task) = with(binding) {
        tvTitle.text = task.title
        tvDescription.text = task.description.ifBlank { getString(R.string.task_no_description) }
        cbCompleted.isChecked = task.isCompleted
        cbCompleted.isEnabled = false // Read-only in detail view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
