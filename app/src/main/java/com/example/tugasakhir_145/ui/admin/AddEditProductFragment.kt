package com.example.tugasakhir_145.ui.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.tugasakhir_145.R
import com.example.tugasakhir_145.data.local.AppDatabase
import com.example.tugasakhir_145.data.local.Product
import com.example.tugasakhir_145.databinding.FragmentAddEditProductBinding
import com.example.tugasakhir_145.ui.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

class AddEditProductFragment : Fragment() {

    private var _binding: FragmentAddEditProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by viewModels() // Shared scope if scoped to activity, but here scoped to Fragment is fine for actions

    private var currentImageUri: Uri? = null
    private var editingProduct: Product? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            currentImageUri = it
            binding.ivProductPreview.load(it)
            
            // Take persistable URI permission to keep access after app restart
            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                // Permission already taken or not available
                // Image will still work for current session
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = arguments?.getInt("productId", -1) ?: -1
        if (productId != -1) {
            loadProduct(productId)
        }

        binding.btnSelectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            saveProduct()
        }
        
        binding.btnDelete.setOnClickListener {
            editingProduct?.let { product ->
                // Safety Requirement: Confirmation dialog before deletion
                android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Hapus Produk")
                    .setMessage("Apakah Anda yakin ingin menghapus produk \"${product.name}\"? Tindakan ini tidak dapat dibatalkan.")
                    .setPositiveButton("Hapus") { _, _ ->
                        viewModel.delete(
                            product,
                            onSuccess = {
                                Toast.makeText(context, "Produk dihapus", Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            },
                            onError = { message ->
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                    .setNegativeButton("Batal", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
        }
    }

    private fun loadProduct(id: Int) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val product = db.productDao().getProductById(id)
            product?.let {
                editingProduct = product
                binding.etProductName.setText(product.name)
                binding.etProductPrice.setText(product.price.toString())
                binding.etProductStock.setText(product.stock.toString())
                
                product.imageUri?.let { uriString ->
                    currentImageUri = Uri.parse(uriString)
                    binding.ivProductPreview.load(currentImageUri) {
                        crossfade(true)
                        placeholder(android.R.drawable.ic_menu_gallery)
                        error(android.R.drawable.ic_menu_gallery)
                        listener(
                            onError = { _, _ ->
                                // If image fails to load, show placeholder
                                binding.ivProductPreview.setImageResource(android.R.drawable.ic_menu_gallery)
                            }
                        )
                    }
                }
                binding.btnDelete.visibility = View.VISIBLE
                binding.btnSave.text = "Perbarui Produk"
            }
        }
    }

    private fun saveProduct() {
        val name = binding.etProductName.text.toString()
        val priceStr = binding.etProductPrice.text.toString()
        val stockStr = binding.etProductStock.text.toString()

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(context, "Harap isi semua data", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toLongOrNull() ?: 0L
        val stock = stockStr.toIntOrNull() ?: 0

        // Safety Requirement: Prevent negative values
        if (price <= 0) {
            Toast.makeText(context, "Harga harus lebih dari 0", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (stock < 0) {
            Toast.makeText(context, "Stok tidak boleh negatif", Toast.LENGTH_SHORT).show()
            return
        }

        val product = Product(
            id = editingProduct?.id ?: 0,
            name = name,
            price = price,
            stock = stock,
            imageUri = currentImageUri?.toString()
        )

        if (editingProduct == null) {
            viewModel.insert(product)
        } else {
            viewModel.update(product)
        }
        
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
