package com.example.onlinedrinkshop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.onlinedrinkshop.data.models.Order
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderCard(
    order: Order,
    onReorder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val orderDate = dateFormat.format(Date(order.orderDate))
    val pickupTime = dateFormat.format(Date(order.pickupTime))

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.id.take(8)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Badge(
                    containerColor = when (order.status) {
                        com.example.onlinedrinkshop.data.models.OrderStatus.PENDING -> MaterialTheme.colorScheme.secondary
                        com.example.onlinedrinkshop.data.models.OrderStatus.PREPARING -> MaterialTheme.colorScheme.tertiary
                        com.example.onlinedrinkshop.data.models.OrderStatus.READY -> MaterialTheme.colorScheme.primary
                        com.example.onlinedrinkshop.data.models.OrderStatus.COMPLETED -> MaterialTheme.colorScheme.outline
                        com.example.onlinedrinkshop.data.models.OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error
                    }
                ) {
                    Text(order.status.name)
                }
            }
            
            Text(
                text = "Ordered: $orderDate",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Text(
                text = "Pickup: $pickupTime",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Text(
                text = "Items: ${order.items.size}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            order.items.forEach { item ->
                Text(
                    text = "• ${item.quantity}x ${item.drink.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: ${order.totalAmount} tokens",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                OutlinedButton(
                    onClick = onReorder,
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reorder")
                }
            }
        }
    }
}