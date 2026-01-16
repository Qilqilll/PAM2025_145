package com.example.tugasakhir_145.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tugasakhir_145.R
import com.example.tugasakhir_145.data.local.AppDatabase
import com.example.tugasakhir_145.data.local.User
import com.example.tugasakhir_145.databinding.FragmentRegisterBinding
import com.example.tugasakhir_145.ui.viewmodel.AuthState
import com.example.tugasakhir_145.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.security.MessageDigest

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup Role Spinner
        // SRS says "admin" or "kasir".
        val roles = arrayOf("kasir", "admin") 
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles)
        binding.spinnerRole.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.RegisterSuccess -> {
                        Toast.makeText(requireContext(), "Registrasi Berhasil", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
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

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            val role = binding.spinnerRole.selectedItem.toString()

            if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Harap isi semua bidang", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if trying to register as admin
            if (role == "admin") {
                lifecycleScope.launch {
                    val db = AppDatabase.getDatabase(requireContext())
                    val adminCount = db.userDao().getAdminCount()
                    
                    if (adminCount > 0) {
                        // Admin already exists
                        Toast.makeText(requireContext(), "Admin sudah ada", Toast.LENGTH_LONG).show()
                        return@launch
                    }
                    
                    // Show confirmation dialog for admin registration
                    android.app.AlertDialog.Builder(requireContext())
                        .setTitle("Konfirmasi Registrasi Admin")
                        .setMessage("Apakah Anda yakin ingin mendaftar sebagai Admin? Hanya boleh ada 1 admin dalam sistem.")
                        .setPositiveButton("Ya") { _, _ ->
                            viewModel.register(name, username, password, role)
                        }
                        .setNegativeButton("Tidak", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                }
            } else {
                // Register as kasir without confirmation
                viewModel.register(name, username, password, role)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
