package com.example.tugasakhir_145.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long

    @Insert
    suspend fun insertTransactionItems(items: List<TransactionItem>)

    @androidx.room.Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getTransactionsWithItems(): Flow<List<TransactionWithItems>>

    @Query("SELECT SUM(total_price) FROM transactions")
    fun getTotalRevenue(): Flow<Long?>
}
