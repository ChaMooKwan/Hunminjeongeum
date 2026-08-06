package kr.ac.sunmoon

// 생성한 스크린과 실시간 소켓 채팅 로직 연결 부분(추후 작성)

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ChatMessage(
    val userName: String,
    val message: String,
)

// 채팅방 정보를 저장하는 클래스 MutableStateFlow와 .update를 통해 변경 시 Compose에게 알림
class ChatViewModel(): ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages

    fun updateChatHistory(message: ChatMessage){
        _messages.update{
            it + message
        }
    }
}