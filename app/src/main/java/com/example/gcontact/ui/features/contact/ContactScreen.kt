package com.example.gcontact.ui.features.contact

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBoxValue.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.gcontact.R
import com.example.gcontact.model.data.Contact
import com.example.gcontact.ui.features.swipetodelete.DismissBackground
import com.example.gcontact.util.MyScreens
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun ContactScreen(
    navController: NavController,
    viewModel: ContactViewModel = hiltViewModel()
) {
    val systemUiController: SystemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = MaterialTheme.colorScheme.background,
        darkIcons = true
    )
    val isShowInfoDialog = remember { mutableStateOf(false) }
    val isShowDeleteAllContactDialog = remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarMain(
                onInfoClick = { isShowInfoDialog.value = true },
                onDeleteClick = { isShowDeleteAllContactDialog.value = true }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(MyScreens.AddContactScreen.route) },
                modifier = Modifier
                    .padding(16.dp)
                    .size(56.dp)
            ) {
                Icon(
                    modifier = Modifier.padding(18.dp),
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Add New Contact",
                    tint = Color.White
                )
            }
        },
        snackbarHost = { ShowErrorSnackBar(snackBarHostState) }
    ) { innerPadding ->
        ContactContent(
            modifier = Modifier.padding(innerPadding),
            contactsFlow = viewModel.contacts,
            onItemClicked = { contactId ->
                navController.navigate("${MyScreens.UpdateContactScreen.route}/$contactId")
            },
            onRemoveClicked = {
                viewModel.deleteContact(it)
            }
        )

        if (isShowInfoDialog.value) {
            InfoDialog { isShowInfoDialog.value = false }
        }

        if (isShowDeleteAllContactDialog.value) {
            DeleteContactDialog(
                title = "Delete All Contacts",
                text = "Are you sure you want to delete all contacts? This action cannot be undone.",
                onDismissRequest = { isShowDeleteAllContactDialog.value = false },
                onConfirmRequest = {
                    if (viewModel.contacts.value.isNotEmpty()) {
                        viewModel.deleteAllContacts()
                        isShowDeleteAllContactDialog.value = false
                    } else {
                        isShowDeleteAllContactDialog.value = false
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = "No contacts to delete.",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            )
        }
    }
}




@Composable
fun ContactContent(
    modifier: Modifier = Modifier,
    contactsFlow: StateFlow<List<Contact>>,
    onItemClicked: (Int) -> Unit,
    onRemoveClicked: (Contact) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val animationRes = if (isDarkTheme) {
        R.raw.no_data_dark
    } else {
        R.raw.no_data_light
    }
    val contacts by contactsFlow.collectAsState(initial = emptyList())

    if (contacts.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            NoDataAnimation(animationRes)
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(
                items = contacts,
                key = { it.id }
            ) { contact ->
                ContactItem(
                    contact = contact,
                    onItemClicked = onItemClicked,
                    onRemove = { onRemoveClicked(contact) }
                )
            }
        }
    }
}

@Composable
fun ContactItem(
    contact: Contact,
    onItemClicked: (Int) -> Unit,
    onRemove: (Contact) -> Unit
) {
    var isShowDeleteContactDialog by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(true) }

    // Swipe to dismiss state
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                EndToStart -> {
                    isShowDeleteContactDialog = true
                    false
                }
                else -> false
            }
        },
        positionalThreshold = { it * 0.5f }
    )

    if (isShowDeleteContactDialog) {
        DeleteContactDialog(
            title = "Delete This Contact",
            text = "Are you sure you want to delete this contact? This action cannot be undone.",
            onDismissRequest = { isShowDeleteContactDialog = false },
            onConfirmRequest = {
                onRemove(contact)
                isVisible = false
                isShowDeleteContactDialog = false
            }
        )
    }

    // Animated visibility for smooth removal
    AnimatedVisibility(
        visible = isVisible,
        exit = slideOutHorizontally { it } + fadeOut() + shrinkVertically(),
        modifier = Modifier.fillMaxWidth()
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = { DismissBackground(dismissState) },
            content = {
                val scale by animateFloatAsState(
                    targetValue = if (dismissState.dismissDirection == EndToStart) 0.95f else 1f,
                    label = "ScaleAnimation"
                )

                Card(
                    modifier = Modifier
                        .graphicsLayer(scaleX = scale, scaleY = scale) // Scale animation
                        .clickable { onItemClicked(contact.id) },
                    shape = ShapeDefaults.Large,
                    colors = CardDefaults.cardColors(Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = contact.pictureUri,
                            contentDescription = "Contact Picture for ${contact.name}",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.img_contact_profile)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "${contact.name} ${contact.family}",
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = contact.phoneNumber.ifEmpty { "No Phone Number" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarMain(onInfoClick: () -> Unit , onDeleteClick:() -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("Contacts") },
        actions = {
            IconButton(onClick = onDeleteClick) {
                Icon(painter = painterResource(R.drawable.ic_delete_all) , contentDescription = "Delete All Contact")
            }
            IconButton(onClick = onInfoClick) {
                Icon(imageVector = Icons.Outlined.Info, contentDescription = "App Info")
            }
        }
    )
}

@Composable
fun DeleteContactDialog(
    title :String,
    text :String,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = { Text(text = text) },
        confirmButton = {
            TextButton(onClick = onConfirmRequest) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
@Composable
fun InfoDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("About Us") },
        text = { Text("Your connections, organized and simplified.") },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Close")
            }
        }
    )
}

@Composable
fun ShowErrorSnackBar(
    snackbarHostState: SnackbarHostState
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData ->
            Snackbar(
                shape = RoundedCornerShape(12.dp),
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "No contact to delete",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    )
}

@Composable
fun NoDataAnimation(drawable :Int) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(drawable))
    LottieAnimation(composition = composition, iterations = LottieConstants.IterateForever)
}