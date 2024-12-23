package com.example.gcontact.model.repository.contact

import com.example.gcontact.model.data.Contact
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing contact data.
 * This interface abstracts the data operations from the ViewModel layer.
 */
interface ContactRepository {
    /**
     * Get all contacts from the database.
     *
     * @return A Flow that emits a list of contacts.
     */
    fun getAllContacts(): Flow<List<Contact>>

    /**
     * Get a specific contact by its ID.
     *
     * @param contactId The ID of the contact.
     * @return A Flow that emits the contact.
     */
    fun getContactById(contactId: Int): Flow<Contact>

    /**
     * Insert a new contact into the database.
     *
     * @param contact The contact to insert.
     */
    suspend fun insertContact(contact: Contact)

    /**
     * Update an existing contact in the database.
     *
     * @param contact The contact with updated values.
     */
    suspend fun updateContact(contact: Contact)

    /**
     * Delete a specific contact from the database.
     *
     * @param contact The contact to delete.
     */
    suspend fun deleteContact(contact: Contact)

    /**
     * Delete all contacts from the database.
     */
    suspend fun deleteAllContacts()
}
