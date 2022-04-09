package com.example.ocoor.Utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import java.util.*


class SpeechRecognizerModule(context: Context) {
    val mainActivity = context as Activity
    val RQ_SPEECH_REC = 42

    init {

    }

    fun askSpeechInput(){
        if(!SpeechRecognizer.isRecognitionAvailable(mainActivity)){
            Toast.makeText(mainActivity, "Speech Recognition is not available", Toast.LENGTH_SHORT).show()
        } else {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "e.g.: 100 g chocolate")
            mainActivity.startActivityForResult(i, RQ_SPEECH_REC)
        }

    }

    companion object : SingletonHolder<SpeechRecognizerModule, Context>(::SpeechRecognizerModule)
}
