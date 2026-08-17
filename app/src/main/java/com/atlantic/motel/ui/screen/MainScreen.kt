package com.atlantic.motel.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atlantic.motel.data.model.Apartment
import com.atlantic.motel.data.model.ApartmentState
import com.atlantic.motel.ui.components.ApartmentCard
import com.atlantic.motel.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onStayClick: (Long) -> Unit,
    onReservationClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onProductsClick: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val apartments by viewModel.apartments.collectAsState()
    var showStartStayDialog by remember { mutableStateOf<Apartment?>(null) }
    var showMaintenanceDialog by remember { mutableStateOf<Apartment?>(null) }
    var showAddApartmentDialog by remember { mutableStateOf(false) }
    var showApartmentActions by remember { mutableStateOf<Apartment?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Atlantic Motel", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Colorado do Oeste - RO", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { showAddApartmentDialog = true }) {
                        Icon(Icons.Default.Add, "Adicionar", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    IconButton(onClick = onProductsClick) {
                        Icon(Icons.Default.Inventory, "Produtos", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    IconButton(onClick = onReservationClick) {
                        Icon(Icons.Default.Event, "Reservas", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    IconButton(onClick = onHistoryClick) {
                        Icon(Icons.Default.History, "Historico", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (apartments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Carregando...", textAlign = TextAlign.Center)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(apartments) { state ->
                    ApartmentCard(
                        apartment = state.apartment,
                        stayDuration = state.duration,
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
        StartStayDialog(
            apartmentNumber = apartment.number,
            onStart = { guestName ->
                viewModel.startStay(apartment.id, guestName)
                showStartStayDialog = null
            },
            onDismiss = { showStartStayDialog = null }
        )
    }

    showApartmentActions?.let { apartment ->
        ApartmentActionsDialog(
            apartment = apartment,
            onClean = {
                viewModel.markCleaned(apartment.id)
                showApartmentActions = null
            },
            onMaintenance = { note ->
                viewModel.setMaintenance(apartment.id, note)
                showApartmentActions = null
            },
            onClearMaintenance = {
                viewModel.clearMaintenance(apartment.id)
                showApartmentActions = null
            },
            onDismiss = { showApartmentActions = null }
        )
    }

    if (showAddApartmentDialog) {
        AddApartmentDialog(
            onAdd = { number ->
                viewModel.addApartment(number)
                showAddApartmentDialog = false
            },
            onDismiss = { showAddApartmentDialog = false }
        )
    }
}

@Composable
fun StartStayDialog(
    apartmentNumber: String,
    onStart: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var guestName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Check-in - Apt $apartmentNumber") },
        text = {
            OutlinedTextField(
                value = guestName,
                onValueChange = { guestName = it },
                label = { Text("Nome do hospede (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onStart(guestName) }) {
                Text("Iniciar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun ApartmentActionsDialog(
    apartment: Apartment,
    onClean: () -> Unit,
    onMaintenance: (String) -> Unit,
    onClearMaintenance: () -> Unit,
    onDismiss: () -> Unit
) {
    var showMaintenanceInput by remember { mutableStateOf(false) }
    var maintenanceNote by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Apt ${apartment.number}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                when (apartment.state) {
                    ApartmentState.LIMPEZA -> {
                        Text("Este apartamento precisa de limpeza.")
                        Button(
                            onClick = onClean,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.CleaningServices, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Limpeza Concluida")
                        }
                    }
                    ApartmentState.MANUTENCAO -> {
                        Text("Em manutencao.")
                        if (apartment.maintenanceNote.isNotBlank()) {
                            Text("Motivo: ${apartment.maintenanceNote}", fontSize = 13.sp)
                        }
                        if (!showMaintenanceInput) {
                            Button(
                                onClick = onClearMaintenance,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Manutencao Concluida")
                            }
                        }
                    }
                    else -> {}
                }

                if (apartment.state == ApartmentState.LIVRE || apartment.state == ApartmentState.MANUTENCAO) {
                    HorizontalDivider()
                    if (showMaintenanceInput) {
                        OutlinedTextField(
                            value = maintenanceNote,
                            onValueChange = { maintenanceNote = it },
                            label = { Text("Motivo da manutencao") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = { onMaintenance(maintenanceNote) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Enviar para Manutencao")
                        }
                    } else {
                        OutlinedButton(
                            onClick = { showMaintenanceInput = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Build, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Colocar em Manutencao")
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Fechar") }
        }
    )
}

@Composable
fun AddApartmentDialog(
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var number by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Apartamento") },
        text = {
            OutlinedTextField(
                value = number,
                onValueChange = { number = it },
                label = { Text("Numero do apartamento") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (number.isNotBlank()) onAdd(number) },
                enabled = number.isNotBlank()
            ) { Text("Adicionar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
