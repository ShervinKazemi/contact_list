package com.example.gcontact.di

import com.example.gcontact.model.db.ContactDao
import com.example.gcontact.model.repository.contact.ContactRepository
import com.example.gcontact.model.repository.contact.ContactRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Dagger Hilt module for providing repository-related dependencies.
 */
@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    /**
     * Provides an implementation of [ContactRepository].
     *
     * This binds the interface [ContactRepository] to its implementation [ContactRepositoryImpl].
     *
     * @param contactDao The [ContactDao] instance used in the repository.
     * @return An instance of [ContactRepositoryImpl].
     */
    @Provides
    fun provideContactRepository(contactDao: ContactDao): ContactRepository {
        return ContactRepositoryImpl(contactDao)
    }
}
