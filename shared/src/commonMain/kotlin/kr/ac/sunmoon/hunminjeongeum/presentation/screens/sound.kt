package kr.ac.sunmoon

import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

object SoundManager {
    private var bgmClip: Clip? = null

    // 로그인 화면 배경음
    fun playLogin() {
        playBgm("../shared/src/commonMain/composeResources/files/LogIn.wav")
    }

    fun playPrepareRoom() {
        playBgm("../shared/src/commonMain/composeResources/files/PrepareRoom.wav")
    }

    fun playGameStart() {
        playBgm("../shared/src/commonMain/composeResources/files/GameStart.wav")
    }

    fun playCorrect() {
        playEffect("../shared/src/commonMain/composeResources/files/Correct.wav")
    }

    // 배경음 재생 (반복)
    private fun playBgm(path: String) {
        try {
            stopBgm()
            println("현재 위치: ${java.io.File(".").absolutePath}")
            val file = java.io.File(path)
            if (!file.exists()) {
                println("파일 없음: ${file.absolutePath}")
                return
            }
            val audio = AudioSystem.getAudioInputStream(file)
            bgmClip = AudioSystem.getClip()
            bgmClip?.open(audio)
            bgmClip?.loop(Clip.LOOP_CONTINUOUSLY)
            bgmClip?.start()
        } catch (e: Exception) {
            println("BGM 재생 실패: ${e.message}")
        }
    }

    // 효과음 재생 (한번만)
    private fun playEffect(path: String) {
        try {
            val file = java.io.File(path)
            if (!file.exists()) {
                println("파일 없음: ${file.absolutePath}")
                return
            }
            val audio = AudioSystem.getAudioInputStream(file)
            val clip = AudioSystem.getClip()
            clip.open(audio)
            clip.start()
        } catch (e: Exception) {
            println("효과음 재생 실패: ${e.message}")
        }
    }

    // 배경음 정지
    fun stopBgm() {
        bgmClip?.stop()
        bgmClip?.close()
        bgmClip = null
    }
}