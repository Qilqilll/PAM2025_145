package com.example.tugasakhir_145.ui.cashier

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tugasakhir_145.R
import com.example.tugasakhir_145.databinding.FragmentCartBinding
import com.example.tugasakhir_145.ui.viewmodel.CartViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = com.example.tugasakhir_145.ui.adapter.CartAdapter { cartItem ->
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Hapus Item")
                .setMessage("Apakah Anda yakin ingin menghapus ${cartItem.product.name} dari keranjang?")
                .setPositiveButton("Ya") { _, _ ->
                    viewModel.removeFromCart(cartItem.product)
                    Toast.makeText(context, "${cartItem.product.name} dihapus", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
        
        binding.rvCartItems.layoutManager = LinearLayoutManager(context)
        binding.rvCartItems.adapter = adapter

        binding.btnClearCart.setOnClickListener {
            if (viewModel.cartItems.value.isEmpty()) return@setOnClickListener
            
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Batalkan Pesanan")
                .setMessage("Apakah Anda yakin ingin membatalkan seluruh pesanan?")
                .setPositiveButton("Ya") { _, _ ->
                    viewModel.clearCart()
                    Toast.makeText(context, "Pesanan Dibatalkan", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cartItems.collect { items ->
                adapter.submitList(items.toList()) // Create copy to force DiffUtil update
                val total = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(viewModel.getTotal())
                // Ensure binding is available (though onViewCreated usually guarantees it, safe call is good)
                if (_binding != null) {
                    binding.tvTotal.text = total
                }
            }
        }

        binding.btnCheckout.setOnClickListener {
            val customer = binding.etCustomerName.text.toString()
            val table = binding.etTableNumber.text.toString()
            
            if (customer.isEmpty() || table.isEmpty()) {
                Toast.makeText(context, "Isi Nama & Meja", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = requireActivity().getSharedPreferences("USER_SESSION", android.content.Context.MODE_PRIVATE)
            val userId = sharedPref.getInt("USER_ID", -1)
            
            if (userId == -1) {
                Toast.makeText(context, "Sesi Invalid, Silahkan Login Ulang", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentTotal = viewModel.getTotal()

            val items = viewModel.cartItems.value
            
            viewModel.checkout(userId, customer, table, 
                onSuccess = { txId ->
                    Toast.makeText(context, "Transaksi Berhasil!", Toast.LENGTH_LONG).show()
                    shareReceipt(txId, customer, currentTotal, items)
                    
                    if (_binding != null) {
                        binding.etCustomerName.text?.clear()
                        binding.etTableNumber.text?.clear()
                    }
                },
                onError = { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
    
    private fun shareReceipt(txId: Long, customer: String, total: Long, items: List<com.example.tugasakhir_145.ui.model.CartItem>) {
        val sb = StringBuilder()
        sb.append("=== POS UMKM RECEIPT ===\n")
        sb.append("ID Transaksi: #$txId\n")
        val date = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        sb.append("Tanggal: $date\n")
        sb.append("Pelanggan: $customer\n")
        sb.append("--------------------------------\n")
        
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        
        for (item in items) {
            sb.append("${item.product.name}\n")
            val price = format.format(item.product.price)
            val sub = format.format(item.product.price * item.quantity)
            sb.append("  ${item.quantity} x $price = $sub\n")
        }
        
        sb.append("--------------------------------\n")
        sb.append("TOTAL: ${format.format(total)}\n")
        sb.append("================================\n")
        sb.append("Terima Kasih!")

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sb.toString())
        }
        try {
            startActivity(Intent.createChooser(intent, "Kirim Struk"))
            
            // Navigate back to Cashier Dashboard after sharing
            // This allows cashier to serve next customer immediately
            findNavController().navigateUp()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Tidak ada aplikasi untuk berbagi", Toast.LENGTH_SHORT).show()
            
            // Even if sharing fails, navigate back to dashboard
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
