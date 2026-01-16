package com.example.tugasakhir_145.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tugasakhir_145.R
import com.example.tugasakhir_145.data.local.AppDatabase
import com.example.tugasakhir_145.data.local.User
import com.example.tugasakhir_145.databinding.FragmentLoginBinding
import com.example.tugasakhir_145.ui.viewmodel.AuthState
import com.example.tugasakhir_145.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.security.MessageDigest

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.LoginSuccess -> {
                        val user = state.user
                        saveUserSession(user.id)
                        Toast.makeText(requireContext(), "Login Berhasil", Toast.LENGTH_SHORT).show()
                        if (user.role.equals("admin", ignoreCase = true)) {
                            findNavController().navigate(R.id.action_loginFragment_to_adminDashboardFragment)
                        } else {
                            findNavController().navigate(R.id.action_loginFragment_to_cashierDashboardFragment)
                        }
                        viewModel.resetState()
                    }
                    is AuthState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                    }
                    else -> {}
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Harap isi semua bidang", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.login(username, password)
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun saveUserSession(userId: Int) {
        val sharedPref = requireActivity().getSharedPreferences("USER_SESSION", android.content.Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("USER_ID", userId)
            apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
