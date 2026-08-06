package kr.ac.sunmoon

import kr.ac.sunmoon.ClientConnection
import kr.ac.sunmoon.ChatViewModel
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
import androidx.compose.material3.Button //Button(onClik)과 같이 버튼 생성 및 클릭 시 호환 로직
import androidx.compose.material3.OutlinedTextFieldDefaults //focused, unfocused로 입력창을 클릭 시/평소 테두리 설정
import androidx.compose.ui.Alignment //Box, Column, Row 등 정렬 방향 설정
import androidx.compose.ui.text.font.FontWeight //FontWeight.Bold,Normal 등 텍스트 굵기 설정
import androidx.compose.ui.unit.sp //사용자 폰트 크기 설정에 반영
import kotlinx.coroutines.delay //시간 확인 -> 서버 처리 할거라 쓸모 없음
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background // 오버레이 화면 띄우고, alpha를 사요해 게임 화면 창 원하는 정도로 흐릿하게 하기
import kotlin.String
import kotlin.Triple

//전체적인 색상변경 및 디자인 변경이 필요함
@Composable
fun mainScreen() {
    var playerList by remember {
        mutableStateOf<List<Triple<String, Int, Int>>>(
            listOf( //더미 리스트 추가 원래였으면 emptyList로 해야함 -> 서버 연동 시 삭제
                Triple("홍길동", 0, 0),
                Triple("김철수", 0, 0),
                Triple("이영희", 0, 0),
                Triple("박민준", 0, 0),
                Triple("최지우", 0, 0),
                Triple("강다은", 0, 0),
                Triple("이준호", 0, 0)
            )
        )
    } //사용자 리스트 받아오기(ip, 포트번호, 사용자명)
    var myName by remember { mutableStateOf("") } //사용자명
    var portNumber by remember { mutableStateOf("") } //포트 번호
    var ipAddress by remember { mutableStateOf("") } //IP 주소
    var isProfileDone by remember { mutableStateOf(false) } //프로필창 전부 채웠는지 여부
    var isGameStart by remember { mutableStateOf(false) } //게임 시작 버튼 클릭 여부(서버에 전달이 갔는가)
    var connection by remember { mutableStateOf<ClientConnection?>(null) } //클라이언트와 서버간에 name, ip, port 상호작용
    var isPortError by remember { mutableStateOf(false) } // 받아온 port를 Int형으로 치환 시 오류 발생 여부
    var isConnectError by remember { mutableStateOf(false) } // 서버 접속 실패 여부

    if (isGameStart) { //프로필 작성 완료 및 버튼 클릭 시 서버에 갔다가 myName 받아오기
        GameScreen(
            myName = myName,
            playerList = playerList,
            connection = connection
        )
    } else if (isProfileDone) { //프로필 작성만 true라면?
        PrepareScreen( // 대기화면으로 이동
            myName = myName,
            connection = connection,
            playerList = playerList,
            onGameStart = { isGameStart = true }
        )
    } else {
        ProfileScreen( //작성 완료까지 기다리다가 완료되면 받아오기
            isPortError = isPortError, //포트 오류 확인
            isConnectError = isConnectError, // 서버 접속 실패 확인 ← 추가
            onConfirm = { name, port, ip ->
                myName = name
                portNumber = port
                ipAddress = ip
                val portInt = portNumber.toIntOrNull()
                if (portInt == null) isPortError = true
                else {
                    isPortError = false
                    try {
                        connection = ClientConnection(
                            ip = ip,
                            port = portInt,
                            userName = name
                        )
                        connection?.connect()
                        isProfileDone = true  // ← 성공 시에만 이동
                    } catch (e: Exception) {
                        /// 기존 코드 (서버 있을 때)
                        // isConnectError = true
                        // isProfileDone = false
                        // 테스트용 더미 -> 서버 열리면 삭제
                        isProfileDone = true  // 서버 없어도 다음 화면으로
                    }
                }
            }
        )
    }
}

