package com.popularmovie.appcomponent.di

import android.content.Context
import androidx.room.Room
import com.popularmovie.BuildConfig
import com.popularmovie.appcomponent.data.remote.AppLevelApi
import com.popularmovie.appcomponent.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthInterceptorOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthAppLevelApi


    @Provides
    @AuthInterceptorOkHttpClient
    fun providesRetrofitForPostLogin(
        @ApplicationContext context: Context
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(getRetrofitClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    private fun getRetrofitClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .also { client ->
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logging)
                }
                client.addNetworkInterceptor {
                    it.proceed(it.request().newBuilder().header("Content-Type", "application/json").build())
                }
            }.build()
    }

    @Provides
    @AuthAppLevelApi
    fun providesPreLoginAppLevelApi(@AuthInterceptorOkHttpClient retrofit: Retrofit)
            : AppLevelApi = retrofit.create(AppLevelApi::class.java)

    @Provides
    @Singleton
    fun providesRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "movie_app_db"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
    }
}