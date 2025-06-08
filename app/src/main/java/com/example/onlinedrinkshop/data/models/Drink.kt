package com.example.onlinedrinkshop.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Drink(
    val id: String,
    val name: String,
    val description: String,
    val price: Int, // in tokens
    val categoryId: String,
    val imageUrl: String,
    val isPopular: Boolean = false
) : Parcelable

@Parcelize
data class DrinkCustomization(
    val sweetness: SweetnessLevel = SweetnessLevel.NORMAL,
    val ice: IceLevel = IceLevel.NORMAL,
    val toppings: List<Topping> = emptyList()
) : Parcelable

@Parcelize
enum class SweetnessLevel(val displayName: String, val priceModifier: Int = 0) : Parcelable {
    NO_SUGAR("No Sugar"),
    LESS_SWEET("Less Sweet"),
    NORMAL("Normal"),
    EXTRA_SWEET("Extra Sweet")
}

@Parcelize
enum class IceLevel(val displayName: String, val priceModifier: Int = 0) : Parcelable {
    NO_ICE("No Ice"),
    LESS_ICE("Less Ice"),
    NORMAL("Normal"),
    EXTRA_ICE("Extra Ice")
}

@Parcelize
data class Topping(
    val id: String,
    val name: String,
    val price: Int // in tokens
) : Parcelable