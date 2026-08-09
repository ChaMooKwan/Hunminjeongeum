package kr.ac.sunmoon

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PrepareScreen(
    myName: String, //현재 사용자 이름
    connection: ClientConnection,
    leftPlayer: Int = 4, //좌측 사용자수
    rightPlayer: Int = 3, //우측 사용자수
    onGameStart: () -> Unit //게임 시작 신호
) {
    // =======================================================================================
    // 더미 데이터 - 서버 연동 시 삭제
    val dummyMyName = if (myName.isEmpty()) "나" else myName // myName 비어있으면 "나"로 대체

    val chatList by connection.chatViewModel.messages.collectAsState()
    val playerList by connection.userInfoViewModel.messages.collectAsState()
    // =======================================================================================

    var input by remember { mutableStateOf("") } //메시지 입력값
    val listState = rememberLazyListState() //스크롤 상태 관리

    // 화면 진입 시 한번 실행
    // 2번에서 startReceiver() 호출(3번에서도 지금 호출한 것을 지속해서 사용)
    LaunchedEffect(Unit) {
        if (connection != null) { //서버 연결되어 있을 때만 실행
            try {
                connection.startReceiver() //백그라운드에서 메시지 수신 시작
            } catch (e: Exception) { } //서버 없을 때 오류 무시
        }
    }

    // 새 메시지 올 때마다 맨 아래로 스크롤
    LaunchedEffect(chatList.size) {
        if (chatList.isNotEmpty()) { //비어있지 않을 때만 실행
            listState.animateScrollToItem(chatList.size - 1)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(LightPurple) //백그라운드 색상
    ) {
        // 좌측 - 플레이어 목록 1~4번
        Column(
            modifier = Modifier
                .weight(0.2f) //가로 20% 차지
                .fillMaxHeight()
                .border(1.dp, Color.Black)
        ) {
            for (i in 0 until leftPlayer) { //leftPlayer 칸 배정
                Card(
                    modifier = Modifier
                        .weight(1f) //균등 분할
                        .fillMaxWidth()
                        .padding(4.dp),
                    border = BorderStroke(
                        1.dp,
                        if (i < playerList.size) Purple else Gray //있으면 보라, 없으면 회색
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (i < playerList.size) LightPurple else White //있으면 연보라, 없으면 흰색
                    ),
                    shape = RectangleShape
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            // 사용자 입장 시 이름 표시, 없으면 대기중 표시
                            text = if (i < playerList.size) playerList[i].userName else "대기중...",
                            color = if (i < playerList.size) Color.Black else Color.Gray,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        // 중앙 - 채팅창 (가로 60% 차지)
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .border(1.dp, Purple) //테두리 보라색
        ) {
            // 채팅 목록
            LazyColumn(
                modifier = Modifier
                    .weight(1f) //입력창 제외한 나머지 공간 차지
                    .fillMaxWidth(),
                state = listState //스크롤 상태
            ) {
                items(chatList) { chatMessage -> //서버 연동 시 sharedChatList로 교체
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = if (chatMessage.userName == myName) //mainScreen에서 표시한 더미데이터
                            Arrangement.End    // 내 메시지 오른쪽
                        else
                            Arrangement.Start  // 상대 메시지 왼쪽
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (chatMessage.userName == dummyMyName) //서버 연동 시 myName으로 교체
                                    LightPurple  // 내 메시지 연보라
                                else
                                    White        // 상대 메시지 흰색
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (chatMessage.userName == dummyMyName) Purple else Gray //서버 연동 시 myName으로 교체
                            )
                        ) {
                            Text(
                                text = "${chatMessage.userName}: ${chatMessage.message}",
                                color = DarkPurple, // 텍스트 다크보라
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            // 채팅 입력창 (엔터로 전송)
            OutlinedTextField(
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple,      // 클릭 시 보라
                    unfocusedBorderColor = PurpleHalf, // 평소 연한 보라
                    cursorColor = Purple
                ),
                value = input,
                onValueChange = {
                    if (it.endsWith("\n")) { // 엔터 감지
                        val message = it.trimEnd() // 앞뒤 공백 제거
                        if (message.isNotEmpty()) { // 빈 메시지 방지
                            if (connection == null) {
                                // 더미 - 서버 없을 때 로컬에 추가
                                //chatList.add(ChatMessage(dummyMyName, message))
                            } else {
                                try {
                                    connection.sendChat(message) // 서버로 메시지 전송
                                } catch (e: Exception) { }
                            }
                            input = "" // 입력창 초기화
                        }
                    } else {
                        input = it // 일반 타이핑 시 input 업데이트
                    }
                },
                placeholder = { Text("메시지 입력") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
        }

        // 우측 - 플레이어 목록 + 게임시작 버튼 (가로 20% 차지)
        Column(
            modifier = Modifier
                .weight(0.2f)
                .fillMaxHeight()
                .border(1.dp, Color.Black)
        ) {
            for (i in leftPlayer until (leftPlayer + rightPlayer)) { //rightPlayer 칸 배정
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(4.dp),
                    border = BorderStroke(
                        1.dp,
                        if (i < playerList.size) Purple else Gray
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (i < playerList.size) LightPurple else White
                    ),
                    shape = RectangleShape
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (i < playerList.size) playerList[i].userName else "대기중...",
                            color = if (i < playerList.size) Color.Black else Color.Gray,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            // 게임 시작 버튼 (우측 하단 마지막 칸)
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Coral // 코랄 색상
                ),
                onClick = {
                    if (connection == null) {
                        onGameStart() // 더미 - 서버 없어도 바로 이동
                    } else {
                        try {
                            connection.startGame() // 서버에 게임 시작 신호 전송
                            onGameStart()
                        } catch (e: Exception) {
                            onGameStart() // 전송 실패해도 이동
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
                    color = White, // 흰색 텍스트
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}