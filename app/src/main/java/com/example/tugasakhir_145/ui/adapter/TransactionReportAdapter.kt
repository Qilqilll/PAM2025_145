package com.example.tugasakhir_145.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasakhir_145.data.local.TransactionWithItems
import com.example.tugasakhir_145.databinding.ItemTransactionReportBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionReportAdapter : ListAdapter<TransactionWithItems, TransactionReportAdapter.TransactionViewHolder>(DiffCallback) {

    class TransactionViewHolder(private val binding: ItemTransactionReportBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TransactionWithItems) {
            val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

            binding.tvTransactionDate.text = dateFormat.format(Date(item.transaction.date))
            binding.tvTransactionId.text = "#ID: ${item.transaction.id}"
            binding.tvCustomerInfo.text = "${item.transaction.customerName} (Meja ${item.transaction.tableNumber})"
            binding.tvTotalPrice.text = currencyFormat.format(item.transaction.totalAmount)

            // Format Items List
            val itemsBuilder = StringBuilder()
            item.items.forEach { detail ->
                val subtotal = currencyFormat.format(detail.subtotal)
                itemsBuilder.append("- ${detail.productName} (${detail.quantity}x) : $subtotal\n")
            }
            binding.tvItemsList.text = itemsBuilder.toString().trim()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<TransactionWithItems>() {
        override fun areItemsTheSame(oldItem: TransactionWithItems, newItem: TransactionWithItems): Boolean {
            return oldItem.transaction.id == newItem.transaction.id
        }

        override fun areContentsTheSame(oldItem: TransactionWithItems, newItem: TransactionWithItems): Boolean {
            return oldItem == newItem
        }
    }
}
