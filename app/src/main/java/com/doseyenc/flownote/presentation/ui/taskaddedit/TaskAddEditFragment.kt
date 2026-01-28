package com.doseyenc.flownote.presentation.ui.taskaddedit

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
import com.doseyenc.flownote.databinding.FragmentTaskAddEditBinding
import com.doseyenc.flownote.presentation.viewstate.ViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskAddEditFragment : Fragment() {

    private var _binding: FragmentTaskAddEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskAddEditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupToolbar()
        setupTextListeners()
        observeFields()
        observeTitleError()
        observeViewState()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        if (viewModel.isEditMode) {
            binding.toolbar.title = getString(R.string.edit_task)
            binding.btnSave.text = getString(R.string.save)
        } else {
            binding.toolbar.title = getString(R.string.add_task)
            binding.btnSave.text = getString(R.string.add_task)
        }
    }

    private fun setupTextListeners() {
        binding.etTitle.addTextChangedListener { text ->
            viewModel.onTitleChanged(text?.toString().orEmpty())
        }

        binding.etDescription.addTextChangedListener { text ->
            viewModel.onDescriptionChanged(text?.toString().orEmpty())
        }
    }

    private fun observeFields() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.title.collect { title ->
                        with(binding) {
                            if (etTitle.text?.toString() != title) {
                                etTitle.setText(title)
                                etTitle.setSelection(title.length)
                            }
                        }
                    }
                }
                launch {
                    viewModel.description.collect { description ->
                        with(binding) {
                            if (etDescription.text?.toString() != description) {
                                etDescription.setText(description)
                                etDescription.setSelection(description.length)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeTitleError() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.titleError.collect { hasError ->
                        binding.titleInputLayout.error = if (hasError) {
                            getString(R.string.error_title_blank)
                        } else {
                            null
                        }
                    }
                }
            }
        }
    }

    private fun observeViewState() {
        var wasSaving = false

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    combine(
                        viewModel.isSaving,
                        viewModel.viewState
                    ) { isSaving, viewState ->
                        if (wasSaving && !isSaving && viewState is ViewState.Success) {
                            findNavController().popBackStack()
                        }
                        wasSaving = isSaving
                    }.collect { }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

