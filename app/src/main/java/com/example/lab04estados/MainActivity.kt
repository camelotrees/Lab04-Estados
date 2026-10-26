// Mantén la línea "package ..." de tu proyecto en la primera línea.

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        RegistroScreen()
                        Spacer(Modifier.height(24.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(24.dp))
                        ContadoresScreen()
                    }
                }
            }
        }
    }
}

/* ---------- Componente reutilizable ----------
   Obligatorios: label, value, onValueChange.
   Opcionales (con valor por defecto): modifier, error, keyboardType. */
@Composable
fun LabelAndField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}

/* ---------- Actividad 01: Registro & Preferencias ---------- */
@Composable
fun RegistroScreen() {
    // rememberSaveable: sobreviven a la rotación
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var acepta by rememberSaveable { mutableStateOf(false) }
    var notificaciones by rememberSaveable { mutableStateOf(true) }
    var resultado by rememberSaveable { mutableStateOf("") }
    var errorNombre by rememberSaveable { mutableStateOf<String?>(null) }
    var errorCorreo by rememberSaveable { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Registro & Preferencias", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        LabelAndField(
            label = "Nombre",
            value = nombre,
            onValueChange = { nombre = it; errorNombre = null },
            error = errorNombre
        )
        LabelAndField(
            label = "Correo",
            value = correo,
            onValueChange = { correo = it; errorCorreo = null },
            error = errorCorreo,
            keyboardType = KeyboardType.Email
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = notificaciones, onCheckedChange = { notificaciones = it })
            Spacer(Modifier.padding(4.dp))
            Text("Recibir notificaciones")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = acepta, onCheckedChange = { acepta = it })
            Text("Acepto los términos")
        }

        // Click permitido + mensaje de error (el usuario entiende qué falta)
        Button(
            onClick = {
                errorNombre = if (nombre.isBlank()) "El nombre es obligatorio" else null
                errorCorreo = if (!correo.contains("@")) "Correo no válido" else null
                resultado = when {
                    errorNombre != null || errorCorreo != null -> ""
                    !acepta -> "Debes aceptar los términos"
                    else -> "Registrado: $nombre ($correo)\nNotificaciones: " +
                            if (notificaciones) "activadas" else "desactivadas"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar") }

        if (resultado.isNotEmpty()) {
            // Orden: layout -> dibujo -> padding al final (espacio interno)
            Text(
                text = resultado,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp))
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            )
        }
    }
}

/* ---------- Actividad 02: remember vs rememberSaveable + bloqueo ---------- */
@Composable
fun ContadoresScreen() {
    var contRemember by remember { mutableIntStateOf(0) }            // se pierde al rotar
    var contSaveable by rememberSaveable { mutableIntStateOf(0) }    // se conserva al rotar
    var bloqueado by rememberSaveable { mutableStateOf(false) }
    var mensaje by rememberSaveable { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Contadores", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Text("remember: $contRemember")
        Button(onClick = {
            if (bloqueado) mensaje = "Contadores bloqueados" else contRemember++
        }) { Text("Sumar (remember)") }

        Text("rememberSaveable: $contSaveable")
        Button(onClick = {
            if (bloqueado) mensaje = "Contadores bloqueados" else contSaveable++
        }) { Text("Sumar (rememberSaveable)") }

        Button(
            onClick = {
                bloqueado = !bloqueado
                mensaje = if (bloqueado) "Contadores bloqueados" else "Contadores desbloqueados"
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (bloqueado) "Desbloquear" else "Bloquear") }

        if (mensaje.isNotEmpty()) {
            Text(mensaje, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLab04() {
    MaterialTheme {
        Column(Modifier.padding(16.dp)) {
            RegistroScreen()
            ContadoresScreen()
        }
    }
}