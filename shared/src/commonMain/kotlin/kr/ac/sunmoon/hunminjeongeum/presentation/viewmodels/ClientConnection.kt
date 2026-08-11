package kr.ac.sunmoon

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
) {
    private lateinit var socket: Socket
    private lateinit var reader: BufferedReader
    private lateinit var writer: PrintWriter
    val chatViewModel = ViewModel<ChatMessage>()
    val questionViewModel = ViewModel<String>() // 마지막 배열 값만 정답 화면에 출력해야 함. .last사용해서
    val userInfoViewModel = ViewModel<UserInfo>()
    val timerViewModel = ViewModel<Int>()
    val hintViewModel = ViewModel<String>()
    val subjectViewModel = ViewModel<String>()
    var isGameStarted by mutableStateOf(false)
        private set
    var isGameOver by mutableStateOf(false)
        private set

    // [1번 화면]의 닉네임, 아이피, 포트 입력하고 확인 눌렀을 때 호출한다.
    fun connect() {
        socket = Socket(ip, port)
        reader = socket.getInputStream().bufferedReader()
        writer = PrintWriter(socket.getOutputStream(), true)
    }

    // [2번 대기자 창]에서 호출한다.
    fun startReceiver() {
        thread(isDaemon = true) {
            enterWaitingRoom()
            while (true){
                val received: String = reader.readLine() ?: break

                if (received.contains("/userNames,")) {
                    println(received)
                    updateUserNames(decodeUserNames(received))
                }
                else if (received.contains("/chat,")) {
                    updateChat(decodeChat(received))
                    println(received)
                }
                else if (received.contains("/playGame,")) {
                    println("playGame!!")
                    updateSubject(received)
                    isGameStarted = true
                    isGameOver = false
                }
                else if (received.contains("/gameOver,")) {
                    subjectViewModel.clearMessages()
                    isGameStarted = false
                    isGameOver = true
                }
                else if (received.contains("/question,")) {
                    updateQuestion(decodeQuestion(received))
                    hintViewModel.clearMessages()
                }
                else if (received.contains("/score,")){
                    updateScore(decodeScore(received))
                }
                else if (received.contains("/timer,")) {
                    updateTimer(decodeTimer(received))
                }
                else if (received.contains("/hint^")) {
                    updateHint(received)
                }
            }
        }
    }
    private fun enterWaitingRoom() {
        writer.println(userName)
    }
    private fun updateSubject(received: String){
        val list = received.split(",")
        val subject = listOf<String>("과일","국가","요리","동물","사자성어")
        subjectViewModel.update(subject[list[1].toInt()-1])
    }

    private fun updateHint(received: String){
        val list: List<String> = received.split("^")
        hintViewModel.update(list[2])
    }
    private fun decodeUserNames(received: String): List<String>{
        val list: List<String> = received.split(",")
        val userNames = mutableListOf<String>()
        for (i in 1 until list.size){
            userNames.add(list[i])
        }
        return userNames
    }
    private fun updateUserNames(userNames: List<String>){
        userInfoViewModel.updateScore(userNames.map { UserInfo(it,0) })
    }
    private fun decodeChat(received: String): ChatMessage {
        val list: List<String> = received.split(",")
        val stringChatMessage = list[1].split("&")
        val chatMessage = ChatMessage(stringChatMessage[0], stringChatMessage.last())
        return chatMessage
    }
    private fun updateChat(chatMessage: ChatMessage){
        chatViewModel.update(chatMessage)
    }
    private fun decodeQuestion(received: String): String{
        val list: List<String> = received.split(",")
        val question = list[1]
        return question
    }
    private fun updateQuestion(question: String){
        questionViewModel.update(question)
    }

    private fun decodeScore(received: String): List<UserInfo>{// "/score,이름$점수,이름&점수"
        val list:List<String> = received.split(",")
        val stringUserInfos = mutableListOf<String>()
        for (i in 1 until list.size){
            stringUserInfos.add(list[i])
        }
        val userInfos = mutableListOf<UserInfo>()
        stringUserInfos.forEach{
            val userInfo = it.split("$")
            userInfos.add(UserInfo(userInfo[0],userInfo[1].toInt()))
        }
        return userInfos
    }

    private fun updateScore(userInfos: List<UserInfo>){
        userInfoViewModel.updateScore(userInfos)
    }

    private fun decodeTimer(received: String): List<Int>{
        val list: List<String> = received.split(",")
        val timer = listOf<Int>(list[1].toInt())
        return timer
    }
    private fun updateTimer(timer: List<Int>){
        timerViewModel.updateScore(timer)
    }

    // [2번 대기자 창]에서 게임 시작 버튼 누르면 onClick ={ a.startGame() }에서 호출
    fun startGame(category: Int) {
        writer.println("/startGame,${category}")
    }

    // [3번 게임 창]에서 확인 버튼 누르면 호출하면 된다.
    fun sendChat(message: String) {
        println("print plz")
        val sendingMessage = "/chat,$userName&$message"
        print("plzzzzzzzzz")
        writer.println(sendingMessage)
        print("yes!")
    }
}
