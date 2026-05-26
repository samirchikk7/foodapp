package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ProductDetailDialog
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryTab
import com.example.ui.viewmodel.GroceryViewModel
import com.example.ui.viewmodel.AuthState

class MainActivity : ComponentActivity() {

    private val viewModel: GroceryViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val authState by viewModel.authState.collectAsState()

                Crossfade(targetState = authState, label = "auth_screen_transition") { state ->
                    if (state is AuthState.Authenticated) {
                        val currentTab by viewModel.currentTab.collectAsState()
                        val cartItems by viewModel.cartItems.collectAsState()
                        val selectedProduct by viewModel.selectedProduct.collectAsState()
                        val userBalance by viewModel.userBalance.collectAsState()
                        val deliveryAddress by viewModel.deliveryAddress.collectAsState()

                        val totalCartCount = cartItems.sumOf { it.quantity }

                        Scaffold(
                            modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
                            topBar = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.background)
                                        .statusBarsPadding()
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // Left: Delivery Location info
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { viewModel.setTab(GroceryTab.PROFILE) }
                                        ) {
                                            Text(
                                                text = "ДОСТАВКА НА",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = SleekTextSecondary,
                                                    letterSpacing = 1.sp
                                                ),
                                                fontSize = 11.sp
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = if (deliveryAddress.isBlank()) "Указать адрес в Профиле" else deliveryAddress,
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onBackground
                                                    ),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "▼",
                                                    fontSize = 9.sp,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        // Right: Interactive Wallet Balance + Custom Initial Badge Avatar
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                                    .clickable { viewModel.setTab(GroceryTab.PROFILE) }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${userBalance.toInt()} ₽",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                                    .clickable { viewModel.setTab(GroceryTab.PROFILE) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val initials = state.name.split(" ")
                                                    .filter { it.isNotBlank() }
                                                    .take(2)
                                                    .map { it.first().uppercase() }
                                                    .joinToString("")
                                                Text(
                                                    text = if (initials.isEmpty()) "СМ" else initials,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                    }
                                }
                            },
                            bottomBar = {
                                NavigationBar(
                                    modifier = Modifier.testTag("bottom_nav_bar").windowInsetsPadding(WindowInsets.navigationBars),
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 6.dp
                                ) {
                                    // Catalog Tab
                                    NavigationBarItem(
                                        selected = currentTab == GroceryTab.CATALOG,
                                        onClick = { viewModel.setTab(GroceryTab.CATALOG) },
                                        label = { Text("Каталог", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = "Catalog"
                                            )
                                        },
                                        modifier = Modifier.testTag("tab_catalog")
                                    )

                                    // Cart Tab (with badges showing selected items quantity)
                                    NavigationBarItem(
                                        selected = currentTab == GroceryTab.CART,
                                        onClick = { viewModel.setTab(GroceryTab.CART) },
                                        label = { Text("Корзина", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                        icon = {
                                            BadgedBox(
                                                badge = {
                                                    if (totalCartCount > 0) {
                                                        Badge(
                                                            containerColor = Color.Red,
                                                            contentColor = Color.White
                                                        ) {
                                                            Text(
                                                                text = totalCartCount.toString(),
                                                                modifier = Modifier.testTag("cart_badge_count")
                                                            )
                                                        }
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ShoppingCart,
                                                    contentDescription = "Cart"
                                                )
                                            }
                                        },
                                        modifier = Modifier.testTag("tab_cart")
                                    )

                                    // Orders Tab
                                    NavigationBarItem(
                                        selected = currentTab == GroceryTab.ORDERS,
                                        onClick = { viewModel.setTab(GroceryTab.ORDERS) },
                                        label = { Text("Заказы", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.Assignment,
                                                contentDescription = "Orders"
                                            )
                                        },
                                        modifier = Modifier.testTag("tab_orders")
                                    )

                                    // Profile Tab
                                    NavigationBarItem(
                                        selected = currentTab == GroceryTab.PROFILE,
                                        onClick = { viewModel.setTab(GroceryTab.PROFILE) },
                                        label = { Text("Профиль", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = "Profile"
                                            )
                                        },
                                        modifier = Modifier.testTag("tab_profile")
                                    )
                                }
                            }
                        ) { innerPadding ->
                            // Beautiful fade tab transition
                            Crossfade(
                                targetState = currentTab,
                                modifier = Modifier.padding(innerPadding),
                                label = "tab_switch_animation"
                            ) { tabState ->
                                when (tabState) {
                                    GroceryTab.CATALOG -> CatalogScreen(viewModel = viewModel)
                                    GroceryTab.CART -> CartScreen(viewModel = viewModel)
                                    GroceryTab.ORDERS -> OrdersScreen(viewModel = viewModel)
                                    GroceryTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                                }
                            }

                            // Popup Dialog overlay layer when product is tapped for detail exploration
                            selectedProduct?.let { product ->
                                val cartItem = cartItems.find { it.productId == product.id }
                                val currQty = cartItem?.quantity ?: 0

                                ProductDetailDialog(
                                    product = product,
                                    quantityInCart = currQty,
                                    onDismissRequest = { viewModel.selectProduct(null) },
                                    onAddToCart = { quantity ->
                                        // Sync quantity difference
                                        val diff = quantity - currQty
                                        viewModel.addToCart(product, diff)
                                    }
                                )
                            }
                        }
                    } else {
                        // Display the premium login form
                        AuthScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
