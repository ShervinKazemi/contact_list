package com.example.gcontact.util

sealed class MyScreens(val route: String) {

    object ContactScreen : MyScreens("contactScreen")
    object AddContactScreen : MyScreens("addContactScreen")
    object UpdateContactScreen : MyScreens("updateContactScreen")

}