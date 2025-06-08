package com.example.onlinedrinkshop.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String
) : Parcelable