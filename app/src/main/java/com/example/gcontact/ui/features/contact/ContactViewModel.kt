package com.example.gcontact.ui.features.contact

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gcontact.model.data.Contact
import com.example.gcontact.model.repository.contact.ContactRepository
import com.example.gcontact.util.ContactUiState
import com.example.gcontact.util.coroutineExceptionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _contact = MutableStateFlow<Contact?>(null)
    val contact: StateFlow<Contact?> = _contact.asStateFlow()

    private val _uiState = MutableStateFlow<ContactUiState>(ContactUiState.Initial)
    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()


    private val _selectedImageProfile = MutableStateFlow<Uri?>(null)
    val selectedImageProfile: StateFlow<Uri?> = _selectedImageProfile.asStateFlow()

    // Method to save and get persistent image path
    suspend fun saveAndGetImagePath(context: Context, uri: Uri): String {
        val savedImagePath = saveImageToLocalStorage(context, uri)
        return savedImagePath
    }

    // Existing method with improvement
    fun setSelectedImage(context: Context, uri: Uri?) {
        viewModelScope.launch {
            val savedPath = uri?.let {
                saveAndGetImagePath(context, it)
            }
            _selectedImageProfile.value = savedPath?.let { Uri.parse(it) }
        }
    }

    // Enhanced image saving method
    private suspend fun saveImageToLocalStorage(context: Context, uri: Uri): String =
        withContext(Dispatchers.IO) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                val filename = "profile_${System.currentTimeMillis()}.jpg"
                val file = File(context.getExternalFilesDir(null), filename)

                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                file.absolutePath
            } catch (e: Exception) {
                Log.e("ImageSave", "Failed to save image", e)
                ""
            }
        }

    init {
        loadContacts()
    }

    /**
     * Load all contacts from the repository
     */
    private fun loadContacts() {
        viewModelScope.launch(coroutineExceptionHandler) {
            contactRepository.getAllContacts().collect { contacts ->
                _contacts.value = contacts
                _uiState.value = if (contacts.isNotEmpty()) {
                    ContactUiState.Success(contacts)
                } else {
                    ContactUiState.Empty
                }
            }
        }
    }
    /**
     * Load a single contact by its ID
     */
    fun loadContact(contactId: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            contactRepository.getContactById(contactId).collect { contact ->
                _contact.value = contact
                _uiState.value = contact?.let {
                    ContactUiState.ContactDetail(it)
                } ?: ContactUiState.Error("Contact not found")
            }
        }
    }

    /**
     * Insert or update a contact, optionally saving an image path
     */
    fun insertContact(contact: Contact) {
        viewModelScope.launch {
            try {
                // Insert the contact (Room will handle auto-generating the contactId)
                contactRepository.insertContact(contact)
            } catch (e: Exception) {
                // Handle any errors (log, show error message, etc.)
                Log.e("ContactViewModel", "Error inserting contact: ${e.message}")
            }
        }
    }

    fun updateContact(contact: Contact) {
        viewModelScope.launch(coroutineExceptionHandler) {
            contactRepository.updateContact(contact)
            loadContact(contact.id)
        }
    }

    /**
     * Delete a contact
     */
    fun deleteContact(contact: Contact) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                contactRepository.deleteContact(contact)
                _uiState.value = ContactUiState.ContactDeleted
            } catch (e: Exception) {
                _uiState.value = ContactUiState.Error(e.message ?: "Failed to delete contact")
            }
        }
    }

    fun deleteAllContacts() {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                contactRepository.deleteAllContacts()
                _uiState.value = ContactUiState.ContactDeleted
            } catch (e: Exception) {
                _uiState.value = ContactUiState.Error(e.message ?: "Failed to delete all contacts")
            }
        }
    }

}