package com.doseyenc.flownote

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FlowNoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
