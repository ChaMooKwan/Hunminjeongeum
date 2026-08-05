package kr.ac.sunmoon

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ProfileScreen(
    onConfirm: (String, String, String) -> Unit  // 이름 입력 후 GameScreen으로 이동
) {
    var myName by remember { mutableStateOf("") } //닉네임 입력
    var portNumber by remember { mutableStateOf("")} //포트번호 입력
    var ipAddress by remember { mutableStateOf("")} // ip 입력
    var isError by remember { mutableStateOf(false) }  // 빈 칸 체크용

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "닉네임을 입력하세요",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        OutlinedTextField(
            value = myName,
            onValueChange = {
                myName = it
                isError = false  // 입력 시 에러 초기화
            },
            placeholder = { Text("닉네임") },
            isError = isError,  // 에러 표시
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "포트번호를 입력하세요",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        OutlinedTextField(
            value = portNumber,
            onValueChange = {
                portNumber = it
                isError = false  // 입력 시 에러 초기화
            },
            placeholder = { Text("포트번호") },
            isError = isError,  // 에러 표시
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "IP를 입력하세요",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        OutlinedTextField(
            value = ipAddress,
            onValueChange = {
                ipAddress = it
                isError = false  // 입력 시 에러 초기화
            },
            placeholder = { Text("ip") },
            isError = isError,  // 에러 표시
            modifier = Modifier.padding(16.dp)
        )
        // 빈 칸일 때 에러 문구
        if (isError) {
            Text(
                text = "닉네임을 입력해주세요",
                color = androidx.compose.ui.graphics.Color.Red,
                modifier = Modifier.padding(4.dp)
            )
        }
        Button(
            onClick = {
                if (myName.isEmpty() || portNumber.isEmpty() || ipAddress.isEmpty()) {
                    isError = true   // ← 빈 칸이면 에러 표시
                } else {
                    onConfirm(myName, portNumber, ipAddress) // ← 이름 전달
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("확인")
        }
    }
}