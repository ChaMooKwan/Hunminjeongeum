package kr.ac.sunmoon

import androidx.compose.foundation.BorderStroke //BorderStroke(1.dp(굵기), Color.gray(색감))를 이용하여 Card 테두리 표시
import androidx.compose.foundation.border //Column, Row등의 테두리 표시
import androidx.compose.foundation.layout.* // fillMaxSize, padding, weight 등 레이아웃 관련 설정
import androidx.compose.foundation.lazy.LazyColumn //채팅 목록용 스크롤
import androidx.compose.foundation.lazy.items //리스트를 LazyColumn에 담기 위함
import androidx.compose.material3.Card //modifier, border, shape 등의 카드 형태 UI
import androidx.compose.material3.OutlinedTextField //value, onValueChange, placeholder와 같이 다른 값으로 들어오는 값 처리
import androidx.compose.material3.Text //color, fontSize 등 텍스트 표시
import androidx.compose.runtime.* //remember, mutableStateOf와 같이 리스트나 값이 바뀌거나 추가되면 자동 갱신
import androidx.compose.ui.Modifier //fillMaxWidth와 같은 크기, 여백, 테두리 설정
import androidx.compose.ui.graphics.Color //모든 색상 설정에 사용
import androidx.compose.ui.unit.dp //화면 해상도가 달라져도 일정한 크기 유지
import androidx.compose.ui.graphics.RectangleShape //자동으로 둥글기 설정된 거 사각형으로 수정
import androidx.compose.foundation.layout.Box //Box에서 Alignment.Center 와 같이 정렬
import androidx.compose.material3.OutlinedTextFieldDefaults //focused, unfocused로 입력창을 클릭 시/평소 테두리 설정
import androidx.compose.ui.Alignment //Box, Column, Row 등 정렬 방향 설정
import androidx.compose.ui.text.font.FontWeight //FontWeight.Bold,Normal 등 텍스트 굵기 설정
import androidx.compose.ui.unit.sp //사용자 폰트 크기 설정에 반영
import kotlinx.coroutines.delay //시간 확인
import androidx.compose.ui.text.style.TextAlign

@Composable
fun mainScreen() {
    GameScreen()
}

