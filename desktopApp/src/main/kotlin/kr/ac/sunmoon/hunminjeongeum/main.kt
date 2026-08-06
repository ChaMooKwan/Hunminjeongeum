package kr.ac.sunmoon.hunminjeongeum

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kr.ac.sunmoon.mainScreen

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Hunminjeongeum",
    ) {
        App()
    }
}