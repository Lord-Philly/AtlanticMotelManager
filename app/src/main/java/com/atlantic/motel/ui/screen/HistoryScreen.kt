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
import com.atlantic.motel.billing.BillingEngine
import com.atlantic.motel.data.model.Payment
import com.atlantic.motel.data.model.PaymentMethod
import com.atlantic.motel.ui.theme.*
import com.atlantic.motel.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val payments by viewModel.recentPayments.collectAsState()

    Scaffold(
        containerColor = DeepBlack,
        topBar = {
            TopAppBar(
                title = { Text("Histórico", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontFamily = CormorantGaramondFamily) },
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
        }
    ) { paddingValues ->
        if (payments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhum registro ainda.", textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = TextDisabled)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DeepBurgundy.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "TOTAL GERAL",
                                fontFamily = CormorantGaramondFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextSecondary,
                                letterSpacing = 2.sp
                            )
                            Text(
                                BillingEngine.formatCurrency(payments.sumOf { it.totalInCents }),
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                fontFamily = JetBrainsMonoFamily,
                                color = DeepCrimson
                            )
                            Text(
                                "${payments.size} registros",
                                fontSize = 12.sp,
                                color = TextDisabled
                            )
                        }
                    }
                }

                items(payments) { payment ->
                    PaymentHistoryItem(payment)
                }
            }
        }
    }
}

@Composable
fun PaymentHistoryItem(payment: Payment) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val dateStr = remember(payment.timestamp) { dateFormat.format(Date(payment.timestamp)) }

    val methodText = when (payment.paymentMethod) {
        PaymentMethod.DINHEIRO -> "Dinheiro"
        PaymentMethod.PIX -> "PIX"
        PaymentMethod.CARTAO -> "Cartão"
    }
    val methodIcon = when (payment.paymentMethod) {
        PaymentMethod.DINHEIRO -> Icons.Default.Payments
        PaymentMethod.PIX -> Icons.Default.QrCode
        PaymentMethod.CARTAO -> Icons.Default.CreditCard
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
                    "Apt ${payment.apartmentNumber}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary,
                    fontFamily = JetBrainsMonoFamily
                )
                Text(
                    BillingEngine.formatCurrency(payment.totalInCents),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    fontFamily = JetBrainsMonoFamily,
                    color = Champagne
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(methodIcon, null, modifier = Modifier.size(14.dp), tint = TextDisabled)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(methodText, fontSize = 12.sp, color = TextDisabled)
                }
                Text(dateStr, fontSize = 11.sp, color = TextDisabled)
            }

            if (payment.stayAmountInCents > 0 && payment.consumptionAmountInCents > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = BorderDark)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Hospedagem: ${BillingEngine.formatCurrency(payment.stayAmountInCents)}",
                        fontSize = 11.sp,
                        color = TextDisabled
                    )
                    Text(
                        "Consumo: ${BillingEngine.formatCurrency(payment.consumptionAmountInCents)}",
                        fontSize = 11.sp,
                        color = TextDisabled
                    )
                }
            }
        }
    }
}
