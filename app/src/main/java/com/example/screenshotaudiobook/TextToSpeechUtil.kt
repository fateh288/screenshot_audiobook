package com.example.screenshotaudiobook

import android.speech.tts.TextToSpeech

fun playAudio(textList: List<String>, tts : TextToSpeech?){
    textList.forEach {
        tts?.speak(it, TextToSpeech.QUEUE_ADD, null, it)
    }
}