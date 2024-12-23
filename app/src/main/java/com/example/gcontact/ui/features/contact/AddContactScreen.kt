package com.example.gcontact.ui.features.contact

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gcontact.R
import com.example.gcontact.model.data.Contact
import com.example.gcontact.util.toUri

@Composable
fun AddContactScreen(
    navController: NavController,
    viewModel: ContactViewModel = hiltViewModel(),
) {
    var name by remember { mutableStateOf("") }
    var family by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current
    val selectedImageUri by viewModel.selectedImageProfile.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.setSelectedImage(context, it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBarAdd(onBackClick = { navController.navigateUp() })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image Section with improved validation
            ContactImageSelector(
                selectedImageUri = selectedImageUri,
                onImageSelect = { imagePickerLauncher.launch("image/*") }
            )

            // Improved text fields with validation
            ContactInputFields(
                name = name,
                onNameChange = { name = it },
                family = family,
                onFamilyChange = { family = it },
                phoneNumber = phoneNumber,
                onPhoneNumberChange = {
                    // Basic phone number validation
                    if (it.length <= 15) phoneNumber = it
                }
            )

            // Save button with comprehensive validation
            Button(
                onClick = {
                    if (validateInput(name, family, phoneNumber)) {
                        val newContact = Contact(
                            name = name.trim(),
                            family = family.trim(),
                            phoneNumber = phoneNumber.trim(),
                            pictureUri = selectedImageUri?.toString() ?: toUri(context , R.drawable.img_contact_profile)
                        )
                        viewModel.insertContact(newContact)
                        navController.navigateUp()
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
                Text("Save Contact")
            }
        }
    }
}

// Helper validation function
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
fun TopAppBarAdd(
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(text = "Add Contact") },
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

@Composable
fun ContactInputFields(
    name: String,
    onNameChange: (String) -> Unit,
    family: String,
    onFamilyChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Enter Name") },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            )
        )

        OutlinedTextField(
            value = family,
            onValueChange = onFamilyChange,
            label = { Text("Enter Family") },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            )
        )

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text("Enter Phone Number") },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            )
        )
    }
}

@Composable
fun ContactImageSelector(
    selectedImageUri: Uri?,
    onImageSelect: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .padding(top = 16.dp)
                .size(124.dp)
                .clip(CircleShape)
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.img_contact_profile),
                    contentDescription = "Add Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Button(
            onClick = onImageSelect,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = "Choose Image")
        }
    }
}