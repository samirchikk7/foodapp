package com.example.data.repository

import com.example.data.db.CartEntity
import com.example.data.db.GroceryDao
import com.example.data.db.OrderEntity
import com.example.data.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GroceryRepository(private val groceryDao: GroceryDao) {

    val cartItems: Flow<List<CartEntity>> = groceryDao.getCartItems()
    val orders: Flow<List<OrderEntity>> = groceryDao.getOrders()

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
        
        // Generate summary text: "Яблоки Ред Делишес и еще 3 товара"
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
            status = "Принят", // Initial status matches user requirements
            totalPrice = grandTotal,
            address = address,
            itemsSummary = itemsSummary,
            estimatedDeliveryTime = System.currentTimeMillis() + 30 * 60 * 1000, // 30 mins
            earnedPoints = earned,
            purchasedProductIds = productIdsStr
        )
        
        val orderId = groceryDao.insertOrder(order)
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
