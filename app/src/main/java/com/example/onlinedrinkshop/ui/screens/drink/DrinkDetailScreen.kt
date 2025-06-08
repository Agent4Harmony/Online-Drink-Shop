package com.example.onlinedrinkshop.ui.screens.drink

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.onlinedrinkshop.data.models.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkDetailScreen(
    drink: Drink,
    toppings: List<Topping>,
    onAddToCart: (Drink, DrinkCustomization, Int) -> Unit,
    onBackClick: () -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    var selectedSweetness by remember { mutableStateOf(SweetnessLevel.NORMAL) }
    var selectedIce by remember { mutableStateOf(IceLevel.NORMAL) }
    var selectedToppings by remember { mutableStateOf<List<Topping>>(emptyList()) }

    val customization = DrinkCustomization(
        sweetness = selectedSweetness,
        ice = selectedIce,
        toppings = selectedToppings
    )

    val totalPrice = (drink.price + selectedToppings.sumOf { it.price }) * quantity

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(drink.name) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Drink image
                AsyncImage(
                    model = drink.imageUrl,
                    contentDescription = drink.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            item {
                // Drink info
                Column {
                    Text(
                        text = drink.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = drink.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${drink.price} tokens",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                // Sweetness level
                Text(
                    text = "Sweetness Level",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SweetnessLevel.values()) { level ->
                        FilterChip(
                            onClick = { selectedSweetness = level },
                            label = { Text(level.displayName) },
                            selected = selectedSweetness == level
                        )
                    }
                }
            }

            item {
                // Ice level
                Text(
                    text = "Ice Level",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(IceLevel.values()) { level ->
                        FilterChip(
                            onClick = { selectedIce = level },
                            label = { Text(level.displayName) },
                            selected = selectedIce == level
                        )
                    }
                }
            }

            item {
                // Toppings
                Text(
                    text = "Toppings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(toppings) { topping ->
                        FilterChip(
                            onClick = {
                                selectedToppings = if (selectedToppings.contains(topping)) {
                                    selectedToppings - topping
                                } else {
                                    selectedToppings + topping
                                }
                            },
                            label = { Text("${topping.name} (+${topping.price})") },
                            selected = selectedToppings.contains(topping)
                        )
                    }
                }
            }

            item {
                // Quantity selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            enabled = quantity > 1
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        IconButton(onClick = { quantity++ }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }
            }
        }

        // Add to cart button
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$totalPrice tokens",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Button(
                    onClick = { onAddToCart(drink, customization, quantity) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add to Cart")
                }
            }
        }
    }
}