package kr.ac.sunmoon

import androidx.compose.ui.graphics.Color

// =====================
// 메인 색상
// =====================
val Purple = Color(0xFF6C63FF)        // 메인 색상 (버튼, 테두리, 포인트)
val LightPurple = Color(0xFFE8E6FF)   // 연한 보라 (배경, 카드 배경)
val DarkPurple = Color(0xFF2C2C54)    // 진한 보라 (텍스트, 오버레이)

// =====================
// 포인트 색상
// =====================
val Coral = Color(0xFFFF6B6B)         // 코랄 (에러, 게임시작 버튼, 카테고리)
val Yellow = Color(0xFFFFD93D)        // 노랑 (강조, 점수, 정답 이펙트)

// =====================
// 기본 색상
// =====================
val White = Color(0xFFFFFFFF)         // 흰색 (배경, 버튼 텍스트)
val Black = Color(0xFF000000)         // 검정 (기본 텍스트)
val Gray = Color(0xFF9E9E9E)          // 회색 (대기중, 비활성화)
val LightGray = Color(0xFFF5F5F5)    // 연한 회색 (비활성화 배경)

// =====================
// 투명도 적용 색상
// =====================
val PurpleHalf = Purple.copy(alpha = 0.5f)      // 보라 50% (평소 입력창 테두리)
val DarkPurpleOverlay = DarkPurple.copy(alpha = 0.7f)  // 게임오버 오버레이 배경

// =====================
// 채팅 색상
// =====================
val MyMessageColor = LightPurple      // 내 메시지 카드 배경
val OtherMessageColor = White         // 상대 메시지 카드 배경

// =====================
// 힌트 색상
// =====================
val EasyHintColor = LightPurple       // 쉬운 힌트 카드 배경
val NormalHintColor = Yellow.copy(alpha = 0.3f)  // 중간 힌트 카드 배경
val HardHintColor = Coral.copy(alpha = 0.3f)     // 어려운 힌트 카드 배경
