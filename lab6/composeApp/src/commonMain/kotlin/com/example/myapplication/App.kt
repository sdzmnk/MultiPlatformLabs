package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

data class CalculationResult(
    val shrKv: Double,
    val shrNe: Double,
    val shrPp: Double,
    val shrQp: Double,
    val shrSp: Double,
    val shrIp: Double,
    val shopKv: Double,
    val shopNe: Double,
    val shopPp: Double,
    val shopQp: Double,
    val shopSp: Double,
    val shopIp: Double
)

object ElectricalLoadCalculator {

    fun Double.roundTo(decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return round(this * factor) / factor
    }

    fun calculate(
        shrSumPh: Double, shrSumPhKv: Double, shrSumPhKvTg: Double, shrSumPh2: Double, shrKp: Double,
        shopSumPh: Double, shopSumPhKv: Double, shopSumPhKvTg: Double, shopSumPh2: Double, shopKp: Double
    ): CalculationResult {
        val shrKv = shrSumPhKv / shrSumPh
        val shrNe = ceil((shrSumPh * shrSumPh) / shrSumPh2)
        val shrPp = shrKp * shrSumPhKv
        val shrQp = 1.0 * shrSumPhKvTg
        val shrSp = sqrt(shrPp * shrPp + shrQp * shrQp)
        val shrIp = shrPp / 0.38

        val shopKv = shopSumPhKv / shopSumPh
        val shopNe = floor((shopSumPh * shopSumPh) / shopSumPh2)
        val shopPp = shopKp * shopSumPhKv
        val shopQp = shopKp * shopSumPhKvTg
        val shopSp = sqrt(shopPp * shopPp + shopQp * shopQp)
        val shopIp = shopPp / 0.38

        return CalculationResult(
            shrKv = shrKv.roundTo(4),
            shrNe = shrNe,
            shrPp = shrPp.roundTo(2),
            shrQp = shrQp.roundTo(3),
            shrSp = shrSp.roundTo(4),
            shrIp = shrIp.roundTo(2),
            shopKv = shopKv.roundTo(2),
            shopNe = shopNe,
            shopPp = shopPp.roundTo(1),
            shopQp = shopQp.roundTo(1),
            shopSp = shopSp.roundTo(0),
            shopIp = shopIp.roundTo(2)
        )
    }
}

