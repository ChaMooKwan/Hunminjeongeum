package kr.ac.sunmoon.hunminjeongeum.presentation.viewmodels

// 생성한 스크린과 실시간 소켓 채팅 로직 연결 부분(추후 작성)

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.emptyList

class ChatMessage(
    val userName: String,
    val message: String,
)

class UserInfo(
    val userName: String,
    val score: Int,
)

// 채팅방 정보를 저장하는 클래스 MutableStateFlow와 .update를 통해 변경 시 Compose에게 알림
class ViewModel<T>(): ViewModel() {
    private val _messages = MutableStateFlow<List<T>>(emptyList())
    val messages = _messages


    fun update(message: T){
        _messages.update{
            it + message
        }
    }
    fun updateScore(scores: List<T>){
        _messages.update{
            scores
        }
    }
}