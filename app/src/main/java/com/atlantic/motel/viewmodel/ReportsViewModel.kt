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

data class ReportEntry(
    val apartmentNumber: String,
    val guestName: String,
    val entryTime: String,
    val exitTime: String,
    val paymentMethod: String,
    val amountFormatted: String,
    val amountInCents: Long
)

class ReportsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val paymentDao = db.paymentDao()
    private val stayDao = db.stayDao()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val fullDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    private val _exportState = MutableStateFlow<String?>(null)
    val exportState: StateFlow<String?> = _exportState

    private val _exportPdfState = MutableStateFlow<String?>(null)
    val exportPdfState: StateFlow<String?> = _exportPdfState

    private fun paymentMethodLabel(method: PaymentMethod): String = when (method) {
        PaymentMethod.DINHEIRO -> "Dinheiro"
        PaymentMethod.PIX -> "PIX"
        PaymentMethod.CARTAO -> "Cartão"
    }

    private suspend fun buildReportEntries(start: Long, end: Long): List<ReportEntry> {
        val payments = paymentDao.getAllBetweenSync(start, end)
        return payments.map { payment ->
            val stay = stayDao.getById(payment.stayId)
            val guestName = stay?.guestName?.ifBlank { "-" } ?: "-"
            val entryTime = stay?.startTime?.let { timeFormat.format(Date(it)) } ?: "-"
            val exitTime = stay?.endTime?.let { if (it > 0) timeFormat.format(Date(it)) else "Em andamento" } ?: "Em andamento"
            ReportEntry(
                apartmentNumber = payment.apartmentNumber,
                guestName = guestName,
                entryTime = entryTime,
                exitTime = exitTime,
                paymentMethod = paymentMethodLabel(payment.paymentMethod),
                amountFormatted = BillingEngine.formatCurrency(payment.totalInCents),
                amountInCents = payment.totalInCents
            )
        }
    }

    private fun periodTitle(period: ReportPeriod): String = when (period) {
        ReportPeriod.DIARIO -> "RELATÓRIO DIÁRIO"
        ReportPeriod.SEMANAL -> "RELATÓRIO SEMANAL"
        ReportPeriod.MENSAL -> "RELATÓRIO MENSAL"
    }

    private fun periodSubtitle(period: ReportPeriod): String = when (period) {
        ReportPeriod.DIARIO -> "HOSPEDAGENS"
        ReportPeriod.SEMANAL -> "HOSPEDAGENS"
        ReportPeriod.MENSAL -> "HOSPEDAGENS"
    }

    private fun periodDateLabel(period: ReportPeriod, start: Long): String = when (period) {
        ReportPeriod.DIARIO -> "Data: ${dateFormat.format(Date(start))}"
        ReportPeriod.SEMANAL -> "Período: ${dateFormat.format(Date(start))} — ${dateFormat.format(Date())}"
        ReportPeriod.MENSAL -> "Período: ${dateFormat.format(Date(start))} — ${dateFormat.format(Date())}"
    }

    fun exportReport(period: ReportPeriod, context: Context) {
        viewModelScope.launch {
            val (start, end) = getPeriodRange(period)
            val entries = buildReportEntries(start, end)

            val sb = StringBuilder()
            sb.appendLine("${periodTitle(period)} — ${periodSubtitle(period)}")
            sb.appendLine(periodDateLabel(period, start))
            sb.appendLine()

            if (entries.isEmpty()) {
                sb.appendLine("Nenhuma hospedagem no período.")
            } else {
                sb.appendLine("AP | Funcionário | Entrada | Saída | Forma de pagamento | Valor")
                for (e in entries) {
                    sb.appendLine("${e.apartmentNumber} | ${e.guestName} | ${e.entryTime} | ${e.exitTime} | ${e.paymentMethod} | ${e.amountFormatted}")
                }
            }

            sb.appendLine()
            sb.appendLine("RESUMO DO PERÍODO")
            sb.appendLine("Total de hospedagens: ${entries.size}")
            val totalCents = entries.sumOf { it.amountInCents }
            sb.appendLine("Total arrecadado: ${BillingEngine.formatCurrency(totalCents)}")

            sb.appendLine()
            sb.appendLine("PAGAMENTOS")
            val byMethod = entries.groupBy { it.paymentMethod }
            for ((method, methodEntries) in byMethod) {
                val methodTotal = methodEntries.sumOf { it.amountInCents }
                sb.appendLine("- $method: ${BillingEngine.formatCurrency(methodTotal)}")
            }

            sb.appendLine()
            sb.appendLine("POR FUNCIONÁRIO")
            val byGuest = entries.groupBy { it.guestName }
            for ((guest, guestEntries) in byGuest) {
                val guestTotal = guestEntries.sumOf { it.amountInCents }
                val count = guestEntries.size
                val label = if (count == 1) "1 hospedagem" else "$count hospedagens"
                sb.appendLine("- $guest: $label — ${BillingEngine.formatCurrency(guestTotal)}")
            }

            sb.appendLine()
            sb.appendLine("Relatório gerado em: ${fullDateFormat.format(Date())}")

            try {
                val fileName = "relatorio_${period.name.lowercase()}_${System.currentTimeMillis()}.txt"
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
            val entries = buildReportEntries(start, end)

            try {
                val document = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = document.startPage(pageInfo)
                val canvas: Canvas = page.canvas

                val titlePaint = Paint().apply {
                    textSize = 16f
                    typeface = Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                    color = android.graphics.Color.parseColor("#1A1A1A")
                }
                val headerPaint = Paint().apply {
                    textSize = 12f
                    typeface = Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                    color = android.graphics.Color.parseColor("#5A0B16")
                }
                val bodyPaint = Paint().apply {
                    textSize = 10f
                    isAntiAlias = true
                    color = android.graphics.Color.parseColor("#333333")
                }
                val boldBodyPaint = Paint().apply {
                    textSize = 10f
                    typeface = Typeface.DEFAULT_BOLD
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
                val colAp = left
                val colFunc = left + 35f
                val colEntry = left + 180f
                val colExit = left + 235f
                val colMethod = left + 290f
                val colAmount = pageWidth - 40f

                canvas.drawText("${periodTitle(period)} — ${periodSubtitle(period)}", left, y, titlePaint)
                y += 18f
                canvas.drawText(periodDateLabel(period, start), left, y, smallPaint)
                y += 16f
                canvas.drawLine(left, y, pageWidth - 40f, y, dividerPaint)
                y += 18f

                canvas.drawText("AP", colAp, y, boldBodyPaint)
                canvas.drawText("Funcionário", colFunc, y, boldBodyPaint)
                canvas.drawText("Entrada", colEntry, y, boldBodyPaint)
                canvas.drawText("Saída", colExit, y, boldBodyPaint)
                canvas.drawText("Pagamento", colMethod, y, boldBodyPaint)
                canvas.drawText("Valor", colAmount - 60f, y, boldBodyPaint)
                y += 14f
                canvas.drawLine(left, y, pageWidth - 40f, y, dividerPaint)
                y += 14f

                if (entries.isEmpty()) {
                    canvas.drawText("Nenhuma hospedagem no período.", left, y, bodyPaint)
                    y += 14f
                } else {
                    for (e in entries) {
                        if (y > 760f) {
                            document.finishPage(page)
                            val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, document.pages.size + 1).create()
                            val newPage = document.startPage(newPageInfo)
                            y = 40f
                        }
                        canvas.drawText(e.apartmentNumber, colAp, y, bodyPaint)
                        canvas.drawText(e.guestName, colFunc, y, bodyPaint)
                        canvas.drawText(e.entryTime, colEntry, y, bodyPaint)
                        canvas.drawText(e.exitTime, colExit, y, bodyPaint)
                        canvas.drawText(e.paymentMethod, colMethod, y, bodyPaint)
                        canvas.drawText(e.amountFormatted, colAmount - 60f, y, bodyPaint)
                        y += 13f
                    }
                }

                y += 10f
                canvas.drawLine(left, y, pageWidth - 40f, y, dividerPaint)
                y += 16f

                canvas.drawText("RESUMO DO PERÍODO", left, y, headerPaint)
                y += 14f
                canvas.drawText("Total de hospedagens: ${entries.size}", left, y, bodyPaint)
                y += 12f
                val totalCents = entries.sumOf { it.amountInCents }
                canvas.drawText("Total arrecadado: ${BillingEngine.formatCurrency(totalCents)}", left, y, bodyPaint)
                y += 18f

                canvas.drawText("PAGAMENTOS", left, y, headerPaint)
                y += 14f
                val byMethod = entries.groupBy { it.paymentMethod }
                for ((method, methodEntries) in byMethod) {
                    val methodTotal = methodEntries.sumOf { it.amountInCents }
                    canvas.drawText("- $method: ${BillingEngine.formatCurrency(methodTotal)}", left, y, bodyPaint)
                    y += 12f
                }
                y += 6f

                canvas.drawText("POR FUNCIONÁRIO", left, y, headerPaint)
                y += 14f
                val byGuest = entries.groupBy { it.guestName }
                for ((guest, guestEntries) in byGuest) {
                    val guestTotal = guestEntries.sumOf { it.amountInCents }
                    val count = guestEntries.size
                    val label = if (count == 1) "1 hospedagem" else "$count hospedagens"
                    canvas.drawText("- $guest: $label — ${BillingEngine.formatCurrency(guestTotal)}", left, y, bodyPaint)
                    y += 12f
                }

                y += 14f
                canvas.drawLine(left, y, pageWidth - 40f, y, dividerPaint)
                y += 14f
                canvas.drawText("Relatório gerado em: ${fullDateFormat.format(Date())}", left, y, smallPaint)

                document.finishPage(page)

                val fileName = "relatorio_${period.name.lowercase()}_${System.currentTimeMillis()}.pdf"
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
