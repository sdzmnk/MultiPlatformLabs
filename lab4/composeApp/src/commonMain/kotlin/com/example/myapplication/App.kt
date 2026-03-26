package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

data class Task1Result(
    val im: Double,
    val imPa: Double,
    val sek: Double,
    val ssMin: Double
)

data class Task2Result(
    val sumX: Double,
    val ip0: Double
)

data class Task3Result(
    val xt: Double,
    val zsh: Double,
    val xshVal: Double,
    val zshMin: Double,
    val xshMinVal: Double,
    val ish3: Double,
    val ish2: Double,
    val ish3Min: Double,
    val ish2Min: Double
)

object ElectricalCalculator {
    private fun Double.roundTo2(): Double = round(this * 100) / 100

    fun calculateTask1(ik: Double, sm: Double, tPhi: Double): Task1Result {
        val im = (sm / 2) / (sqrt(3.0) * 10)
        val imPa = 2 * im
        val sek = im / 1.4
        val ssMin = (ik * sqrt(tPhi)) / 92

        return Task1Result(im.roundTo2(), imPa.roundTo2(), sek.roundTo2(), ssMin.roundTo2())
    }

    fun calculateTask2(kzPower: Double): Task2Result {
        val sumX = (10.5 * 10.5) / kzPower + (10.5 / 100) * ((10.5 * 10.5) / 6.3)
        val ip0 = 10.5 / (sqrt(3.0) * sumX)

        return Task2Result(sumX.roundTo2(), ip0.roundTo2())
    }

    fun calculateTask3(
        ukMax: Double, uvn: Double, snom: Double,
        rsh: Double, xsh: Double, rshMin: Double, xshMin: Double
    ): Task3Result {
        val xt = (ukMax / 100) * (uvn * uvn / snom)
        val zsh = sqrt(rsh * rsh + (xt + xsh).pow(2))
        val zshMin = sqrt(rshMin * rshMin + (xt + xshMin).pow(2))
        val ish3 = (uvn * 1000) / (sqrt(3.0) * zsh)
        val ish2 = ish3 * (sqrt(3.0) / 2)
        val ish3Min = (uvn * 1000) / (sqrt(3.0) * zshMin)
        val ish2Min = ish3Min * (sqrt(3.0) / 2)

        return Task3Result(
            xt = xt.roundTo2(),
            zsh = zsh.roundTo2(),
            xshVal = (xt + xsh).roundTo2(),
            zshMin = zshMin.roundTo2(),
            xshMinVal = (xt + xshMin).roundTo2(),
            ish3 = ish3.roundTo2(),
            ish2 = ish2.roundTo2(),
            ish3Min = ish3Min.roundTo2(),
            ish2Min = ish2Min.roundTo2()
        )
    }
}

