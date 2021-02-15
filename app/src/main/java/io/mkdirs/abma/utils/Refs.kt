package io.mkdirs.abma.utils

import com.google.firebase.database.FirebaseDatabase

const val PASSWORD_PREFS_KEY = "password"
const val PREFS_NAME = "prefs"

const val PRIVATE_MESSAGES_CHAT_TYPE = "PRIVATE_MESSAGES"
const val GROUP_CHAT_TYPE = "GROUP"
const val DATABASE_NULL = "NONE"

val DB = FirebaseDatabase.getInstance()