package app.vdh.org.vdhapp

import android.app.Application
import app.vdh.org.vdhapp.di.appModule
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.android.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(
                Fabric.Builder(this)
                        .kits(Crashlytics())
                        .appIdentifier(BuildConfig.APPLICATION_ID)
                        .build()
        )
        startKoin(this, listOf(appModule))
    }
}