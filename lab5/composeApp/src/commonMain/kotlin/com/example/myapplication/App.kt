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
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

data class Task1Result(
    val omegaSum: Double,
    val tvos: Double,
    val kaos: Double,
    val kpos: Double,
    val doubleOmega: Double,
    val systemOmega: Double
)

data class Task2Result(
    val wneda: Double,
    val wnedp: Double,
    val zper: Double
)

object ReliabilityCalculator {

    private fun Double.roundTo(decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return round(this * factor) / factor
    }

    fun calculateTask1(
        pl110Omega: Double, t110Omega: Double, v110Omega: Double, v10Omega: Double, tiresOmega: Double,
        pl110Tvi: Double, t110Tvi: Double, v110Tvi: Double, v10Tvi: Double, tiresTvi: Double,
        plannedKMax: Double
    ): Task1Result {
        val omegaSum = pl110Omega * 10 + t110Omega + v110Omega + v10Omega + 6 * tiresOmega
        val tvos = (pl110Omega * 10 * pl110Tvi + t110Omega * t110Tvi + v110Omega * v110Tvi + v10Omega * v10Tvi + tiresOmega * 6 * tiresTvi) / omegaSum
        val kaos = (omegaSum * tvos) / 8760.0
        val kpos = 1.2 * (plannedKMax / 8760.0)
        val doubleOmega = 2 * omegaSum * (kaos + kpos)
        val systemOmega = doubleOmega + 0.02

        return Task1Result(
            omegaSum = omegaSum.roundTo(4),
            tvos = tvos.roundTo(2),
            kaos = kaos,
            kpos = kpos,
            doubleOmega = doubleOmega,
            systemOmega = systemOmega.roundTo(4)
        )
    }

    fun calculateTask2(
        zperA: Double, zperP: Double, omega: Double, tv: Double, pm: Double, tm: Double, kp: Double
    ): Task2Result {
        val wneda = omega * tv * pm * tm
        val wnedp = kp * pm * tm
        val zper = zperA * wneda + zperP * wnedp

        return Task2Result(
            wneda = wneda.roundTo(0),
            wnedp = wnedp.roundTo(0),
            zper = zper.roundTo(0)
        )
    }
}