// =====================
// 메인 화면
// 각 컴포넌트 조립만 담당
// =====================
@Composable
fun GameScreen(
    // 게임 로직 담당자에게 받는 데이터
    //더미데이터는 실제 실행 시 지울 것
    playerList: List<Triple<String, Int, Int>> = emptyList(), // 플레이어 리스트 가져오기
    myName:String = "이동욱", //더미, 사용자명
    connection: ClientConnection ?= null, //서버 불러오기
    quizCategory: String = "동물", //더미, 문제 카테고리
    wordQuiz: String = "ㄱㅁㅎㄱ", // 더미, 문제 초성
    countRound: Int = 1, // 더미, 현재 판 수
    totalRound: Int = 5, // 더미, 총 판 수
    timer: Int = 30, // 더미, 실제 서버 시간에서 받아올 것
    userChat: String = "", // 게임 로직에서 하나씩 전달
    isGameOver: Boolean = false, // 게임 종료 시 true

    // AI 프롬프트 담당자에게 받는 데이터
    easyWordHint: String = "ㄱ미ㅎㄱ",  //더미, 쉬움 초성 힌트(단어가 3개 이하)가 들어올 시 문제 업데이트용
    normalWordHint: String = "개미ㅎㄱ",  //더미, 초성 힌트(단어가 4개 이상)가 들어올 시 문제 업데이트용
    easyHint: String = "줄무늬가 있고, 주로 나비라고 불립니다", //더미, 쉬움 특성 힌트(단어가 2개)가 들어올 시 삽입됨
    normalHint: String = "육식동물이며 초원지대에 살고 있습니다", //더미, 중간 특성 힌트(단어가 3개)가 들어올 시 삽입함
    hardHint: String = "주식으로 개미를 먹습니다" //더미, 어려움 특성 힌트(단어가 4개 이상)가 들어올 시 삽입함
) {
    var input by remember { mutableStateOf("") }
    val chatList = remember { mutableStateListOf("안녕", "이거 강아지 아님?") } // 더미
    val sortedPlayerList = playerList.sortedByDescending { it.second } //내림차순 정렬로 큰 수가 위로 가게 정렬
    // ============================================================
    // //GameOver로직이 잘 작동되는지 확인하기 위한 더미 기믹 - 구현 시 삭제
    var dummyGameOver by remember{mutableStateOf(false)}
    var timeLeft by remember { mutableStateOf(30) }
    // ============================================================
    val wordCount = wordQuiz.length //단어 개수확인용
    val hintState = quizSelector(wordCount, timeLeft) //값 집어넣기
    val currentWord = when{ //단어 덮어씌우기
        hintState["normalWordHint"] == true -> normalWordHint
        hintState["easyWordHint"] == true -> easyWordHint
        else -> wordQuiz
    }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
        dummyGameOver = true  // ← 0초 되면 자동으로 GameOver
    }
    // ============================================================

    //채팅 입력 시(값이 바뀜 = 입력) 실행
    LaunchedEffect(userChat) {
        if (userChat.isNotEmpty()) { //빈 채팅 여부
            chatList.add(userChat)
        }
    }

    //서버에서 채팅 메시지 가져오기
    val chatMessages by connection?.viewModel?.messages
        ?.collectAsState(emptyList())
        ?: remember { mutableStateOf(emptyList()) }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 좌측 - 플레이어 목록
            Column(
                modifier = Modifier
                    .weight(0.2f) //20% 차지
                    .fillMaxHeight() //세로 최대 차지
                    .border(1.dp, Color.Black) //테두리 검정
            ) {
                sortedPlayerList.forEach { (name, score, correct) ->
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
                RoundDisplay( //라운드 표시
                    countRound = countRound
                )

                TimerDisplay(timer = timeLeft) // 시간 표시, 현재 더미용 timeLeft로 시간 확인 나중에 timer로 복구할것

                WordDisplay( //문제 표시
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxWidth(),
                    word = currentWord //처음엔 초성, 그 후로는 초성힌트로 업데이트
                )

                CategoryDisplay( //카테고리 표시
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxWidth(),
                    quizCategory = quizCategory
                )

                HintDisplay(
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxWidth(),
                    easyHint   = if (hintState["easyHint"]   == true) easyHint   else "",
                    normalHint = if (hintState["normalHint"] == true) normalHint else "",
                    hardHint   = if (hintState["hardHint"]   == true) hardHint   else ""
                )

                WordInput(
                    input = input,
                    onInputChange = { input = it }
                )
            } // 중앙 끝
            // 우측 - 채팅 목록
            ChatList(
                chatMessages = chatMessages,
                myName = myName,
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxHeight()
                    .border(1.dp, Color.Gray)
            )
        } // 우측 끝
        if(dummyGameOver){ //나중에 isGameOver로 변경해야함
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)), //반투명 화면정도 조절
                contentAlignment = Alignment.Center
            ){
                GameOver(
                    playerList = playerList,
                    totalRound = totalRound,
                    myName = myName,
                    onConfirm = {dummyGameOver = false }
                )
            }
        }
    }
}

    // =====================
// 플레이어 카드
// 이름 + 점수 표시
// =====================
@Composable
fun PlayerCard(
//사용자 UI 설정
    name: String, //사용자명
    score: Int = 0, //사용자 점수
) {
    Card(
        modifier = Modifier
            .fillMaxWidth() //너비 최대(할당된 비율을 꽉 채움)
            .height(60.dp) //크기:60
            .padding(4.dp), //사이간격: 4
        border = BorderStroke(1.dp, Color.Black), //테두리
        shape = RectangleShape //사각형으로 카드 Radius 변경
        ){
        Text(
            text = "$name 점수: $score",
            modifier = Modifier.padding(8.dp)
        )
    }
}

// =========================
// 판수 표시
// 최종 판수는 시간 종료 후 표시
// =========================
@Composable
fun RoundDisplay(
    countRound: Int
) {
    Text(
        text = "${countRound}라운드",
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        textAlign = TextAlign.Center
    )
}

