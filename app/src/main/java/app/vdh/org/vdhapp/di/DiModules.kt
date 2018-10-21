package app.vdh.org.vdhapp.di

import app.vdh.org.vdhapp.data.AppDatabase
import app.vdh.org.vdhapp.data.ReportRepository
import app.vdh.org.vdhapp.data.ReportRepositoryImpl
import app.vdh.org.vdhapp.viewmodels.ReportMapViewModel
import app.vdh.org.vdhapp.viewmodels.ReportingViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {

    single { AppDatabase.create(androidApplication()) }

    single {
        val database = get() as AppDatabase
        database.declarationDao()
    }

    single<ReportRepository> { ReportRepositoryImpl(get()) }

    viewModel { ReportingViewModel(androidApplication(), get()) }
    viewModel { ReportMapViewModel(androidApplication(), get ()) }
}