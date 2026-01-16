package com.example.tugasakhir_145.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasakhir_145.data.local.AppDatabase
import com.example.tugasakhir_145.data.local.Product
import com.example.tugasakhir_145.data.local.Transaction
import com.example.tugasakhir_145.data.local.TransactionItem
import com.example.tugasakhir_145.ui.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(product: Product) {
        _cartItems.update { currentItems ->
            val existingItem = currentItems.find { it.product.id == product.id }
            if (existingItem != null) {
                currentItems.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                currentItems + CartItem(product, 1)
            }
        }
    }

    fun removeFromCart(product: Product) {
        _cartItems.update { currentItems ->
             currentItems.filter { it.product.id != product.id }
        }
    }
    
    fun clearCart() {
        _cartItems.value = emptyList()
    }
    
    fun getTotal(): Long {
        return _cartItems.value.sumOf { it.product.price * it.quantity }
    }

    fun checkout(userId: Int, customerName: String, tableNo: String, onSuccess: (Long) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                if (_cartItems.value.isEmpty()) {
                    onError("Keranjang kosong")
                    return@launch
                }
                
                // 1. Create Transaction
                val transaction = Transaction(
                    userId = userId,
                    customerName = customerName,
                    tableNumber = tableNo,
                    date = System.currentTimeMillis(),
                    totalAmount = getTotal()
                )
                val transactionId = db.transactionDao().insertTransaction(transaction)

                // 2. Create Items & Decrease Stock
                val items = _cartItems.value.map { cartItem ->
                    // Verify if product still exists in DB
                    val existingProduct = db.productDao().getProductById(cartItem.product.id)
                    val finalProductId = if (existingProduct != null) cartItem.product.id else null

                    TransactionItem(
                        transactionId = transactionId.toInt(),
                        productId = finalProductId,
                        productName = cartItem.product.name,
                        quantity = cartItem.quantity,
                        subtotal = cartItem.product.price * cartItem.quantity
                    )
                }
                
                db.transactionDao().insertTransactionItems(items)

                // 3. Decrease Stock (Only for existing products)
                for (item in items) {
                     item.productId?.let { pid ->
                         val rows = db.productDao().decreaseStock(pid, item.quantity)
                         // if rows==0, it means safely ignored or stock issue
                     }
                }

                clearCart()
                onSuccess(transactionId)
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }
}

