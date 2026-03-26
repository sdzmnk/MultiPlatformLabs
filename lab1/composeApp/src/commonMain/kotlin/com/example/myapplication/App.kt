package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class FuelResult(
    val kpc: Double, val kpg: Double,
    val qph: Double, val qch: Double, val qgh: Double,
    val dry: Map<String, Double>, val burn: Map<String, Double>
)

data class MazutResult(
    val c: Double, val h: Double, val o: Double,
    val s: Double, val a: Double, val v: Double, val q: Double
)

object EnergyCalculator {
    fun calculateTask1(h: Double, c: Double, s: Double, n: Double, o: Double, w: Double, a: Double): FuelResult {
        val kpc = 100.0 / (100.0 - w)
        val kpg = 100.0 / (100.0 - w - a)
        val qph = 339 * c + 1030 * h - 108.8 * (o - s) - 25 * w
        val qch = (qph / 1000 + 0.025 * w) * kpc
        val qgh = (qph / 1000 + 0.025 * w) * kpg
        val dry = mapOf("H" to h * kpc, "C" to c * kpc, "S" to s * kpc, "N" to n * kpc, "O" to o * kpc, "A" to a * kpc)
        val burn = mapOf("H" to h * kpg, "C" to c * kpg, "S" to s * kpg, "N" to n * kpg, "O" to o * kpg, "A" to 0.0)
        return FuelResult(kpc, kpg, qph, qch, qgh, dry, burn)
    }

    fun calculateTask2(c: Double, h: Double, o: Double, s: Double, w: Double, a: Double, q: Double, v: Double): MazutResult {
        return MazutResult(
            c = c * (100 - w - a) / 100,
            h = h * (100 - w - a) / 100,
            o = o * (100 - w / 10 - a / 10) / 100,
            s = s * (100 - w - a) / 100,
            a = a * (100 - w) / 100,
            v = v * (100 - w) / 100,
            q = q * (100 - w - a) / 100 - 0.025 * w
        )
    }
}

@Composable
fun App() {
    MaterialTheme {
        var selectedTab by remember { mutableStateOf(0) }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Завдання 1", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Завдання 2", modifier = Modifier.padding(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                Task1Screen()
            } else {
                Task2Screen()
            }
        }
    }
}

@Composable
fun Task1Screen() {
    var h by remember { mutableStateOf("") }
    var c by remember { mutableStateOf("") }
    var s by remember { mutableStateOf("") }
    var n by remember { mutableStateOf("") }
    var o by remember { mutableStateOf("") }
    var w by remember { mutableStateOf("") }
    var a by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<FuelResult?>(null) }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Розрахунок складу твердого палива", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InputField("H (%)", h) { h = it }
            InputField("C (%)", c) { c = it }
            InputField("S (%)", s) { s = it }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InputField("N (%)", n) { n = it }
            InputField("O (%)", o) { o = it }
            InputField("W (%)", w) { w = it }
            InputField("A (%)", a) { a = it }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            result = EnergyCalculator.calculateTask1(
                h.toDoubleOrNull() ?: 0.0, c.toDoubleOrNull() ?: 0.0, s.toDoubleOrNull() ?: 0.0,
                n.toDoubleOrNull() ?: 0.0, o.toDoubleOrNull() ?: 0.0, w.toDoubleOrNull() ?: 0.0, a.toDoubleOrNull() ?: 0.0
            )
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Розрахувати")
        }

        result?.let { res ->
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            ResultRow("Коефіцієнт KPC / KPG", "%.4f / %.4f".format(res.kpc, res.kpg))
            ResultRow("Q (Робоча) МДж/кг", "%.2f".format(res.qph))
            ResultRow("Q (Суха) МДж/кг", "%.2f".format(res.qch))
            ResultRow("Q (Горюча) МДж/кг", "%.2f".format(res.qgh))

            Spacer(modifier = Modifier.height(16.dp))
            listOf("H", "C", "S", "N", "O", "A").forEach { el ->
                ResultRow("$el %", "Суха: %.2f | Горюча: %.2f".format(res.dry[el], res.burn[el]))
            }
        }
    }
}

@Composable
fun Task2Screen() {
    var c by remember { mutableStateOf("") }
    var h by remember { mutableStateOf("") }
    var o by remember { mutableStateOf("") }
    var s by remember { mutableStateOf("") }
    var w by remember { mutableStateOf("") }
    var a by remember { mutableStateOf("") }
    var q by remember { mutableStateOf("") }
    var v by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<MazutResult?>(null) }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Розрахунок складу мазуту", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InputField("C (%)", c) { c = it }
            InputField("H (%)", h) { h = it }
            InputField("O (%)", o) { o = it }
            InputField("S (%)", s) { s = it }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InputField("W (%)", w) { w = it }
            InputField("A (%)", a) { a = it }
            InputField("Q (МДж/кг)", q) { q = it }
            InputField("V (мг/кг)", v) { v = it }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            result = EnergyCalculator.calculateTask2(
                c.toDoubleOrNull() ?: 0.0, h.toDoubleOrNull() ?: 0.0, o.toDoubleOrNull() ?: 0.0,
                s.toDoubleOrNull() ?: 0.0, w.toDoubleOrNull() ?: 0.0, a.toDoubleOrNull() ?: 0.0,
                q.toDoubleOrNull() ?: 0.0, v.toDoubleOrNull() ?: 0.0
            )
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Розрахувати")
        }

        result?.let { res ->
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            ResultRow("Вуглець (C)", "%.2f".format(res.c))
            ResultRow("Водень (H)", "%.2f".format(res.h))
            ResultRow("Кисень (O)", "%.2f".format(res.o))
            ResultRow("Сірка (S)", "%.2f".format(res.s))
            ResultRow("Зольність (A)", "%.2f".format(res.a))
            ResultRow("Ванадій (V)", "%.2f".format(res.v))
            ResultRow("Теплота (Q)", "%.2f".format(res.q))
        }
    }
}

@Composable
fun RowScope.InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.weight(1f).padding(vertical = 4.dp),
        singleLine = true
    )
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
}