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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atlantic.motel.data.model.Laundry
import com.atlantic.motel.data.model.LaundryItem
import com.atlantic.motel.data.model.LaundryStatus
import com.atlantic.motel.ui.theme.*
import com.atlantic.motel.viewmodel.LaundryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaundryScreen(
    onBack: () -> Unit,
    viewModel: LaundryViewModel = viewModel()
) {
    val sujoCount by viewModel.sujoCount.collectAsState()
    val lavandoCount by viewModel.lavandoCount.collectAsState()
    val limpoCount by viewModel.limpoCount.collectAsState()
    val fronhas by viewModel.fronhas.collectAsState()
    val lencois by viewModel.lencois.collectAsState()
    val allLaundryItems by viewModel.allLaundry.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DeepBlack,
        topBar = {
            TopAppBar(
                title = { Text("Lavanderia", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontFamily = CormorantGaramondFamily) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceBlack,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextSecondary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = DeepCrimson,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Adicionar item")
            }
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
                Text(
                    "Resumo por Status",
                    fontFamily = CormorantGaramondFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LaundryCounter("Sujo", sujoCount ?: 0, MetallicRed, Modifier.weight(1f))
                    LaundryCounter("Lavando", lavandoCount ?: 0, Champagne, Modifier.weight(1f))
                    LaundryCounter("Limpo", limpoCount ?: 0, LivreColor, Modifier.weight(1f))
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Itens por Tipo",
                    fontFamily = CormorantGaramondFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ItemTypeCard("Fronhas", fronhas, Modifier.weight(1f))
                    ItemTypeCard("Lençóis", lencois, Modifier.weight(1f))
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Todos os Itens",
                    fontFamily = CormorantGaramondFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }

            items(allLaundryItems) { item ->
                LaundryItemCard(
                    item = item,
                    onStatusChange = { newStatus ->
                        viewModel.updateStatus(item, newStatus)
                    },
                    onQuantityChange = { newQty ->
                        viewModel.updateQuantity(item, newQty)
                    },
                    onRemove = { viewModel.removeItem(item) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddLaundryDialog(
            onAdd = { item, qty, apt ->
                viewModel.addItem(item, qty, apt)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
fun LaundryCounter(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                count.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = JetBrainsMonoFamily,
                color = color
            )
            Text(label, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
fun ItemTypeCard(label: String, items: List<Laundry>, modifier: Modifier = Modifier) {
    val total = items.sumOf { it.quantity }
    val sujo = items.filter { it.status == LaundryStatus.SUJO }.sumOf { it.quantity }
    val lavando = items.filter { it.status == LaundryStatus.LAVANDO }.sumOf { it.quantity }
    val limpo = items.filter { it.status == LaundryStatus.LIMPO }.sumOf { it.quantity }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceBlack),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Total: $total", fontSize = 12.sp, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("● $sujo", fontSize = 11.sp, color = MetallicRed)
                Text("● $lavando", fontSize = 11.sp, color = Champagne)
                Text("● $limpo", fontSize = 11.sp, color = LivreColor)
            }
        }
    }
}

@Composable
fun LaundryItemCard(
    item: Laundry,
    onStatusChange: (LaundryStatus) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val itemLabel = when (item.item) {
        LaundryItem.FRONHA -> "Fronha"
        LaundryItem.LENCOL -> "Lençol"
        LaundryItem.TOALHA -> "Toalha"
        LaundryItem.OUTRO -> "Outro"
    }
    val statusColor = when (item.status) {
        LaundryStatus.SUJO -> MetallicRed
        LaundryStatus.LAVANDO -> Champagne
        LaundryStatus.LIMPO -> LivreColor
    }
    val statusLabel = when (item.status) {
        LaundryStatus.SUJO -> "Sujo"
        LaundryStatus.LAVANDO -> "Lavando"
        LaundryStatus.LIMPO -> "Limpo"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceBlack),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(itemLabel, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextPrimary)
                if (item.apartmentNumber.isNotBlank()) {
                    Text("Apt ${item.apartmentNumber}", fontSize = 11.sp, color = TextDisabled)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Qtd:", fontSize = 12.sp, color = TextSecondary)
                    IconButton(
                        onClick = {
                            if (item.quantity > 1) onQuantityChange(item.quantity - 1)
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Remove, "Diminuir", modifier = Modifier.size(14.dp), tint = TextSecondary)
                    }
                    Text(
                        item.quantity.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JetBrainsMonoFamily,
                        color = TextPrimary,
                        modifier = Modifier.width(24.dp),
                        textAlign = TextAlign.Center
                    )
                    IconButton(
                        onClick = { onQuantityChange(item.quantity + 1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Add, "Aumentar", modifier = Modifier.size(14.dp), tint = TextSecondary)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        statusLabel,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LaundryStatus.values().forEach { status ->
                    val label = when (status) {
                        LaundryStatus.SUJO -> "Sujo"
                        LaundryStatus.LAVANDO -> "Lavando"
                        LaundryStatus.LIMPO -> "Limpo"
                    }
                    val color = when (status) {
                        LaundryStatus.SUJO -> MetallicRed
                        LaundryStatus.LAVANDO -> Champagne
                        LaundryStatus.LIMPO -> LivreColor
                    }
                    val isSelected = item.status == status
                    OutlinedButton(
                        onClick = { onStatusChange(status) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) color.copy(alpha = 0.15f) else Color.Transparent,
                            contentColor = if (isSelected) color else TextDisabled
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (isSelected) color else TextDisabled.copy(alpha = 0.3f)
                            )
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, "Remover", modifier = Modifier.size(16.dp), tint = MetallicRed)
                }
            }
        }
    }
}

@Composable
fun AddLaundryDialog(
    onAdd: (LaundryItem, Int, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedItem by remember { mutableStateOf(LaundryItem.FRONHA) }
    var quantity by remember { mutableStateOf("1") }
    var apartment by remember { mutableStateOf("") }

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
                Text("Adicionar Lavanderia", fontWeight = FontWeight.SemiBold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Item:", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = TextSecondary)
                listOf(LaundryItem.FRONHA, LaundryItem.LENCOL, LaundryItem.TOALHA, LaundryItem.OUTRO).forEach { item ->
                    val label = when (item) {
                        LaundryItem.FRONHA -> "Fronha"
                        LaundryItem.LENCOL -> "Lençol"
                        LaundryItem.TOALHA -> "Toalha"
                        LaundryItem.OUTRO -> "Outro"
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedItem == item,
                            onClick = { selectedItem = item },
                            colors = RadioButtonDefaults.colors(selectedColor = DeepCrimson, unselectedColor = TextDisabled)
                        )
                        Text(label, modifier = Modifier.padding(start = 4.dp), color = TextPrimary)
                    }
                }
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                    label = { Text("Quantidade", color = TextSecondary) },
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
                    value = apartment,
                    onValueChange = { apartment = it },
                    label = { Text("Apartamento (opcional)", color = TextSecondary) },
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
                Button(
                    onClick = { onAdd(selectedItem, quantity.toIntOrNull() ?: 1, apartment) },
                    enabled = (quantity.toIntOrNull() ?: 0) > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Adicionar") }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}
