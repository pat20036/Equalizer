package com.pat.equalizer.bassboost.core.di

import android.media.AudioManager
import android.media.audiofx.BassBoost
import com.pat.equalizer.bassboost.core.BassBoostController
import com.pat.equalizer.bassboost.core.BassBoostControllerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DI {
    @Singleton
    @Provides
    fun provideBassBoost() = BassBoost(AudioManager.STREAM_MUSIC, 0)

}

@Module
@InstallIn(SingletonComponent::class)
interface BassBoostBindsModule {

    @Binds
    @Singleton
    fun bindBassBoostController(impl: BassBoostControllerImpl): BassBoostController
}