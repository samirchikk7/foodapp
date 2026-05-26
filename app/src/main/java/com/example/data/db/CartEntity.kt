package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey val productId: String,
    val name: String,
    val price: Double,
    val weight: String,
    val emoji: String,
    val quantity: Int,
    val category: String
)
