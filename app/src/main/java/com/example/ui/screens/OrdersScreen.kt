package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.OrderEntity
import com.example.ui.viewmodel.GroceryTab
import com.example.ui.viewmodel.GroceryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrdersScreen(
    viewModel: GroceryViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()

    if (orders.isEmpty()) {
        // --- Empty State for Orders ---
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
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = "No orders",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Заказов пока нет",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Здесь будет отображаться статус и история ваших\nдоставок. Сделайте первый заказ прямо сейчас!",
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
                    modifier = Modifier.testTag("orders_go_to_catalog")
                ) {
                    Text(text = "Выбрать продукты", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    } else {
        // --- Orders list разделенный на активные и доставленные ---
        val activeOrders = orders.filter { !it.status.contains("Доставлен") }
        val completedOrders = orders.filter { it.status.contains("Доставлен") }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier.fillMaxSize()
        ) {
            // Title
            item {
                Text(
                    text = "Мои заказы",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Section: Active Deliveries
            if (activeOrders.isNotEmpty()) {
                item {
                    Text(
                        text = "В процессе доставки",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                items(activeOrders, key = { it.id }) { order ->
                    ActiveOrderCard(
                        order = order,
                        onCancelClick = { viewModel.cancelOrder(order.id) }
                    )
                }
            }

            // Section: Order Archives
            if (completedOrders.isNotEmpty()) {
                item {
                    Text(
                        text = "История заказов",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(completedOrders, key = { it.id }) { order ->
                    CompletedOrderCard(
                        order = order,
                        onRepeatClick = {
                            // Quick simulation: adds some dummy values and shifts to Cart
                            viewModel.setTab(GroceryTab.CART)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveOrderCard(
    order: OrderEntity,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Progress calculation based on current status
    val currentStep = when (order.status) {
        "Принят" -> 1
        "Готовится" -> 2
        "В пути" -> 3
        "Доставлен" -> 4
        else -> 1
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("active_order_card_${order.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            // Order meta & ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Заказ №1082${order.id}",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    Text(
                        text = formatTime(order.timestamp),
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text(
                        text = order.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Beautiful dynamic ETA (Estimated Time of Arrival) Tracker matching user tracking requirements
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "ETA Tracker",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Примерное время доставки",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Около ~${formatEstimatedTime(order.estimatedDeliveryTime)}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Progress Bar / Segment Indicators
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(4) { idx ->
                    val isCompleted = currentStep > idx
                    val color = if (isCompleted) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step Details (Verbose status lists)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TrackerStep(label = "Заказ принят в систему", active = currentStep >= 1, done = currentStep > 1)
                TrackerStep(label = "Сборщики бережно упаковывают продукты", active = currentStep >= 2, done = currentStep > 2)
                TrackerStep(label = "Курьер доставляет заказ на электровелосипеде", active = currentStep >= 3, done = currentStep > 3)
                TrackerStep(label = "Передача продуктов бесконтактно у вашей двери", active = currentStep >= 4, done = currentStep > 4)
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

            // Items & Billing Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Состав заказа",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = order.itemsSummary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                Text(
                    text = "${order.totalPrice.toInt()} ₽",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cancel action button
            if (currentStep < 3) {
                OutlinedButton(
                    onClick = onCancelClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.Red.copy(alpha = 0.5f))),
                    modifier = Modifier.fillMaxWidth().height(38.dp).testTag("cancel_order_button")
                ) {
                    Icon(imageVector = Icons.Default.Cancel, contentDescription = "Cancel", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Отменить заказ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CompletedOrderCard(
    order: OrderEntity,
    onRepeatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Заказ №1082${order.id}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = formatTime(order.timestamp),
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Доставлен",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = order.itemsSummary,
                fontSize = 12.sp,
                color = Color.DarkGray,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.totalPrice.toInt()} ₽",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Button(
                    onClick = onRepeatClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.Replay, contentDescription = "Repeat", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Повторить", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TrackerStep(
    label: String,
    active: Boolean,
    done: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val color = when {
            done -> MaterialTheme.colorScheme.primary
            active -> MaterialTheme.colorScheme.primary
            else -> Color.DarkGray.copy(alpha = 0.3f)
        }
        val textWeight = if (active && !done) FontWeight.Bold else FontWeight.Medium
        val textColor = if (active) MaterialTheme.colorScheme.onSurface else Color.LightGray

        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = textWeight,
            color = textColor
        )
    }
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMMM, HH:mm", Locale("ru"))
    return sdf.format(Date(timestamp))
}

fun formatEstimatedTime(timestamp: Long): String {
    if (timestamp == 0L) return "30 минут"
    val sdf = SimpleDateFormat("HH:mm", Locale("ru"))
    return sdf.format(Date(timestamp))
}
