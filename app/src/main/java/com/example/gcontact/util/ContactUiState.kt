package com.example.gcontact.util

import com.example.gcontact.model.data.Contact

/**
 * Represents the different UI states for contact management.
 */
sealed class ContactUiState {
    /**
     * Initial state when no operation has occurred yet.
     */
    object Initial : ContactUiState()

    /**
     * State when no contacts are available.
     */
    object Empty : ContactUiState()

    /**
     * State indicating a successful fetch of contacts.
     *
     * @param contacts List of fetched contacts.
     */
    data class Success(val contacts: List<Contact>) : ContactUiState()

    /**
     * State representing detailed information of a single contact.
     *
     * @param contact The contact details.
     */
    data class ContactDetail(val contact: Contact) : ContactUiState()

    /**
     * State when a contact is successfully saved.
     */
    object ContactSaved : ContactUiState()

    /**
     * State when a contact is successfully deleted.
     */
    object ContactDeleted : ContactUiState()

    /**
     * State representing an error during any operation.
     *
     * @param message Description of the error.
     */
    data class Error(val message: String) : ContactUiState()
}
