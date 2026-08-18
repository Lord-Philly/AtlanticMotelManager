package com.atlantic.motel.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.math.min

object PhosphorIcons {
    val Regular = PhosphorRegular
}

object PhosphorRegular {
    val House: ImageVector by lazy { buildIcon("House", "M104,216V152h48v64h64V120a8,8,0,0,0-2.34-5.66l-80-80a8,8,0,0,0-11.32,0l-80,80A8,8,0,0,0,40,120v96Z") }
    val User: ImageVector by lazy { buildIcon("User", "M128,96a64,64,0,1,0-64-64A64.07,64.07,0,0,0,128,96Z", "M32,216c19.37-33.47,54.55-56,96-56s76.63,22.53,96,56") }
    val Lock: ImageVector by lazy { buildIcon("Lock", "M88,88V56a40,40,0,0,1,80,0V88", "M40,88h176v128a8,8,0,0,1-8,8H48a8,8,0,0,1-8-8Z") }
    val Plus: ImageVector by lazy { buildIcon("Plus", "M40,128H216", "M128,40V216") }
    val Minus: ImageVector by lazy { buildIcon("Minus", "M40,128H216") }
    val Package: ImageVector by lazy { buildIcon("Package", "M219.84,182.84l-88,48.18a8,8,0,0,1-7.68,0l-88-48.18a8,8,0,0,1-4.16-7V80.18a8,8,0,0,1,4.16-7l88-48.18a8,8,0,0,1,7.68,0l88,48.18a8,8,0,0,1,4.16,7v95.64A8,8,0,0,1,219.84,182.84Z", "M128,129.09V231.97") }
    val Calendar: ImageVector by lazy { buildIcon("Calendar", "M40,40H216a8,8,0,0,1,8,8V208a8,8,0,0,1-8,8H40a8,8,0,0,1-8-8V48A8,8,0,0,1,40,40Z", "M176,24V56", "M80,24V56", "M40,88H216", "M138.14,128a16,16,0,1,1,26.64,17.63L136,184h32") }
    val ClockCounterClockwise: ImageVector by lazy { buildIcon("Clock", "M128,56A88,88,0,1,0,216,144,88.1,88.1,0,0,0,128,56Z", "M128,80V128l28.28,28.29") }
    val SignOut: ImageVector by lazy { buildIcon("SignOut", "M112,128H224", "M184,96l32,32-32,32", "M200,40H120a8,8,0,0,0-8,8V208a8,8,0,0,0,8,8h80") }
    val ArrowLeft: ImageVector by lazy { buildIcon("ArrowLeft", "M216,128H40", "M104,56l-64,64,64,64") }
    val ShoppingCart: ImageVector by lazy { buildIcon("Cart", "M24,40H56l27.2,146.86a8,8,0,0,0,7.8,6.14h96.4a8,8,0,0,0,7.8-6.14L216,80H64", "M92,204a20,20,0,1,1-20-20A20,20,0,0,1,92,204Z", "M188,204a20,20,0,1,1-20-20A20,20,0,0,1,188,204Z") }
    val CheckCircle: ImageVector by lazy { buildIcon("CheckCircle", "M128,216a88,88,0,1,0-88-88A88,88,0,0,0,128,216Z", "M88,128l24,24,56-56") }
    val Trash: ImageVector by lazy { buildIcon("Trash", "M216,56H176V40a16,16,0,0,0-16-16H96A16,16,0,0,0,80,40V56H40a8,8,0,0,0,0,16H208a8,8,0,0,0,0-16Z", "M48,56l8,152a8,8,0,0,0,8,8H192a8,8,0,0,0,8-8l8-152", "M104,104v64", "M152,104v64") }
    val PencilSimple: ImageVector by lazy { buildIcon("Pencil", "M92.69,216H48a8,8,0,0,1-8-8V163.31a8,8,0,0,1,2.34-5.65L165.66,34.34a8,8,0,0,1,11.31,0L221.66,79a8,8,0,0,1,0,11.31L98.34,213.66A8,8,0,0,1,92.69,216Z", "M136,64l56,56") }
    val UserCircle: ImageVector by lazy { buildIcon("UserCircle", "M128,24a104,104,0,1,0,104,104A104.11,104.11,0,0,0,128,24Z", "M128,120a40,40,0,1,0-40-40A40,40,0,0,0,128,120Z", "M63.8,199.37a72,72,0,0,1,128.4,0") }
    val Broom: ImageVector by lazy { buildIcon("Broom", "M192,152c0,31.67-13.31,59-40,72H61A103.65,103.65,0,0,1,32,152c0-28.21,11.23-50.89,29.47-69.64a8,8,0,0,1,8.67-1.81L95.52,90.83a16,16,0,0,0,20.82-9l21-53.11c4.15-10,15.47-15.32,25.63-11.53a20,20,0,0,1,11.51,26.4L153.13,96.69a16,16,0,0,0,8.93,20.76L187,127.29a8,8,0,0,1,5,7.43Z", "M43.93,105.57l148.87,59.55") }
    val Wrench: ImageVector by lazy { buildIcon("Wrench", "M192.35,104a64.07,64.07,0,0,1-65.41,65.41h0L73,217a24,24,0,0,1-34-34l47.57-53.93A64.07,64.07,0,0,1,192.35,104Z", "M144,80l5.66,26.34L176,112l43.35-40a64,64,0,0,0-80.29-8.29") }
    val Money: ImageVector by lazy { buildIcon("Money", "M16,64V192a8,8,0,0,0,8,8H232a8,8,0,0,0,8-8V64a8,8,0,0,0-8-8H24A8,8,0,0,0,16,64Z", "M128,168a40,40,0,1,0,40-40A40,40,0,0,0,128,168Z") }
    val QrCode: ImageVector by lazy { buildIcon("QrCode", "M48,48H112v64H48Z", "M48,144h64v64H48Z", "M144,48h64v64H144Z", "M144,144v32", "M176,160h32", "M208,192v16", "M144,192h16v16H144Z", "M192,144h16v16H192Z") }
    val CreditCard: ImageVector by lazy { buildIcon("CreditCard", "M24,56h208a8,8,0,0,1,8,8V200a8,8,0,0,1-8,8H24a8,8,0,0,1-8-8V64A8,8,0,0,1,24,56Z", "M16,104H240", "M168,168h32", "M120,168h16") }
    val Eye: ImageVector by lazy { buildIcon("Eye", "M246.13,117.76S208,56,128,56,9.87,117.76,9.87,117.76,48,192,128,192s118.13-74.24,118.13-74.24", "M128,152a40,40,0,1,0,0-80,40,40,0,0,0,0,80Z") }
    val EyeSlash: ImageVector by lazy { buildIcon("EyeSlash", "M24.49,143.79C36.58,129.13,70.67,72,128,72a132.67,132.67,0,0,1,38.59,5.79", "M200.12,112.48C213.64,124.62,232,152,232,152S194.26,216,128,216c-17.19,0-32.84-4.7-46.19-12.38", "M48,48L208,208") }
    val Key: ImageVector by lazy { buildIcon("Key", "M88.6,92.58A71.66,71.66,0,0,1,160.09,24h.55A72.08,72.08,0,0,1,232,96v0a72.08,72.08,0,0,1-71.36,72H161a71.66,71.66,0,0,1-50.4-20.77", "M120,176H96v24H72v24H40a8,8,0,0,1-8-8V187.31a8,8,0,0,1,2.34-5.65l58.83-58.83") }
    val Female: ImageVector by lazy { buildIcon("Female", "M128,96a72,72,0,1,0-72-72A72.08,72.08,0,0,0,128,96Z", "M128,168V240", "M88,208H168") }
    val Male: ImageVector by lazy { buildIcon("Male", "M104,152a72,72,0,1,0-72-72A72.08,72.08,0,0,0,104,152Z", "M154.91,101.09L216,40", "M168,40H216V88") }
}

