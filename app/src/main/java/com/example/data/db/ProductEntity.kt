package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Product

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val oldPrice: Double?,
    val weight: String,
    val emoji: String,
    val description: String,
    val calories: Int,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
    val rating: Double,
    val stockStatus: String, // "IN_STOCK", "LOW_STOCK", "OUT_OF_STOCK"
    val isAvailable: Int, // 1 for true, 0 for false
    val badgesString: String // Comma separated, e.g. "LOCAL,ORGANIC"
) {
    fun toProduct(): Product {
        return Product(
            id = id,
            name = name,
            category = category,
            price = price,
            weight = weight,
            emoji = emoji,
            description = description,
            calories = calories,
            proteins = proteins,
            fats = fats,
            carbs = carbs,
            rating = rating,
            oldPrice = oldPrice,
            stockStatus = stockStatus,
            isAvailable = isAvailable == 1,
            badges = if (badgesString.isBlank()) emptyList() else badgesString.split(",").map { it.trim() }
        )
    }

    companion object {
        fun fromProduct(product: Product): ProductEntity {
            return ProductEntity(
                id = product.id,
                name = product.name,
                category = product.category,
                price = product.price,
                oldPrice = product.oldPrice,
                weight = product.weight,
                emoji = product.emoji,
                description = product.description,
                calories = product.calories,
                proteins = product.proteins,
                fats = product.fats,
                carbs = product.carbs,
                rating = product.rating,
                stockStatus = product.stockStatus,
                isAvailable = if (product.isAvailable) 1 else 0,
                badgesString = product.badges.joinToString(",")
            )
        }
    }
}
