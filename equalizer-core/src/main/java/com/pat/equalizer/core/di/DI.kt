package com.pat.equalizer.core.di

import android.media.AudioManager
import android.media.audiofx.Equalizer
import com.pat.equalizer.core.EqualizerController
import com.pat.equalizer.core.EqualizerControllerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EqualizerModule {
    @Singleton
    @Provides
    fun provideEqualizer(): Equalizer = Equalizer(AudioManager.STREAM_MUSIC, 0)
}

@Module
@InstallIn(SingletonComponent::class)
interface EqualizerBindsModule {
    @Binds
    @Singleton
    fun bindEqualizerController(impl: EqualizerControllerImpl): EqualizerController
}