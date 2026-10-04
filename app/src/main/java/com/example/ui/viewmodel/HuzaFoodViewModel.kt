package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SampleData
import com.example.data.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed class Screen {
    data object Home : Screen()
    data object Search : Screen()
    data object Orders : Screen()
    data object Cart : Screen()
    data object Profile : Screen()
    data class RestaurantDetail(val restaurantId: String) : Screen()
    data class OrderTracking(val orderId: String) : Screen()
    data object AdminDashboard : Screen()
}

class HuzaFoodViewModel : ViewModel() {

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _language = MutableStateFlow(Language.EN)
    val language: StateFlow<Language> = _language.asStateFlow()

    private val _selectedLocation = MutableStateFlow(SampleData.supportedLocations[0]) // Kishoreganj Sadar
    val selectedLocation: StateFlow<DistrictArea> = _selectedLocation.asStateFlow()

    private val _restaurants = MutableStateFlow<List<Restaurant>>(SampleData.sampleRestaurants)
    val restaurants: StateFlow<List<Restaurant>> = _restaurants.asStateFlow()

    private val _menuItems = MutableStateFlow<List<MenuItem>>(SampleData.sampleMenuItems)
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _appliedPromo = MutableStateFlow<PromoCode?>(null)
    val appliedPromo: StateFlow<PromoCode?> = _appliedPromo.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _activeOrder = MutableStateFlow<Order?>(null)
    val activeOrder: StateFlow<Order?> = _activeOrder.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Filters & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<FoodCategory?>(null)
    val selectedCategory: StateFlow<FoodCategory?> = _selectedCategory.asStateFlow()

    private val _filterFreeDelivery = MutableStateFlow(false)
    val filterFreeDelivery: StateFlow<Boolean> = _filterFreeDelivery.asStateFlow()

    private val _filterOffersOnly = MutableStateFlow(false)
    val filterOffersOnly: StateFlow<Boolean> = _filterOffersOnly.asStateFlow()

    private val _filterMinRating = MutableStateFlow(0.0)
    val filterMinRating: StateFlow<Double> = _filterMinRating.asStateFlow()

