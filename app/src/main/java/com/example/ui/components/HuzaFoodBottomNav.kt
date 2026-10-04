package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.Language
import com.example.ui.theme.HuzaGreen
import com.example.ui.viewmodel.Screen

data class NavTabItem(
    val screen: Screen,
    val titleEn: String,
    val titleBn: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val testTag: String
)

@Composable
fun HuzaFoodBottomNav(
    currentScreen: Screen,
    language: Language,
    cartItemCount: Int,
    onTabSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = language == Language.BN

    val tabs = listOf(
        NavTabItem(Screen.Home, "Home", "হোম", Icons.Filled.Home, Icons.Outlined.Home, "nav_tab_home"),
        NavTabItem(Screen.Search, "Search", "অনুসন্ধান", Icons.Filled.Search, Icons.Outlined.Search, "nav_tab_search"),
        NavTabItem(Screen.Orders, "Orders", "অর্ডার", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong, "nav_tab_orders"),
        NavTabItem(Screen.Cart, "Cart", "কার্ট", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag, "nav_tab_cart"),
        NavTabItem(Screen.Profile, "Profile", "প্রোফাইল", Icons.Filled.Person, Icons.Outlined.Person, "nav_tab_profile")
    )

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        tabs.forEach { tab ->
            val isSelected = when (tab.screen) {
                is Screen.Home -> currentScreen is Screen.Home
                is Screen.Search -> currentScreen is Screen.Search
                is Screen.Orders -> currentScreen is Screen.Orders || currentScreen is Screen.OrderTracking
                is Screen.Cart -> currentScreen is Screen.Cart
                is Screen.Profile -> currentScreen is Screen.Profile || currentScreen is Screen.AdminDashboard
                else -> false
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab.screen) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (tab.screen == Screen.Cart && cartItemCount > 0) {
                                Badge(containerColor = HuzaGreen) {
                                    Text(cartItemCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                            contentDescription = if (isBn) tab.titleBn else tab.titleEn
                        )
                    }
                },
                label = {
                    Text(
                        text = if (isBn) tab.titleBn else tab.titleEn,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = HuzaGreen,
                    selectedTextColor = HuzaGreen,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.testTag(tab.testTag)
            )
        }
    }
}
