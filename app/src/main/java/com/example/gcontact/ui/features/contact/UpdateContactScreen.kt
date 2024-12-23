package com.example.gcontact.ui.features.contact

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.gcontact.R
import com.example.gcontact.model.data.Contact
import com.example.gcontact.util.ContactUiState
import com.example.gcontact.util.toUri

@Composable
fun UpdateContactScreen(
    contactId: Int,
    navController: NavController,
    viewModel: ContactViewModel = hiltViewModel(),
) {
    // State variables to manage contact details
    var name by remember { mutableStateOf("") }
    var family by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Load contact details when the screen is initialized
    LaunchedEffect(contactId) {
        viewModel.loadContact(contactId)
    }

    // Update UI state when contact details are loaded
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ContactUiState.ContactDetail -> {
                val contact = state.contact
                name = contact.name
                family = contact.family
                phoneNumber = contact.phoneNumber
                selectedImageUri = contact.pictureUri.let { Uri.parse(it) }
            }
            is ContactUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                navController.navigateUp() // Navigate back on error
            }
            else -> {}
        }
    }

    // Launcher for picking an image from the gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBarUpdate(onBackClick = { navController.navigateUp() })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Section for selecting and displaying a contact's image
            ContactImageSelector(
                selectedImageUri = selectedImageUri,
                onImageSelect = { imagePickerLauncher.launch("image/*") }
            )

            // Input fields for name, family, and phone number
            ContactInputFields(
                name = name,
                onNameChange = { name = it },
                family = family,
                onFamilyChange = { family = it },
                phoneNumber = phoneNumber,
                onPhoneNumberChange = {
                    if (it.length <= 15) phoneNumber = it
                }
            )

            // Button for updating contact
            Button(
                onClick = {
                    if (validateInput(name, family, phoneNumber)) {
                        val updatedContact = Contact(
                            id = contactId, // Use the existing contact ID
                            name = name.trim(),
                            family = family.trim(),
                            phoneNumber = phoneNumber.trim(),
                            pictureUri = selectedImageUri?.toString()
                                ?: toUri(context, R.drawable.img_contact_profile)
                        )
                        viewModel.updateContact(updatedContact)
                        navController.navigateUp() // Navigate back after update
                    } else {
                        Toast.makeText(
                            context,
                            "Please fill all fields correctly",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 32.dp)
                    .height(48.dp)
            ) {
                Text("Update Contact")
            }
        }
    }
}

// Validation function for input fields
private fun validateInput(
    name: String,
    family: String,
    phoneNumber: String
): Boolean = name.isNotBlank() &&
        family.isNotBlank() &&
        phoneNumber.isNotBlank() &&
        phoneNumber.length in 10..15

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarUpdate(
    onBackClick: () -> Unit
) {
    // Top app bar with a back button and title
    CenterAlignedTopAppBar(
        title = { Text(text = "Edit Contact") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back to Contacts"
                )
            }
        }
    )
}
