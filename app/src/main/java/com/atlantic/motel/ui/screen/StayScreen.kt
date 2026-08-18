package com.atlantic.motel.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atlantic.motel.billing.BillingEngine
import com.atlantic.motel.data.model.Consumption
import com.atlantic.motel.data.model.PaymentMethod
import com.atlantic.motel.data.model.Product
import com.atlantic.motel.ui.theme.*
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
    val stayDetail by stayViewModel.enrichedDetail.collectAsState()

    LaunchedEffect(apartmentId) {
        stayViewModel.loadByApartment(apartmentId)
    }

    DisposableEffect(Unit) {
        onDispose { stayViewModel.stopTimer() }
    }

    LaunchedEffect(stayDetail.stay?.id) {
        stayDetail.stay?.id?.let { consumptionViewModel.loadStay(it) }
    }

    val products by consumptionViewModel.products.collectAsState()
    val consumptions by consumptionViewModel.consumptions.collectAsState()
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var showAddConsumptionDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DeepBlack,
        topBar = {
            TopAppBar(
                title = {
                        Text(
                            "Apt ${stayDetail.apartment?.number ?: "..."}",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = CormorantGaramondFamily
                        )
                },
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
                    IconButton(onClick = { showAddConsumptionDialog = true }) {
                        Icon(Icons.Default.AddShoppingCart, "Consumo", tint = TextSecondary)
                    }
                    IconButton(onClick = { showCheckoutDialog = true }) {
                        Icon(Icons.Default.CheckCircle, "Finalizar", tint = DeepCrimson)
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
                    colors = CardDefaults.cardColors(containerColor = SurfaceBlack),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Hospedagem",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        if (stayDetail.stay?.guestName?.isNotBlank() == true) {
                            InfoRow("Hóspede", stayDetail.stay!!.guestName)
                        }
                        if (stayDetail.durationHMS.isNotBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Tempo", fontSize = 14.sp, color = TextDisabled)
                                Text(
                                    stayDetail.durationHMS,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = JetBrainsMonoFamily,
                                    color = DeepCrimson,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                        if (stayDetail.duration.isNotBlank()) {
                            InfoRow("Duração", stayDetail.duration)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(color = BorderDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Hospedagem", fontSize = 14.sp, color = TextSecondary)
                            Text(
                                BillingEngine.formatCurrency(stayDetail.stayAmount),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = JetBrainsMonoFamily,
                                color = Champagne
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceBlack),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Consumo",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                BillingEngine.formatCurrency(stayDetail.consumptionTotal),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                fontFamily = JetBrainsMonoFamily,
                                color = Champagne
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
                HorizontalDivider(color = BorderDark, modifier = Modifier.padding(vertical = 4.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepBurgundy.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "TOTAL",
                            fontFamily = CormorantGaramondFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextSecondary,
                            letterSpacing = 2.sp
                        )
                        Text(
                            BillingEngine.formatCurrency(stayDetail.total),
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            fontFamily = JetBrainsMonoFamily,
                            color = DeepCrimson
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = TextDisabled)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

@Composable
fun ConsumptionItem(
    consumption: Consumption,
    onRemove: () -> Unit,
    onUpdateQty: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceBlack),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(consumption.productName, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextPrimary)
                Text(
                    "${consumption.quantity}x ${BillingEngine.formatCurrency(consumption.unitPriceInCents)}",
                    fontSize = 12.sp,
                    color = TextDisabled
                )
            }
            Text(
                BillingEngine.formatCurrency(consumption.quantity * consumption.unitPriceInCents),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = JetBrainsMonoFamily,
                color = Champagne
            )
            Row {
                IconButton(onClick = { onUpdateQty(consumption.quantity - 1) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Remove, "Menos", modifier = Modifier.size(16.dp), tint = TextSecondary)
                }
                Text(
                    "${consumption.quantity}",
                    modifier = Modifier.padding(horizontal = 4.dp).align(Alignment.CenterVertically),
                    color = TextPrimary,
                    fontFamily = JetBrainsMonoFamily
                )
                IconButton(onClick = { onUpdateQty(consumption.quantity + 1) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Add, "Mais", modifier = Modifier.size(16.dp), tint = DeepCrimson)
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, "Remover", modifier = Modifier.size(16.dp), tint = MetallicRed)
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
        containerColor = ElevatedSurface,
        titleContentColor = TextPrimary,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, "Fechar", modifier = Modifier.size(18.dp), tint = TextSecondary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Finalizar Hospedagem", fontWeight = FontWeight.SemiBold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Total: ${BillingEngine.formatCurrency(total)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    fontFamily = JetBrainsMonoFamily,
                    color = DeepCrimson
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Forma de pagamento:", fontWeight = FontWeight.Medium, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = { onCheckout(PaymentMethod.DINHEIRO) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Payments, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DINHEIRO", fontWeight = FontWeight.Medium)
                }
                Button(
                    onClick = { onCheckout(PaymentMethod.PIX) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Burgundy, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.QrCode, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PIX", fontWeight = FontWeight.Medium)
                }
                Button(
                    onClick = { onCheckout(PaymentMethod.CARTAO) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson.copy(alpha = 0.7f), contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CreditCard, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CARTÃO", fontWeight = FontWeight.Medium)
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
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
        containerColor = ElevatedSurface,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, "Fechar", modifier = Modifier.size(18.dp), tint = TextSecondary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adicionar Consumo", fontWeight = FontWeight.SemiBold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (products.isEmpty()) {
                    Text("Nenhum produto cadastrado.\nCadastre em Produtos.", color = TextDisabled)
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
                                onClick = { selectedProduct = product },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = DeepCrimson,
                                    unselectedColor = TextDisabled
                                )
                            )
                            Text(product.name, modifier = Modifier.weight(1f), color = TextPrimary)
                            Text(
                                BillingEngine.formatCurrency(product.priceInCents),
                                fontSize = 13.sp,
                                fontFamily = JetBrainsMonoFamily,
                                color = Champagne
                            )
                        }
                    }
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                        label = { Text("Quantidade", color = TextSecondary) },
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
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            selectedProduct?.let { product ->
                                onAdd(product, quantity.toIntOrNull() ?: 1)
                            }
                        },
                        enabled = selectedProduct != null && (quantity.toIntOrNull() ?: 0) > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Adicionar") }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}
