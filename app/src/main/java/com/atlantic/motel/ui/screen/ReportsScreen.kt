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
    val exportPdfState by viewModel.exportPdfState.collectAsState()

    LaunchedEffect(exportState) {
        exportState?.let { path ->
            if (!path.startsWith("ERRO")) {
                shareFile(context, path, "text/plain", "Compartilhar Relatório")
            }
            viewModel.clearExportState()
        }
    }

    LaunchedEffect(exportPdfState) {
        exportPdfState?.let { path ->
            if (!path.startsWith("ERRO")) {
                shareFile(context, path, "application/pdf", "Compartilhar Relatório PDF")
            }
            viewModel.clearExportPdfState()
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

            ReportSection(
                title = "Diário",
                onTxt = { viewModel.exportReport(ReportPeriod.DIARIO, context) },
                onPdf = { viewModel.exportReportPdf(ReportPeriod.DIARIO, context) }
            )
            ReportSection(
                title = "Semanal",
                subtitle = "Últimos 7 dias",
                onTxt = { viewModel.exportReport(ReportPeriod.SEMANAL, context) },
                onPdf = { viewModel.exportReportPdf(ReportPeriod.SEMANAL, context) }
            )
            ReportSection(
                title = "Mensal",
                subtitle = "Últimos 30 dias",
                onTxt = { viewModel.exportReport(ReportPeriod.MENSAL, context) },
                onPdf = { viewModel.exportReportPdf(ReportPeriod.MENSAL, context) }
            )
        }
    }
}

private fun shareFile(context: android.content.Context, path: String, mimeType: String, title: String) {
    val file = java.io.File(path)
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(Intent.createChooser(intent, title))
}

@Composable
fun ReportSection(
    title: String,
    subtitle: String = "",
    onTxt: () -> Unit,
    onPdf: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceBlack),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = DeepCrimson,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                    if (subtitle.isNotBlank()) {
                        Text(subtitle, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTxt,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.TextSnippet, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("TXT", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Button(
                    onClick = onPdf,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Burgundy, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PDF", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = DeepCrimson,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                Text(subtitle, fontSize = 12.sp, color = TextSecondary)
            }
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.FileDownload,
                    contentDescription = null,
                    tint = TextDisabled,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
