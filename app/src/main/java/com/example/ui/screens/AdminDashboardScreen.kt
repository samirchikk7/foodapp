package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.RecipeEntity
import com.example.data.db.RecipeIngredientEntity
import com.example.data.model.Product
import com.example.ui.theme.*
import com.example.ui.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: GroceryViewModel,
    modifier: Modifier = Modifier
) {
    var adminTab by remember { mutableStateOf(0) } // 0 = Products, 1 = Recipes, 2 = Orders
    val tabs = listOf("Продукты", "Рецепты", "Заказы")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Upper Title block
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Панель Управления Store",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                // Handy badge confirming Admin Role
                Surface(
                    color = Color.Red.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "ADMIN",
                        color = Color.Red,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Text(
                text = "Корпоративный доступ к каталогу, кулинарной книге и заказам магазина.",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        TabRow(
            selectedTabIndex = adminTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = SleekPrimary
        ) {
            tabs.forEachIndexed { index, label ->
                Tab(
                    selected = adminTab == index,
                    onClick = { adminTab = index },
                    modifier = Modifier.testTag("admin_tab_$index")
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = if (adminTab == index) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (adminTab) {
                0 -> AdminProductsTab(viewModel = viewModel)
                1 -> AdminRecipesTab(viewModel = viewModel)
                2 -> AdminOrdersTab(viewModel = viewModel)
            }
        }
    }
}

// ==========================================
// 1. PRODUCTS MANAGEMENT TAB
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsTab(viewModel: GroceryViewModel) {
    val products by viewModel.products.collectAsState()
    val context = LocalContext.current

    var selectedProductForEdit by remember { mutableStateOf<Product?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Товары в базе (${products.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Создать товар", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedProductForEdit = product },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, SleekBorderLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SleekPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(product.emoji, fontSize = 28.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.name, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = SleekOnSurface)
                                Text("${product.category} • ${product.weight}", fontSize = 11.sp, color = Color.Gray)

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text("${product.price.toInt()} ₽", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
                                    if (product.oldPrice != null) {
                                        Text("${product.oldPrice.toInt()} ₽", fontSize = 11.sp, color = Color.Gray, style = MaterialTheme.typography.bodySmall.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough))
                                    }

                                    // Availability / Stock labels
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when {
                                            !product.isAvailable -> Color.LightGray.copy(alpha = 0.3f)
                                            product.stockStatus == "OUT_OF_STOCK" -> Color.Red.copy(alpha = 0.1f)
                                            product.stockStatus == "LOW_STOCK" -> Color(0xFFFFF3E0)
                                            else -> Color(0xFFE8F5E9)
                                        }
                                    ) {
                                        Text(
                                            text = when {
                                                !product.isAvailable -> "Снят с продажи"
                                                product.stockStatus == "OUT_OF_STOCK" -> "Нет"
                                                product.stockStatus == "LOW_STOCK" -> "Мало"
                                                else -> "Есть"
                                            },
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                !product.isAvailable -> Color.DarkGray
                                                product.stockStatus == "OUT_OF_STOCK" -> Color.Red
                                                product.stockStatus == "LOW_STOCK" -> Color(0xFFE65100)
                                                else -> SleekPrimary
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // Badges checklist render
                                if (product.badges.isNotEmpty()) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        product.badges.take(2).forEach { badge ->
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = SleekPrimary.copy(alpha = 0.08f),
                                                border = BorderStroke(0.5.dp, SleekPrimary.copy(alpha = 0.4f))
                                            ) {
                                                Text(
                                                    text = badge,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = SleekPrimary,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Row {
                                IconButton(onClick = { selectedProductForEdit = product }) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = SleekPrimary)
                                }
                                IconButton(onClick = {
                                    viewModel.deleteProduct(product.id)
                                    Toast.makeText(context, "Товар удален", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            ProductManagementDialog(
                product = null,
                onDismiss = { showAddDialog = false },
                onSave = { p ->
                    viewModel.saveProduct(p)
                    showAddDialog = false
                    Toast.makeText(context, "Товар добавлен", Toast.LENGTH_SHORT).show()
                }
            )
        }

        if (selectedProductForEdit != null) {
            ProductManagementDialog(
                product = selectedProductForEdit,
                onDismiss = { selectedProductForEdit = null },
                onSave = { p ->
                    viewModel.saveProduct(p)
                    selectedProductForEdit = null
                    Toast.makeText(context, "Товар обновлен", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductManagementDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    val isEdit = product != null

    var name by remember { mutableStateOf(product?.name ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var category by remember { mutableStateOf(product?.category ?: "Овощи и Фрукты") }
    var priceStr by remember { mutableStateOf(product?.price?.toInt()?.toString() ?: "") }
    var oldPriceStr by remember { mutableStateOf(product?.oldPrice?.toInt()?.toString() ?: "") }
    var weight by remember { mutableStateOf(product?.weight ?: "450 г") }
    var emoji by remember { mutableStateOf(product?.emoji ?: "🍎") }
    var caloriesStr by remember { mutableStateOf(product?.calories?.toString() ?: "50") }
    var proteinsStr by remember { mutableStateOf(product?.proteins?.toString() ?: "1.0") }
    var fatsStr by remember { mutableStateOf(product?.fats?.toString() ?: "0.2") }
    var carbsStr by remember { mutableStateOf(product?.carbs?.toString() ?: "10.0") }
    
    var stockStatus by remember { mutableStateOf(product?.stockStatus ?: "IN_STOCK") }
    var isAvailable by remember { mutableStateOf(product?.isAvailable ?: true) }

    // Badges: List of badges (Allowed types: PREMIUM, BEST_PRICE, NEW, TOP_SELLER, LOCAL, ORGANIC)
    val allowedBadges = listOf("PREMIUM", "BEST_PRICE", "NEW", "TOP_SELLER", "LOCAL", "ORGANIC")
    var selectedBadges by remember { mutableStateOf(product?.badges?.toSet() ?: emptySet()) }

    var errorMsg by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEdit) "Редактировать товар" else "Создать новый товар", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMsg.isNotBlank()) {
                    Text(errorMsg, color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Название товара") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = emoji, onValueChange = { emoji = it }, label = { Text("Emoji-иконка (e.g. 🥦)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Описание") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
                
                // Category dropdown mock / string
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Категория (например: Овощи и Фрукты, Напитки)") }, modifier = Modifier.fillMaxWidth())

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("Цена (₽)") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = oldPriceStr, onValueChange = { oldPriceStr = it }, label = { Text("Старая цена") }, modifier = Modifier.weight(1f))
                }

                OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Вес / Кол-во (e.g. 500 г, 10 шт)") }, modifier = Modifier.fillMaxWidth())

                // Stock status selection
                Text("Наличие на складе:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("IN_STOCK" to "Есть", "LOW_STOCK" to "Мало", "OUT_OF_STOCK" to "Нет").forEach { (status, label) ->
                        val selected = stockStatus == status
                        FilterChip(
                            selected = selected,
                            onClick = { stockStatus = status },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }

                // Available Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Доступен для продажи:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Switch(checked = isAvailable, onCheckedChange = { isAvailable = it }, colors = SwitchDefaults.colors(checkedThumbColor = SleekPrimary, checkedTrackColor = SleekPrimaryContainer))
                }

                // Badges selector (limiting selection to maximum 2 badges per product constraint)
                Text("Ярлыки (Баджи) (Максимум 2):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allowedBadges.forEach { badge ->
                        val isChecked = selectedBadges.contains(badge)
                        FilterChip(
                            selected = isChecked,
                            onClick = {
                                if (isChecked) {
                                    selectedBadges = selectedBadges - badge
                                } else {
                                    if (selectedBadges.size < 2) {
                                        selectedBadges = selectedBadges + badge
                                    } else {
                                        // Max 2 constraint rule
                                    }
                                }
                            },
                            label = { Text(badge, fontSize = 10.sp) }
                        )
                    }
                }

                // Nutritions
                Text("Пищевая ценность:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(value = caloriesStr, onValueChange = { caloriesStr = it }, label = { Text("Ккал") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = proteinsStr, onValueChange = { proteinsStr = it }, label = { Text("Белки") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = fatsStr, onValueChange = { fatsStr = it }, label = { Text("Жиры") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = carbsStr, onValueChange = { carbsStr = it }, label = { Text("Угл") }, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pDouble = priceStr.toDoubleOrNull()
                    if (name.isBlank() || emoji.isBlank() || pDouble == null) {
                        errorMsg = "Пожалуйста замутите валидные название, emoji и цену."
                        return@Button
                    }
                    val finalProduct = Product(
                        id = product?.id ?: "product_${System.currentTimeMillis()}",
                        name = name,
                        category = category,
                        price = pDouble,
                        weight = weight,
                        emoji = emoji,
                        description = description,
                        calories = caloriesStr.toIntOrNull() ?: 0,
                        proteins = proteinsStr.toDoubleOrNull() ?: 0.0,
                        fats = fatsStr.toDoubleOrNull() ?: 0.0,
                        carbs = carbsStr.toDoubleOrNull() ?: 0.0,
                        oldPrice = oldPriceStr.toDoubleOrNull(),
                        stockStatus = stockStatus,
                        isAvailable = isAvailable,
                        badges = selectedBadges.toList()
                    )
                    onSave(finalProduct)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary)
            ) {
                Text("Сохранить", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = Color.Gray)
            }
        }
    )
}


// ==========================================
// 2. RECIPE MANAGEMENT TAB
// ==========================================
@Composable
fun AdminRecipesTab(viewModel: GroceryViewModel) {
    val recipes by viewModel.recipes.collectAsState()
    val products by viewModel.products.collectAsState()
    val context = LocalContext.current

    var showEditDialogForRecipe by remember { mutableStateOf<RecipeEntity?>(null) }
    var showRecipeCreateDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Кулинарные рецепты (${recipes.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Button(
                onClick = { showRecipeCreateDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary)
            ) {
                Icon(imageVector = Icons.Default.RestaurantMenu, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Добавить рецепт", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(recipes, key = { it.id }) { recipe ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, SleekBorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(recipe.image, fontSize = 36.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(recipe.title, fontSize = 14.sp, fontWeight = FontWeight.Black)
                            Text("${recipe.cookTime} • ${recipe.difficulty}", fontSize = 12.sp, color = Color.Gray)
                        }
                        IconButton(onClick = { showEditDialogForRecipe = recipe }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = SleekPrimary)
                        }
                        IconButton(onClick = {
                            viewModel.deleteRecipe(recipe.id)
                            Toast.makeText(context, "Рецепт удален", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }

    if (showRecipeCreateDialog) {
        RecipeManagementDialog(
            recipe = null,
            products = products,
            viewModel = viewModel,
            onDismiss = { showRecipeCreateDialog = false },
            onSave = { r, ingredients ->
                viewModel.saveRecipe(r, ingredients)
                showRecipeCreateDialog = false
                Toast.makeText(context, "Рецепт создан", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showEditDialogForRecipe != null) {
        RecipeManagementDialog(
            recipe = showEditDialogForRecipe,
            products = products,
            viewModel = viewModel,
            onDismiss = { showEditDialogForRecipe = null },
            onSave = { r, ingredients ->
                viewModel.saveRecipe(r, ingredients)
                showEditDialogForRecipe = null
                Toast.makeText(context, "Рецепт изменен", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeManagementDialog(
    recipe: RecipeEntity?,
    products: List<Product>,
    viewModel: GroceryViewModel,
    onDismiss: () -> Unit,
    onSave: (RecipeEntity, List<RecipeIngredientEntity>) -> Unit
) {
    val isEdit = recipe != null

    var title by remember { mutableStateOf(recipe?.title ?: "") }
    var image by remember { mutableStateOf(recipe?.image ?: "🍲") }
    var cookTime by remember { mutableStateOf(recipe?.cookTime ?: "20 мин") }
    var difficulty by remember { mutableStateOf(recipe?.difficulty ?: "Легко") }

    // Ingredients composition logic
    var rawIngredientsList = remember { mutableStateListOf<RecipeIngredientEntity>() }

    // Load initial ingredients only on edit
    val databaseIngredientsState = if (recipe != null) {
        viewModel.getRecipeIngredientsFlow(recipe.id).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }

    LaunchedEffect(databaseIngredientsState.value) {
        if (isEdit && rawIngredientsList.isEmpty() && databaseIngredientsState.value.isNotEmpty()) {
            rawIngredientsList.addAll(databaseIngredientsState.value)
        }
    }

    // Input fields for current new ingredient row additions
    var ingredientNameInput by remember { mutableStateOf("") }
    var ingredientQtyInput by remember { mutableStateOf("") }
    var selectedLinkedProductId by remember { mutableStateOf(products.firstOrNull()?.id ?: "") }

    var errorMsg by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEdit) "Редактировать рецепт" else "Добавить рецепт", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMsg.isNotBlank()) {
                    Text(errorMsg, color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Название рецепта") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = image, onValueChange = { image = it }, label = { Text("Emoji-Иконка блюда") }, modifier = Modifier.fillMaxWidth())

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = cookTime, onValueChange = { cookTime = it }, label = { Text("Время готовки") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = difficulty, onValueChange = { difficulty = it }, label = { Text("Сложность (Легко/Средне/Сложно)") }, modifier = Modifier.weight(1f))
                }

                Divider(color = SleekBorderLight)

                Text("Состав закупки (связь с товарами магазина):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)

                // Current Ingredients List
                rawIngredientsList.forEach { ing ->
                    val linkedPName = products.find { it.id == ing.linkedProductId }?.name ?: "Неизвестно"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F6F0), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${ing.productName} (${ing.quantityNeeded})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Сеть связи: $linkedPName", fontSize = 10.sp, color = Color.Gray)
                        }
                        IconButton(
                            onClick = { rawIngredientsList.remove(ing) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Delete ingredient", tint = Color.Red, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Add Ingredient Row Component Form
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE8F5E9).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Text("Связать товар из каталога:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    
                    // Simple select product row (using dropdown or list mockup for convenience)
                    var expandedProductsMenu by remember { mutableStateOf(false) }
                    val currentSelectedProductObj = products.find { it.id == selectedLinkedProductId }
                    
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Button(
                            onClick = { expandedProductsMenu = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = SleekOnSurface),
                            border = BorderStroke(1.dp, SleekBorderMedium),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentSelectedProductObj?.let { "${it.emoji} ${it.name}" } ?: "Выбрать товар...",
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                        DropdownMenu(
                            expanded = expandedProductsMenu,
                            onDismissRequest = { expandedProductsMenu = false },
                            modifier = Modifier.fillMaxWidth(0.8f).heightIn(max = 200.dp)
                        ) {
                            products.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text("${p.emoji} ${p.name} (${p.price.toInt()}₽)", fontSize = 12.sp) },
                                    onClick = {
                                        selectedLinkedProductId = p.id
                                        expandedProductsMenu = false
                                        // Auto fill text input for convenience
                                        if (ingredientNameInput.isBlank()) {
                                            ingredientNameInput = p.name
                                        }
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = ingredientNameInput,
                        onValueChange = { ingredientNameInput = it },
                        label = { Text("Отображаемое имя ингредиента", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )

                    OutlinedTextField(
                        value = ingredientQtyInput,
                        onValueChange = { ingredientQtyInput = it },
                        label = { Text("Количество (например, 250 г)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            if (ingredientNameInput.isBlank() || ingredientQtyInput.isBlank()) {
                                return@Button
                            }
                            val newIng = RecipeIngredientEntity(
                                id = "ing_${System.currentTimeMillis()}_${(0..1000).random()}",
                                recipeId = recipe?.id ?: "temp_id",
                                productName = ingredientNameInput,
                                linkedProductId = selectedLinkedProductId,
                                quantityNeeded = ingredientQtyInput
                            )
                            rawIngredientsList.add(newIng)
                            // Clear row state inputs
                            ingredientNameInput = ""
                            ingredientQtyInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                        modifier = Modifier.align(Alignment.End),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+ Добавить в список", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank() || rawIngredientsList.isEmpty()) {
                        errorMsg = "Пожалуйста заполните название и добавьте хотя бы 1 продукт."
                        return@Button
                    }
                    val finalRecipeId = recipe?.id ?: "recipe_${System.currentTimeMillis()}"
                    val recipeObj = RecipeEntity(
                        id = finalRecipeId,
                        title = title,
                        image = image,
                        cookTime = cookTime,
                        difficulty = difficulty
                    )
                    // Bind temp components ids straight to proper finalized Recipe ID
                    val finalizedIngredients = rawIngredientsList.map {
                        it.copy(recipeId = finalRecipeId)
                    }
                    onSave(recipeObj, finalizedIngredients)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary)
            ) {
                Text("Сохранить флот", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = Color.Gray)
            }
        }
    )
}

// ==========================================
// 3. ORDERS ADMINISTRATIVE MANAGEMENT TAB
// ==========================================
@Composable
fun AdminOrdersTab(viewModel: GroceryViewModel) {
    val orders by viewModel.orders.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Активные и прошлые заказы (${orders.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(16.dp)
        )

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("Нет размещенных заказов", color = Color.Gray, fontWeight = FontWeight.Bold)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(orders, key = { it.id }) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, SleekBorderLight)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Заказ #${order.id}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )

                                // Unified dynamic status color bubble helper
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (order.status) {
                                        "PENDING" -> Color(0xFFECEFF1)
                                        "CONFIRMED" -> Color(0xFFE3F2FD)
                                        "PREPARING" -> Color(0xFFFFF3E0)
                                        "DELIVERING" -> Color(0xFFF3E5F5)
                                        "COMPLETED" -> Color(0xFFE8F5E9)
                                        else -> Color(0xFFFFEBEE) // CANCELLED
                                    }
                                ) {
                                    Text(
                                        text = order.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = when (order.status) {
                                            "PENDING" -> Color(0xFF37474F)
                                            "CONFIRMED" -> Color(0xFF1E88E5)
                                            "PREPARING" -> Color(0xFFF57C00)
                                            "DELIVERING" -> Color(0xFF8E24AA)
                                            "COMPLETED" -> SleekPrimary
                                            else -> Color(0xFFD32F2F)
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(text = "Итоговая цена: ${order.totalPrice.toInt()} ₽", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
                            Text(text = "Адрес: ${order.address}", fontSize = 12.sp, color = SleekOnSurface)
                            Text(text = "Состав: ${order.itemsSummary}", fontSize = 11.sp, color = Color.Gray)

                            Spacer(modifier = Modifier.height(12.dp))

                            Divider(color = SleekBorderLight)

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Сменить Статус (State Machine):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                            // Quick actions to cycle status through PENDING, CONFIRMED, PREPARING, DELIVERING, COMPLETED, CANCELLED
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp)
                            ) {
                                val availableStatuses = listOf("CONFIRMED", "PREPARING", "DELIVERING", "COMPLETED", "CANCELLED")
                                
                                availableStatuses.forEach { statusText ->
                                    val isCurrent = order.status == statusText
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isCurrent) SleekPrimary else Color(0xFFF1F6F0))
                                            .clickable {
                                                viewModel.updateOrderStatus(order.id, statusText)
                                                Toast.makeText(context, "Статус заказа #${order.id} изменен на $statusText", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = when(statusText) {
                                                "CONFIRMED" -> "Принять"
                                                "PREPARING" -> "Готовить"
                                                "DELIVERING" -> "В путь"
                                                "COMPLETED" -> "Выполнен"
                                                else -> "Отмена"
                                            },
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrent) Color.White else SleekPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
