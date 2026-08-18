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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atlantic.motel.data.model.Apartment
import com.atlantic.motel.data.model.Reservation
import com.atlantic.motel.data.model.ReservationStatus
import com.atlantic.motel.ui.theme.*
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
        containerColor = DeepBlack,
        topBar = {
            TopAppBar(
                title = { Text("Reservas", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontFamily = CormorantGaramondFamily) },
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
                        Icon(Icons.Default.Add, "Nova Reserva", tint = DeepCrimson)
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
                Text(
                    "Nenhuma reserva ativa.\nToque em + para criar.",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = TextDisabled
                )
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
        ReservationStatus.PENDENTE -> Champagne
        ReservationStatus.CONFIRMADA -> LivreColor
        ReservationStatus.CANCELADA -> MetallicRed
        ReservationStatus.CONCLUIDA -> TextDisabled
    }
    val statusText = when (reservation.status) {
        ReservationStatus.PENDENTE -> "Pendente"
        ReservationStatus.CONFIRMADA -> "Confirmada"
        ReservationStatus.CANCELADA -> "Cancelada"
        ReservationStatus.CONCLUIDA -> "Concluída"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceBlack),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Apt ${reservation.apartmentNumber}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary,
                    fontFamily = JetBrainsMonoFamily
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        statusText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(reservation.guestName, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextPrimary)
            Text(
                "${reservation.date} às ${reservation.time}",
                fontSize = 12.sp,
                color = TextDisabled
            )
            if (reservation.notes.isNotBlank()) {
                Text("Obs: ${reservation.notes}", fontSize = 11.sp, color = TextDisabled)
            }

            if (reservation.status == ReservationStatus.PENDENTE || reservation.status == ReservationStatus.CONFIRMADA) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (reservation.status == ReservationStatus.PENDENTE) {
                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson, contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) { Text("Confirmar", fontSize = 12.sp) }
                    }
                    if (reservation.status != ReservationStatus.CANCELADA && reservation.status != ReservationStatus.CONCLUIDA) {
                        Button(
                            onClick = onComplete,
                            colors = ButtonDefaults.buttonColors(containerColor = LivreColor, contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) { Text("Concluir", fontSize = 12.sp) }
                        OutlinedButton(
                            onClick = onCancel,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            border = ButtonDefaults.outlinedButtonBorder
                        ) { Text("Cancelar", fontSize = 12.sp, color = MetallicRed) }
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
    var day by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

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
                Text("Nova Reserva", fontWeight = FontWeight.SemiBold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Apartamento:", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = TextSecondary)
                apartments.filter { it.number.isNotBlank() }.forEach { apt ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedApartment?.id == apt.id,
                            onClick = { selectedApartment = apt },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = DeepCrimson,
                                unselectedColor = TextDisabled
                            )
                        )
                        Text("Apt ${apt.number}", modifier = Modifier.padding(start = 4.dp), color = TextPrimary)
                    }
                }
                OutlinedTextField(
                    value = guestName,
                    onValueChange = { guestName = it },
                    label = { Text("Nome do cliente", color = TextSecondary) },
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = day,
                        onValueChange = { day = it.filter { c -> c.isDigit() }.take(2) },
                        label = { Text("Dia", color = TextSecondary) },
                        placeholder = { Text("dd", color = TextDisabled) },
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
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = month,
                        onValueChange = { month = it.filter { c -> c.isDigit() }.take(2) },
                        label = { Text("Mês", color = TextSecondary) },
                        placeholder = { Text("mm", color = TextDisabled) },
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
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it.filter { c -> c.isDigit() }.take(4) },
                        label = { Text("Ano (opc.)", color = TextSecondary) },
                        placeholder = { Text("aaaa", color = TextDisabled) },
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
                        modifier = Modifier.weight(1.3f)
                    )
                }
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Horário (hh:mm)", color = TextSecondary) },
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
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observações (opcional)", color = TextSecondary) },
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
                    onClick = {
                        val apt = selectedApartment
                        val d = day.padStart(2, '0')
                        val m = month.padStart(2, '0')
                        val fullDate = if (year.isNotBlank()) "$d/$m/$year" else "$d/$m"
                        if (apt != null && guestName.isNotBlank() && day.isNotBlank() && month.isNotBlank() && time.isNotBlank()) {
                            onAdd(apt.id, apt.number, guestName, fullDate, time, notes)
                        }
                    },
                    enabled = selectedApartment != null && guestName.isNotBlank() && day.isNotBlank() && month.isNotBlank() && time.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Salvar") }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}
