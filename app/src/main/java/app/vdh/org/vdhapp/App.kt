package app.vdh.org.vdhapp

import android.app.Application
import android.util.Log
import app.vdh.org.vdhapp.core.di.appModule
import app.vdh.org.vdhapp.core.helpers.AuthHelper
import org.koin.android.ext.android.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
        AuthHelper.signInAnonymously {
            Log.d("App", "Authenticated anonymously")
        }
    }
}