package app.vdh.org.vdhapp.di

import app.vdh.org.vdhapp.data.AppDatabase
import app.vdh.org.vdhapp.data.DeclarationRepository
import app.vdh.org.vdhapp.data.DeclarationRepositoryImpl
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

    single<DeclarationRepository> { DeclarationRepositoryImpl(get()) }

    viewModel { ReportingViewModel(androidApplication(), get()) }
    viewModel { ReportMapViewModel(androidApplication()) }
}