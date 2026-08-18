package com.atlantic.motel.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.Typeface
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.atlantic.motel.billing.BillingEngine
import com.atlantic.motel.data.database.AppDatabase
import com.atlantic.motel.data.model.PaymentMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

enum class ReportPeriod {
    DIARIO,
    SEMANAL,
    MENSAL
}

class ReportsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val paymentDao = db.paymentDao()
    private val stayDao = db.stayDao()
    private val apartmentDao = db.apartmentDao()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    private val _exportState = MutableStateFlow<String?>(null)
    val exportState: StateFlow<String?> = _exportState

    private val _exportPdfState = MutableStateFlow<String?>(null)
    val exportPdfState: StateFlow<String?> = _exportPdfState

    fun exportReport(period: ReportPeriod, context: Context) {
        viewModelScope.launch {
            val (start, end) = getPeriodRange(period)
            val payments = paymentDao.getAllBetweenSync(start, end)
            val stays = stayDao.getRecent(999).first()
            val apartments = apartmentDao.getAllSync()
            val aptMap = apartments.associateBy { it.id }

            val title = when (period) {
                ReportPeriod.DIARIO -> "RELATÓRIO DIÁRIO"
                ReportPeriod.SEMANAL -> "RELATÓRIO SEMANAL"
                ReportPeriod.MENSAL -> "RELATÓRIO MENSAL"
            }

            val sb = StringBuilder()
            sb.appendLine("═══════════════════════════")
            sb.appendLine("  MOTEL MANAGER")
            sb.appendLine("  $title")
            sb.appendLine("  ${dateFormat.format(Date(start))} — ${dateFormat.format(Date(end))}")
            sb.appendLine("═══════════════════════════")
            sb.appendLine()

            val totalGeral = payments.sumOf { it.totalInCents }
            sb.appendLine("TOTAL GERAL: ${BillingEngine.formatCurrency(totalGeral)}")
            sb.appendLine("  ${payments.size} pagamento(s) registrado(s)")
            sb.appendLine()
            sb.appendLine("─── HOSPEDAGENS ───")
            val recentStays = stays.filter { it.startTime >= start && it.startTime <= end }
            if (recentStays.isEmpty()) {
                sb.appendLine("  Nenhuma hospedagem no periodo.")
            } else {
                for (stay in recentStays) {
                    val aptNum = aptMap[stay.apartmentId]?.number ?: "?"
                    val entrada = dateFormat.format(Date(stay.startTime))
                    val saida = if (stay.endTime != null && stay.endTime > 0) dateFormat.format(Date(stay.endTime)) else "Em andamento"
                    sb.appendLine("  Apt $aptNum")
                    sb.appendLine("    Entrada: $entrada")
                    sb.appendLine("    Saida:   $saida")
                    sb.appendLine()
                }
            }
            sb.appendLine("─── PAGAMENTOS ───")
            if (payments.isEmpty()) {
                sb.appendLine("  Nenhum pagamento no periodo.")
            } else {
                for (p in payments) {
                    val method = when (p.paymentMethod) {
                        PaymentMethod.DINHEIRO -> "Dinheiro"
                        PaymentMethod.PIX -> "PIX"
                        PaymentMethod.CARTAO -> "Cartao"
                    }
                    sb.appendLine("  Apt ${p.apartmentNumber} | $method | ${BillingEngine.formatCurrency(p.totalInCents)}")
                }
            }
            sb.appendLine()
            sb.appendLine("═══════════════════════════")
            sb.appendLine("  Gerado em ${dateFormat.format(Date())}")
            sb.appendLine("═══════════════════════════")

            try {
                val fileName = "relato_${period.name.lowercase()}_${System.currentTimeMillis()}.txt"
                val file = File(context.cacheDir, fileName)
                file.writeText(sb.toString())
                _exportState.value = file.absolutePath
            } catch (e: Exception) {
                _exportState.value = "ERRO: ${e.message}"
            }
        }
    }

    fun exportReportPdf(period: ReportPeriod, context: Context) {
        viewModelScope.launch {
            val (start, end) = getPeriodRange(period)
            val payments = paymentDao.getAllBetweenSync(start, end)
            val stays = stayDao.getRecent(999).first()
            val apartments = apartmentDao.getAllSync()
            val aptMap = apartments.associateBy { it.id }

            val title = when (period) {
                ReportPeriod.DIARIO -> "RELATÓRIO DIÁRIO"
                ReportPeriod.SEMANAL -> "RELATÓRIO SEMANAL"
                ReportPeriod.MENSAL -> "RELATÓRIO MENSAL"
            }

            try {
                val document = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = document.startPage(pageInfo)
                val canvas: Canvas = page.canvas

                val titlePaint = Paint().apply {
                    textSize = 18f
                    typeface = Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                    color = android.graphics.Color.parseColor("#1A1A1A")
                }
                val headerPaint = Paint().apply {
                    textSize = 14f
                    typeface = Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                    color = android.graphics.Color.parseColor("#5A0B16")
                }
                val bodyPaint = Paint().apply {
                    textSize = 11f
                    isAntiAlias = true
                    color = android.graphics.Color.parseColor("#333333")
                }
                val smallPaint = Paint().apply {
                    textSize = 9f
                    isAntiAlias = true
                    color = android.graphics.Color.parseColor("#888888")
                }
                val dividerPaint = Paint().apply {
                    strokeWidth = 0.5f
                    color = android.graphics.Color.parseColor("#CCCCCC")
                }

                var y = 40f
                val left = 40f
                val pageWidth = 595f

                canvas.drawText("MOTEL MANAGER", left, y, titlePaint)
                y += 22f
                canvas.drawText(title, left, y, headerPaint)
                y += 16f
                canvas.drawText("${dateFormat.format(Date(start))} — ${dateFormat.format(Date(end))}", left, y, smallPaint)
                y += 20f
                canvas.drawLine(left, y, pageWidth - 40f, y, dividerPaint)
                y += 20f

                val totalGeral = payments.sumOf { it.totalInCents }
                canvas.drawText("TOTAL GERAL: ${BillingEngine.formatCurrency(totalGeral)}", left, y, headerPaint)
                y += 14f
                canvas.drawText("${payments.size} pagamento(s) registrado(s)", left, y, bodyPaint)
                y += 24f

                canvas.drawText("HOSPEDAGENS", left, y, headerPaint)
                y += 16f
                val recentStays = stays.filter { it.startTime >= start && it.startTime <= end }
                if (recentStays.isEmpty()) {
                    canvas.drawText("Nenhuma hospedagem no periodo.", left, y, bodyPaint)
                    y += 14f
                } else {
                    for (stay in recentStays) {
                        val aptNum = aptMap[stay.apartmentId]?.number ?: "?"
                        val entrada = dateFormat.format(Date(stay.startTime))
                        val saida = if (stay.endTime != null && stay.endTime > 0) dateFormat.format(Date(stay.endTime)) else "Em andamento"
                        canvas.drawText("Apt $aptNum  |  Entrada: $entrada  |  Saida: $saida", left, y, bodyPaint)
                        y += 14f
                    }
                }
                y += 10f

                canvas.drawText("PAGAMENTOS", left, y, headerPaint)
                y += 16f
                if (payments.isEmpty()) {
                    canvas.drawText("Nenhum pagamento no periodo.", left, y, bodyPaint)
                    y += 14f
                } else {
                    for (p in payments) {
                        val method = when (p.paymentMethod) {
                            PaymentMethod.DINHEIRO -> "Dinheiro"
                            PaymentMethod.PIX -> "PIX"
                            PaymentMethod.CARTAO -> "Cartao"
                        }
                        canvas.drawText("Apt ${p.apartmentNumber}  |  $method  |  ${BillingEngine.formatCurrency(p.totalInCents)}", left, y, bodyPaint)
                        y += 14f
                    }
                }

                y += 20f
                canvas.drawLine(left, y, pageWidth - 40f, y, dividerPaint)
                y += 14f
                canvas.drawText("Gerado em ${dateFormat.format(Date())}", left, y, smallPaint)

                document.finishPage(page)

                val fileName = "relato_${period.name.lowercase()}_${System.currentTimeMillis()}.pdf"
                val file = File(context.cacheDir, fileName)
                FileOutputStream(file).use { out ->
                    document.writeTo(out)
                }
                document.close()
                _exportPdfState.value = file.absolutePath
            } catch (e: Exception) {
                _exportPdfState.value = "ERRO: ${e.message}"
            }
        }
    }

    fun clearExportState() {
        _exportState.value = null
    }

    fun clearExportPdfState() {
        _exportPdfState.value = null
    }

    private fun getPeriodRange(period: ReportPeriod): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        val end = cal.timeInMillis
        when (period) {
            ReportPeriod.DIARIO -> cal.set(Calendar.HOUR_OF_DAY, 0)
            ReportPeriod.SEMANAL -> cal.add(Calendar.DAY_OF_YEAR, -7)
            ReportPeriod.MENSAL -> cal.add(Calendar.MONTH, -1)
        }
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return Pair(cal.timeInMillis, end)
    }
}
