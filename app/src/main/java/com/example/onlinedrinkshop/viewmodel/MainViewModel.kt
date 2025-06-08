package com.example.onlinedrinkshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlinedrinkshop.data.models.*
import com.example.onlinedrinkshop.data.repository.DataRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = DataRepository()

    val currentUser = repository.currentUser.asStateFlow()
    val categories = repository.categories.asStateFlow()
    val drinks = repository.drinks.asStateFlow()
    val toppings = repository.toppings.asStateFlow()
    val cartItems = repository.cartItems.asStateFlow()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Drink>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()

    val popularDrinks = drinks.map { drinks ->
        drinks.filter { it.isPopular }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            repository.login(email, password)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    loadUserOrders()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            repository.register(email, password, name)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    loadUserOrders()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _orders.value = emptyList()
            _uiState.value = UiState()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun searchDrinks(query: String) {
        _searchResults.value = repository.searchDrinks(query)
    }

    fun getDrinksByCategory(categoryId: String): List<Drink> {
        return repository.getDrinksByCategory(categoryId)
    }

    fun addToCart(drink: Drink, customization: DrinkCustomization, quantity: Int) {
        viewModelScope.launch {
            repository.addToCart(drink, customization, quantity)
        }
    }

    fun updateCartItemQuantity(itemId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartItemQuantity(itemId, quantity)
        }
    }

    fun removeFromCart(itemId: String) {
        viewModelScope.launch {
            repository.removeFromCart(itemId)
        }
    }

    fun placeOrder() {
        viewModelScope.launch {
            repository.placeOrder()
                .onSuccess { order ->
                    loadUserOrders()
                    _uiState.value = _uiState.value.copy(
                        showOrderSuccess = true,
                        lastOrderId = order.id
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.message)
                }
        }
    }

    fun reorder(order: Order) {
        viewModelScope.launch {
            repository.reorder(order)
        }
    }

    fun topUpTokens(amount: Int) {
        viewModelScope.launch {
            repository.topUpTokens(amount)
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.message)
                }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            repository.changePassword(oldPassword, newPassword)
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.message)
                }
        }
    }

    fun dismissOrderSuccess() {
        _uiState.value = _uiState.value.copy(showOrderSuccess = false, lastOrderId = null)
    }

    private fun loadUserOrders() {
        _orders.value = repository.getUserOrders()
    }

    data class UiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val showOrderSuccess: Boolean = false,
        val lastOrderId: String? = null
    )
}