package com.example.gcontact.model.repository.contact

import com.example.gcontact.model.data.Contact
import com.example.gcontact.model.db.ContactDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao
) : ContactRepository {

    override fun getAllContacts(): Flow<List<Contact>> = contactDao.getAllContacts()

    override fun getContactById(contactId: Int): Flow<Contact> = contactDao.getContactById(contactId)

    override suspend fun insertContact(contact: Contact) {
        contactDao.insertContact(contact)
    }

    override suspend fun updateContact(contact: Contact) {
        contactDao.updateContact(contact)
    }

    override suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact)
    }

    override suspend fun deleteAllContacts() {
        contactDao.deleteAllContacts()
    }

}
