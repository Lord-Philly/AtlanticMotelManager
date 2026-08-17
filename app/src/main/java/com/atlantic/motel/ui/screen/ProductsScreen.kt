package com.atlantic.motel.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atlantic.motel.billing.BillingEngine
import com.atlantic.motel.data.model.Product
import com.atlantic.motel.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onBack: () -> Unit,
    viewModel: ProductViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Produtos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Adicionar", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (products.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhum produto cadastrado.\nToque em + para adicionar.", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.name, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                                Text(
                                    BillingEngine.formatCurrency(product.priceInCents),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Row {
                                IconButton(onClick = { editingProduct = product }) {
                                    Icon(Icons.Default.Edit, "Editar")
                                }
                                IconButton(onClick = { viewModel.deleteProduct(product) }) {
                                    Icon(Icons.Default.Delete, "Excluir", tint = MaterialTheme.colorScheme.error)
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
            onSave = { name, priceCents ->
                viewModel.addProduct(name, priceCents)
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
            onSave = { name, priceCents ->
                viewModel.updateProduct(product.copy(name = name, priceInCents = priceCents))
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
    onSave: (String, Long) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var priceText by remember {
        mutableStateOf(
            if (initialPrice > 0) (initialPrice / 100).toString() else ""
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { c -> c.isDigit() || c == ',' || c == '.' } },
                    label = { Text("Preco (R$)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val priceCents = parsePriceToCents(priceText)
                    if (name.isNotBlank() && priceCents > 0) {
                        onSave(name, priceCents)
                    }
                },
                enabled = name.isNotBlank() && priceText.isNotBlank()
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
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
