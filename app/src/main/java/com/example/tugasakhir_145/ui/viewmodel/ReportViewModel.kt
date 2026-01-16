package com.example.tugasakhir_145.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.tugasakhir_145.data.local.AppDatabase
import com.example.tugasakhir_145.data.local.TransactionWithItems
import kotlinx.coroutines.flow.Flow

class ReportViewModel(application: Application) : AndroidViewModel(application) {
    private val transactionDao = AppDatabase.getDatabase(application).transactionDao()
    
    val allTransactions: Flow<List<TransactionWithItems>> = transactionDao.getTransactionsWithItems()
    val totalRevenue: Flow<Long?> = transactionDao.getTotalRevenue()
}