    private val _sortBy = MutableStateFlow("relevance") // "relevance", "rating", "delivery_time", "delivery_fee"
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    // Dialog state flags
    private val _showLocationDialog = MutableStateFlow(false)
    val showLocationDialog: StateFlow<Boolean> = _showLocationDialog.asStateFlow()

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    private val _showAboutDialog = MutableStateFlow(false)
    val showAboutDialog: StateFlow<Boolean> = _showAboutDialog.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        // Seed 1 completed past order for realistic initial state
        val sampleRest = SampleData.sampleRestaurants[0]
        val sampleItem1 = SampleData.sampleMenuItems[0]
        val sampleItem2 = SampleData.sampleMenuItems[27] // Borhani
        val pastOrder = Order(
            id = "HUZ-82914",
            restaurantId = sampleRest.id,
            restaurantNameEn = sampleRest.nameEn,
            restaurantNameBn = sampleRest.nameBn,
            items = listOf(
                CartItem(sampleItem1, 2),
                CartItem(sampleItem2, 2)
            ),
            subtotal = 900.0,
            deliveryFee = 25.0,
            vat = 45.0,
            discount = 100.0,
            total = 870.0,
            deliveryAddress = "House 14, Road 4, Kishoreganj Sadar",
            district = "Kishoreganj",
            area = "Kishoreganj Sadar",
            recipientPhone = "+880 1700-123456",
            recipientName = "Muhammad Huzaifa",
            paymentMethod = PaymentMethod.BKASH,
            status = OrderStatus.DELIVERED,
            placedAtTimestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 24),
            estimatedMinutes = 25
        )
        _orders.value = listOf(pastOrder)
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == Language.EN) Language.BN else Language.EN
    }

    fun setLocation(location: DistrictArea) {
        _selectedLocation.value = location
        _showLocationDialog.value = false
    }

    fun setShowLocationDialog(show: Boolean) {
        _showLocationDialog.value = show
    }

    fun setShowAuthDialog(show: Boolean) {
        _showAuthDialog.value = show
    }

    fun setShowAboutDialog(show: Boolean) {
        _showAboutDialog.value = show
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: FoodCategory?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun setFilterFreeDelivery(enabled: Boolean) {
        _filterFreeDelivery.value = enabled
    }

    fun setFilterOffersOnly(enabled: Boolean) {
        _filterOffersOnly.value = enabled
    }

    fun setFilterMinRating(rating: Double) {
        _filterMinRating.value = rating
    }

    fun setSortBy(sort: String) {
        _sortBy.value = sort
    }

    // Cart Management
    fun addToCart(item: MenuItem, specialInstructions: String = "") {
        _cart.update { current ->
            val existing = current.find { it.menuItem.id == item.id }
            if (existing != null) {
                current.map {
                    if (it.menuItem.id == item.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                current + CartItem(item, 1, specialInstructions)
            }
        }
        val isBn = _language.value == Language.BN
        _snackbarMessage.value = if (isBn) "কার্টে যোগ করা হয়েছে: ${item.nameBn}" else "Added to cart: ${item.nameEn}"
    }

    fun removeFromCart(menuItemId: String) {
        _cart.update { current ->
            val existing = current.find { it.menuItem.id == menuItemId } ?: return@update current
            if (existing.quantity > 1) {
                current.map {
                    if (it.menuItem.id == menuItemId) it.copy(quantity = it.quantity - 1) else it
                }
            } else {
                current.filter { it.menuItem.id != menuItemId }
            }
        }
    }

    fun removeCartItemCompletely(menuItemId: String) {
        _cart.update { it.filter { item -> item.menuItem.id != menuItemId } }
    }

    fun clearCart() {
        _cart.value = emptyList()
        _appliedPromo.value = null
    }

    fun applyPromoCode(code: String): Boolean {
        val promo = SampleData.promoCodes.find { it.code.equals(code.trim(), ignoreCase = true) }
        val isBn = _language.value == Language.BN
        return if (promo != null) {
            val subtotal = getCartSubtotal()
            if (subtotal >= promo.minOrder) {
                _appliedPromo.value = promo
                _snackbarMessage.value = if (isBn) "প্রোমোকোড প্রয়োগ হয়েছে! ${promo.descBn}" else "Promo applied! ${promo.descEn}"
                true
            } else {
                _snackbarMessage.value = if (isBn) "সর্বনিম্ন অর্ডার ৳${promo.minOrder.toInt()} হতে হবে" else "Minimum order ৳${promo.minOrder.toInt()} required"
                false
            }
        } else {
            _snackbarMessage.value = if (isBn) "অবৈধ প্রোমোকোড" else "Invalid promo code"
            false
        }
    }

    fun removePromo() {
        _appliedPromo.value = null
    }

    fun getCartSubtotal(): Double {
        return _cart.value.sumOf { it.menuItem.price * it.quantity }
    }

    fun getDeliveryFee(): Double {
        if (_cart.value.isEmpty()) return 0.0
        val restId = _cart.value.first().menuItem.restaurantId
        val rest = _restaurants.value.find { it.id == restId }
        return rest?.deliveryFee ?: 30.0
    }

    fun getVatAmount(): Double {
        return (getCartSubtotal() * 0.05) // 5% VAT in Bangladesh
    }

    fun getDiscountAmount(): Double {
        val promo = _appliedPromo.value ?: return 0.0
        val subtotal = getCartSubtotal()
        return if (promo.discountPercent > 0) {
            (subtotal * promo.discountPercent).coerceAtMost(100.0)
        } else {
            promo.flatDiscount
        }
    }

    fun getCartTotal(): Double {
        if (_cart.value.isEmpty()) return 0.0
        val subtotal = getCartSubtotal()
        val fee = getDeliveryFee()
        val vat = getVatAmount()
        val discount = getDiscountAmount()
        return (subtotal + fee + vat - discount).coerceAtLeast(0.0)
    }

    fun placeOrder(
        deliveryAddress: String,
        phone: String,
        recipientName: String,
        paymentMethod: PaymentMethod,
        notes: String
    ) {
        val items = _cart.value
        if (items.isEmpty()) return

        val restId = items.first().menuItem.restaurantId
        val rest = _restaurants.value.find { it.id == restId } ?: _restaurants.value.first()
        val orderId = "HUZ-${(10000..99999).random()}"

        val newOrder = Order(
            id = orderId,
            restaurantId = rest.id,
            restaurantNameEn = rest.nameEn,
            restaurantNameBn = rest.nameBn,
            items = items,
            subtotal = getCartSubtotal(),
            deliveryFee = getDeliveryFee(),
            vat = getVatAmount(),
            discount = getDiscountAmount(),
            total = getCartTotal(),
            deliveryAddress = deliveryAddress.ifBlank { "${_selectedLocation.value.area}, ${_selectedLocation.value.district}" },
            district = _selectedLocation.value.district,
            area = _selectedLocation.value.area,
            recipientPhone = phone.ifBlank { _userProfile.value.phone },
            recipientName = recipientName.ifBlank { _userProfile.value.name },
            paymentMethod = paymentMethod,
            status = OrderStatus.PLACED,
            placedAtTimestamp = System.currentTimeMillis(),
            estimatedMinutes = 28,
            notes = notes
        )

        _orders.update { listOf(newOrder) + it }
        _activeOrder.value = newOrder
        clearCart()
        navigateTo(Screen.OrderTracking(newOrder.id))

        // Start live order tracking simulation
        viewModelScope.launch {
            delay(5000)
            updateOrderStatus(orderId, OrderStatus.PREPARING)
            delay(8000)
            updateOrderStatus(orderId, OrderStatus.ON_THE_WAY)
            delay(12000)
            updateOrderStatus(orderId, OrderStatus.DELIVERED)
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        _orders.update { list ->
            list.map { if (it.id == orderId) it.copy(status = newStatus) else it }
        }
        if (_activeOrder.value?.id == orderId) {
            _activeOrder.value = _activeOrder.value?.copy(status = newStatus)
        }
    }

    // Auth & Profile
    fun loginWithPhone(phone: String, name: String) {
        _userProfile.update {
            it.copy(
                phone = phone,
                name = name.ifBlank { "HuzaFood User" },
                isLoggedIn = true
            )
        }
        _showAuthDialog.value = false
        val isBn = _language.value == Language.BN
        _snackbarMessage.value = if (isBn) "স্বাগতম! লগইন সফল হয়েছে" else "Welcome! Logged in successfully"
    }

    fun logout() {
        _userProfile.update { it.copy(isLoggedIn = false) }
        val isBn = _language.value == Language.BN
        _snackbarMessage.value = if (isBn) "লগআউট করা হয়েছে" else "Logged out successfully"
    }

    // Admin Dashboard
    fun addRestaurant(restaurant: Restaurant) {
        _restaurants.update { listOf(restaurant) + it }
        val isBn = _language.value == Language.BN
        _snackbarMessage.value = if (isBn) "নতুন রেস্তোরাঁ সফলভাবে যোগ হয়েছে!" else "New restaurant added successfully!"
    }

    fun addMenuItem(item: MenuItem) {
        _menuItems.update { listOf(item) + it }
        val isBn = _language.value == Language.BN
        _snackbarMessage.value = if (isBn) "নতুন মেনু আইটেম যুক্ত হয়েছে!" else "New menu item added successfully!"
    }
}
