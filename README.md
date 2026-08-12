# Hunminjeongeum

훈민정음은 실시간 멀티플레이 초성 퀴즈 데스크톱 앱입니다.

사용자는 서버 IP와 포트, 닉네임을 입력해 대기실에 입장하고, 카테고리를 선택해 게임을 시작합니다. 게임 중에는 초성 문제, 제한 시간, 힌트, 채팅, 점수가 실시간으로 갱신됩니다.

## 주요 기능

- 서버 접속을 위한 프로필 입력 화면
- 대기실 사용자 목록 및 게임 시작 화면
- 카테고리 선택 기반 초성 퀴즈 진행
- 실시간 채팅 및 정답 입력
- 점수판, 라운드 표시, 타이머 표시
- 단계별 힌트 표시
- 정답 효과음 및 게임 시작 효과음
- 게임 종료 결과 화면

## 기술 스택

- Kotlin 2.4.10
- Kotlin Multiplatform JVM
- Compose Multiplatform Desktop
- Material 3
- Kotlin Coroutines
- Java Socket
- Gradle Wrapper

## 프로젝트 구조

```text
.
├── desktopApp/
│   └── src/main/kotlin/.../main.kt          # 데스크톱 앱 진입점
├── shared/
│   └── src/commonMain/
│       ├── kotlin/kr/ac/sunmoon/hunminjeongeum/
│       │   ├── app/                         # 앱 레벨 구성
│       │   ├── core/                        # 네트워크, 데이터베이스, 테마 설정
│       │   ├── data/                        # 원격 통신 및 저장소 구현 위치
│       │   ├── domain/                      # 유스케이스 영역
│       │   └── presentation/                # 화면, 컴포넌트, ViewModel, 소켓 연결
│       └── composeResources/
│           ├── files/                       # 효과음 리소스
│           └── font/                        # 앱 폰트
└── gradle/
```

## 실행 전 준비

이 앱은 별도의 서버 앱이 먼저 실행되어 있어야 정상적으로 게임을 진행할 수 있습니다.

1. `hunminjeongeum_server` 프로젝트를 실행합니다.
2. 서버 앱에서 사용할 포트 번호를 입력합니다.
3. 이 클라이언트 앱을 실행합니다.
4. 닉네임, 서버 IP, 서버 포트를 입력해 접속합니다.

로컬에서 함께 실행하는 경우 IP 주소는 보통 `127.0.0.1` 또는 `localhost`를 사용할 수 있습니다.

## 실행 방법

Windows PowerShell 기준:

```powershell
.\gradlew.bat :desktopApp:run
```

macOS/Linux 기준:

```bash
./gradlew :desktopApp:run
```

## 테스트

```powershell
.\gradlew.bat test
```

## 게임 흐름

1. 프로필 화면에서 이름, 서버 IP, 포트 번호를 입력합니다.
2. 대기실에서 접속한 플레이어 목록을 확인합니다.
3. 카테고리를 선택하고 게임을 시작합니다.
4. 초성 문제를 보고 정답을 채팅 입력창에 제출합니다.
5. 정답을 맞힌 사용자는 점수를 얻고 다음 문제가 표시됩니다.
6. 모든 문제가 끝나거나 제한 시간이 종료되면 결과 화면이 표시됩니다.

## 서버 메시지 처리

클라이언트는 서버에서 전달되는 문자열 명령을 해석해 화면 상태를 갱신합니다.

- `/userNames,`: 대기실 사용자 목록 갱신
- `/chat,`: 채팅 메시지 수신
- `/playGame,`: 게임 시작 및 카테고리 수신
- `/question,`: 새 초성 문제 수신
- `/score,`: 점수판 갱신
- `/timer,`: 남은 시간 갱신
- `/hint^`: 힌트 추가
- `/gameOver,`: 게임 종료

## 참고

- 서버와 클라이언트는 같은 포트 번호를 사용해야 합니다.
- 같은 컴퓨터가 아니라면 방화벽 또는 네트워크 설정 때문에 접속이 차단될 수 있습니다.
- 일부 소스 주석은 기존 인코딩 문제로 깨져 보일 수 있으므로, 새 문서는 UTF-8 기준으로 작성했습니다.
