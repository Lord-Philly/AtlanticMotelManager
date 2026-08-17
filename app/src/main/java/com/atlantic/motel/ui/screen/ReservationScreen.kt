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
import com.atlantic.motel.data.model.Apartment
import com.atlantic.motel.data.model.Reservation
import com.atlantic.motel.data.model.ReservationStatus
import com.atlantic.motel.viewmodel.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    onBack: () -> Unit,
    viewModel: ReservationViewModel = viewModel()
) {
    val reservations by viewModel.reservations.collectAsState()
    val apartments by viewModel.apartments.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reservas") },
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
                        Icon(Icons.Default.Add, "Nova Reserva", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (reservations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhuma reserva ativa.\nToque em + para criar.", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reservations) { reservation ->
                    ReservationItem(
                        reservation = reservation,
                        onConfirm = { viewModel.confirmReservation(reservation.id) },
                        onCancel = { viewModel.cancelReservation(reservation.id) },
                        onComplete = { viewModel.completeReservation(reservation.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddReservationDialog(
            apartments = apartments,
            onAdd = { aptId, aptNumber, name, date, time, notes ->
                viewModel.addReservation(aptId, aptNumber, name, date, time, notes)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
fun ReservationItem(
    reservation: Reservation,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit
) {
    val statusColor = when (reservation.status) {
        ReservationStatus.PENDENTE -> MaterialTheme.colorScheme.tertiary
        ReservationStatus.CONFIRMADA -> MaterialTheme.colorScheme.primary
        ReservationStatus.CANCELADA -> MaterialTheme.colorScheme.error
        ReservationStatus.CONCLUIDA -> MaterialTheme.colorScheme.outline
    }
    val statusText = when (reservation.status) {
        ReservationStatus.PENDENTE -> "Pendente"
        ReservationStatus.CONFIRMADA -> "Confirmada"
        ReservationStatus.CANCELADA -> "Cancelada"
        ReservationStatus.CONCLUIDA -> "Concluida"
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Apt ${reservation.apartmentNumber}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        statusText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(reservation.guestName, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text("${reservation.date} as ${reservation.time}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            if (reservation.notes.isNotBlank()) {
                Text("Obs: ${reservation.notes}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }

            if (reservation.status == ReservationStatus.PENDENTE || reservation.status == ReservationStatus.CONFIRMADA) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (reservation.status == ReservationStatus.PENDENTE) {
                        FilledTonalButton(onClick = onConfirm) { Text("Confirmar", fontSize = 12.sp) }
                    }
                    if (reservation.status != ReservationStatus.CANCELADA && reservation.status != ReservationStatus.CONCLUIDA) {
                        FilledTonalButton(onClick = onComplete) { Text("Concluir", fontSize = 12.sp) }
                        OutlinedButton(onClick = onCancel) { Text("Cancelar", fontSize = 12.sp) }
                    }
                }
            }
        }
    }
}

@Composable
fun AddReservationDialog(
    apartments: List<Apartment>,
    onAdd: (Long, String, String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedApartment by remember { mutableStateOf<Apartment?>(null) }
    var guestName by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Reserva") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Apartamento:", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                apartments.filter { it.number.isNotBlank() }.forEach { apt ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedApartment?.id == apt.id,
                            onClick = { selectedApartment = apt }
                        )
                        Text("Apt ${apt.number}", modifier = Modifier.padding(start = 4.dp))
                    }
                }
                OutlinedTextField(
                    value = guestName,
                    onValueChange = { guestName = it },
                    label = { Text("Nome do cliente") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Data (aaaa-mm-dd)") },
                    placeholder = { Text(today) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Horario (hh:mm)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observacoes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val apt = selectedApartment
                    if (apt != null && guestName.isNotBlank() && date.isNotBlank() && time.isNotBlank()) {
                        onAdd(apt.id, apt.number, guestName, date.ifBlank { today }, time, notes)
                    }
                },
                enabled = selectedApartment != null && guestName.isNotBlank() && date.isNotBlank() && time.isNotBlank()
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
