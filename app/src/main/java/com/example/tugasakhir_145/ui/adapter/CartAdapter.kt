package com.example.tugasakhir_145.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasakhir_145.databinding.ItemCartBinding
import com.example.tugasakhir_145.ui.model.CartItem
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(private val onDeleteClick: (CartItem) -> Unit) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback) {

    class CartViewHolder(private val binding: ItemCartBinding, val onDeleteClick: (CartItem) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem) {
            val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            binding.tvProductName.text = cartItem.product.name
            binding.tvProductPrice.text = "${format.format(cartItem.product.price)} x ${cartItem.quantity}"
            
            binding.btnDelete.setOnClickListener {
                onDeleteClick(cartItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            // Check quantity too as it updates
            return oldItem == newItem
        }
    }
}
