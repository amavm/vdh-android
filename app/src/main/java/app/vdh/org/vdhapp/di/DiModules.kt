package app.vdh.org.vdhapp.di

import app.vdh.org.vdhapp.data.DeclarationRepository
import app.vdh.org.vdhapp.data.DeclarationRepositoryImpl
import app.vdh.org.vdhapp.viewmodels.DeclarationViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {

    single<DeclarationRepository> { DeclarationRepositoryImpl() }

    viewModel { DeclarationViewModel(androidApplication(), get()) }
}