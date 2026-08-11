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
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun ProfileScreen(
    isPortError: Boolean = false, //포트가 제대로 들어갔는지 확인
    isConnectError: Boolean = false,
    onConfirm: (String, String, String) -> Unit,  // 이름 입력 후 GameScreen으로 이동
) {
    var myName by remember { mutableStateOf("") } //닉네임 입력
    var portNumber by remember { mutableStateOf("")} //포트번호 입력
    var ipAddress by remember { mutableStateOf("")} // ip 입력
    var isError by remember { mutableStateOf(false) }  // 빈 칸 체크용
    val focusManager = LocalFocusManager.current //탭 입력 시 원하는 칸으로 이동

    LaunchedEffect(Unit) {  //프로필 음악
        SoundManager.playLogin()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .onPreviewKeyEvent{keyEvent -> //onKeyEvent는 compose가 tab키를 먼저 감지하나 onPreviewKeyEvent는 compose가 잡기 전에 실행
                if (keyEvent.key == Key.Tab &&
                    keyEvent.type == KeyEventType.KeyDown) {
                    focusManager.moveFocus(FocusDirection.Down)
                    true
                } else {
                    false
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text( //닉네임 처리
            text = "닉네임을 입력하세요",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DarkPurple, //텍스트 다크 퍼플이 좋대서 해봄 - 변경 가능
            modifier = Modifier.padding(16.dp)
        )
        OutlinedTextField(
            value = myName,
            onValueChange = {
                myName = it
                isError = false  //입력 시 에러 초기화
            },
            placeholder = { Text("닉네임") },
            isError = isError,  //에러 표시
            //
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple, //클릭 시 보라
                unfocusedBorderColor = Purple.copy(alpha = 0.5f), //평소 연한 보라
                focusedLabelColor = Purple,
                cursorColor = Purple
            ),
            modifier = Modifier.padding(16.dp)
        )
        Text( //포트번호 처리
            text = "포트번호를 입력하세요",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DarkPurple, //텍스트 다크 퍼플이 좋대서 해봄
            modifier = Modifier.padding(16.dp)
        )
        OutlinedTextField(
            value = portNumber,
            onValueChange = {
                portNumber = it
                isError = false  //입력 시 에러 초기화
            },
            placeholder = { Text("포트번호") },
            isError = isError,  //에러 표시
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple,
                unfocusedBorderColor = Purple.copy(alpha = 0.5f),
                focusedLabelColor = Purple,
                cursorColor = Purple
            ),
            modifier = Modifier.padding(16.dp)
        )
        if(isPortError){ //포트 오류 안내 문구
            Text(
                text = "포트 오류: 숫자 입력",
                color = Color.Red,
                modifier = Modifier.padding(4.dp)
            )
        }
        Text( //IP 처리
            text = "IP를 입력하세요",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DarkPurple, //텍스트 색
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
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple,
                unfocusedBorderColor = Purple.copy(alpha = 0.5f),
                focusedLabelColor = Purple,
                cursorColor = Purple
            ),
            modifier = Modifier.padding(16.dp)
        )
        //빈 칸일 때 에러 문구
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
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple  // ← 버튼 보라
            ),
            modifier = Modifier.padding(16.dp)
        ) {
            Text("확인")
        }
    }
}