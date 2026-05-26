package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey val id: String, // format "orderId_productId"
    val orderId: Int,
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val emoji: String
)
