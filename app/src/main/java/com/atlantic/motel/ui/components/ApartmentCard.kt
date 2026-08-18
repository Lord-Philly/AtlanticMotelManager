package com.atlantic.motel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atlantic.motel.data.model.Apartment
import com.atlantic.motel.data.model.ApartmentState
import com.atlantic.motel.ui.theme.*

@Composable
fun ApartmentCard(
    apartment: Apartment,
    stayDuration: String? = null,
    stayDurationHMS: String? = null,
    stayAmount: String? = null,
    guestName: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(12.dp)

    val (borderColor, statusColor, statusLabel) = when (apartment.state) {
        ApartmentState.LIVRE -> Triple(
            LivreColor.copy(alpha = 0.4f),
            LivreColor,
            "LIVRE"
        )
        ApartmentState.OCUPADO -> Triple(
            DeepCrimson.copy(alpha = 0.6f),
            DeepCrimson,
            "OCUPADO"
        )
        ApartmentState.LIMPEZA -> Triple(
            LimpezaColor.copy(alpha = 0.4f),
            LimpezaColor,
            "LIMPEZA"
        )
        ApartmentState.MANUTENCAO -> Triple(
            ManutencaoColor.copy(alpha = 0.5f),
            ManutencaoColor,
            "MANUTENÇÃO"
        )
    }

    Box(
        modifier = modifier
            .clip(cardShape)
            .border(1.dp, borderColor, cardShape)
            .background(SurfaceBlack)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = apartment.number,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = JetBrainsMonoFamily,
                    color = TextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = when (apartment.state) {
                                ApartmentState.LIMPEZA -> Icons.Default.CleaningServices
                                ApartmentState.MANUTENCAO -> Icons.Default.Build
                                else -> Icons.Default.Home
                            },
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = statusColor
                        )
                        Text(
                            text = statusLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            letterSpacing = 0.5.sp
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
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }
                    if (!stayDurationHMS.isNullOrBlank()) {
                        Text(
                            text = stayDurationHMS,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JetBrainsMonoFamily,
                            color = DeepCrimson,
                            letterSpacing = 1.sp
                        )
                    }
                    if (!stayDuration.isNullOrBlank()) {
                        Text(
                            text = stayDuration,
                            fontSize = 11.sp,
                            color = TextDisabled
                        )
                    }
                    if (!stayAmount.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stayAmount,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JetBrainsMonoFamily,
                            color = Champagne
                        )
                    }
                }
                ApartmentState.LIMPEZA -> {
                    Text(
                        text = "Aguardando limpeza",
                        fontSize = 12.sp,
                        color = LimpezaColor
                    )
                }
                ApartmentState.MANUTENCAO -> {
                    Text(
                        text = if (apartment.maintenanceNote.isNotBlank())
                            apartment.maintenanceNote else "Em manutenção",
                        fontSize = 12.sp,
                        color = ManutencaoColor
                    )
                }
                ApartmentState.LIVRE -> {
                    Text(
                        text = "Disponível",
                        fontSize = 12.sp,
                        color = LivreColor
                    )
                }
            }
        }
    }
}
