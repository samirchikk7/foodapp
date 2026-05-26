package com.example.data.repository

import com.example.data.db.*
import com.example.data.model.Product
import com.example.data.model.PREDEFINED_PRODUCTS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GroceryRepository(private val groceryDao: GroceryDao) {

    val cartItems: Flow<List<CartEntity>> = groceryDao.getCartItems()
    val orders: Flow<List<OrderEntity>> = groceryDao.getOrders()

    val products: Flow<List<Product>> = groceryDao.getProducts().map { list ->
        list.map { it.toProduct() }
    }

    val recipes: Flow<List<RecipeEntity>> = groceryDao.getRecipes()

    fun getRecipeIngredients(recipeId: String): Flow<List<RecipeIngredientEntity>> {
        return groceryDao.getRecipeIngredients(recipeId)
    }

    suspend fun getRecipeIngredientsSync(recipeId: String): List<RecipeIngredientEntity> = withContext(Dispatchers.IO) {
        groceryDao.getRecipeIngredientsSync(recipeId)
    }

    suspend fun seedDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        val existingProducts = groceryDao.getProductsSync()
        if (existingProducts.isEmpty()) {
            val entities = PREDEFINED_PRODUCTS.map { product ->
                // Assign some dynamic badges and low-stocks to predefined list for a realistic feel
                val customBadges = when (product.id) {
                    "v1" -> listOf("LOCAL", "ORGANIC")
                    "v2" -> listOf("BEST_PRICE")
                    "v5" -> listOf("PREMIUM", "ORGANIC")
                    "d2" -> listOf("LOCAL", "TOP_SELLER")
                    "m2" -> listOf("PREMIUM")
                    else -> emptyList()
                }
                val stock = when (product.id) {
                    "v2" -> "LOW_STOCK"
                    "dr2" -> "OUT_OF_STOCK"
                    else -> "IN_STOCK"
                }
                val oldPrice = if (product.id == "v1" || product.id == "d2") {
                    product.price * 1.25
                } else null

                ProductEntity.fromProduct(
                    product.copy(
                        badges = customBadges,
                        stockStatus = stock,
                        oldPrice = oldPrice
                    )
                )
            }
            groceryDao.insertProducts(entities)
        }

        val existingRecipes = groceryDao.getRecipes().first()
        if (existingRecipes.isEmpty()) {
            // Seed 3 delicious recipes
            val recipe1 = RecipeEntity(
                id = "r1",
                title = "Полезный Авокадо Тост",
                image = "🥑🍞",
                cookTime = "15 мин",
                difficulty = "Легко"
            )
            val recipe2 = RecipeEntity(
                id = "r2",
                title = "Паста с Черри и Багетом",
                image = "🍝🍅",
                cookTime = "25 мин",
                difficulty = "Средне"
            )
            val recipe3 = RecipeEntity(
                id = "r3",
                title = "Стейк Рибай с Овощами",
                image = "🥩🥒",
                cookTime = "35 мин",
                difficulty = "Сложно"
            )

            groceryDao.insertRecipe(recipe1)
            groceryDao.insertRecipe(recipe2)
            groceryDao.insertRecipe(recipe3)

            // Ingredients for Avocado Toast: Avocado, Crusty Baget, Organic Eggs
            groceryDao.insertRecipeIngredient(RecipeIngredientEntity("ri1", "r1", "Спелое авокадо Хасс", "v5", "2 шт"))
            groceryDao.insertRecipeIngredient(RecipeIngredientEntity("ri2", "r1", "Багет французский", "b1", "1 шт"))
            groceryDao.insertRecipeIngredient(RecipeIngredientEntity("ri3", "r1", "Яйца куриные С0", "d2", "2 шт"))

            // Ingredients for Cherry Pasta: Cherry Tomatoes, French Baget
            groceryDao.insertRecipeIngredient(RecipeIngredientEntity("ri4", "r2", "Томаты Черри спелые", "v1", "250 г"))
            groceryDao.insertRecipeIngredient(RecipeIngredientEntity("ri5", "r2", "Багет французский", "b1", "1 шт"))

            // Ingredients for Steak & Veggies: Ribeye, cucumbers, fresh greenery
            groceryDao.insertRecipeIngredient(RecipeIngredientEntity("ri6", "r3", "Стейк Рибай мраморный", "m2", "350 г"))
            groceryDao.insertRecipeIngredient(RecipeIngredientEntity("ri7", "r3", "Огурцы короткоплодные", "v2", "450 г"))
            groceryDao.insertRecipeIngredient(RecipeIngredientEntity("ri8", "r3", "Свежая зелень укропа", "g2", "100 г"))
        }
    }

    // --- PRODUCTS MANAGEMENT ---
    suspend fun saveProduct(product: Product) {
        groceryDao.insertProduct(ProductEntity.fromProduct(product))
    }

    suspend fun deleteProduct(productId: String) {
        groceryDao.deleteProduct(productId)
    }

    // --- RECIPES MANAGEMENT ---
    suspend fun saveRecipe(recipe: RecipeEntity, ingredients: List<RecipeIngredientEntity>) {
        groceryDao.insertRecipe(recipe)
        groceryDao.deleteRecipeIngredients(recipe.id)
        ingredients.forEach {
            groceryDao.insertRecipeIngredient(it)
        }
    }

    suspend fun deleteRecipe(recipeId: String) {
        groceryDao.deleteRecipe(recipeId)
        groceryDao.deleteRecipeIngredients(recipeId)
    }

    suspend fun addToCart(product: Product, count: Int = 1) {
        val currentItems = cartItems.first()
        val existingItem = currentItems.find { it.productId == product.id }
        if (existingItem != null) {
            val newQty = existingItem.quantity + count
            if (newQty > 0) {
                groceryDao.insertCartItem(existingItem.copy(quantity = newQty))
            } else {
                groceryDao.deleteCartItem(product.id)
            }
        } else {
            if (count > 0) {
                groceryDao.insertCartItem(
                    CartEntity(
                        productId = product.id,
                        name = product.name,
                        price = product.price,
                        weight = product.weight,
                        emoji = product.emoji,
                        quantity = count,
                        category = product.category
                    )
                )
            }
        }
    }

    suspend fun updateCartQuantity(productId: String, change: Int) {
        val currentItems = cartItems.first()
        val item = currentItems.find { it.productId == productId } ?: return
        val newQty = item.quantity + change
        if (newQty > 0) {
            groceryDao.insertCartItem(item.copy(quantity = newQty))
        } else {
            groceryDao.deleteCartItem(productId)
        }
    }

    suspend fun removeFromCart(productId: String) {
        groceryDao.deleteCartItem(productId)
    }

    suspend fun clearCart() {
        groceryDao.clearCart()
    }

    suspend fun placeOrder(
        address: String, 
        items: List<CartEntity>, 
        shippingFee: Double, 
        couponApplied: Boolean = false, 
        pointsSpent: Int = 0
    ): Long {
        if (items.isEmpty()) return -1L
        
        val itemsSumPrice = items.sumOf { it.price * it.quantity }
        val couponDiscount = if (couponApplied) itemsSumPrice * 0.15 else 0.0
        val grandTotal = (itemsSumPrice - couponDiscount + shippingFee - pointsSpent).coerceAtLeast(0.0)
        
        // Generate summary text
        val firstItemName = items.first().name
        val remainingCount = items.sumOf { it.quantity } - items.first().quantity
        val itemsSummary = if (remainingCount > 0) {
            val countText = if (remainingCount == 1) "1 товар" else "$remainingCount товара"
            "$firstItemName и еще $countText"
        } else {
            "$firstItemName (1 шт)"
        }

        val earned = (grandTotal * 0.1).toInt()
        val productIdsStr = items.map { it.productId }.joinToString(",")

        val order = OrderEntity(
            timestamp = System.currentTimeMillis(),
            status = "PENDING", // Unified state machine starts at "PENDING"
            totalPrice = grandTotal,
            address = address,
            itemsSummary = itemsSummary,
            estimatedDeliveryTime = System.currentTimeMillis() + 30 * 60 * 1000, // 30 mins
            earnedPoints = earned,
            purchasedProductIds = productIdsStr
        )
        
        val orderId = groceryDao.insertOrder(order)
        
        // Relational order item linkages
        items.forEach { cartItem ->
            val orderItemId = "${orderId}_${cartItem.productId}"
            groceryDao.insertOrderItem(
                OrderItemEntity(
                    id = orderItemId,
                    orderId = orderId.toInt(),
                    productId = cartItem.productId,
                    name = cartItem.name,
                    price = cartItem.price,
                    quantity = cartItem.quantity,
                    emoji = cartItem.emoji
                )
            )
        }

        groceryDao.clearCart()
        return orderId
    }

    suspend fun updateOrderStatus(orderId: Int, status: String) {
        groceryDao.updateOrderStatus(orderId, status)
    }

    suspend fun cancelOrder(orderId: Int) {
        groceryDao.deleteOrder(orderId)
    }
}
