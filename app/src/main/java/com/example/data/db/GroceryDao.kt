package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryDao {

    // --- CARTS LOGIC ---
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartEntity)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteCartItem(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // --- ORDERS LOGIC ---
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Int, status: String)

    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrder(orderId: Int)
}
