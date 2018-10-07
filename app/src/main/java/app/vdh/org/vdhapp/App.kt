package app.vdh.org.vdhapp

import android.app.Application
import app.vdh.org.vdhapp.di.appModule
import org.koin.android.ext.android.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
    }
}