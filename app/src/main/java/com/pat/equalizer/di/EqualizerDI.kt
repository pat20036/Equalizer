package com.pat.equalizer.di

import android.content.Context
import android.media.AudioManager
import android.media.audiofx.Equalizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EqualizerDI {

    @Singleton
    @Provides
    fun provideEqualizer() = Equalizer(AudioManager.STREAM_MUSIC, 0)

    @Provides
    fun provideContext(@ApplicationContext context: Context) = context
}