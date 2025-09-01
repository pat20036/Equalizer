package com.pat.equalizer.di

import android.content.Context
import com.pat.equalizer.repository.EqualizerController
import com.pat.equalizer.repository.EqualizerControllerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class EqualizerDI {

    @Provides
    fun provideContext(@ApplicationContext context: Context) = context

    @Provides
    fun provideEqualizerController(@ApplicationContext context: Context): EqualizerController = EqualizerControllerImpl(context)
}