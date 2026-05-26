package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.RecipeEntity
import com.example.data.db.RecipeIngredientEntity
import com.example.data.model.Product
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    viewModel: GroceryViewModel,
    modifier: Modifier = Modifier
) {
    val recipes by viewModel.recipes.collectAsState()
    val products by viewModel.products.collectAsState()
    var selectedRecipe by remember { mutableStateOf<RecipeEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Кулинарные Рецепты",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "Выбирайте блюда и добавляйте все нужные ингредиенты прямо в корзину за один клик!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (recipes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👨‍🍳", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Нет доступных рецептов",
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }
        } else {
            if (selectedRecipe == null) {
                // Recipe List View
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(recipes, key = { it.id }) { recipe ->
                        RecipeItemCard(
                            recipe = recipe,
                            onClick = { selectedRecipe = recipe }
                        )
                    }
                }
            } else {
                // Recipe Ingredient Selection & Commerce Detail Screen
                val currentRecipe = selectedRecipe!!
                RecipeCommerceScreen(
                    recipe = currentRecipe,
                    viewModel = viewModel,
                    products = products,
                    onBackClick = { selectedRecipe = null }
                )
            }
        }
    }
}

@Composable
fun RecipeItemCard(
    recipe: RecipeEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("recipe_card_${recipe.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SleekBorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle Avatar Graphic
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SleekPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = recipe.image,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SleekOnSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F6F0)
                    ) {
                        Text(
                            text = recipe.cookTime,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (recipe.difficulty) {
                            "Легко" -> Color(0xFFE3F2FD)
                            "Средне" -> Color(0xFFFFF3E0)
                            else -> Color(0xFFFFEBEE)
                        }
                    ) {
                        Text(
                            text = recipe.difficulty,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (recipe.difficulty) {
                                "Легко" -> Color(0xFF1E88E5)
                                "Средне" -> Color(0xFFF57C00)
                                else -> Color(0xFFD32F2F)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Details",
                tint = Color.LightGray
            )
        }
    }
}

@Composable
fun RecipeCommerceScreen(
    recipe: RecipeEntity,
    viewModel: GroceryViewModel,
    products: List<Product>,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val ingredientsState = viewModel.getRecipeIngredientsFlow(recipe.id).collectAsState(initial = emptyList())
    val ingredients = ingredientsState.value

    // Track chosen ingredients
    var selections by remember(ingredients) {
        mutableStateOf(ingredients.associate { it.id to true })
    }

    // Explicitly update when ingredients arrive
    LaunchedEffect(ingredients) {
        selections = ingredients.associate { it.id to true }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Back Navigation Panel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackClick) {
                Text("← Назад к списку", color = SleekPrimary, fontWeight = FontWeight.Bold)
            }
        }

        // Hero Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SleekPrimaryContainer.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(recipe.image, fontSize = 56.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("⏳ ${recipe.cookTime}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
                        Text("🔥 Сложность: ${recipe.difficulty}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SleekTextSecondary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ингредиенты из нашего магазина:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (ingredients.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SleekPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ingredients) { ingredient ->
                    val isChecked = selections[ingredient.id] ?: false
                    // Find actual product linked
                    val linkedProduct = ingredient.linkedProductId?.let { id ->
                        products.find { it.id == id }
                    }

                    val isAvailable = linkedProduct != null && linkedProduct.isAvailable && linkedProduct.stockStatus != "OUT_OF_STOCK"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recipe_ingredient_item_${ingredient.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAvailable) Color.White else Color(0x3DF1F3F1)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isChecked && isAvailable) SleekPrimary else SleekBorderLight
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Checkbox to select ingredient
                            Checkbox(
                                checked = isChecked && isAvailable,
                                enabled = isAvailable,
                                onCheckedChange = { check ->
                                    val updated = selections.toMutableMap()
                                    updated[ingredient.id] = check
                                    selections = updated
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = SleekPrimary,
                                    uncheckedColor = Color.LightGray
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Product Emoji
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF9FBF9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = linkedProduct?.emoji ?: "📦",
                                    fontSize = 20.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ingredient.productName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAvailable) SleekOnSurface else Color.Gray,
                                    textDecoration = if (isAvailable) TextDecoration.None else TextDecoration.LineThrough
                                )
                                Text(
                                    text = "Нужно: ${ingredient.quantityNeeded}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )

                                if (linkedProduct != null && isAvailable) {
                                    Text(
                                        text = "${linkedProduct.price.toInt()} ₽ / ${linkedProduct.weight}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SleekPrimary
                                    )
                                }
                            }

                            // Dynamic Availability Tag helper
                            Box(modifier = Modifier.padding(start = 8.dp)) {
                                if (linkedProduct == null) {
                                    Text(
                                        text = "Не найдено",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Red,
                                        modifier = Modifier
                                            .background(Color(0xFFFFEBEE), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                } else if (!linkedProduct.isAvailable) {
                                    Text(
                                        text = "Снят с продажи",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.DarkGray,
                                        modifier = Modifier
                                            .background(Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                } else if (linkedProduct.stockStatus == "OUT_OF_STOCK") {
                                    Text(
                                        text = "Нет на складе",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Red,
                                        modifier = Modifier
                                            .background(Color(0xFFFFEBEE), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                } else if (linkedProduct.stockStatus == "LOW_STOCK") {
                                    Text(
                                        text = "Мало",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF57C00),
                                        modifier = Modifier
                                            .background(Color(0xFFFFF3E0), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                } else {
                                    Text(
                                        text = "В наличии",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SleekPrimary,
                                        modifier = Modifier
                                            .background(Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Checkout / Action Button Container
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                color = Color.White
            ) {
                val selectedItemsCount = ingredients.count { selections[it.id] == true && products.find { p -> p.id == it.linkedProductId }?.let { lp -> lp.isAvailable && lp.stockStatus != "OUT_OF_STOCK" } == true }

                Button(
                    onClick = {
                        val toAdd = ingredients.map { it to (selections[it.id] ?: false) }

                        viewModel.addRecipeIngredientsToCart(toAdd) { added, failed ->
                            if (failed.isNotEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Добавлено: $added ингред. Следующие товары отсутствуют: ${failed.joinToString(", ")}",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Все ингредиенты ($added шт.) успешно добавлены в корзину!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            onBackClick()
                        }
                    },
                    enabled = selectedItemsCount > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp)
                        .testTag("add_ingredients_to_cart_button")
                ) {
                    Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Добавить в Корзину ($selectedItemsCount)",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
