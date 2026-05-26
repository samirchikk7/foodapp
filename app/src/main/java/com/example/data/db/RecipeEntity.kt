package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val image: String, // emoji representation or URL e.g. "🍝"
    val cookTime: String, // e.g. "30 мин"
    val difficulty: String // "Легко", "Средне", "Сложно"
)
