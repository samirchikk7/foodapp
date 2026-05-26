package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.CartEntity
import com.example.data.db.OrderEntity
import com.example.data.model.Product
import com.example.data.model.PREDEFINED_PRODUCTS
import com.example.data.repository.GroceryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class GroceryTab {
    CATALOG, CART, ORDERS, PROFILE
}

class GroceryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GroceryRepository
    private val prefs = application.getSharedPreferences("grocery_prefs", Context.MODE_PRIVATE)

    val cartItems: StateFlow<List<CartEntity>>
    val orders: StateFlow<List<OrderEntity>>
    val recommendedProducts: StateFlow<List<Product>>

    // UI States
    private val _currentTab = MutableStateFlow(GroceryTab.CATALOG)
    val currentTab: StateFlow<GroceryTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Все")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _deliveryAddress = MutableStateFlow(prefs.getString("delivery_address", "ул. Ленина, д. 45, кв. 112") ?: "ул. Ленина, д. 45, кв. 112")
    val deliveryAddress: StateFlow<String> = _deliveryAddress.asStateFlow()

    private val _userBalance = MutableStateFlow(prefs.getFloat("user_balance", 1500.0f).toDouble())
    val userBalance: StateFlow<Double> = _userBalance.asStateFlow()

    // Loyalty Points (initialized to 150 points for quick testing)
    private val _loyaltyPoints = MutableStateFlow(prefs.getInt("loyalty_points", 150))
    val loyaltyPoints: StateFlow<Int> = _loyaltyPoints.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = GroceryRepository(database.groceryDao())
        
        cartItems = repository.cartItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
        orders = repository.orders.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Machine Learning-style recommendation algorithm analyzing real order history
        recommendedProducts = orders.map { orderList ->
            val purchasedIds = orderList.flatMap { order ->
                order.purchasedProductIds.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
            }.toSet()

            if (purchasedIds.isEmpty()) {
                // Return highly-rated products if there's no history
                PREDEFINED_PRODUCTS.filter { it.rating >= 4.9 }.take(6)
            } else {
                // 1. Map to actual products
                val purchasedProducts = PREDEFINED_PRODUCTS.filter { it.id in purchasedIds }
                
                // 2. Identify favorite categories in order history
                val categoryFrequencies = purchasedProducts
                    .groupBy { it.category }
                    .mapValues { it.value.size }
                
                val favoriteCategory = categoryFrequencies.maxByOrNull { it.value }?.key
                
                // 3. Recommend products from favorite categories that haven't been bought yet
                val categoryRecommendations = PREDEFINED_PRODUCTS.filter { 
                    it.category == favoriteCategory && it.id !in purchasedIds 
                }
                
                // 4. Complement with other highly rated items that user hasn't bought
                val generalRecommendations = PREDEFINED_PRODUCTS.filter { 
                    it.id !in purchasedIds && it.rating >= 4.8 
                }
                
                val finalRecs = (categoryRecommendations + generalRecommendations).distinctBy { it.id }.take(6)
                if (finalRecs.size < 4) {
                    // Fallback to top rated
                    (finalRecs + PREDEFINED_PRODUCTS.filter { it.id !in purchasedIds }).distinctBy { it.id }.take(6)
                } else {
                    finalRecs
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PREDEFINED_PRODUCTS.filter { it.rating >= 4.9 }.take(6)
        )
    }

    fun setTab(tab: GroceryTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectProduct(product: Product?) {
        _selectedProduct.value = product
    }

    fun setDeliveryAddress(address: String) {
        _deliveryAddress.value = address
        prefs.edit().putString("delivery_address", address).apply()
    }

    fun topUpBalance() {
        _userBalance.value += 500.0
        prefs.edit().putFloat("user_balance", _userBalance.value.toFloat()).apply()
    }

    fun addLoyaltyPoints(points: Int) {
        _loyaltyPoints.value += points
        prefs.edit().putInt("loyalty_points", _loyaltyPoints.value).apply()
    }

    fun spendLoyaltyPoints(points: Int) {
        _loyaltyPoints.value = (_loyaltyPoints.value - points).coerceAtLeast(0)
        prefs.edit().putInt("loyalty_points", _loyaltyPoints.value).apply()
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product, quantity)
        }
    }

    fun updateCartQuantity(productId: String, change: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, change)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun placeOrder(address: String, shippingFee: Double, pointsToSpend: Int = 0, couponApplied: Boolean = false) {
        viewModelScope.launch {
            val items = cartItems.value
            if (items.isEmpty()) return@launch
            
            val subtotal = items.sumOf { it.price * it.quantity }
            val couponDiscount = if (couponApplied) subtotal * 0.15 else 0.0
            val grandTotal = (subtotal - couponDiscount + shippingFee - pointsToSpend).coerceAtLeast(0.0)
            
            // Deduct from simulator user balance
            if (_userBalance.value >= grandTotal) {
                _userBalance.value -= grandTotal
                prefs.edit().putFloat("user_balance", _userBalance.value.toFloat()).apply()
            }

            if (pointsToSpend > 0) {
                spendLoyaltyPoints(pointsToSpend)
            }

            val orderId = repository.placeOrder(
                address = address, 
                items = items, 
                shippingFee = shippingFee, 
                couponApplied = couponApplied, 
                pointsSpent = pointsToSpend
            )
            
            if (orderId > 0) {
                // Earn loyalty points: 10% of checkout total
                val earned = (grandTotal * 0.1).toInt()
                if (earned > 0) {
                    addLoyaltyPoints(earned)
                }

                // Switch to orders tab
                _currentTab.value = GroceryTab.ORDERS
                // Simulate shipping progress
                simulateOrderJourney(orderId.toInt())
            }
        }
    }

    fun cancelOrder(orderId: Int) {
        viewModelScope.launch {
            repository.cancelOrder(orderId)
        }
    }

    private fun simulateOrderJourney(orderId: Int) {
        viewModelScope.launch {
            // Snappy and fully matching statuses: Принят -> Готовится -> В пути -> Доставлен
            delay(5000)
            repository.updateOrderStatus(orderId, "Готовится")
            
            delay(10000)
            repository.updateOrderStatus(orderId, "В пути")
            
            delay(12000)
            repository.updateOrderStatus(orderId, "Доставлен")
        }
    }
}
