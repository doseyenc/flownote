package com.doseyenc.flownote.presentation.ui.tasklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.doseyenc.flownote.R
import com.doseyenc.flownote.databinding.FragmentTaskListBinding
import com.doseyenc.flownote.domain.model.FilterType
import com.doseyenc.flownote.domain.model.Task
import com.doseyenc.flownote.presentation.viewstate.ViewState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels()

    private lateinit var taskAdapter: TaskListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBinding()
        setupRecyclerView()
        setupSearch()
        setupChips()
        setupFab()
        observeViewState()
    }

    private fun setupBinding() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskListAdapter(viewModel)

        binding.rvTasks.adapter = taskAdapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { editable ->
            viewModel.onSearchQueryChanged(editable?.toString().orEmpty())
        }
    }

    private fun setupChips() {
        binding.chipAll.setOnClickListener {
            viewModel.onFilterChanged(FilterType.ALL)
        }
        binding.chipActive.setOnClickListener {
            viewModel.onFilterChanged(FilterType.PENDING)
        }
        binding.chipCompleted.setOnClickListener {
            viewModel.onFilterChanged(FilterType.COMPLETED)
        }
    }

    private fun setupFab() {
        binding.fabAddTask.setOnClickListener {
            findNavController().navigate(
                R.id.action_taskListFragment_to_taskAddEditFragment
            )
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

    private fun renderState(state: ViewState<List<Task>>) = with(binding) {
        when (state) {
            is ViewState.Loading -> {
                loadingAnimation.visibility = View.VISIBLE
                emptyAnimation.visibility = View.GONE
                tvEmptyTitle.visibility = View.GONE
                tvEmptySubtitle.visibility = View.GONE
                rvTasks.visibility = View.GONE
            }

            is ViewState.Success -> {
                loadingAnimation.visibility = View.GONE
                emptyAnimation.visibility = View.GONE
                tvEmptyTitle.visibility = View.GONE
                tvEmptySubtitle.visibility = View.GONE
                rvTasks.visibility = View.VISIBLE
                taskAdapter.submitList(state.data)
            }

            is ViewState.Empty -> {
                loadingAnimation.visibility = View.GONE
                emptyAnimation.visibility = View.VISIBLE
                tvEmptyTitle.visibility = View.VISIBLE
                tvEmptySubtitle.visibility = View.VISIBLE
                rvTasks.visibility = View.GONE
                taskAdapter.submitList(emptyList())
            }

            is ViewState.Error -> {
                loadingAnimation.visibility = View.GONE
                emptyAnimation.visibility = View.VISIBLE
                tvEmptyTitle.visibility = View.VISIBLE
                tvEmptySubtitle.visibility = View.VISIBLE
                rvTasks.visibility = View.GONE
                taskAdapter.submitList(emptyList())

                Snackbar.make(
                    root,
                    state.message.ifBlank { getString(R.string.error_generic) },
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

