package com.example.onlinedrinkshop.data.repository

import com.example.onlinedrinkshop.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class DataRepository {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _categories = MutableStateFlow(getInitialCategories())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _drinks = MutableStateFlow(getInitialDrinks())
    val drinks: StateFlow<List<Drink>> = _drinks.asStateFlow()

    private val _toppings = MutableStateFlow(getInitialToppings())
    val toppings: StateFlow<List<Topping>> = _toppings.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // Authentication
    suspend fun register(email: String, password: String, name: String): Result<User> {
        return try {
            val existingUser = _users.value.find { it.email == email }
            if (existingUser != null) {
                Result.failure(Exception("User already exists"))
            } else {
                val newUser = User(
                    id = UUID.randomUUID().toString(),
                    email = email,
                    name = name,
                    tokens = 100
                )
                _users.value = _users.value + newUser
                _currentUser.value = newUser
                Result.success(newUser)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val user = _users.value.find { it.email == email }
            if (user != null) {
                _currentUser.value = user
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        _currentUser.value = null
        _cartItems.value = emptyList()
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            // In a real app, you'd verify the old password
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Token management
    suspend fun topUpTokens(amount: Int): Result<User> {
        return try {
            val user = _currentUser.value ?: return Result.failure(Exception("No user logged in"))
            val updatedUser = user.copy(tokens = user.tokens + amount)
            updateUser(updatedUser)
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun updateUser(user: User) {
        _users.value = _users.value.map { if (it.id == user.id) user else it }
        _currentUser.value = user
    }

    // Cart management
    suspend fun addToCart(drink: Drink, customization: DrinkCustomization, quantity: Int = 1) {
        val cartItem = CartItem(
            id = UUID.randomUUID().toString(),
            drink = drink,
            customization = customization,
            quantity = quantity
        )
        _cartItems.value = _cartItems.value + cartItem
    }

    suspend fun updateCartItemQuantity(itemId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(itemId)
        } else {
            _cartItems.value = _cartItems.value.map { item ->
                if (item.id == itemId) item.copy(quantity = quantity) else item
            }
        }
    }

    suspend fun removeFromCart(itemId: String) {
        _cartItems.value = _cartItems.value.filter { it.id != itemId }
    }

    suspend fun clearCart() {
        _cartItems.value = emptyList()
    }

    // Order management
    suspend fun placeOrder(): Result<Order> {
        return try {
            val user = _currentUser.value ?: return Result.failure(Exception("No user logged in"))
            val items = _cartItems.value
            if (items.isEmpty()) {
                return Result.failure(Exception("Cart is empty"))
            }

            val totalAmount = items.sumOf { it.totalPrice }
            if (user.tokens < totalAmount) {
                return Result.failure(Exception("Insufficient tokens"))
            }

            val order = Order(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                items = items,
                totalAmount = totalAmount,
                orderDate = System.currentTimeMillis()
            )

            _orders.value = _orders.value + order
            updateUser(user.copy(tokens = user.tokens - totalAmount))
            clearCart()

            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun reorder(order: Order): Result<Unit> {
        return try {
            _cartItems.value = order.items
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserOrders(): List<Order> {
        val userId = _currentUser.value?.id ?: return emptyList()
        return _orders.value.filter { it.userId == userId }.sortedByDescending { it.orderDate }
    }

    fun searchDrinks(query: String): List<Drink> {
        return _drinks.value.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.description.contains(query, ignoreCase = true)
        }
    }

    fun getDrinksByCategory(categoryId: String): List<Drink> {
        return _drinks.value.filter { it.categoryId == categoryId }
    }

    private fun getInitialCategories(): List<Category> {
        return listOf(
            Category("1", "Tea", "Traditional and modern tea varieties", "https://images.pexels.com/photos/1638280/pexels-photo-1638280.jpeg"),
            Category("2", "Coffee", "Premium coffee drinks", "https://images.pexels.com/photos/302899/pexels-photo-302899.jpeg"),
            Category("3", "Fruit Tea", "Fresh fruit-infused teas", "https://images.pexels.com/photos/1638280/pexels-photo-1638280.jpeg"),
            Category("4", "Milk Tea", "Creamy milk tea selections", "https://images.pexels.com/photos/1638280/pexels-photo-1638280.jpeg"),
            Category("5", "Smoothies", "Fresh fruit smoothies", "https://images.pexels.com/photos/1092730/pexels-photo-1092730.jpeg")
        )
    }

    private fun getInitialDrinks(): List<Drink> {
        return listOf(
            // Tea
            Drink("1", "Classic Black Tea", "Traditional black tea with rich flavor", 15, "1", "https://images.pexels.com/photos/1638280/pexels-photo-1638280.jpeg", true),
            Drink("2", "Green Tea", "Fresh and light green tea", 12, "1", "https://images.pexels.com/photos/1638280/pexels-photo-1638280.jpeg"),
            Drink("3", "Oolong Tea", "Semi-fermented tea with complex taste", 18, "1", "https://images.pexels.com/photos/1638280/pexels-photo-1638280.jpeg"),
            
            // Coffee
            Drink("4", "Americano", "Classic black coffee", 20, "2", "https://images.pexels.com/photos/302899/pexels-photo-302899.jpeg"),
            Drink("5", "Cappuccino", "Espresso with steamed milk foam", 25, "2", "https://images.pexels.com/photos/302899/pexels-photo-302899.jpeg", true),
            Drink("6", "Latte", "Smooth espresso with steamed milk", 28, "2", "https://images.pexels.com/photos/302899/pexels-photo-302899.jpeg"),
            
            // Fruit Tea
            Drink("7", "Passion Fruit Tea", "Refreshing passion fruit tea", 22, "3", "https://images.pexels.com/photos/1638280/pexels-photo-1638280.jpeg", true),
            Drink("8", "Lemon Tea", "Zesty lemon-infused tea", 18, "3", "https://images.pexels.com/photos/1638280/pexels-photo-1638280.jpeg"),
            Drink("9", "Peach Tea", "Sweet peach flavored tea", 20, "3", "https://images.pexels.com/photos/1638280/pexels-photo-1638280.jpeg"),
            
            // Milk Tea
            Drink("10", "Classic Milk Tea", "Traditional milk tea", 25, "4", "https://images.pexels.com/photos/1638280/pexels-photo-1638280.jpeg", true),
            Drink("11", "Taro Milk Tea", "Creamy taro flavored milk tea", 28, "4", "https://images.pexels.com/photos/1638280/pexels-photo-1638280.jpeg"),
            Drink("12", "Matcha Milk Tea", "Japanese matcha with milk", 30, "4", "https://images.pexels.com/photos/1638280/pexels-photo-1638280.jpeg"),
            
            // Smoothies
            Drink("13", "Mango Smoothie", "Fresh mango smoothie", 32, "5", "https://images.pexels.com/photos/1092730/pexels-photo-1092730.jpeg"),
            Drink("14", "Berry Smoothie", "Mixed berry smoothie", 30, "5", "https://images.pexels.com/photos/1092730/pexels-photo-1092730.jpeg"),
            Drink("15", "Banana Smoothie", "Creamy banana smoothie", 28, "5", "https://images.pexels.com/photos/1092730/pexels-photo-1092730.jpeg")
        )
    }

    private fun getInitialToppings(): List<Topping> {
        return listOf(
            Topping("1", "Pearls", 5),
            Topping("2", "Jelly", 3),
            Topping("3", "Pudding", 4),
            Topping("4", "Red Bean", 4),
            Topping("5", "Coconut", 3),
            Topping("6", "Aloe Vera", 4)
        )
    }
}