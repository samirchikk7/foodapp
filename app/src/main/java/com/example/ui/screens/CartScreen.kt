package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.CartEntity
import com.example.ui.viewmodel.GroceryTab
import com.example.ui.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: GroceryViewModel,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val deliveryAddress by viewModel.deliveryAddress.collectAsState()
    val userBalance by viewModel.userBalance.collectAsState()
    val loyaltyPoints by viewModel.loyaltyPoints.collectAsState()

    var showCheckoutDialog by remember { mutableStateOf(false) }
    var selectedDeliveryTime by remember { mutableStateOf("Прямо сейчас (30 мин)") }
    var selectedPaymentMethod by remember { mutableStateOf("Банковская карта") }
    var inputtingCoupon by remember { mutableStateOf("") }
    var appliedDiscount by remember { mutableStateOf(0.0) } // 15% discount if coupon matches
    var couponMessage by remember { mutableStateOf("") }
    var usePointsDiscount by remember { mutableStateOf(false) }

    val itemsSubtotal = cartItems.sumOf { it.price * it.quantity }
    val freeShippingLimit = 1000.0
    val shippingFee = if (itemsSubtotal >= freeShippingLimit) 0.0 else 149.0
    val discountCost = itemsSubtotal * appliedDiscount
    
    // Loyalty points calculations: 1 point = 1 ruble, up to 50% of subtotal order value
    val maxSpendablePoints = (itemsSubtotal * 0.5).toInt().coerceAtMost(loyaltyPoints)
    val pointsSpent = if (usePointsDiscount) maxSpendablePoints else 0
    val grandTotal = (itemsSubtotal - discountCost - pointsSpent + shippingFee).coerceAtLeast(0.0)

    if (cartItems.isEmpty()) {
        // --- Visual Empty State ----
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBasket,
                        contentDescription = "Empty Basket",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Ваша корзина пуста",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Выберите в каталоге самые свежие продукты\nс супербыстрой доставкой за 30 минут!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    onClick = { viewModel.setTab(GroceryTab.CATALOG) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("go_to_catalog_button")
                ) {
                    Text(text = "Перейти в каталог", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    } else {
        // --- Full Cart Contents ---
        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 120.dp, start = 16.dp, end = 16.dp, top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Header (Title & Clear button)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ваш заказ",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(
                            onClick = { viewModel.clearCart() },
                            modifier = Modifier.testTag("clear_cart_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Clear cart",
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Очистить", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // List of cart items
                items(cartItems, key = { it.productId }) { item ->
                    CartItemRow(
                        item = item,
                        onMinusClick = { viewModel.updateCartQuantity(item.productId, -1) },
                        onPlusClick = { viewModel.updateCartQuantity(item.productId, 1) },
                        onRemoveClick = { viewModel.removeFromCart(item.productId) }
                    )
                }

                // Free Shipping Progression Alert
                item {
                    FreeShippingCard(subtotal = itemsSubtotal, limit = freeShippingLimit)
                }

                // Delivery address info card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("delivery_address_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = "Delivery",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Адрес доставки",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = deliveryAddress,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(
                                onClick = { viewModel.setTab(GroceryTab.PROFILE) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit address",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Promocode Box
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = inputtingCoupon,
                                onValueChange = { inputtingCoupon = it },
                                placeholder = { Text("Купон (напр. FRESH15)", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(10.dp)
                            )
                            Button(
                                onClick = {
                                    if (inputtingCoupon.trim().equals("FRESH15", ignoreCase = true)) {
                                        appliedDiscount = 0.15
                                        couponMessage = "Промокод FRESH15 применен! Скидка -15% 🎉"
                                    } else {
                                        couponMessage = "Неверный промокод"
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Text("Ввод", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        if (couponMessage.isNotEmpty()) {
                            Text(
                                text = couponMessage,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (appliedDiscount > 0) MaterialTheme.colorScheme.primary else Color.Red,
                                modifier = Modifier.padding(start = 14.dp, bottom = 12.dp)
                            )
                        }
                    }
                }

                // Loyalty Points Redemption Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("loyalty_spend_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🌟", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Списать баллы лояльности",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "Доступно: $loyaltyPoints Б • Списать до $maxSpendablePoints Б",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                Switch(
                                    checked = usePointsDiscount,
                                    onCheckedChange = { usePointsDiscount = it },
                                    enabled = maxSpendablePoints > 0,
                                    modifier = Modifier.testTag("loyalty_switch")
                                )
                            }
                            if (usePointsDiscount && pointsSpent > 0) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                                ) {
                                    Text(
                                        text = "Будет списано $pointsSpent баллов. Скидка составляет -$pointsSpent ₽!",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Bill calculation segment
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Товары", color = Color.Gray, fontSize = 13.sp)
                                Text("${itemsSubtotal.toInt()} ₽", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            if (appliedDiscount > 0.0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Скидка 15%", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                                    Text("-${discountCost.toInt()} ₽", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                                }
                            }
                            if (usePointsDiscount && pointsSpent > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Списание баллов", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                                    Text("-$pointsSpent ₽", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Доставка", color = Color.Gray, fontSize = 13.sp)
                                Text(
                                    text = if (shippingFee == 0.0) "Бесплатно" else "${shippingFee.toInt()} ₽",
                                    fontWeight = FontWeight.Bold,
                                    color = if (shippingFee == 0.0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp
                                )
                            }
                            Divider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Итого к оплате", fontWeight = FontWeight.Black, fontSize = 16.sp)
                                Text("${grandTotal.toInt()} ₽", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }

            // --- Floating Action Footer ---
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background.copy(alpha = 0.95f), MaterialTheme.colorScheme.background)
                        )
                    )
                    .padding(16.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Button(
                    onClick = { showCheckoutDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("checkout_order_button")
                ) {
                    Text(
                        text = "Оформить на адрес • ${grandTotal.toInt()} ₽",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Checkout Overlay Dialog
            if (showCheckoutDialog) {
                CheckoutSheet(
                    grandTotal = grandTotal,
                    userBalance = userBalance,
                    currentAddress = deliveryAddress,
                    selectedTime = selectedDeliveryTime,
                    selectedPayment = selectedPaymentMethod,
                    onTimeSelect = { selectedDeliveryTime = it },
                    onPaymentSelect = { selectedPaymentMethod = it },
                    onDismiss = { showCheckoutDialog = false },
                    onConfirmCheckout = {
                        viewModel.placeOrder(
                            address = deliveryAddress, 
                            shippingFee = shippingFee, 
                            pointsToSpend = pointsSpent, 
                            couponApplied = (appliedDiscount > 0.0)
                        )
                        showCheckoutDialog = false
                    },
                    modifier = Modifier.testTag("checkout_sheet")
                )
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartEntity,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji representation
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.emoji, fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.price.toInt()} ₽ • ${item.weight}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action controller elements (Minus, quantity, Plus)
            Row(
                modifier = Modifier
                    .width(100.dp)
                    .height(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onMinusClick, modifier = Modifier.size(30.dp)) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Less", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                }
                Text(
                    text = item.quantity.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = onPlusClick, modifier = Modifier.size(30.dp)) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "More", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun FreeShippingCard(subtotal: Double, limit: Double) {
    val progress = (subtotal / limit).toFloat().coerceIn(0f, 1f)
    val neededAmount = (limit - subtotal).coerceAtLeast(0.0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (neededAmount > 0) "🚴 Экспресс-доставка 149 ₽" else "🚴 Бесплатная доставка!",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            if (neededAmount > 0) {
                Text(
                    text = "Добавьте еще продуктов на ${neededAmount.toInt()} ₽ для бесплатной доставки",
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.LightGray.copy(alpha = 0.5f)
                )
            } else {
                Text(
                    text = "Ваш заказ доставляется бесплатно! Экономия 149 ₽.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Custom Checkout Overlay Modal Screen
@Composable
fun CheckoutSheet(
    grandTotal: Double,
    userBalance: Double,
    currentAddress: String,
    selectedTime: String,
    selectedPayment: String,
    onTimeSelect: (String) -> Unit,
    onPaymentSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirmCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val times = listOf("Прямо сейчас (30 мин)", "Сегодня, 18:00 - 20:00", "Завтра, 10:00 - 12:00")
    val payments = listOf("Банковская карта", "СБП (Система быстрых платежей)", "Наличными курьеру")
    val isBlockedBalance = userBalance < grandTotal && selectedPayment == "Банковская карта"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            ) {
                // Title & Dismiss
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Оформление заказа",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Time selector
                Text(
                    text = "Время доставки",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    times.forEach { time ->
                        val isSel = time == selectedTime
                        Card(
                            onClick = { onTimeSelect(time) },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSel,
                                    onClick = { onTimeSelect(time) },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = time,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payment selector
                Text(
                    text = "Способ оплаты",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    payments.forEach { method ->
                        val isSel = method == selectedPayment
                        Card(
                            onClick = { onPaymentSelect(method) },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSel,
                                    onClick = { onPaymentSelect(method) },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = method,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                    )
                                    if (method == "Банковская карта") {
                                        Text(
                                            text = "Баланс: ${userBalance.toInt()} ₽",
                                            fontSize = 10.sp,
                                            color = if (userBalance < grandTotal) Color.Red else Color.Gray,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Delivery warning if balance insufficient
                if (isBlockedBalance) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDE8E8))
                    ) {
                        Text(
                            text = "⚠️ Недостаточно средств на виртуальном балансе для оплаты картой. Выберите 'Наличными' или пополните баланс в профиле!",
                            color = Color.Red,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp),
                            lineHeight = 15.sp
                        )
                    }
                }

                // Confirm checkout button
                Button(
                    onClick = onConfirmCheckout,
                    enabled = !isBlockedBalance,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_delivery_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Купить за ${grandTotal.toInt()} ₽",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
