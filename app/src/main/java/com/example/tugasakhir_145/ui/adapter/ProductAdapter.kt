package com.example.tugasakhir_145.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.tugasakhir_145.data.local.Product
import com.example.tugasakhir_145.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val onItemClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.binding.apply {
            tvName.text = product.name
            tvPrice.text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(product.price)
            tvStock.text = "Stok: ${product.stock}"
            
            // Load image with error handling
            if (!product.imageUri.isNullOrEmpty()) {
                ivProduct.load(Uri.parse(product.imageUri)) {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_menu_gallery)
                    listener(
                        onError = { _, _ ->
                            // If image fails to load, show placeholder
                            ivProduct.setImageResource(android.R.drawable.ic_menu_gallery)
                        }
                    )
                }
            } else {
                ivProduct.setImageResource(android.R.drawable.ic_menu_gallery)
            }
            
            root.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // The bind method is no longer needed as logic is moved to onBindViewHolder
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
