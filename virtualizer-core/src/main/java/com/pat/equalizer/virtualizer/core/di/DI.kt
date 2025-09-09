package com.pat.equalizer.virtualizer.core.di

import android.media.AudioManager
import android.media.audiofx.Virtualizer
import com.pat.equalizer.virtualizer.core.VirtualizerController
import com.pat.equalizer.virtualizer.core.VirtualizerControllerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DI {
    @Suppress("DEPRECATION")
    @Singleton
    @Provides
    fun provideVirtualizer() = Virtualizer(AudioManager.STREAM_MUSIC, 0)

}

@Module
@InstallIn(SingletonComponent::class)
interface VirtualizerBindModule {

    @Binds
    @Singleton
    fun bindVirtualizerController(impl: VirtualizerControllerImpl): VirtualizerController
}