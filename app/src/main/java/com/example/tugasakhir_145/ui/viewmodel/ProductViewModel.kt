package com.example.tugasakhir_145.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasakhir_145.data.local.AppDatabase
import com.example.tugasakhir_145.data.local.Product
import com.example.tugasakhir_145.data.local.ProductDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val productDao: ProductDao = AppDatabase.getDatabase(application).productDao()
    
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    fun insert(product: Product) = viewModelScope.launch {
        productDao.insertProduct(product)
    }

    fun update(product: Product) = viewModelScope.launch {
        productDao.updateProduct(product)
    }

    fun delete(product: Product, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) = viewModelScope.launch {
        try {
            productDao.deleteProduct(product)
            onSuccess()
        } catch (e: android.database.sqlite.SQLiteConstraintException) {
            onError("Produk tidak bisa dihapus karena sudah ada riwayat transaksi.")
        } catch (e: Exception) {
            onError(e.message ?: "Gagal menghapus produk")
        }
    }
}
