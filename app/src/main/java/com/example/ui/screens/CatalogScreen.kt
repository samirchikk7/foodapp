package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PREDEFINED_PRODUCTS
import com.example.data.model.Product
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: GroceryViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val recommendedProducts by viewModel.recommendedProducts.collectAsState()

    val categories = listOf("Все", "Овощи и Фрукты", "Молоко и Яйца", "Хлеб и Выпечка", "Мясо и Птица", "Зелень", "Напитки")

    // Filter products based on category and search query
    val filteredProducts = PREDEFINED_PRODUCTS.filter { product ->
        val matchesCategory = selectedCategory == "Все" || product.category == selectedCategory
        val matchesSearch = product.name.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. Sleek Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Поиск продуктов, зелени...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SleekPrimary,
                    unfocusedBorderColor = SleekBorderMedium,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedLabelColor = SleekPrimary,
                    unfocusedLabelColor = Color.Gray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input")
                    .height(54.dp),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )
        }

        // 2. Categories Scroll
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSelectedCategory(category) },
                    label = { 
                        Text(
                            text = category,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        ) 
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        selectedBorderColor = Color.Transparent,
                        selectedBorderWidth = 0.dp,
                        enabled = true,
                        selected = isSelected
                    ),
                    modifier = Modifier.testTag("category_chip_${category}")
                )
            }
        }

        // 3. Grid Catalog list of Products
        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🔍",
                        fontSize = 54.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "Ничего не найдено",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Попробуйте изменить запрос или категорию",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("products_grid")
            ) {
                // Promotional banner at the very top of the grid spanning full width
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    PromoBannerCard()
                }

                // Beautiful "Рекомендуем для вас" analytical section matching user requirements
                if (searchQuery.isEmpty() && selectedCategory == "Все" && recommendedProducts.isNotEmpty()) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = "Рекомендуем для вас",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            val hasPastOrders = viewModel.orders.collectAsState().value.isNotEmpty()
                            Text(
                                text = if (hasPastOrders) "На основе ваших прошлых покупок" else "Популярно в этом месяце",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = SleekTextSecondary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            LazyRow(
                                contentPadding = PaddingValues(end = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(recommendedProducts) { product ->
                                    val cartItem = cartItems.find { it.productId == product.id }
                                    val currentQuantity = cartItem?.quantity ?: 0
                                    RecommendedProductCard(
                                        product = product,
                                        quantityInCart = currentQuantity,
                                        onAddClick = { viewModel.addToCart(product, 1) },
                                        onMinusClick = { viewModel.updateCartQuantity(product.id, -1) },
                                        onPlusClick = { viewModel.updateCartQuantity(product.id, 1) },
                                        onProductDetailClick = { viewModel.selectProduct(product) }
                                    )
                                }
                            }
                        }
                    }
                }

                items(filteredProducts, key = { it.id }) { product ->
                    val cartItem = cartItems.find { it.productId == product.id }
                    val currentQuantity = cartItem?.quantity ?: 0

                    ProductCard(
                        product = product,
                        quantityInCart = currentQuantity,
                        onAddClick = { viewModel.addToCart(product, 1) },
                        onMinusClick = { viewModel.updateCartQuantity(product.id, -1) },
                        onPlusClick = { viewModel.updateCartQuantity(product.id, 1) },
                        onProductDetailClick = { viewModel.selectProduct(product) }
                    )
                }
            }
        }
    }
}

@Composable
fun PromoBannerCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SleekBannerContainer)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            // Background circular blob overlay matching the HTML mockup
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp, y = 10.dp)
                    .clip(CircleShape)
                    .background(SleekBannerContainerDarkBg.copy(alpha = 0.5f))
            )

            // Content Left / Right
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Скидка 25%\nна первый заказ!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SleekOnPrimaryContainer,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(100),
                        color = SleekPrimary,
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = "Заказать сейчас",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Text(
                    text = "🥦",
                    fontSize = 56.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    quantityInCart: Int,
    onAddClick: () -> Unit,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit,
    onProductDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductDetailClick() }
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Emoji Visual Presentation + Rating Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF8F9F8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.emoji,
                    fontSize = 48.sp,
                    textAlign = TextAlign.Center
                )

                // Rating Badge
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = SleekPrimary,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = product.rating.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Product Name
            Text(
                text = product.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = SleekOnSurface,
                modifier = Modifier.fillMaxWidth()
            )

            // Dynamic Weight label
            Text(
                text = product.weight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = SleekTextSecondary,
                modifier = Modifier.padding(top = 1.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Pricing & Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${product.price.toInt()} ₽",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = SleekPrimary
                )

                // Add or counter action container
                Box(
                    modifier = Modifier.size(width = 80.dp, height = 34.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (quantityInCart == 0) {
                        Button(
                            onClick = onAddClick,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("add_button_${product.id}"),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SleekPrimaryContainer,
                                contentColor = SleekOnPrimaryContainer
                            )
                        ) {
                            Text(
                                text = "+",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SleekPrimary),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = onMinusClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .testTag("minus_button_${product.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Minus",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }

                            Text(
                                text = quantityInCart.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )

                            IconButton(
                                onClick = onPlusClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .testTag("plus_button_${product.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Plus",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendedProductCard(
    product: Product,
    quantityInCart: Int,
    onAddClick: () -> Unit,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit,
    onProductDetailClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable { onProductDetailClick() }
            .testTag("recommended_product_card_${product.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekBorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8F9F8)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = product.emoji, fontSize = 36.sp)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "", tint = SleekPrimary, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = product.rating.toString(), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = SleekOnSurface
            )
            Text(
                text = product.weight,
                fontSize = 10.sp,
                color = SleekTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${product.price.toInt()} ₽",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = SleekPrimary
                )
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 26.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (quantityInCart > 0) SleekPrimary else SleekPrimaryContainer)
                        .clickable { if (quantityInCart == 0) onAddClick() else onProductDetailClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (quantityInCart > 0) {
                        Text(text = quantityInCart.toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    } else {
                        Text(text = "+", color = SleekOnPrimaryContainer, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
