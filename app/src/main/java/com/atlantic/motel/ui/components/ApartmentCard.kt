package com.atlantic.motel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atlantic.motel.billing.BillingEngine
import com.atlantic.motel.data.model.Apartment
import com.atlantic.motel.data.model.ApartmentState

@Composable
fun ApartmentCard(
    apartment: Apartment,
    stayDuration: String? = null,
    stayAmount: String? = null,
    guestName: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor, icon, stateLabel) = when (apartment.state) {
        ApartmentState.LIVRE -> Quadruple(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.onSurface,
            Icons.Default.Home,
            "LIVRE"
        )
        ApartmentState.OCUPADO -> Quadruple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            Icons.Default.Home,
            "OCUPADO"
        )
        ApartmentState.LIMPEZA -> Quadruple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            Icons.Default.CleaningServices,
            "LIMPEZA"
        )
        ApartmentState.MANUTENCAO -> Quadruple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            Icons.Default.Build,
            "MANUTENCAO"
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = apartment.number,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = contentColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = contentColor
                        )
                        Text(
                            text = stateLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (apartment.state) {
                ApartmentState.OCUPADO -> {
                    if (!guestName.isNullOrBlank()) {
                        Text(
                            text = guestName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = contentColor
                        )
                    }
                    if (!stayDuration.isNullOrBlank()) {
                        Text(
                            text = stayDuration,
                            fontSize = 13.sp,
                            color = contentColor.copy(alpha = 0.8f)
                        )
                    }
                    if (!stayAmount.isNullOrBlank()) {
                        Text(
                            text = stayAmount,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                }
                ApartmentState.LIMPEZA -> {
                    Text(
                        text = "Aguardando limpeza",
                        fontSize = 13.sp,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
                ApartmentState.MANUTENCAO -> {
                    Text(
                        text = if (apartment.maintenanceNote.isNotBlank())
                            apartment.maintenanceNote else "Em manutencao",
                        fontSize = 13.sp,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
                ApartmentState.LIVRE -> {
                    Text(
                        text = "Disponivel",
                        fontSize = 13.sp,
                        color = contentColor.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
