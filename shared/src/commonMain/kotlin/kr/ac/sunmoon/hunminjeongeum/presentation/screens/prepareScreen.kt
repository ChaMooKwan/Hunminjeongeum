package kr.ac.sunmoon

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrepareScreen(
    myName: String,
    connection: ClientConnection? = null,
    playerList: List<Triple<String, Int, Int>> = emptyList(), // 더미 제거 mainScreen에서 관리
    maxPlayer: Int = 7,
    onGameStart: () -> Unit
) {
    var input by remember { mutableStateOf("") }

    // 2번에서 startReceiver() 호출
    LaunchedEffect(Unit) {
        if (connection != null) {
            try {
                connection.startReceiver()
            } catch (e: Exception) { }
        }
    }

    // viewModel에서 채팅 메시지 가져오기
    val chatMessages by connection?.viewModel?.messages
        ?.collectAsState(emptyList())
        ?: remember { mutableStateOf(emptyList()) }

    Row(modifier = Modifier.fillMaxSize()) {

        // 좌측 - 플레이어 목록 1~4번
        Column(
            modifier = Modifier
                .weight(0.2f)
                .fillMaxHeight()
                .border(1.dp, Color.Black)
        ) {
            for (i in 0 until 4) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(4.dp),
                    border = BorderStroke(
                        1.dp,
                        if (i < playerList.size) Color.Black else Color.Gray
                    ),
                    shape = RectangleShape
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (i < playerList.size) playerList[i].first else "대기중...",
                            color = if (i < playerList.size) Color.Black else Color.Gray,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        // 중앙 - 채팅창
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .border(1.dp, Color.Black)
        ) {
            // 채팅 목록
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(chatMessages.reversed()) { chatMessage ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = if (chatMessage.userName == myName)
                            Arrangement.End    // 내 메시지 오른쪽
                        else
                            Arrangement.Start  // 상대 메시지 왼쪽
                    ) {
                        Card(
                            border = BorderStroke(1.dp, Color.Gray)
                        ) {
                            Text(
                                text = "${chatMessage.userName}: ${chatMessage.message}",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            // 채팅 입력창 (엔터로 전송)
            OutlinedTextField(
                value = input,
                onValueChange = {
                    if (it.endsWith("\n")) {
                        val message = it.trimEnd()
                        if (message.isNotEmpty()) {
                            if (connection == null) {
                                // 더미 - 서버 없을 때
                            } else {
                                try {
                                    connection.send(message)
                                } catch (e: Exception) { }
                            }
                            input = ""
                        }
                    } else {
                        input = it
                    }
                },
                placeholder = { Text("메시지 입력") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
        }

        // 우측 - 플레이어 목록 5~7번 + 게임시작 버튼
        Column(
            modifier = Modifier
                .weight(0.2f)
                .fillMaxHeight()
                .border(1.dp, Color.Black)
        ) {
            for (i in 4 until 7) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(4.dp),
                    border = BorderStroke(
                        1.dp,
                        if (i < playerList.size) Color.Black else Color.Gray
                    ),
                    shape = RectangleShape
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (i < playerList.size) playerList[i].first else "대기중...",
                            color = if (i < playerList.size) Color.Black else Color.Gray,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            // 게임 시작 버튼
            Button(
                onClick = {
                    if (connection == null) {
                        onGameStart()  // 더미
                    } else {
                        try {
                            connection.startGame()
                            onGameStart()
                        } catch (e: Exception) {
                            onGameStart()
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(4.dp),
                shape = RectangleShape
            ) {
                Text(
                    text = "게임 시작",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}