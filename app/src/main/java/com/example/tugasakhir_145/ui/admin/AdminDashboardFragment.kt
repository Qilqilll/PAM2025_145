package com.example.tugasakhir_145.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tugasakhir_145.R
import com.example.tugasakhir_145.data.local.AppDatabase
import com.example.tugasakhir_145.databinding.FragmentAdminDashboardBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle back button press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Show exit confirmation dialog
                android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Keluar Aplikasi")
                    .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
                    .setPositiveButton("Ya") { _, _ ->
                        requireActivity().finish()
                    }
                    .setNegativeButton("Tidak", null)
                    .show()
            }
        })

        // Load and display total revenue
        loadTotalRevenue()

        binding.btnManageProducts.setOnClickListener {
            // Navigate to Product List
            findNavController().navigate(R.id.action_adminDashboardFragment_to_productListFragment)
        }
        
        // Future: Reports
        // Reports
        binding.btnReports.setOnClickListener {
             findNavController().navigate(R.id.action_global_transactionReportFragment)
        }

        binding.btnLogout.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboardFragment_to_loginFragment)
        }
    }

    private fun loadTotalRevenue() {
        val db = AppDatabase.getDatabase(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            db.transactionDao().getTotalRevenue().collect { total ->
                val revenue = total ?: 0L
                val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                if (_binding != null) {
                    binding.tvTotalRevenue.text = format.format(revenue)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
