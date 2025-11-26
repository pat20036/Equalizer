package com.pat.equalizer.loudness.core.di

import android.media.audiofx.LoudnessEnhancer
import com.pat.equalizer.loudness.core.LoudnessController
import com.pat.equalizer.loudness.core.LoudnessControllerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LoudnessModule {

    @Provides
    @Singleton
    fun provideLoudnessController(): LoudnessEnhancer = LoudnessEnhancer(0)
}

@Module
@InstallIn(SingletonComponent::class)
interface LoudnessBindModule {

    @Binds
    @Singleton
    fun bindLoudnessController(impl: LoudnessControllerImpl): LoudnessController
}