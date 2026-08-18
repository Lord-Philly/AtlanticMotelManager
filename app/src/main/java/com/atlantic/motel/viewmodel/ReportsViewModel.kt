package com.atlantic.motel.viewmodel

import android.app.Application
import android.content.Context
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

    fun exportReport(period: ReportPeriod, context: Context) {
        viewModelScope.launch {
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
            val start = cal.timeInMillis

            val payments = paymentDao.getAllBetweenSync(start, end)
            val stays = stayDao.getRecent(999).first()

            val apartments = apartmentDao.getAllSync()
            val aptMap = apartments.associateBy { it.id }

            val sb = StringBuilder()
            val title = when (period) {
                ReportPeriod.DIARIO -> "RELATÓRIO DIÁRIO"
                ReportPeriod.SEMANAL -> "RELATÓRIO SEMANAL"
                ReportPeriod.MENSAL -> "RELATÓRIO MENSAL"
            }
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

    fun clearExportState() {
        _exportState.value = null
    }
}
