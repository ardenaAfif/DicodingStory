package com.pratama.dicodingstory.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.pratama.dicodingstory.R
import com.pratama.dicodingstory.utils.animateVisibility
import com.google.android.material.snackbar.Snackbar
import com.pratama.dicodingstory.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var registerJob: Job = Job()
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setActions() {
        binding.apply {
            login?.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_registerFragment_to_loginFragment)
            )

            btnRegister.setOnClickListener {
                val name = binding.etFullName.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString()
                setLoading(true)

                lifecycleScope.launchWhenResumed {
                    if (registerJob.isActive) registerJob.cancel()

                    registerJob = launch {
                        viewModel.register(name, email, password).collect { result ->
                            result.onSuccess {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.registration_success),
                                    Toast.LENGTH_SHORT
                                ).show()

                                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                            }

                            result.onFailure {
                                Snackbar.make(
                                    binding.root,
                                    getString(R.string.registration_error_message),
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                setLoading(false)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.apply {
            etEmail.isEnabled = !isLoading
            etPassword.isEnabled = !isLoading
            etFullName.isEnabled = !isLoading
            btnRegister.isEnabled = !isLoading

            if (isLoading) {
                viewLoading.animateVisibility(true)
            } else {
                viewLoading.animateVisibility(false)
            }
        }
    }
}