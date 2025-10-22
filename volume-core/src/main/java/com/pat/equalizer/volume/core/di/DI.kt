package com.pat.equalizer.volume.core.di

import com.pat.equalizer.volume.core.VolumeController
import com.pat.equalizer.volume.core.VolumeControllerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface VolumeBindModule {

    @Binds
    @Singleton
    fun bindVolumeController(impl: VolumeControllerImpl): VolumeController
}