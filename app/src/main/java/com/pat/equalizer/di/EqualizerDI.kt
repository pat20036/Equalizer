package com.pat.equalizer.di

import android.media.AudioManager
import android.media.audiofx.Equalizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EqualizerDI {

    @Singleton
    @Provides
    fun provideEqualizer() = Equalizer(AudioManager.STREAM_MUSIC, 0)

}