package app.vdh.org.vdhapp.core.di

import app.vdh.org.vdhapp.feature.report.data.common.local.AppDatabase
import app.vdh.org.vdhapp.feature.report.data.common.remote.ApiRetrofitClient
import app.vdh.org.vdhapp.feature.report.domain.common.repository.ReportRepository
import app.vdh.org.vdhapp.feature.report.data.common.repository.ReportRepositoryImpl
import app.vdh.org.vdhapp.feature.report.data.common.remote.ObservationApiClient
import app.vdh.org.vdhapp.feature.report.data.common.remote.ObservationApiClientImpl
import app.vdh.org.vdhapp.feature.report.data.common.remote.ObservationApiClientImpl.Companion.BASE_URL
import app.vdh.org.vdhapp.feature.report.data.map.remote.BicyclePathApiClient
import app.vdh.org.vdhapp.feature.report.data.map.remote.BicyclePathApiClientImpl
import app.vdh.org.vdhapp.feature.report.data.map.repository.BicyclePathRepositoryImpl
import app.vdh.org.vdhapp.feature.report.domain.map.repository.BicyclePathRepository
import app.vdh.org.vdhapp.feature.report.domain.map.usecase.GetBicyclePathUseCase
import app.vdh.org.vdhapp.feature.report.domain.map.usecase.GetReportListUseCase
import app.vdh.org.vdhapp.feature.report.domain.reporting.usecase.DeleteReportUseCase
import app.vdh.org.vdhapp.feature.report.domain.reporting.usecase.SaveReportUseCase
import app.vdh.org.vdhapp.feature.report.presentation.map.viewmodel.HoursFilterViewModel
import app.vdh.org.vdhapp.feature.report.presentation.map.viewmodel.ReportMapViewModel
import app.vdh.org.vdhapp.feature.report.presentation.reporting.viewmodel.ReportingViewModel
import app.vdh.org.vdhapp.feature.report.presentation.map.viewmodel.StatusFilterViewModel
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    single { AppDatabase.create(androidApplication()) }

    single {
        val database = get() as AppDatabase
        database.declarationDao()
    }

    single<ApiRetrofitClient> {
        Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(ApiRetrofitClient::class.java)
    }

    single<ObservationApiClient> { ObservationApiClientImpl(get()) }
    single<BicyclePathApiClient> { BicyclePathApiClientImpl(get()) }

    single<ReportRepository> { ReportRepositoryImpl(get(), get(), androidApplication().applicationContext) }
    single<BicyclePathRepository> { BicyclePathRepositoryImpl(get()) }

    factory { GetReportListUseCase(get()) }
    factory { GetBicyclePathUseCase(get()) }
    factory { SaveReportUseCase(get(), androidApplication().applicationContext) }
    factory { DeleteReportUseCase(get()) }

    viewModel { ReportingViewModel(androidApplication(), get(), get()) }
    viewModel { ReportMapViewModel(androidApplication(), get(), get()) }
    viewModel { StatusFilterViewModel(androidApplication()) }
    viewModel { HoursFilterViewModel(androidApplication()) }
}