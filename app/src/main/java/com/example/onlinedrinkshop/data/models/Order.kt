package com.example.onlinedrinkshop.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Order(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val totalAmount: Int,
    val orderDate: Long,
    val status: OrderStatus = OrderStatus.PENDING,
    val pickupTime: Long = orderDate + (30 * 60 * 1000) // 30 minutes later
) : Parcelable

@Parcelize
enum class OrderStatus : Parcelable {
    PENDING,
    PREPARING,
    READY,
    COMPLETED,
    CANCELLED
}