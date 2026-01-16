package com.example.tugasakhir_145.ui.cashier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tugasakhir_145.R
import com.example.tugasakhir_145.databinding.FragmentCashierDashboardBinding
import com.example.tugasakhir_145.ui.adapter.ProductAdapter
import com.example.tugasakhir_145.ui.viewmodel.CartViewModel
import com.example.tugasakhir_145.ui.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

class CashierDashboardFragment : Fragment() {

    private var _binding: FragmentCashierDashboardBinding? = null
    private val binding get() = _binding!!
    private val productViewModel: ProductViewModel by viewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCashierDashboardBinding.inflate(inflater, container, false)
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

        val adapter = ProductAdapter { product ->
            // REQ-22: Validate stock availability before adding to cart
            if (product.stock <= 0) {
                Toast.makeText(context, "Stok ${product.name} habis!", Toast.LENGTH_SHORT).show()
                return@ProductAdapter
            }
            
            cartViewModel.addToCart(product)
            Toast.makeText(context, "${product.name} masuk keranjang", Toast.LENGTH_SHORT).show()
        }

        binding.rvProducts.layoutManager = GridLayoutManager(context, 2)
        binding.rvProducts.adapter = adapter
        
        viewLifecycleOwner.lifecycleScope.launch {
            productViewModel.allProducts.collect { products ->
                adapter.submitList(products)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
             cartViewModel.cartItems.collect { items ->
                 val count = items.sumOf { it.quantity }
                 val total = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("id", "ID")).format(cartViewModel.getTotal())
                 if (_binding != null) {
                    binding.btnViewCart.text = "Lihat Keranjang ($count) - $total"
                 }
             }
        }

        binding.btnViewCart.setOnClickListener {
            findNavController().navigate(R.id.action_cashierDashboardFragment_to_cartFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
