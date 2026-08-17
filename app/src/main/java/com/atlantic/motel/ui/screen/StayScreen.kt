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
import com.atlantic.motel.data.model.Consumption
import com.atlantic.motel.data.model.PaymentMethod
import com.atlantic.motel.data.model.Product
import com.atlantic.motel.viewmodel.ConsumptionViewModel
import com.atlantic.motel.viewmodel.StayViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StayScreen(
    apartmentId: Long,
    onBack: () -> Unit,
    stayViewModel: StayViewModel = viewModel(),
    consumptionViewModel: ConsumptionViewModel = viewModel()
) {
    val stayDetail by stayViewModel.stayDetail.collectAsState()

    LaunchedEffect(apartmentId) {
        stayViewModel.loadByApartment(apartmentId)
    }

    LaunchedEffect(stayDetail.stay?.id) {
        stayDetail.stay?.id?.let { consumptionViewModel.loadStay(it) }
    }

    val products by consumptionViewModel.products.collectAsState()
    val consumptions by consumptionViewModel.consumptions.collectAsState()
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var showAddConsumptionDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Apt ${stayDetail.apartment?.number ?: "..."}") },
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
                    IconButton(onClick = { showAddConsumptionDialog = true }) {
                        Icon(Icons.Default.AddShoppingCart, "Consumo", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    IconButton(onClick = { showCheckoutDialog = true }) {
                        Icon(Icons.Default.CheckCircle, "Checkout", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Hospedagem",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (stayDetail.stay?.guestName?.isNotBlank() == true) {
                            InfoRow("Hospede", stayDetail.stay!!.guestName)
                        }
                        InfoRow("Tempo", stayDetail.duration)
                        InfoRow("Valor hospedagem", BillingEngine.formatCurrency(stayDetail.stayAmount))
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Consumo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                BillingEngine.formatCurrency(stayDetail.consumptionTotal),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            if (consumptions.isNotEmpty()) {
                items(consumptions) { consumption ->
                    ConsumptionItem(
                        consumption = consumption,
                        onRemove = { consumptionViewModel.removeConsumption(consumption) },
                        onUpdateQty = { newQty -> consumptionViewModel.updateQuantity(consumption, newQty) }
                    )
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "TOTAL",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            BillingEngine.formatCurrency(stayDetail.total),
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }

    if (showCheckoutDialog) {
        CheckoutDialog(
            total = stayDetail.total,
            onCheckout = { method ->
                stayViewModel.checkout(method)
                showCheckoutDialog = false
                onBack()
            },
            onDismiss = { showCheckoutDialog = false }
        )
    }

    if (showAddConsumptionDialog) {
        AddConsumptionDialog(
            products = products,
            onAdd = { product, qty ->
                consumptionViewModel.addConsumption(product, qty)
                showAddConsumptionDialog = false
            },
            onDismiss = { showAddConsumptionDialog = false }
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
fun ConsumptionItem(
    consumption: Consumption,
    onRemove: () -> Unit,
    onUpdateQty: (Int) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(consumption.productName, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(
                    "${consumption.quantity}x ${BillingEngine.formatCurrency(consumption.unitPriceInCents)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Text(
                BillingEngine.formatCurrency(consumption.quantity * consumption.unitPriceInCents),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Row {
                IconButton(onClick = { onUpdateQty(consumption.quantity - 1) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Remove, "Menos", modifier = Modifier.size(16.dp))
                }
                Text("${consumption.quantity}", modifier = Modifier.padding(horizontal = 4.dp).align(Alignment.CenterVertically))
                IconButton(onClick = { onUpdateQty(consumption.quantity + 1) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Add, "Mais", modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, "Remover", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun CheckoutDialog(
    total: Long,
    onCheckout: (PaymentMethod) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Finalizar Hospedagem") },
        text = {
            Column {
                Text(
                    "Total: ${BillingEngine.formatCurrency(total)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Forma de pagamento:", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(
                    onClick = { onCheckout(PaymentMethod.DINHEIRO) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("DINHEIRO") }
                Button(
                    onClick = { onCheckout(PaymentMethod.PIX) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) { Text("PIX") }
                Button(
                    onClick = { onCheckout(PaymentMethod.CARTAO) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) { Text("CARTAO") }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun AddConsumptionDialog(
    products: List<Product>,
    onAdd: (Product, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var quantity by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar Consumo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (products.isEmpty()) {
                    Text("Nenhum produto cadastrado.\nCadastre em Produtos.")
                } else {
                    products.forEach { product ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedProduct?.id == product.id,
                                onClick = { selectedProduct = product }
                            )
                            Text(product.name, modifier = Modifier.weight(1f))
                            Text(BillingEngine.formatCurrency(product.priceInCents), fontSize = 13.sp)
                        }
                    }
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                        label = { Text("Quantidade") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedProduct?.let { product ->
                        onAdd(product, quantity.toIntOrNull() ?: 1)
                    }
                },
                enabled = selectedProduct != null && (quantity.toIntOrNull() ?: 0) > 0
            ) { Text("Adicionar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
