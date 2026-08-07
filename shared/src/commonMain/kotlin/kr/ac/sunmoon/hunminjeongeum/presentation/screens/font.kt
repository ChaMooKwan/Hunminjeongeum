package kr.ac.sunmoon

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import hunminjeongeum.shared.generated.resources.Res
import hunminjeongeum.shared.generated.resources.jua_regular
import org.jetbrains.compose.resources.Font

@Composable
fun juaFamily() = FontFamily(
    Font(Res.font.jua_regular, FontWeight.Normal)
)