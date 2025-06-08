package com.example.onlinedrinkshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.onlinedrinkshop.data.models.Category
import com.example.onlinedrinkshop.data.models.Drink
import com.example.onlinedrinkshop.ui.screens.auth.LoginScreen
import com.example.onlinedrinkshop.ui.screens.auth.RegisterScreen
import com.example.onlinedrinkshop.ui.screens.cart.CartScreen
import com.example.onlinedrinkshop.ui.screens.category.CategoryScreen
import com.example.onlinedrinkshop.ui.screens.drink.DrinkDetailScreen
import com.example.onlinedrinkshop.ui.screens.home.HomeScreen
import com.example.onlinedrinkshop.ui.screens.orders.OrdersScreen
import com.example.onlinedrinkshop.ui.screens.profile.ProfileScreen
import com.example.onlinedrinkshop.ui.screens.search.SearchScreen
import com.example.onlinedrinkshop.ui.theme.OnlineDrinkShopTheme
import com.example.onlinedrinkshop.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnlineDrinkShopTheme {
                TeaShopApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeaShopApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val currentUser by viewModel.currentUser.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val drinks by viewModel.drinks.collectAsState()
    val toppings by viewModel.toppings.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val popularDrinks by viewModel.popularDrinks.collectAsState()

    // Clear error when it's shown
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }

    // Show order success notification
    if (uiState.showOrderSuccess) {
        LaunchedEffect(uiState.showOrderSuccess) {
            kotlinx.coroutines.delay(3000)
            viewModel.dismissOrderSuccess()
        }
    }

    if (currentUser == null) {
        // Authentication flow
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") {
                LoginScreen(
                    onLogin = viewModel::login,
                    onNavigateToRegister = { navController.navigate("register") },
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage
                )
            }
            composable("register") {
                RegisterScreen(
                    onRegister = viewModel::register,
                    onNavigateToLogin = { navController.navigate("login") },
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage
                )
            }
        }
    } else {
        // Main app flow
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (currentDestination?.route in listOf("home", "cart", "orders", "profile")) {
                    NavigationBar {
                        bottomNavItems.forEach { item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = remember { SnackbarHostState() }.apply {
                        LaunchedEffect(uiState.errorMessage) {
                            uiState.errorMessage?.let { showSnackbar(it) }
                        }
                        LaunchedEffect(uiState.showOrderSuccess) {
                            if (uiState.showOrderSuccess) {
                                showSnackbar("Order placed successfully! Ready for pickup in 30 minutes.")
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(
                        user = currentUser,
                        categories = categories,
                        popularDrinks = popularDrinks,
                        onCategoryClick = { category ->
                            navController.navigate("category/${category.id}/${category.name}")
                        },
                        onDrinkClick = { drink ->
                            navController.navigate("drink/${drink.id}")
                        },
                        onSearchClick = {
                            navController.navigate("search")
                        }
                    )
                }

                composable("search") {
                    SearchScreen(
                        searchResults = searchResults,
                        onSearch = viewModel::searchDrinks,
                        onDrinkClick = { drink ->
                            navController.navigate("drink/${drink.id}")
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable("category/{categoryId}/{categoryName}") { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                    val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
                    val category = categories.find { it.id == categoryId }
                    val categoryDrinks = viewModel.getDrinksByCategory(categoryId)

                    if (category != null) {
                        CategoryScreen(
                            category = category,
                            drinks = categoryDrinks,
                            onDrinkClick = { drink ->
                                navController.navigate("drink/${drink.id}")
                            },
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }

                composable("drink/{drinkId}") { backStackEntry ->
                    val drinkId = backStackEntry.arguments?.getString("drinkId") ?: ""
                    val drink = drinks.find { it.id == drinkId }

                    if (drink != null) {
                        DrinkDetailScreen(
                            drink = drink,
                            toppings = toppings,
                            onAddToCart = { drinkItem, customization, quantity ->
                                viewModel.addToCart(drinkItem, customization, quantity)
                                navController.popBackStack()
                            },
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }

                composable("cart") {
                    CartScreen(
                        user = currentUser,
                        cartItems = cartItems,
                        onUpdateQuantity = viewModel::updateCartItemQuantity,
                        onRemoveItem = viewModel::removeFromCart,
                        onPlaceOrder = viewModel::placeOrder,
                        onOrderResult = { success, message -> }
                    )
                }

                composable("orders") {
                    OrdersScreen(
                        orders = orders,
                        onReorder = viewModel::reorder
                    )
                }

                composable("profile") {
                    ProfileScreen(
                        user = currentUser,
                        onTopUp = viewModel::topUpTokens,
                        onChangePassword = viewModel::changePassword,
                        onLogout = viewModel::logout
                    )
                }
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("home", "Home", Icons.Default.Home),
    BottomNavItem("cart", "Cart", Icons.Default.ShoppingCart),
    BottomNavItem("orders", "Orders", Icons.Default.Receipt),
    BottomNavItem("profile", "Profile", Icons.Default.AccountBox)
)