// =====================
// 메인 화면
// 각 컴포넌트 조립만 담당
// =====================
@Composable
fun GameScreen(
    // 게임 로직 담당자에게 받는 데이터
    playerList: List<Pair<String, Int>> = listOf(
        Pair("이동욱", 0),
        Pair("이리듐", 150),
        Pair("나트륨", 300)
    ), // 더미, 나중에 실제 데이터로 교체
    category: String = "동물", //더미, 문제 카테고리
    wordCase: String = "ㄱㅇㅇ", // 더미, 문제 초성
    countRound: Int = 1, // 더미, 현재 판 수
    totalRound: Int = 5, // 더미, 총 판 수
    chat: String = "", // 게임 로직에서 하나씩 전달

    // AI 프롬프트 담당자에게 받는 데이터
    wordHint: String = "ㄱ양ㅇ",  //더미, 초성 힌트가 들어올 시 문제 업데이트용
    quizHint: String = "줄무늬가 있고, 주로 나비라고 불립니다" //더미, 특성 힌트가 들어올 시 삽입됨
) {
    var input by remember { mutableStateOf("") }
    val chatList = remember { mutableStateListOf("안녕", "이거 강아지 아님?") } // 더미
    val sortedPlayerList = playerList.sortedByDescending { it.second } //내림차순 정렬로 큰 수가 위로 가게 정렬
    val currentWord = if (wordHint.isEmpty()) wordCase else wordHint //힌트가 들어온다면 wordHint, 아니면 처음 초성인 wordCase표시
    var timer by remember { mutableStateOf(30) } //제한시간 30초 설정

    //채팅 입력 시(값이 바뀜 = 입력) 실행
    LaunchedEffect(chat) {
        if (chat.isNotEmpty()) { //빈 채팅 여부
            chatList.add(chat)
        }
    }

    //타이머가 0초일 시 종료
    LaunchedEffect(Unit) {
        while (timer > 0) {
            delay(1000L) //1초마다(Long타입)
            timer-- //타이머 1초 감소
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {

        // 좌측 - 플레이어 목록
        Column(
            modifier = Modifier
                .weight(0.2f) //20% 차지
                .fillMaxHeight() //세로 최대 차지
                .border(1.dp, Color.Black) //테두리 검정
        ) {
            sortedPlayerList.forEach { (name, score) ->
                PlayerCard(name = name, score = score) //카드에 삽입 및 추가
            }
        } // ← 좌측 Column 닫힘

        // 중앙 - 게임 영역
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .border(1.dp, Color.Black)
        ) {
            //상단 시간 표시
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$countRound / $totalRound",
                    modifier = Modifier.weight(1f)
                )
                // 시간 중앙
                Text(
                    text = "⏱ ${timer}초",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                // 우측 빈칸 (좌우 균형)
                Text(
                    text = "",
                    modifier = Modifier.weight(1f)
                )
            } // ← Row 닫힘

            WordDisplay(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth(),
                word = currentWord //처음엔 초성, 그 후로는 초성힌트로 업데이트
            )

            Text(
                text = "카테고리: $category",
                modifier = Modifier.padding(8.dp)
            )

            HintDisplay(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                quizHint = quizHint // 초성 힌트는 정답지에 표시할 것
            )

            WordInput(
                input = input,
                onInputChange = { input = it }
            )
        } // ← 중앙 Column 닫힘

        // 우측 - 채팅 목록
        ChatList(
            chatList = chatList,
            modifier = Modifier
                .weight(0.2f)
                .fillMaxHeight()
                .border(1.dp, Color.Gray)
        )
    } // ← Row 닫힘
}

// =====================
// 플레이어 카드
// 이름 + 점수 표시
// =====================
@Composable
fun PlayerCard( //사용자 UI 설정
    name: String, //사용자명
    score: Int = 0 //사용자 점수
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp) //크기:60
            .padding(4.dp), //사이간격: 4
        border = BorderStroke(1.dp, Color.Black), //테두리
        shape = RectangleShape //사각형으로 카드 Radius 변경
    ) {
        Text(
            text = "$name 점수: $score",
            modifier = Modifier.padding(8.dp)
        )
    }
}

// =====================
// 초성 표시
// 맞춰야 할 단어의 초성
// =====================
@Composable
fun WordDisplay(
    modifier: Modifier = Modifier,
    word: String
) {
    Card(
        modifier = modifier.padding(8.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center // ← 중앙 정렬
        ) {
            Text(
                text = word,
                fontSize = 100.sp, //크기 설정
                fontWeight = FontWeight.Bold, //굵기 설정
                //텍스트 패딩은 중앙 정렬이라 필요 없어서 제거
            )
        }
    }
}

// =====================
// 힌트 표시
// 특성 힌트 1칸만 표시
// =====================
@Composable
fun HintDisplay(
    modifier: Modifier = Modifier,
    quizHint: String = "", // 기본값 빈 문자열
) {
    Card(
        modifier = modifier.padding(4.dp),
        border = BorderStroke(1.dp, Color.Black),
        shape = RectangleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = quizHint, // 받아온 힌트 표시
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

// =====================
// 정답 입력창
// 사용자가 정답 입력하는 곳
// =====================
@Composable
fun WordInput(
    input: String,
    onInputChange: (String) -> Unit
) {
    OutlinedTextField(
        value = input,
        onValueChange = onInputChange,
        placeholder = { Text("정답을 입력하세요") },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Blue, //클릭 시 파랑
            unfocusedBorderColor = Color.Black, //평소엔 검정
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    )
}

// =====================
// 채팅/오답 목록
// 우측에 표시되는 오답 리스트
// =====================
@Composable
fun ChatList(
    chatList: List<String>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(chatList) { chat ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text(
                    text = chat,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}