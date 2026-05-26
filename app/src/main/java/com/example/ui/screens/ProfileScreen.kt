package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Room
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: GroceryViewModel,
    modifier: Modifier = Modifier
) {
    val deliveryAddress by viewModel.deliveryAddress.collectAsState()
    val userBalance by viewModel.userBalance.collectAsState()
    val loyaltyPoints by viewModel.loyaltyPoints.collectAsState()
    val authState by viewModel.authState.collectAsState()

    val userName = when (val state = authState) {
        is com.example.ui.viewmodel.AuthState.Authenticated -> state.name
        else -> "Самир Мусабалиев"
    }
    val userEmail = when (val state = authState) {
        is com.example.ui.viewmodel.AuthState.Authenticated -> state.email
        else -> "samir.musabaliev@example.com"
    }
    val role = when (val state = authState) {
        is com.example.ui.viewmodel.AuthState.Authenticated -> state.role
        else -> "customer"
    }
    val userInitials = userName.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercase() }
        .joinToString("")

    var addressInput by remember { mutableStateOf(deliveryAddress) }
    var showAddressSavedSnackbar by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Profile Avatar Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("profile_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (userInitials.isEmpty()) "СМ" else userInitials,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (role == "admin") Color.Red.copy(alpha = 0.1f) else Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = if (role == "admin") "Администратор 🛠️" else "Покупатель 🛒",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (role == "admin") Color.Red else Color(0xFF2E7D32),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Sign Out Action Button
                IconButton(
                    onClick = { viewModel.signOut() },
                    modifier = Modifier.testTag("sign_out_button")
                ) {
                    Text(text = "🚪", fontSize = 22.sp)
                }
            }
        }

        // Sandbox Role Switcher Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("sandbox_role_switcher_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(text = "🛡️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Режим тестирования",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Коснуться для входа под STORE_ADMIN",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
                Switch(
                    checked = role == "admin",
                    onCheckedChange = {
                        viewModel.toggleUserRole()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Red,
                        checkedTrackColor = Color.Red.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("role_switcher_switch")
                )
            }
        }

        // 2. Interactive Wallet (Fictitious account balance for ordering testing)
        Card(
            modifier = Modifier.fillMaxWidth().testTag("balance_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(
                    text = "Виртуальный баланс",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${userBalance.toInt()} ₽",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Button(
                        onClick = {
                            viewModel.topUpBalance()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp).testTag("top_up_button")
                    ) {
                        Icon(imageVector = Icons.Default.AddCard, contentDescription = "Add card", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Пополнить (+500 ₽)", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
                
                Text(
                    text = "Используется для симуляции оплаты покупок в приложении",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 2b. Beautiful Loyalty Points Status Dashboard card
        val currentPoints = loyaltyPoints
        val tierName = when {
            currentPoints >= 700 -> "Золотой статус (Кэшбэк 20% 👑)"
            currentPoints >= 300 -> "Серебряный статус (Кэшбэк 15% 🥈)"
            else -> "Бронзовый статус (Кэшбэк 10% 🥉)"
        }
        val pointsToNextTier = when {
            currentPoints >= 700 -> 0
            currentPoints >= 300 -> 700 - currentPoints
            else -> 300 - currentPoints
        }
        val tierProgress = when {
            currentPoints >= 700 -> 1.0f
            currentPoints >= 300 -> (currentPoints - 300).toFloat() / 400f
            else -> currentPoints.toFloat() / 300f
        }
        val nextTierLabel = when {
            currentPoints >= 700 -> "Достигнут максимальный уровень лояльности 🎉"
            currentPoints >= 300 -> "До Золотого уровня осталось ${pointsToNextTier} Б"
            else -> "До Серебряного уровня осталось ${pointsToNextTier} Б"
        }

        Card(
            modifier = Modifier.fillMaxWidth().testTag("loyalty_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🌟", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Программа лояльности",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "1 Б = 1 ₽",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Накоплено баллов",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$currentPoints Б",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = tierName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = nextTierLabel,
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    LinearProgressIndicator(
                        progress = tierProgress,
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.LightGray.copy(alpha = 0.4f)
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("• Кэшбэк до 20% возвращается на счет баллами", fontSize = 10.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                    Text("• Оплачивайте баллами до 50% от суммы заказа в корзине", fontSize = 10.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                }
            }
        }

        // 3. Address Customizer Text field
        Card(
            modifier = Modifier.fillMaxWidth().testTag("address_change_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Изменить адрес доставки",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = addressInput,
                    onValueChange = { addressInput = it },
                    placeholder = { Text("Укажите ваш домашний адрес") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Room, contentDescription = "Address", tint = MaterialTheme.colorScheme.primary) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        viewModel.setDeliveryAddress(addressInput)
                        focusManager.clearFocus()
                        showAddressSavedSnackbar = true
                    }),
                    modifier = Modifier.fillMaxWidth().height(56.dp).testTag("address_input_profile"),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        viewModel.setDeliveryAddress(addressInput)
                        focusManager.clearFocus()
                        showAddressSavedSnackbar = true
                    },
                    modifier = Modifier.fillMaxWidth().height(40.dp).testTag("save_address_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Сохранить адрес", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                if (showAddressSavedSnackbar) {
                    Text(
                        text = "✓ Адрес доставки успешно сохранен!",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                    )
                }
            }
        }

        // 4. Delivery Quality Info cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Информация о доставке",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = "Shield", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(
                        text = "100% Гарантия свежести: отбираем только спелые овощи и фрукты, следим за сроками годности.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = Color.DarkGray
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Clock", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(
                        text = "Экспресс-режим за 30 минут: собираем корзину за 5 минут, курьер на электробайке отвозит заказ.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = Color.DarkGray
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContactSupport, contentDescription = "Support", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(
                        text = "Горячая линия круглосуточно: 8 (800) 555-35-35 • help@freshgrocery.ru (Бесплатные звонки)",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}
