package com.example.onlinedrinkshop.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,
    val email: String,
    val name: String,
    val tokens: Int = 100
) : Parcelable