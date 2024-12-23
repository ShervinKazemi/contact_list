package com.example.gcontact.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gcontact.model.data.Contact

/**
 * Database class for the Contact database. This class defines the database configuration
 * and serves as the main access point for the underlying database.
 */
@Database(
    entities = [Contact::class], // List of entities (tables) that belong to the database
    version = 2, // Database version
    exportSchema = false // Disable schema export, can be useful for debugging
)
abstract class ContactDatabase : RoomDatabase() {

    /**
     * Provides access to the ContactDao, which contains the database operations.
     *
     * @return The ContactDao instance.
     */
    abstract fun contactDao(): ContactDao

    companion object {
        // Singleton pattern to ensure only one instance of the database is created
        @Volatile
        private var INSTANCE: ContactDatabase? = null

        /**
         * Retrieves the singleton instance of the ContactDatabase.
         * If no instance exists, it creates one.
         *
         * @param context The application context used to create the database.
         * @return The instance of the ContactDatabase.
         */
        fun getDatabase(context: Context): ContactDatabase {
            return INSTANCE ?: synchronized(this) {
                // Create the database if it doesn't exist
                Room.databaseBuilder(
                    context.applicationContext,
                    ContactDatabase::class.java,
                    "contact_database" // Database name
                )
                    .fallbackToDestructiveMigration() // Allows migration fallback on version changes
                    .build()
                    .also { INSTANCE = it } // Save instance to prevent recreation
            }
        }
    }
}
