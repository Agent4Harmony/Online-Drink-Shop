package com.example.onlinedrinkshop.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.onlinedrinkshop.data.models.Category
import com.example.onlinedrinkshop.data.models.Drink
import com.example.onlinedrinkshop.data.models.User
import com.example.onlinedrinkshop.ui.components.CategoryCard
import com.example.onlinedrinkshop.ui.components.DrinkCard
import com.example.onlinedrinkshop.ui.components.SearchBar

@Composable
fun HomeScreen(
    user: User,
    categories: List<Category>,
    popularDrinks: List<Drink>,
    onCategoryClick: (Category) -> Unit,
    onDrinkClick: (Drink) -> Unit,
    onSearchClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Welcome section
            Column {
                Text(
                    text = "Hello, ${user.name}!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tokens: ${user.tokens}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        item {
            // Search bar
            SearchBar(
                onClick = onSearchClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            // Categories section
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { onCategoryClick(category) }
                    )
                }
            }
        }

        item {
            Text(
                text = "Popular Drinks",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(popularDrinks) { drink ->
            DrinkCard(
                drink = drink,
                onClick = { onDrinkClick(drink) }
            )
        }
    }
}