private fun buildIcon(name: String, vararg svgPaths: String): ImageVector {
    val builder = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 256f,
        viewportHeight = 256f
    )
    for (svgPath in svgPaths) {
        builder.path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 16f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            parseSvgPath(svgPath)
        }
    }
    return builder.build()
}

private fun PathBuilder.parseSvgPath(d: String) {
    val tokens = tokenizeSvgPath(d)
    var i = 0
    var curX = 0f
    var curY = 0f
    var startX = 0f
    var startY = 0f
    var lastCx = 0f
    var lastCy = 0f

    while (i < tokens.size) {
        val cmd = tokens[i]
        when (cmd) {
            "M" -> {
                curX = tokens[i + 1].toFloat(); curY = tokens[i + 2].toFloat()
                startX = curX; startY = curY
                moveTo(curX, curY)
                i += 3
            }
            "m" -> {
                curX += tokens[i + 1].toFloat(); curY += tokens[i + 2].toFloat()
                startX = curX; startY = curY
                moveTo(curX, curY)
                i += 3
            }
            "L" -> {
                curX = tokens[i + 1].toFloat(); curY = tokens[i + 2].toFloat()
                lineTo(curX, curY)
                i += 3
            }
            "l" -> {
                curX += tokens[i + 1].toFloat(); curY += tokens[i + 2].toFloat()
                lineTo(curX, curY)
                i += 3
            }
            "H" -> {
                curX = tokens[i + 1].toFloat()
                lineTo(curX, curY)
                i += 2
            }
            "h" -> {
                curX += tokens[i + 1].toFloat()
                lineTo(curX, curY)
                i += 2
            }
            "V" -> {
                curY = tokens[i + 1].toFloat()
                lineTo(curX, curY)
                i += 2
            }
            "v" -> {
                curY += tokens[i + 1].toFloat()
                lineTo(curX, curY)
                i += 2
            }
            "C" -> {
                val x1 = tokens[i + 1].toFloat(); val y1 = tokens[i + 2].toFloat()
                val x2 = tokens[i + 3].toFloat(); val y2 = tokens[i + 4].toFloat()
                curX = tokens[i + 5].toFloat(); curY = tokens[i + 6].toFloat()
                lastCx = x2; lastCy = y2
                curveTo(x1, y1, x2, y2, curX, curY)
                i += 7
            }
            "c" -> {
                val x1 = curX + tokens[i + 1].toFloat(); val y1 = curY + tokens[i + 2].toFloat()
                val x2 = curX + tokens[i + 3].toFloat(); val y2 = curY + tokens[i + 4].toFloat()
                curX += tokens[i + 5].toFloat(); curY += tokens[i + 6].toFloat()
                lastCx = x2; lastCy = y2
                curveTo(x1, y1, x2, y2, curX, curY)
                i += 7
            }
            "S" -> {
                val x2 = tokens[i + 1].toFloat(); val y2 = tokens[i + 2].toFloat()
                curX = tokens[i + 3].toFloat(); curY = tokens[i + 4].toFloat()
                val x1 = curX * 2 - lastCx; val y1 = curY * 2 - lastCy
                lastCx = x2; lastCy = y2
                curveTo(x1, y1, x2, y2, curX, curY)
                i += 5
            }
            "s" -> {
                val x2 = curX + tokens[i + 1].toFloat(); val y2 = curY + tokens[i + 2].toFloat()
                curX += tokens[i + 3].toFloat(); curY += tokens[i + 4].toFloat()
                val x1 = curX * 2 - lastCx; val y1 = curY * 2 - lastCy
                lastCx = x2; lastCy = y2
                curveTo(x1, y1, x2, y2, curX, curY)
                i += 5
            }
            "Q" -> {
                val x1 = tokens[i + 1].toFloat(); val y1 = tokens[i + 2].toFloat()
                curX = tokens[i + 3].toFloat(); curY = tokens[i + 4].toFloat()
                lastCx = x1; lastCy = y1
                quadTo(x1, y1, curX, curY)
                i += 5
            }
            "q" -> {
                val x1 = curX + tokens[i + 1].toFloat(); val y1 = curY + tokens[i + 2].toFloat()
                curX += tokens[i + 3].toFloat(); curY += tokens[i + 4].toFloat()
                lastCx = x1; lastCy = y1
                quadTo(x1, y1, curX, curY)
                i += 5
            }
            "A" -> {
                val rx = tokens[i + 1].toFloat(); val ry = tokens[i + 2].toFloat()
                val angle = tokens[i + 3].toFloat()
                val largeArc = tokens[i + 4].toInt() == 1
                val sweep = tokens[i + 5].toInt() == 1
                curX = tokens[i + 6].toFloat(); curY = tokens[i + 7].toFloat()
                arcTo(rx, ry, angle, largeArc, sweep, curX, curY)
                i += 8
            }
            "a" -> {
                val rx = tokens[i + 1].toFloat(); val ry = tokens[i + 2].toFloat()
                val angle = tokens[i + 3].toFloat()
                val largeArc = tokens[i + 4].toInt() == 1
                val sweep = tokens[i + 5].toInt() == 1
                curX += tokens[i + 6].toFloat(); curY += tokens[i + 7].toFloat()
                arcTo(rx, ry, angle, largeArc, sweep, curX, curY)
                i += 8
            }
            "Z", "z" -> {
                close()
                curX = startX; curY = startY
                i += 1
            }
            else -> i += 1
        }
    }
}