// =====================
// 타이머 표시
// 로직 팀원에게 timer 받아옴
// =====================
@Composable
fun TimerDisplay(
    timer: Int = 30  // 더미, 로직 팀원이 전달
) {
    Text(
        text = "${timer}초",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        textAlign = TextAlign.Center
    )
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
        modifier = modifier
            .fillMaxWidth(),
        border = BorderStroke(1.dp, Color.Black),
        shape = RectangleShape
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
// 카테고리 표시
// 관할 카테고리 가져오기
// =====================
@Composable
fun CategoryDisplay(
    quizCategory:String,
    modifier: Modifier = Modifier
){
    Card(
        modifier = modifier
            .fillMaxWidth(),
        border = BorderStroke(1.dp, Color.Black),
        shape = RectangleShape
    ){
        Text(
            text = "카테고리: $quizCategory",
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
    }
}
// =====================
// 힌트 표시
// 특성 힌트 1칸만 표시
// =====================
@Composable
fun HintDisplay(
    modifier: Modifier = Modifier,
    easyHint: String = "", // 기본값 빈 문자열
    normalHint: String = "",
    hardHint: String = ""
) {
    Column(modifier = modifier){
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            border = BorderStroke(1.dp, Color.Black),
            shape = RectangleShape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = easyHint, // 받아온 힌트 표시
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            border = BorderStroke(1.dp, Color.Black),
            shape = RectangleShape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = normalHint, // 받아온 힌트 표시
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            border = BorderStroke(1.dp, Color.Black),
            shape = RectangleShape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = hardHint, // 받아온 힌트 표시
                    modifier = Modifier.padding(8.dp)
                )
            }
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
    chatMessages: List<ChatMessage>,  // ← String → ChatMessage
    myName: String,                   // ← 추가
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(chatMessages.reversed()) { chatMessage ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = if (chatMessage.userName == myName)
                    Arrangement.End
                else
                    Arrangement.Start
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
}

// =====================
// 최종 점수 화면
// 게임 종료 시 표시
// =====================
@Composable
fun GameOver(
    // 게임 로직에서 받아올 것
    playerList: List<Triple<String, Int, Int>>, // 이름, 맞힌 수
    totalRound: Int, // 총 문제 수
    myName: String, // 사용자명
    onConfirm: () -> Unit // 확인 버튼 클릭
) {
    val myResult = playerList.find {it.first == myName}
    Card(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .fillMaxHeight(0.5f),
        border = BorderStroke(1.dp, Color.Black),
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), //전체 화면 채우기 수정해야함 작은 화면에 표시할 에정
            horizontalAlignment = Alignment.CenterHorizontally, //중앙 표시
            verticalArrangement = Arrangement.Center // 중앙표시 2
        ) {
            Text(
                text = "게임 종료!",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp) //중앙으로 변경할 예정
            )
            // 플레이어별 결과 표시
            myResult?.let{ (name, score, correct) -> //myResult가 있을 시 실행
                Text(
                    text = "$name : $correct / $totalRound\n 점수: $score", //이동욱: 3 / 10, 밑에는 점수:150 으로 표현
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
            } ?: Text( //반환되는 값이 없을 때(없으면 오류인듯)
                text = "결과를 찾을 수 없어요",
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
            // 확인 버튼
            Button(onClick = onConfirm) {
                Text("확인")
            }
        }
    }
}
// =====================
// 힌트 조건 선택
// 음절 수와 시간에 따라 힌트 공개 여부 결정
// =====================
//compose를 안사용하기때문에 Composable제거
fun quizSelector(
    wordCount: Int, //wordQuiz.length
    timer: Int
): Map<String, Boolean> {
    return when {
        // 1음절
        wordCount == 1 ->  mapOf(
            "easyHint"      to false,
            "easyWordHint"  to false,
            "normalHint"    to (timer <= 15), //특성힌트2
            "normalWordHint" to false,
            "hardHint"      to false
        )
        // 2음절
        wordCount == 2 -> mapOf(
            "easyHint"      to (timer <= 25),  // 특성힌트1
            "easyWordHint"  to (timer <= 15),  // 초성힌트1
            "normalHint"    to false,
            "normalWordHint" to false,
            "hardHint"      to false
        )
        // 3음절
        wordCount == 3 -> mapOf(
            "easyHint"      to (timer <= 25),  // 특성힌트1
            "normalHint"    to (timer <= 20),  // 특성힌트2
            "easyWordHint"  to (timer <= 15),  // 초성힌트1
            "normalWordHint" to false,
            "hardHint"      to false
        )
        // 4-5음절
        else -> mapOf(
            "easyHint"      to (timer <= 25),  // 특성힌트1
            "easyWordHint"  to (timer <= 20),  // 초성힌트1
            "normalHint"    to (timer <= 15),  // 특성힌트2
            "normalWordHint" to (timer <= 10), // 초성힌트2
            "hardHint"      to (timer <= 5)    // 특성힌트3
        )
    }
}