package app.vdh.org.vdhapp.core.di

import app.vdh.org.vdhapp.BuildConfig
import app.vdh.org.vdhapp.core.consts.ApiConst.BASE_URL
import app.vdh.org.vdhapp.feature.report.data.common.local.AppDatabase
import app.vdh.org.vdhapp.feature.report.data.common.remote.RetrofitClient
import app.vdh.org.vdhapp.feature.report.domain.common.repository.ReportRepository
import app.vdh.org.vdhapp.feature.report.data.common.repository.ReportRepositoryImpl
import app.vdh.org.vdhapp.feature.report.data.common.remote.ObservationApiClient
import app.vdh.org.vdhapp.feature.report.data.common.remote.ObservationApiClientImpl
import app.vdh.org.vdhapp.feature.report.data.common.remote.mock.RetrofitMockClient
import app.vdh.org.vdhapp.feature.report.data.map.remote.BicyclePathApiClient
import app.vdh.org.vdhapp.feature.report.data.map.remote.BicyclePathApiClientImpl
import app.vdh.org.vdhapp.feature.report.data.map.repository.BicyclePathRepositoryImpl
import app.vdh.org.vdhapp.feature.report.domain.map.repository.BicyclePathRepository
import app.vdh.org.vdhapp.feature.report.domain.map.usecase.GetBicyclePathUseCase
import app.vdh.org.vdhapp.feature.report.domain.map.usecase.GetReportListUseCase
import app.vdh.org.vdhapp.feature.report.domain.map.usecase.SyncReportListUseCase
import app.vdh.org.vdhapp.feature.report.domain.moderation.usecase.GetReportByModerationStatusUseCase
import app.vdh.org.vdhapp.feature.report.domain.moderation.usecase.UpdateModerationStatusUseCase
import app.vdh.org.vdhapp.feature.report.domain.reporting.usecase.DeleteReportUseCase
import app.vdh.org.vdhapp.feature.report.domain.reporting.usecase.SaveReportUseCase
import app.vdh.org.vdhapp.feature.report.presentation.map.viewmodel.HoursFilterViewModel
import app.vdh.org.vdhapp.feature.report.presentation.map.viewmodel.ReportMapViewModel
import app.vdh.org.vdhapp.feature.report.presentation.reporting.viewmodel.ReportingViewModel
import app.vdh.org.vdhapp.feature.report.presentation.map.viewmodel.StatusFilterViewModel
import app.vdh.org.vdhapp.feature.report.presentation.moderation.viewmodel.ReportModerationViewModel
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.mock.MockRetrofit

val appModule = module {

    single { AppDatabase.create(androidApplication()) }

    single {
        val database = get() as AppDatabase
        database.declarationDao()
    }

    single {
        val mockRetrofit = MockRetrofit.Builder(buildBaseRetrofit())
                .build()
        val delegate = mockRetrofit.create(RetrofitClient::class.java)
        RetrofitMockClient(delegate, get() as AppDatabase)
    }

    single<RetrofitClient> {
        if (BuildConfig.isMockDataEnabled) {
            get() as RetrofitMockClient
        } else {
            buildBaseRetrofit().create(RetrofitClient::class.java)
        }
    }

    single<ObservationApiClient> { ObservationApiClientImpl(get()) }
    single<BicyclePathApiClient> { BicyclePathApiClientImpl(get()) }

    single<ReportRepository> { ReportRepositoryImpl(get(), get(), androidApplication().applicationContext) }
    single<BicyclePathRepository> { BicyclePathRepositoryImpl(get()) }

    factory { GetReportListUseCase(get()) }
    factory { GetBicyclePathUseCase(get()) }
    factory { SaveReportUseCase(get(), androidApplication().applicationContext) }
    factory { DeleteReportUseCase(get()) }
    factory { GetReportByModerationStatusUseCase(get()) }
    factory { SyncReportListUseCase(get()) }
    factory { UpdateModerationStatusUseCase(get()) }

    viewModel { ReportingViewModel(androidApplication(), get(), get()) }
    viewModel { ReportMapViewModel(get(), get(), get()) }
    viewModel { StatusFilterViewModel(androidApplication()) }
    viewModel { HoursFilterViewModel(androidApplication()) }
    viewModel { ReportModerationViewModel(get(), get(), get()) }
}

private fun buildBaseRetrofit(): Retrofit {
    return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(BASE_URL)
            .build() as Retrofit
}