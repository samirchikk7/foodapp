package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_ingredients")
data class RecipeIngredientEntity(
    @PrimaryKey val id: String,
    val recipeId: String,
    val productName: String,
    val linkedProductId: String?, // References ProductEntity.id
    val quantityNeeded: String // e.g. "250 г" or "3 шт"
)
