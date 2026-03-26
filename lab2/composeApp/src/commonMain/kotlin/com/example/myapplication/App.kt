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


data class EmissionResult(
    val coalEI: Double, val coalTotal: Double,
    val oilEI: Double, val oilTotal: Double,
    val gasEI: Double, val gasTotal: Double
)

object EcoCalculator {
    fun calculate(coal: Double, fuelOil: Double, naturalGas: Double): EmissionResult {
        val qCoal = 20.47
        val aVunCoal = 0.8
        val aCoal = 25.2
        val gVunCoal = 1.5
        val nzu = 0.985

        val qOil = 40.40
        val wOil = 2.0
        val aOil = 0.15
        val aVunOil = 1.0
        val gVunOil = 0.0

        val coalEI = (10.0.pow(6) / qCoal) * aVunCoal * (aCoal / (100 - gVunCoal)) * (1 - nzu)
        val coalTotal = 10.0.pow(-6) * coalEI * qCoal * coal

        val qOilWorking = qOil * (100 - wOil - aOil) / 100 - 0.025 * wOil
        val oilEI = (10.0.pow(6) / qOilWorking) * aVunOil * (aOil / (100 - gVunOil)) * (1 - nzu)
        val oilTotal = 10.0.pow(-6) * oilEI * qOilWorking * fuelOil

        return EmissionResult(
            coalEI = round(coalEI * 100) / 100,
            coalTotal = round(coalTotal * 100) / 100,
            oilEI = round(oilEI * 100) / 100,
            oilTotal = round(oilTotal * 100) / 100,
            gasEI = 0.0,
            gasTotal = 0.0
        )
    }
}

@Composable
fun App() {
    var coal by remember { mutableStateOf("") }
    var fuelOil by remember { mutableStateOf("") }
    var naturalGas by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<EmissionResult?>(null) }

    val primaryColor = Color(0xFF2563EB)
    val bgColor = Color(0xFFF8FAFC)
    val cardColor = Color(0xFFFFFFFF)
    val headerColor = Color(0xFFF1F5F9)

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = primaryColor,
            background = bgColor,
            surface = cardColor
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(30.dp)) {
                        Text(
                            text = "КАЛЬКУЛЯТОР",
                            color = primaryColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                            textAlign = TextAlign.Center
                        )

                        InputField("Донецьке газове вугілля марки ГР (т):", coal) { coal = it }
                        InputField("Високосірчистий мазут марки 40 (т):", fuelOil) { fuelOil = it }
                        InputField("Природний газ із газопроводу Уренгой-Ужгород (м3):", naturalGas) { naturalGas = it }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                result = EcoCalculator.calculate(
                                    coal.toDoubleOrNull() ?: 0.0,
                                    fuelOil.toDoubleOrNull() ?: 0.0,
                                    naturalGas.toDoubleOrNull() ?: 0.0
                                )
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                        ) {
                            Text("Розрахувати", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        result?.let { res ->
                            Spacer(modifier = Modifier.height(25.dp))

                            Row(modifier = Modifier.fillMaxWidth().background(headerColor).padding(10.dp)) {
                                Text("Паливо", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Емісія (г/ГДж)", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Валовий викид (т)", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            HorizontalDivider(color = Color(0xFFE2E8F0))

                            TableRow("Вугілля", res.coalEI.toString(), res.coalTotal.toString(), primaryColor)
                            TableRow("Мазут", res.oilEI.toString(), res.oilTotal.toString(), primaryColor)
                            TableRow("Газ", res.gasEI.toString(), res.gasTotal.toString(), primaryColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp)) {
        Text(
            text = label,
            color = Color(0xFF64748B),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedBorderColor = Color(0xFF2563EB)
            )
        )
    }
}

@Composable
fun TableRow(fuel: String, ei: String, total: String, primaryColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
        Text(fuel, modifier = Modifier.weight(1f))
        Text(ei, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = primaryColor)
        Text(total, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = primaryColor)
    }
    HorizontalDivider(color = Color(0xFFE2E8F0))
}