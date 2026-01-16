package com.example.tugasakhir_145.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "transaction_id") val id: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "customer_name") val customerName: String,
    @ColumnInfo(name = "table_number") val tableNumber: String,
    @ColumnInfo(name = "date") val date: Long, // Timestamp
    @ColumnInfo(name = "total_price") val totalAmount: Long
)

@Entity(
    tableName = "transaction_items",
    foreignKeys = [
        ForeignKey(
            entity = Transaction::class,
            parentColumns = ["transaction_id"],
            childColumns = ["transaction_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["product_id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class TransactionItem(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "item_id") val id: Int = 0,
    @ColumnInfo(name = "transaction_id") val transactionId: Int,
    @ColumnInfo(name = "product_id") val productId: Int?,
    @ColumnInfo(name = "product_name") val productName: String,
    @ColumnInfo(name = "qty") val quantity: Int,
    @ColumnInfo(name = "subtotal") val subtotal: Long
)
