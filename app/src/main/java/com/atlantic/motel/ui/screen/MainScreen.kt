package com.atlantic.motel.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atlantic.motel.AtlanticMotelApp
import com.atlantic.motel.billing.BillingEngine
import com.atlantic.motel.data.model.Apartment
import com.atlantic.motel.data.model.ApartmentState
import com.atlantic.motel.data.model.UserGender
import com.atlantic.motel.ui.components.ApartmentCard
import com.atlantic.motel.ui.theme.*
import com.atlantic.motel.viewmodel.MainViewModel

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Apartamentos : BottomNavItem("apartamentos", Icons.Default.Villa, "Apts")
    data object Produtos : BottomNavItem("products", Icons.Default.Inventory, "Produtos")
    data object Lavanderia : BottomNavItem("laundry", Icons.Default.LocalLaundryService, "Lavanderia")
    data object Reservas : BottomNavItem("reservations", Icons.Default.Event, "Reservas")
    data object Relatorios : BottomNavItem("reports", Icons.Default.Receipt, "Relatórios")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onStayClick: (Long) -> Unit,
    onReservationClick: () -> Unit,
    onProductsClick: () -> Unit,
    onLaundryClick: () -> Unit = {},
    onReportsClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: MainViewModel = viewModel()
) {
    val apartments by viewModel.apartments.collectAsState()
    val dailyTotal by viewModel.dailyTotal.collectAsState()
    var showStartStayDialog by remember { mutableStateOf<Apartment?>(null) }
    var showAddApartmentDialog by remember { mutableStateOf(false) }
    var showApartmentActions by remember { mutableStateOf<Apartment?>(null) }

    val user = AtlanticMotelApp.instance.getCurrentUser()

    val bottomItems = listOf(
        BottomNavItem.Apartamentos,
        BottomNavItem.Produtos,
        BottomNavItem.Lavanderia,
        BottomNavItem.Reservas,
        BottomNavItem.Relatorios
    )

    Scaffold(
        containerColor = DeepBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Motel Manager",
                            fontFamily = CormorantGaramondFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TextPrimary
                        )
                        if (user != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (user.gender) {
                                        UserGender.FEMININO -> Icons.Default.Female
                                        UserGender.MASCULINO -> Icons.Default.Male
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = TextSecondary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    user.displayName,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = "Total do dia",
                            modifier = Modifier.size(18.dp),
                            tint = Champagne
                        )
                        Text(
                            BillingEngine.formatCurrency(dailyTotal),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = JetBrainsMonoFamily,
                            color = Champagne
                        )
                    }
                    IconButton(onClick = { showAddApartmentDialog = true }) {
                        Icon(Icons.Default.Add, "Adicionar Apt", tint = TextSecondary)
                    }
                    IconButton(onClick = {
                        AtlanticMotelApp.instance.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.Logout, "Sair", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceBlack,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceBlack,
                contentColor = TextSecondary,
                tonalElevation = 0.dp
            ) {
                bottomItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(item.icon, contentDescription = item.label, modifier = Modifier.size(20.dp))
                        },
                        label = {
                            Text(item.label, fontSize = 10.sp)
                        },
                        selected = false,
                        onClick = {
                            when (item) {
                                BottomNavItem.Apartamentos -> { /* already here */ }
                                BottomNavItem.Produtos -> onProductsClick()
                                BottomNavItem.Lavanderia -> onLaundryClick()
                                BottomNavItem.Reservas -> onReservationClick()
                                BottomNavItem.Relatorios -> onReportsClick()
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepCrimson,
                            selectedTextColor = DeepCrimson,
                            unselectedIconColor = TextDisabled,
                            unselectedTextColor = TextDisabled,
                            indicatorColor = DeepCrimson.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        if (apartments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(DeepBlack),
                contentAlignment = Alignment.Center
            ) {
                Text("Carregando...", textAlign = TextAlign.Center, color = TextDisabled)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(DeepBlack),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(apartments) { state ->
                    ApartmentCard(
                        apartment = state.apartment,
                        stayDuration = state.duration,
                        stayDurationHMS = state.durationHMS,
                        stayAmount = state.amount,
                        guestName = state.guestName,
                        onClick = {
                            when (state.apartment.state) {
                                ApartmentState.LIVRE -> showStartStayDialog = state.apartment
                                ApartmentState.OCUPADO -> onStayClick(state.apartment.id)
                                ApartmentState.LIMPEZA -> showApartmentActions = state.apartment
                                ApartmentState.MANUTENCAO -> showApartmentActions = state.apartment
                            }
                        }
                    )
                }
            }
        }
    }

    showStartStayDialog?.let { apartment ->
        CloseableDialog(
            title = "Check-in — Apt ${apartment.number}",
            onDismiss = { showStartStayDialog = null }
        ) {
            var guestName by remember { mutableStateOf("") }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DarkTextField(
                    value = guestName,
                    onValueChange = { guestName = it },
                    label = "Nome do hóspede (opcional)"
                )
                DarkButton(
                    onClick = {
                        viewModel.startStay(apartment.id, guestName)
                        showStartStayDialog = null
                    },
                    text = "Iniciar"
                )
            }
        }
    }

    showApartmentActions?.let { apartment ->
        CloseableDialog(
            title = "Apt ${apartment.number}",
            onDismiss = { showApartmentActions = null }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                when (apartment.state) {
                    ApartmentState.LIMPEZA -> {
                        Text("Precisa de limpeza.", color = TextSecondary, fontSize = 13.sp)
                        DarkButton(
                            onClick = {
                                viewModel.markCleaned(apartment.id)
                                showApartmentActions = null
                            },
                            text = "Limpeza Concluída"
                        )
                    }
                    ApartmentState.MANUTENCAO -> {
                        Text("Em manutenção.", color = TextSecondary, fontSize = 13.sp)
                        if (apartment.maintenanceNote.isNotBlank()) {
                            Text("Motivo: ${apartment.maintenanceNote}", fontSize = 12.sp, color = TextDisabled)
                        }
                        DarkButton(
                            onClick = {
                                viewModel.clearMaintenance(apartment.id)
                                showApartmentActions = null
                            },
                            text = "Manutenção Concluída"
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    if (showAddApartmentDialog) {
        CloseableDialog(
            title = "Novo Apartamento",
            onDismiss = { showAddApartmentDialog = false }
        ) {
            var number by remember { mutableStateOf("") }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DarkTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = "Número do apartamento"
                )
                DarkButton(
                    onClick = {
                        if (number.isNotBlank()) {
                            viewModel.addApartment(number)
                            showAddApartmentDialog = false
                        }
                    },
                    text = "Adicionar",
                    enabled = number.isNotBlank()
                )
            }
        }
    }
}

@Composable
fun CloseableDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
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
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextPrimary)
            }
        },
        text = { content() },
        confirmButton = {},
        dismissButton = {}
    )
}

@Composable
fun DarkDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    CloseableDialog(title = title, onDismiss = onDismiss, content = content)
}

@Composable
fun DarkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary) },
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
}

@Composable
fun DarkButton(
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = DeepCrimson,
            contentColor = androidx.compose.ui.graphics.Color.White,
            disabledContainerColor = DeepBurgundy.copy(alpha = 0.5f),
            disabledContentColor = TextDisabled
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
