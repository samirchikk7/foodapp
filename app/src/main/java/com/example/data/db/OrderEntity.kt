package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val status: String, // e.g., "Принят", "Готовится", "В пути", "Доставлен"
    val totalPrice: Double,
    val address: String,
    val itemsSummary: String, // e.g., "Бананы, Томаты Черри и 2 других товара"
    val estimatedDeliveryTime: Long = 0L, // Expected delivery time stamp
    val earnedPoints: Int = 0, // Cashback points awarded for this order
    val purchasedProductIds: String = "" // Comma-separated list of product IDs in this order for analytics
)
