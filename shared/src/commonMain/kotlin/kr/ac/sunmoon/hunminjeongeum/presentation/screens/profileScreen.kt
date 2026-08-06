package kr.ac.sunmoon

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color

@Composable
fun ProfileScreen(
    isPortError:Boolean = false, //포트가 제대로 들어갔는지 확인
    isConnectError:Boolean = false,
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
        if(isPortError){
            Text(
                text = "포트 오류: 숫자 입력",
                color = Color.Red,
                modifier = Modifier.padding(4.dp)
            )
        }
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
        if (isConnectError) {
            Text(
                text = "서버 접속 실패: IP와 포트를 확인해주세요",
                color = Color.Red,
                modifier = Modifier.padding(4.dp)
            )
        }
        Button(
            onClick = {
                val trimName = myName.trim()
                val trimIp = ipAddress.trim()
                val trimPort = portNumber.trim()

                if (trimName.isEmpty() || trimIp.isEmpty() || trimPort.isEmpty()) {
                    isError = true
                } else {
                    onConfirm(trimName, trimPort, trimIp)
                }
            }
        ) {
            Text("확인")
        }
    }
}