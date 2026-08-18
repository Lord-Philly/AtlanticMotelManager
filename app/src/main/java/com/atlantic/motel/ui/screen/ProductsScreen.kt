package com.atlantic.motel.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atlantic.motel.billing.BillingEngine
import com.atlantic.motel.data.model.Product
import com.atlantic.motel.data.model.ProductCategory
import com.atlantic.motel.ui.theme.*
import com.atlantic.motel.viewmodel.ProductViewModel

private fun ProductCategory.icon(): ImageVector = when (this) {
    ProductCategory.CERVEJA -> Icons.Default.SportsBar
    ProductCategory.DRINK -> Icons.Default.LocalBar
    ProductCategory.COMBO -> Icons.Default.Liquor
    ProductCategory.GERAL -> Icons.Default.Inventory
}

private fun ProductCategory.label(): String = when (this) {
    ProductCategory.CERVEJA -> "Cerveja"
    ProductCategory.DRINK -> "Drink"
    ProductCategory.COMBO -> "Combo"
    ProductCategory.GERAL -> "Geral"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onBack: () -> Unit,
    viewModel: ProductViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }

    val filteredProducts = if (selectedCategory != null) {
        products.filter { it.category == selectedCategory }
    } else {
        products
    }

    Scaffold(
        containerColor = DeepBlack,
        topBar = {
            TopAppBar(
                title = { Text("Produtos", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontFamily = CormorantGaramondFamily) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceBlack,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextSecondary
                ),
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Adicionar", tint = DeepCrimson)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("Todos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepCrimson.copy(alpha = 0.2f),
                        selectedLabelColor = DeepCrimson
                    )
                )
                ProductCategory.entries.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.label()) },
                        leadingIcon = {
                            Icon(cat.icon(), null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepCrimson.copy(alpha = 0.2f),
                            selectedLabelColor = DeepCrimson
                        )
                    )
                }
            }

            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Nenhum produto cadastrado.\nToque em + para adicionar.",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = TextDisabled
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredProducts) { product ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SurfaceBlack),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    product.category.icon(),
                                    null,
                                    modifier = Modifier.size(20.dp),
                                    tint = DeepCrimson
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = TextPrimary)
                                    Text(
                                        BillingEngine.formatCurrency(product.priceInCents),
                                        fontSize = 14.sp,
                                        fontFamily = JetBrainsMonoFamily,
                                        color = Champagne
                                    )
                                }
                                Row {
                                    IconButton(onClick = { editingProduct = product }) {
                                        Icon(Icons.Default.Edit, "Editar", tint = TextSecondary)
                                    }
                                    IconButton(onClick = { viewModel.deleteProduct(product) }) {
                                        Icon(Icons.Default.Delete, "Excluir", tint = MetallicRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ProductDialog(
            title = "Novo Produto",
            onSave = { name, priceCents, category ->
                viewModel.addProduct(name, priceCents, category)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    editingProduct?.let { product ->
        ProductDialog(
            title = "Editar Produto",
            initialName = product.name,
            initialPrice = product.priceInCents,
            initialCategory = product.category,
            onSave = { name, priceCents, category ->
                viewModel.updateProduct(product.copy(name = name, priceInCents = priceCents, category = category))
                editingProduct = null
            },
            onDismiss = { editingProduct = null }
        )
    }
}

@Composable
fun ProductDialog(
    title: String,
    initialName: String = "",
    initialPrice: Long = 0,
    initialCategory: ProductCategory = ProductCategory.GERAL,
    onSave: (String, Long, ProductCategory) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var priceText by remember {
        mutableStateOf(
            if (initialPrice > 0) (initialPrice / 100).toString() else ""
        )
    }
    var category by remember { mutableStateOf(initialCategory) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ElevatedSurface,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = { Text(title, fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DeepCrimson,
                        unfocusedBorderColor = BorderDark,
                        focusedContainerColor = SurfaceBlack,
                        unfocusedContainerColor = SurfaceBlack,
                        cursorColor = DeepCrimson
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { c -> c.isDigit() || c == ',' || c == '.' } },
                    label = { Text("Preço (R$)", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DeepCrimson,
                        unfocusedBorderColor = BorderDark,
                        focusedContainerColor = SurfaceBlack,
                        unfocusedContainerColor = SurfaceBlack,
                        cursorColor = DeepCrimson
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Categoria:", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProductCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.label(), fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(cat.icon(), null, modifier = Modifier.size(14.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepCrimson.copy(alpha = 0.2f),
                                selectedLabelColor = DeepCrimson
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val priceCents = parsePriceToCents(priceText)
                    if (name.isNotBlank() && priceCents > 0) {
                        onSave(name, priceCents, category)
                    }
                },
                enabled = name.isNotBlank() && priceText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextDisabled) }
        }
    )
}

private fun parsePriceToCents(text: String): Long {
    val cleaned = text.replace(",", ".")
    val parts = cleaned.split(".")
    return when {
        parts.size == 1 -> (parts[0].toLongOrNull() ?: 0) * 100
        parts.size == 2 -> {
            val reais = parts[0].toLongOrNull() ?: 0
            val centavosStr = parts[1].padEnd(2, '0').take(2)
            val centavos = centavosStr.toLongOrNull() ?: 0
            reais * 100 + centavos
        }
        else -> 0
    }
}
