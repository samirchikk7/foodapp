package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailDialog(
    product: Product,
    quantityInCart: Int,
    onDismissRequest: () -> Unit,
    onAddToCart: (Int) -> Unit
) {
    var tempQuantity by remember { mutableStateOf(if (quantityInCart > 0) quantityInCart else 1) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .testTag("product_detail_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row (Close Button)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("close_detail_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Emoji and Graphic presentation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFF8F9F8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = product.emoji,
                        fontSize = 80.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Product Name, Weight and Price
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${product.price.toInt()} ₽",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Вес/Кол-во: ${product.weight}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                }

                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // Description
                Text(
                    text = "Описание товара",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Nutrition Values (Пищевая ценность) in a modern 4x1 grid
                Text(
                    text = "Пищевая ценность на 100 г",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NutritionItem(label = "Ккал", value = product.calories.toString(), modifier = Modifier.weight(1f))
                    NutritionItem(label = "Белки", value = "${product.proteins}г", modifier = Modifier.weight(1f))
                    NutritionItem(label = "Жиры", value = "${product.fats}г", modifier = Modifier.weight(1f))
                    NutritionItem(label = "Углеводы", value = "${product.carbs}г", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Row: Quantity Selector + Save Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Minus / Plus buttons box
                    Row(
                        modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (tempQuantity > 1) tempQuantity-- },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = MaterialTheme.colorScheme.primary)
                        }
                        Text(
                            text = tempQuantity.toString(),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.width(32.dp),
                            textAlign = TextAlign.Center
                        )
                        IconButton(
                            onClick = { tempQuantity++ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    // Add button
                    Button(
                        onClick = {
                            onAddToCart(tempQuantity)
                            onDismissRequest()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("apply_add_detail_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Bag", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        val totalPrice = product.price * tempQuantity
                        Text(
                            text = "Добавить • ${totalPrice.toInt()} ₽",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}
