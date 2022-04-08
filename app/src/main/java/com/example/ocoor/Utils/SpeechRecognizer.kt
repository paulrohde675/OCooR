package com.example.ocoor.Utils

import android.content.Context

class SpeechRecognizer private constructor(context: Context) {
    init {
        // do something with context
    }

    companion object : SingletonHolder<SpeechRecognizer, Context>(::SpeechRecognizer)
}
