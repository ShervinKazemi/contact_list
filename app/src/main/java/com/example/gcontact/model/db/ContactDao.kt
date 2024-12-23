package com.example.gcontact.model.db

import androidx.room.*
import com.example.gcontact.model.data.Contact
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for interacting with the Contact database.
 */
@Dao
interface ContactDao {

    /**
     * Retrieves all contacts from the contact_table.
     *
     * @return A Flow containing a list of all contacts.
     */
    @Query("SELECT * FROM contact_table")
    fun getAllContacts(): Flow<List<Contact>>

    /**
     * Retrieves a specific contact by its ID.
     *
     * @param contactId The ID of the contact to retrieve.
     * @return A Flow containing the contact with the specified ID.
     */
    @Query("SELECT * FROM contact_table WHERE id = :contactId")
    fun getContactById(contactId: Int): Flow<Contact>

    /**
     * Inserts a new contact into the database.
     * If the contact already exists, it will be replaced.
     *
     * @param contact The contact to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    /**
     * Updates an existing contact in the database.
     *
     * @param contact The contact with updated values.
     */
    @Update
    suspend fun updateContact(contact: Contact)

    /**
     * Deletes a specific contact from the database.
     *
     * @param contact The contact to delete.
     */
    @Delete
    suspend fun deleteContact(contact: Contact)

    /**
     * Deletes all contacts from the contact_table.
     */
    @Query("DELETE FROM contact_table")
    suspend fun deleteAllContacts()
}