private fun tokenizeSvgPath(d: String): List<String> {
    val tokens = mutableListOf<String>()
    var i = 0
    val len = d.length
    while (i < len) {
        val c = d[i]
        if (c.isWhitespace() || c == ',') {
            i++
            continue
        }
        if (c == '-' || c == '+' || c == '.') {
            if (c == '-' && tokens.isNotEmpty() && tokens.last().last().let { it == 'E' || it == 'e' }) {
                val last = tokens.removeLast()
                var end = i + 1
                while (end < len && (d[end].isDigit() || d[end] == '.')) end++
                tokens.add(last + d.substring(i, end))
                i = end
            } else {
                var end = i + 1
                while (end < len && (d[end].isDigit() || d[end] == '.')) end++
                tokens.add(d.substring(i, end))
                i = end
            }
        } else if (c.isLetter()) {
            tokens.add(c.toString())
            i++
        } else if (c.isDigit()) {
            var end = i
            while (end < len && (d[end].isDigit() || d[end] == '.')) end++
            if (end < len && (d[end] == 'e' || d[end] == 'E')) {
                end++
                if (end < len && (d[end] == '+' || d[end] == '-')) end++
                while (end < len && d[end].isDigit()) end++
            }
            tokens.add(d.substring(i, end))
            i = end
        } else {
            i++
        }
    }
    return tokens
}
