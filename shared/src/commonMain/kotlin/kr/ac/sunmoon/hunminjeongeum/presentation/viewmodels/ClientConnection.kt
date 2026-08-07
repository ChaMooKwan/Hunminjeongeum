package kr.ac.sunmoon.hunminjeongeum.presentation.viewmodels

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
    val userNameViewModel = ViewModel<String>()
    val questionViewModel = ViewModel<String>() // 마지막 배열 값만 정답 화면에 출력해야 함. .last사용해서
    val scoreViewModel = ViewModel<Int>()

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
                val received: String = reader.readLine()

                if (received.contains("/userNames,")) {
                    updateUserNames(decodeUserNames(received))
                }
                else if (received.contains("/chat,")) {
                    updateChat(decodeChat(received))
                }
                else if (received.contains("/playGame,")) {
                    // 3번 창으로 넘어가기(3번창 호출?)
                    // isStarted = true??
                }
                else if (received.contains("/question,")) {
                    updateQuestion(decodeQuestion(received))
                }
                else if (received.contains("/score,")){
                    updateScore(decodeScore(received))
                }
            }
        }
    }
    private fun enterWaitingRoom() {
        writer.println(userName)
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
        userNameViewModel.update(userName)
    }
    private fun decodeChat(received: String): ChatMessage {
        // !! 서버는 ChatMessage 클래스에서 속성인 userName과 message를 ,를 구분자로 하여 전송한다.
        val list: List<String> = received.split(",")
        val chatMessage = ChatMessage(list[1], list.last())
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

    private fun decodeScore(received: String): List<Int>{
        val list:List<String> = received.split(",")
        val scores = mutableListOf<Int>()
        for (i in 1 until list.size){
            scores.add(list[i].toInt())
        }
        return scores
    }

    private fun updateScore(scores: List<Int>){
        scoreViewModel.updateScore(scores)
    }

    // [2번 대기자 창]에서 게임 시작 버튼 누르면 onClick ={ a.startGame() }에서 호출
    fun startGame() {
        writer.println("/startGame")
    }

    // [3번 게임 창]에서 확인 버튼 누르면 호출하면 된다.
    private fun sendChat(message: String) {
        val sendingMessage = "/chat,$userName,$message"
        writer.println(sendingMessage)
    }
}