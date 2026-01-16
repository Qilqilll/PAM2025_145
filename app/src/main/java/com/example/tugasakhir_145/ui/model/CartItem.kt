package com.example.tugasakhir_145.ui.model

import com.example.tugasakhir_145.data.local.Product

data class CartItem(
    val product: Product,
    var quantity: Int
)