@Composable
fun App() {
    var shrSumPh by remember { mutableStateOf("456") }
    var shrSumPhKv by remember { mutableStateOf("95.16") }
    var shrSumPhKvTg by remember { mutableStateOf("107.302") }
    var shrSumPh2 by remember { mutableStateOf("14732") }
    var shrKp by remember { mutableStateOf("1.25") }

    var shopSumPh by remember { mutableStateOf("2330") }
    var shopSumPhKv by remember { mutableStateOf("752") }
    var shopSumPhKvTg by remember { mutableStateOf("657") }
    var shopSumPh2 by remember { mutableStateOf("96388") }
    var shopKp by remember { mutableStateOf("0.7") }

    var result by remember { mutableStateOf<CalculationResult?>(null) }

    val bgColor = Color(0xFFF0F4F8)
    val primaryColor = Color(0xFF0284C7)

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = bgColor) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Електричні навантаження",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor,
                    modifier = Modifier.padding(vertical = 12.dp),
                    textAlign = TextAlign.Center
                )

                TaskCard(title = "Дані для ШР1", primaryColor = primaryColor) {
                    InputRow(
                        "∑n·Pн (кВт)", shrSumPh, { shrSumPh = it },
                        "∑n·Pн·Kв", shrSumPhKv, { shrSumPhKv = it }
                    )
                    InputRow(
                        "∑n·Pн·Kв·tgφ", shrSumPhKvTg, { shrSumPhKvTg = it },
                        "∑n·Pн²", shrSumPh2, { shrSumPh2 = it }
                    )
                    InputRow(
                        "Kр (за табл. 6.3)", shrKp, { shrKp = it },
                        null, "", {}
                    )
                }

                TaskCard(title = "Дані для Цеху в цілому", primaryColor = primaryColor) {
                    InputRow(
                        "∑Pн (кВт)", shopSumPh, { shopSumPh = it },
                        "∑Pн·Kв", shopSumPhKv, { shopSumPhKv = it }
                    )
                    InputRow(
                        "∑Pн·Kв·tgφ", shopSumPhKvTg, { shopSumPhKvTg = it },
                        "∑n·Pн²", shopSumPh2, { shopSumPh2 = it }
                    )
                    InputRow(
                        "Kр (за табл. 6.4)", shopKp, { shopKp = it },
                        null, "", {}
                    )
                }

                CalculateButton(primaryColor) {
                    result = ElectricalLoadCalculator.calculate(
                        shrSumPh.toDoubleOrNull() ?: 0.0,
                        shrSumPhKv.toDoubleOrNull() ?: 0.0,
                        shrSumPhKvTg.toDoubleOrNull() ?: 0.0,
                        shrSumPh2.toDoubleOrNull() ?: 1.0,
                        shrKp.toDoubleOrNull() ?: 0.0,
                        shopSumPh.toDoubleOrNull() ?: 0.0,
                        shopSumPhKv.toDoubleOrNull() ?: 0.0,
                        shopSumPhKvTg.toDoubleOrNull() ?: 0.0,
                        shopSumPh2.toDoubleOrNull() ?: 1.0,
                        shopKp.toDoubleOrNull() ?: 0.0
                    )
                }

                result?.let { res ->
                    Spacer(modifier = Modifier.height(16.dp))

                    TaskCard(title = "Результати для ШР1", primaryColor = primaryColor) {
                        ResultRow("Груповий коефіцієнт (Kв)", "${res.shrKv}")
                        ResultRow("Ефективна кількість (ne)", "${res.shrNe.toInt()}")
                        ResultRow("Активне навантаження (Pp)", "${res.shrPp} кВт")
                        ResultRow("Реактивне навантаження (Qp)", "${res.shrQp} квар")
                        ResultRow("Повна потужність (Sp)", "${res.shrSp} кВ*А")
                        ResultRow("Розрахунковий струм (Ip)", "${res.shrIp} А", isBold = true, textColor = primaryColor)
                    }

                    TaskCard(title = "Результати для Цеху", primaryColor = primaryColor) {
                        ResultRow("Коефіцієнт використання (Kв)", "${res.shopKv}")
                        ResultRow("Ефективна кількість (ne)", "${res.shopNe.toInt()}")
                        ResultRow("Активне навантаження (Pp)", "${res.shopPp} кВт")
                        ResultRow("Реактивне навантаження (Qp)", "${res.shopQp} квар")
                        ResultRow("Повна потужність (Sp)", "${res.shopSp} кВ*А")
                        ResultRow("Розрахунковий струм (Ip)", "${res.shopIp} А", isBold = true, textColor = primaryColor)
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(title: String, primaryColor: Color, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth().padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            content()
        }
    }
}

@Composable
fun InputRow(
    label1: String, value1: String, onValueChange1: (String) -> Unit,
    label2: String?, value2: String, onValueChange2: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ModernInput(label1, value1, onValueChange1, Modifier.weight(1f))
        if (label2 != null) {
            ModernInput(label2, value2, onValueChange2, Modifier.weight(1f))
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ModernInput(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 15.sp,
            lineHeight = 18.sp
        ),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color(0xFFCBD5E1),
            focusedBorderColor = Color(0xFF0284C7),
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        )
    )
}

@Composable
fun CalculateButton(primaryColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text("Розрахувати навантаження", fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ResultRow(label: String, value: String, isBold: Boolean = false, textColor: Color = Color(0xFF334155)) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color(0xFF64748B), fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold,
            fontSize = if (isBold) 15.sp else 13.sp,
            color = textColor,
            textAlign = TextAlign.End
        )
    }
}