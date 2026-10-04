package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Language
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.HuzaFoodTheme
import com.example.ui.viewmodel.HuzaFoodViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HuzaFoodTheme {
                HuzaFoodApp()
            }
        }
    }
}

@Composable
fun HuzaFoodApp(
    viewModel: HuzaFoodViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val currentLocation by viewModel.selectedLocation.collectAsStateWithLifecycle()
    val restaurants by viewModel.restaurants.collectAsStateWithLifecycle()
    val menuItems by viewModel.menuItems.collectAsStateWithLifecycle()
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val appliedPromo by viewModel.appliedPromo.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val activeOrder by viewModel.activeOrder.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val filterFreeDelivery by viewModel.filterFreeDelivery.collectAsStateWithLifecycle()
    val filterOffersOnly by viewModel.filterOffersOnly.collectAsStateWithLifecycle()
    val filterMinRating by viewModel.filterMinRating.collectAsStateWithLifecycle()
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()

    val showLocationDialog by viewModel.showLocationDialog.collectAsStateWithLifecycle()
    val showAuthDialog by viewModel.showAuthDialog.collectAsStateWithLifecycle()
    val showAboutDialog by viewModel.showAboutDialog.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    val cartItemCount = cart.sumOf { it.quantity }
    val showBottomNav = currentScreen !is Screen.RestaurantDetail && currentScreen !is Screen.OrderTracking && currentScreen !is Screen.AdminDashboard

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen is Screen.Home) {
                HuzaFoodHeader(
                    currentLocation = currentLocation,
                    language = language,
                    cartItemCount = cartItemCount,
                    onLocationClick = { viewModel.setShowLocationDialog(true) },
                    onLanguageToggle = { viewModel.toggleLanguage() },
                    onCartClick = { viewModel.navigateTo(Screen.Cart) },
                    onAboutClick = { viewModel.setShowAboutDialog(true) }
                )
            }
        },
        bottomBar = {
            if (showBottomNav) {
                HuzaFoodBottomNav(
                    currentScreen = currentScreen,
                    language = language,
                    cartItemCount = cartItemCount,
                    onTabSelected = { viewModel.navigateTo(it) }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        currentLocation = currentLocation,
                        language = language,
                        restaurants = restaurants,
                        selectedCategory = selectedCategory,
                        filterFreeDelivery = filterFreeDelivery,
                        filterOffersOnly = filterOffersOnly,
                        filterMinRating = filterMinRating,
                        sortBy = sortBy,
                        onCategorySelected = { viewModel.selectCategory(it) },
                        onFilterFreeDeliveryChange = { viewModel.setFilterFreeDelivery(it) },
                        onFilterOffersChange = { viewModel.setFilterOffersOnly(it) },
                        onFilterMinRatingChange = { viewModel.setFilterMinRating(it) },
                        onSortByChange = { viewModel.setSortBy(it) },
                        onRestaurantClick = { viewModel.navigateTo(Screen.RestaurantDetail(it)) },
                        onSearchTrigger = { viewModel.navigateTo(Screen.Search) },
                        onLocationClick = { viewModel.setShowLocationDialog(true) }
                    )
                }

                is Screen.Search -> {
                    SearchScreen(
                        searchQuery = searchQuery,
                        language = language,
                        restaurants = restaurants,
                        menuItems = menuItems,
                        cart = cart,
                        selectedCategory = selectedCategory,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onCategorySelected = { viewModel.selectCategory(it) },
                        onRestaurantClick = { viewModel.navigateTo(Screen.RestaurantDetail(it)) },
                        onAddToCart = { viewModel.addToCart(it) },
                        onRemoveFromCart = { viewModel.removeFromCart(it) }
                    )
                }

                is Screen.Orders -> {
                    OrdersHistoryScreen(
                        orders = orders,
                        activeOrder = activeOrder,
                        language = language,
                        onTrackOrder = { viewModel.navigateTo(Screen.OrderTracking(it)) },
                        onReorder = { items ->
                            items.forEach { viewModel.addToCart(it.menuItem) }
                            viewModel.navigateTo(Screen.Cart)
                        },
                        onBrowseFoodClick = { viewModel.navigateTo(Screen.Home) }
                    )
                }

                is Screen.Cart -> {
                    CartScreen(
                        cart = cart,
                        currentLocation = currentLocation,
                        language = language,
                        appliedPromo = appliedPromo,
                        subtotal = viewModel.getCartSubtotal(),
                        deliveryFee = viewModel.getDeliveryFee(),
                        vat = viewModel.getVatAmount(),
                        discount = viewModel.getDiscountAmount(),
                        total = viewModel.getCartTotal(),
                        userProfile = userProfile,
                        onAddToCart = { viewModel.addToCart(it) },
                        onRemoveFromCart = { viewModel.removeFromCart(it) },
                        onDeleteCartItem = { viewModel.removeCartItemCompletely(it) },
                        onClearCart = { viewModel.clearCart() },
                        onApplyPromo = { viewModel.applyPromoCode(it) },
                        onRemovePromo = { viewModel.removePromo() },
                        onChangeLocationClick = { viewModel.setShowLocationDialog(true) },
                        onPlaceOrder = { address, phone, name, payment, notes ->
                            viewModel.placeOrder(address, phone, name, payment, notes)
                        },
                        onBrowseFoodClick = { viewModel.navigateTo(Screen.Home) }
                    )
                }

                is Screen.Profile -> {
                    ProfileScreen(
                        userProfile = userProfile,
                        language = language,
                        onLoginClick = { viewModel.setShowAuthDialog(true) },
                        onLogoutClick = { viewModel.logout() },
                        onLanguageToggle = { viewModel.toggleLanguage() },
                        onOpenAdminDashboard = { viewModel.navigateTo(Screen.AdminDashboard) },
                        onOpenAbout = { viewModel.setShowAboutDialog(true) },
                        onViewOrders = { viewModel.navigateTo(Screen.Orders) }
                    )
                }

                is Screen.RestaurantDetail -> {
                    val restaurant = restaurants.find { it.id == screen.restaurantId } ?: restaurants.first()
                    RestaurantDetailScreen(
                        restaurant = restaurant,
                        allMenuItems = menuItems,
                        cart = cart,
                        language = language,
                        onBack = { viewModel.navigateTo(Screen.Home) },
                        onAddToCart = { viewModel.addToCart(it) },
                        onRemoveFromCart = { viewModel.removeFromCart(it) },
                        onViewCart = { viewModel.navigateTo(Screen.Cart) }
                    )
                }

                is Screen.OrderTracking -> {
                    val order = orders.find { it.id == screen.orderId } ?: orders.firstOrNull()
                    if (order != null) {
                        OrderTrackingScreen(
                            order = order,
                            language = language,
                            onBack = { viewModel.navigateTo(Screen.Orders) },
                            onViewAllOrders = { viewModel.navigateTo(Screen.Orders) }
                        )
                    } else {
                        viewModel.navigateTo(Screen.Orders)
                    }
                }

                is Screen.AdminDashboard -> {
                    AdminDashboardScreen(
                        orders = orders,
                        restaurants = restaurants,
                        menuItems = menuItems,
                        language = language,
                        onBack = { viewModel.navigateTo(Screen.Profile) },
                        onUpdateOrderStatus = { orderId, newStatus ->
                            viewModel.updateOrderStatus(orderId, newStatus)
                        },
                        onAddRestaurant = { viewModel.addRestaurant(it) },
                        onAddMenuItem = { viewModel.addMenuItem(it) }
                    )
                }
            }
        }
    }

    // Modal Dialogs
    if (showLocationDialog) {
        LocationSelectorDialog(
            currentLocation = currentLocation,
            language = language,
            onLocationSelected = { viewModel.setLocation(it) },
            onDismiss = { viewModel.setShowLocationDialog(false) }
        )
    }

    if (showAuthDialog) {
        AuthOtpDialog(
            language = language,
            onLoginSuccess = { phone, name -> viewModel.loginWithPhone(phone, name) },
            onDismiss = { viewModel.setShowAuthDialog(false) }
        )
    }

    if (showAboutDialog) {
        AboutDialog(
            language = language,
            onDismiss = { viewModel.setShowAboutDialog(false) }
        )
    }
}
