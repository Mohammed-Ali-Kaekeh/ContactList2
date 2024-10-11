package com.example.contactlistproject

import android.net.Uri

data class Contact(
    val id : Int,
    val name : String,
    val phoneNO : String,
    val email : String,
    val address : String,
    val image : Uri?
)


val contactList: MutableList<Contact> = mutableListOf()


fun addContact(contact: Contact) {
    contactList.add(contact)
}