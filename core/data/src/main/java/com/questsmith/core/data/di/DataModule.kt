package com.questsmith.core.data.di

import com.questsmith.core.data.network.OpenRouterClient
import com.questsmith.core.data.network.QuestSmithSupabaseClient
import com.questsmith.core.data.repository.DefaultUserIdProvider
import com.questsmith.core.data.repository.QuizRepositoryImpl
import com.questsmith.core.data.repository.UserIdProvider
import com.questsmith.core.domain.repository.QuizRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindingsModule {
    @Binds
    abstract fun bindQuizRepository(impl: QuizRepositoryImpl): QuizRepository

    @Binds
    abstract fun bindUserIdProvider(impl: DefaultUserIdProvider): UserIdProvider
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()

    @Provides
    @Singleton
    fun provideOpenRouterClient(
        client: OkHttpClient,
        json: Json,
        @Named("openRouterKey") apiKey: String,
        @Named("packageName") packageName: String
    ): OpenRouterClient = OpenRouterClient(
        client = client,
        json = json,
        apiKeyProvider = { apiKey },
        packageNameProvider = { packageName }
    )

    @Provides
    @Singleton
    fun provideSupabaseClient(
        @Named("supabaseUrl") url: String,
        @Named("supabaseAnonKey") anonKey: String
    ): SupabaseClient? = if (url.isBlank() || anonKey.isBlank()) {
        null
    } else {
        createSupabaseClient(url, anonKey) {
            install(Postgrest)
            install(GoTrue)
        }
    }

    @Provides
    @Singleton
    fun provideQuestSmithSupabaseClient(
        supabaseClient: SupabaseClient?,
        json: Json
    ): QuestSmithSupabaseClient = QuestSmithSupabaseClient(supabaseClient, json)
}