@Composable
fun App() {
    var pl110Omega by remember { mutableStateOf("0.007") }
    var t110Omega by remember { mutableStateOf("0.015") }
    var v110Omega by remember { mutableStateOf("0.01") }
    var v10Omega by remember { mutableStateOf("0.02") }
    var tiresOmega by remember { mutableStateOf("0.03") }
    var pl110Tvi by remember { mutableStateOf("10") }
    var t110Tvi by remember { mutableStateOf("100") }
    var v110Tvi by remember { mutableStateOf("30") }
    var v10Tvi by remember { mutableStateOf("15") }
    var tiresTvi by remember { mutableStateOf("2") }
    var plannedKMax by remember { mutableStateOf("43") }

    var res1 by remember { mutableStateOf<Task1Result?>(null) }

    var zperA by remember { mutableStateOf("23.6") }
    var zperP by remember { mutableStateOf("17.6") }
    var omega2 by remember { mutableStateOf("0.01") }
    var tv2 by remember { mutableStateOf("0.045") }
    var pm2 by remember { mutableStateOf("5120") }
    var tm2 by remember { mutableStateOf("6451") }
    var kp2 by remember { mutableStateOf("0.004") }

    var res2 by remember { mutableStateOf<Task2Result?>(null) }

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
                    text = "Надійність та Збитки",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor,
                    modifier = Modifier.padding(vertical = 12.dp),
                    textAlign = TextAlign.Center
                )

                TaskCard(title = "Завдання 1: Частоти відмов", primaryColor = primaryColor) {
                    InputRow(
                        "ПЛ-110 кВ (Ω)", pl110Omega, { pl110Omega = it },
                        "Т-110 кВ (Ω)", t110Omega, { t110Omega = it }
                    )
                    InputRow(
                        "В-110 кВ (Ω)", v110Omega, { v110Omega = it },
                        "В-10 кВ (Ω)", v10Omega, { v10Omega = it }
                    )
                    InputRow(
                        "Шини 10кВ (Ω)", tiresOmega, { tiresOmega = it },
                        "ПЛ-110 (t)", pl110Tvi, { pl110Tvi = it }
                    )
                    InputRow(
                        "Т-110 (t)", t110Tvi, { t110Tvi = it },
                        "В-110 (t)", v110Tvi, { v110Tvi = it }
                    )
                    InputRow(
                        "В-10 (t)", v10Tvi, { v10Tvi = it },
                        "Шини (t)", tiresTvi, { tiresTvi = it }
                    )
                    InputRow(
                        "Kmax", plannedKMax, { plannedKMax = it },
                        null, "", {}
                    )

                    CalculateButton(primaryColor) {
                        res1 = ReliabilityCalculator.calculateTask1(
                            pl110Omega.toDoubleOrNull() ?: 0.0, t110Omega.toDoubleOrNull() ?: 0.0,
                            v110Omega.toDoubleOrNull() ?: 0.0, v10Omega.toDoubleOrNull() ?: 0.0,
                            tiresOmega.toDoubleOrNull() ?: 0.0, pl110Tvi.toDoubleOrNull() ?: 0.0,
                            t110Tvi.toDoubleOrNull() ?: 0.0, v110Tvi.toDoubleOrNull() ?: 0.0,
                            v10Tvi.toDoubleOrNull() ?: 0.0, tiresTvi.toDoubleOrNull() ?: 0.0,
                            plannedKMax.toDoubleOrNull() ?: 0.0
                        )
                    }

                    res1?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        ResultRow("ω одноколової системи", "${it.omegaSum}")
                        ResultRow("t відновлення", "${it.tvos}")
                        ResultRow("К-т аварійного простою", "${(it.kaos * 10000).roundToInt()} ×10⁻⁴")
                        ResultRow("К-т планового простою", "${(it.kpos * 10000).roundToInt()} ×10⁻⁴")
                        ResultRow("ω двоколової системи", "${(it.doubleOmega * 10000).roundToInt()} ×10⁻⁴")
                        ResultRow("ω з секційним вимикачем", "${it.systemOmega}")
                    }
                }

                TaskCard(title = "Завдання 2: Збитки", primaryColor = primaryColor) {
                    InputRow(
                        "Зб. аварійні", zperA, { zperA = it },
                        "Зб. планові", zperP, { zperP = it }
                    )
                    InputRow(
                        "ω (відмови)", omega2, { omega2 = it },
                        "t (відновлення)", tv2, { tv2 = it }
                    )
                    InputRow(
                        "Pм (кВт)", pm2, { pm2 = it },
                        "Tм (год)", tm2, { tm2 = it }
                    )
                    InputRow(
                        "k (плановий)", kp2, { kp2 = it },
                        null, "", {}
                    )

                    CalculateButton(primaryColor) {
                        res2 = ReliabilityCalculator.calculateTask2(
                            zperA.toDoubleOrNull() ?: 0.0, zperP.toDoubleOrNull() ?: 0.0,
                            omega2.toDoubleOrNull() ?: 0.0, tv2.toDoubleOrNull() ?: 0.0,
                            pm2.toDoubleOrNull() ?: 0.0, tm2.toDoubleOrNull() ?: 0.0,
                            kp2.toDoubleOrNull() ?: 0.0
                        )
                    }

                    res2?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        ResultRow("М(W нед.а)", "${it.wneda.toInt()} кВт·год")
                        ResultRow("М(W нед.п)", "${it.wnedp.toInt()} кВт·год")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE2E8F0))
                        ResultRow("М(Зпер)", "${it.zper.toInt()} грн", isBold = true, textColor = primaryColor)
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
    Spacer(modifier = Modifier.height(4.dp))
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(46.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text("Обрахувати", fontSize = 15.sp, fontWeight = FontWeight.Bold)
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