@Composable
fun App() {
    var ikVal by remember { mutableStateOf("2500") }
    var smVal by remember { mutableStateOf("1300") }
    var tPhiVal by remember { mutableStateOf("2.5") }
    var res1 by remember { mutableStateOf<Task1Result?>(null) }

    var kzPower by remember { mutableStateOf("200") }
    var res2 by remember { mutableStateOf<Task2Result?>(null) }

    var ukMax by remember { mutableStateOf("11.1") }
    var uvn by remember { mutableStateOf("115") }
    var snom by remember { mutableStateOf("6.3") }
    var rsh by remember { mutableStateOf("10.65") }
    var xsh by remember { mutableStateOf("24.02") }
    var rshMin by remember { mutableStateOf("34.88") }
    var xshMin by remember { mutableStateOf("65.68") }
    var res3 by remember { mutableStateOf<Task3Result?>(null) }

    val bgColor = Color(0xFFF3F4F6)
    val primaryColor = Color(0xFF4F46E5)

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = bgColor) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Електротехнічні розрахунки",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor,
                    modifier = Modifier.padding(bottom = 24.dp, top = 16.dp),
                    textAlign = TextAlign.Center
                )

                TaskCard(title = "Завдання 1", primaryColor = primaryColor) {
                    ModernInput("Струм КЗ (Iₖ)", ikVal) { ikVal = it }
                    ModernInput("Розрахункове навантаження (Sₘ)", smVal) { smVal = it }
                    ModernInput("Фіктивний час (tф)", tPhiVal) { tPhiVal = it }

                    CalculateButton(primaryColor) {
                        res1 = ElectricalCalculator.calculateTask1(
                            ikVal.toDoubleOrNull() ?: 0.0,
                            smVal.toDoubleOrNull() ?: 0.0,
                            tPhiVal.toDoubleOrNull() ?: 0.0
                        )
                    }

                    res1?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        ResultRow("Розрахунковий струм", "${it.im} А")
                        ResultRow("Післяаварійний струм", "${it.imPa} А")
                        ResultRow("Економічний переріз", "${it.sek} мм²")
                        ResultRow("Термічна стійкість", "${it.ssMin} мм²")
                    }
                }

                TaskCard(title = "Завдання 2", primaryColor = primaryColor) {
                    ModernInput("Потужність КЗ", kzPower) { kzPower = it }

                    CalculateButton(primaryColor) {
                        res2 = ElectricalCalculator.calculateTask2(kzPower.toDoubleOrNull() ?: 1.0)
                    }

                    res2?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        ResultRow("Сумарний опір", "${it.sumX} Ом")
                        ResultRow("Струм КЗ", "${it.ip0} А")
                    }
                }

                TaskCard(title = "Завдання 3", primaryColor = primaryColor) {
                    ModernInput("Макс. напруга КЗ", ukMax) { ukMax = it }
                    ModernInput("Ном. напруга вольтажу", uvn) { uvn = it }
                    ModernInput("Ном. потужність трансформатора", snom) { snom = it }
                    ModernInput("Активний опір статора", rsh) { rsh = it }
                    ModernInput("Реактивний опір статора", xsh) { xsh = it }
                    ModernInput("Мін. активний опір статора", rshMin) { rshMin = it }
                    ModernInput("Мін. реактивний опір статора", xshMin) { xshMin = it }

                    CalculateButton(primaryColor) {
                        res3 = ElectricalCalculator.calculateTask3(
                            ukMax.toDoubleOrNull() ?: 0.0,
                            uvn.toDoubleOrNull() ?: 0.0,
                            snom.toDoubleOrNull() ?: 1.0,
                            rsh.toDoubleOrNull() ?: 0.0,
                            xsh.toDoubleOrNull() ?: 0.0,
                            rshMin.toDoubleOrNull() ?: 0.0,
                            xshMin.toDoubleOrNull() ?: 0.0
                        )
                    }

                    res3?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        ResultRow("Реактивний опір трансформатора", "${it.xt} Ом")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE5E7EB))
                        ResultRow("Z (норм. режим)", "${it.zsh} Ом")
                        ResultRow("X (норм. режим)", "${it.xshVal} Ом")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE5E7EB))
                        ResultRow("Z (мін. режим)", "${it.zshMin} Ом")
                        ResultRow("X (мін. режим)", "${it.xshMinVal} Ом")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE5E7EB))
                        ResultRow("I(3) (норм. режим)", "${it.ish3} А")
                        ResultRow("I(2) (норм. режим)", "${it.ish2} А")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE5E7EB))
                        ResultRow("I(3) (мін. режим)", "${it.ish3Min} А")
                        ResultRow("I(2) (мін. режим)", "${it.ish2Min} А")
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(title: String, primaryColor: Color, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth().padding(bottom = 24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = title,
                fontSize = 20.sp,
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
fun ModernInput(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun CalculateButton(primaryColor: Color, onClick: () -> Unit) {
    Spacer(modifier = Modifier.height(8.dp))
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text("Обрахувати", fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color(0xFF4B5563), fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.End)
    }
}