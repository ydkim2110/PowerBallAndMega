package com.example.inappreview.di

import com.example.inappreview.InAppReviewManager
import com.example.inappreview.InAppReviewManagerImpl
import com.example.inappreview.preferences.InAppReviewPreferences
import com.example.inappreview.preferences.InAppReviewPreferencesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InAppReviewBinds {

    @Binds
    @Singleton
    abstract fun bindInAppReviewPreferences(
        inAppReviewPreferencesImpl: InAppReviewPreferencesImpl
    ): InAppReviewPreferences

    @Binds
    @Singleton
    abstract fun bindInAppReviewManager(
        inAppReviewManagerImpl: InAppReviewManagerImpl
    ): InAppReviewManager

}