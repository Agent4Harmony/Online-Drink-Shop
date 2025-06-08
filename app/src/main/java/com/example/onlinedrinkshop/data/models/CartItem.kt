package com.example.onlinedrinkshop.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val id: String,
    val drink: Drink,
    val customization: DrinkCustomization,
    val quantity: Int = 1
) : Parcelable {
    val totalPrice: Int
        get() = (drink.price + customization.toppings.sumOf { it.price }) * quantity
}