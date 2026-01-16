package com.example.tugasakhir_145.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionWithItems(
    @Embedded val transaction: Transaction,
    @Relation(
        parentColumn = "transaction_id",
        entityColumn = "transaction_id"
    )
    val items: List<TransactionItem>
)
