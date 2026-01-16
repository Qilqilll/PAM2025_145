package com.example.tugasakhir_145.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "product_id") val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "price") val price: Long,
    @ColumnInfo(name = "stock") val stock: Int,
    @ColumnInfo(name = "image_uri") val imageUri: String? // URI string for the image
)
