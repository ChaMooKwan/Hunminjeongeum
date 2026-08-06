package kr.ac.sunmoon

import java.io.PrintWriter
import java.net.Socket
import java.io.BufferedReader
import kotlin.concurrent.thread

/*
1. [사용자명과, IP, Port 입력 화면] 에서 받는 정보만 Connection 객체로 다룬다.
ip, port, nickname은 이미 초기화되었다고 가정한다.
*/

class ClientConnection(
    val ip: String,
    val port: Int,
    val userName: String,
) { //   val cl = ClientConnetion(ip,port,userName).connect()
    private lateinit var socket: Socket
    private lateinit var reader: BufferedReader
    private lateinit var writer: PrintWriter
    val viewModel = ChatViewModel() //채팅 메시지를 직접 가져올 수 있게(CientConnection으로 부르지 않더라도 가능하게)

    // [1번 화면]의 닉네임, 아이피, 포트 입력하고 확인 눌렀을 때 호출한다.
    fun connect() {
        socket = Socket(ip, port)
        reader = socket.getInputStream().bufferedReader()
        writer = PrintWriter(socket.getOutputStream(), true)
    }

    // [2번 대기자 창]에 진입하기 위해 호출한다.
    fun goToWaitingRoom(reader: BufferedReader) {
        giveUserName(userName)
        while (true) {
            // 시작할때까지 대기. 새로운 입장자가 등장하면 서버가 대기자 명단을 줌(,를 구분자로)
            val encodedNames = reader.readLine()
            val userNames: List<String> = encodedNames.split(",")
        }
    }

    fun giveUserName(userName: String) {
        writer.println(userName)
    }

    // [2번 대기자 창]에서 게임 시작 버튼 누르면 onClick ={ a.startGame() }에서 호출
    fun startGame() {
        writer.println("/startGame")
    }

    // [3번 게임 창]에서 확인 버튼 누르면 호출하면 된다.
    fun send(message: String) {
        writer.println(message)
    }

    /*
     [3번 게임 창]에서 호출하면 된다.
     "val chatMessages by viewModel.messages.collectAsState()"를 제일 위에 선언 후
     LazyColumn의 items(chatMessages.reversed()) { chatMessage -> Card{Text(chatMessage.message) }로 사용 가능하다.
     이 때 나의 메시지의 경우 Card의 배치를 오른쪽, 다른 사람의 메시지는 왼쪽에 배치한다.(어려울 경우 색으로 한다)
     -> 닉네임으로 자기 메시지 식별하는 것으로 생각중.
     */
    fun startReceiver() {
        thread(isDaemon = true) {
            // !! 서버는 ChatMessage 클래스에서 속성인 userName과 message를 ,를 구분자로 하여 전송한다.
            val encodedMessage: List<String> = reader.readLine().split(",")
            val chatMessage = ChatMessage(encodedMessage.first(), encodedMessage.last())
            viewModel.updateChatHistory(chatMessage)
        }
    }
}