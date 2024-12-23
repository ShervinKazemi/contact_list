package com.example.gcontact.di

import android.content.Context
import com.example.gcontact.model.db.ContactDao
import com.example.gcontact.model.db.ContactDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing database-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides a singleton instance of [ContactDatabase].
     *
     * @param context The application context.
     * @return A singleton instance of the ContactDatabase.
     */
    @Provides
    @Singleton
    fun provideContactDatabase(@ApplicationContext context: Context): ContactDatabase {
        return ContactDatabase.getDatabase(context)
    }

    /**
     * Provides a singleton instance of [ContactDao] from the database.
     *
     * @param database The [ContactDatabase] instance.
     * @return A singleton instance of ContactDao.
     */
    @Provides
    @Singleton
    fun provideContactDao(database: ContactDatabase): ContactDao {
        return database.contactDao()
    }
}
