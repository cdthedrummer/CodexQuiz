package com.questsmith.app.di

import android.content.Context
import com.questsmith.app.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {
    @Provides
    @Named("openRouterKey")
    fun provideOpenRouterKey(): String = BuildConfig.OPENROUTER_API_KEY

    @Provides
    @Named("supabaseUrl")
    fun provideSupabaseUrl(): String = BuildConfig.SUPABASE_URL

    @Provides
    @Named("supabaseAnonKey")
    fun provideSupabaseAnonKey(): String = BuildConfig.SUPABASE_ANON_KEY

    @Provides
    @Named("packageName")
    fun providePackageName(@ApplicationContext context: Context): String = context.packageName
}
