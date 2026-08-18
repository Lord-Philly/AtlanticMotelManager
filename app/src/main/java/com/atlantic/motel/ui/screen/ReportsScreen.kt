package com.atlantic.motel.ui.screen

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atlantic.motel.ui.theme.*
import com.atlantic.motel.viewmodel.ReportPeriod
import com.atlantic.motel.viewmodel.ReportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onBack: () -> Unit,
    onHistoryClick: () -> Unit = {},
    viewModel: ReportsViewModel = viewModel()
) {
    val context = LocalContext.current
    val exportState by viewModel.exportState.collectAsState()

    LaunchedEffect(exportState) {
        exportState?.let { path ->
            if (!path.startsWith("ERRO")) {
                val file = java.io.File(path)
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(intent, "Compartilhar Relatório"))
            }
            viewModel.clearExportState()
        }
    }

    Scaffold(
        containerColor = DeepBlack,
        topBar = {
            TopAppBar(
                title = { Text("Relatórios", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontFamily = CormorantGaramondFamily) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TextSnippet, null, tint = DeepCrimson, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Relatórios",
                    fontFamily = CormorantGaramondFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = TextPrimary
                )
            }
            Text(
                "Exporte relatórios com total de capital, funcionários, apartamentos e formas de pagamento.",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            ReportButton(
                icon = Icons.Default.CurrencyExchange,
                title = "Histórico de Pagamentos",
                subtitle = "Ver todos os registros",
                onClick = onHistoryClick
            )

            ReportButton(
                icon = Icons.Default.CalendarToday,
                title = "Relatório Diário",
                subtitle = "Total e pagamentos de hoje",
                onClick = { viewModel.exportReport(ReportPeriod.DIARIO, context) }
            )
            ReportButton(
                icon = Icons.Default.DateRange,
                title = "Relatório Semanal",
                subtitle = "Últimos 7 dias",
                onClick = { viewModel.exportReport(ReportPeriod.SEMANAL, context) }
            )
            ReportButton(
                icon = Icons.Default.CalendarMonth,
                title = "Relatório Mensal",
                subtitle = "Últimos 30 dias",
                onClick = { viewModel.exportReport(ReportPeriod.MENSAL, context) }
            )
        }
    }
}

@Composable
fun ReportButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceBlack),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, null, tint = DeepCrimson, modifier = Modifier.size(28.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                Text(subtitle, fontSize = 12.sp, color = TextSecondary)
            }
            Icon(Icons.Default.FileDownload, null, tint = TextDisabled, modifier = Modifier.size(20.dp))
        }
    }
}
