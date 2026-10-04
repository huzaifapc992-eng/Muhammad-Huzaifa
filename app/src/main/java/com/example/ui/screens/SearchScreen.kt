package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.MenuItemCard
import com.example.ui.components.RestaurantCard
import com.example.ui.theme.HuzaGreen

@Composable
fun SearchScreen(
    searchQuery: String,
    language: Language,
    restaurants: List<Restaurant>,
    menuItems: List<MenuItem>,
    cart: List<CartItem>,
    selectedCategory: FoodCategory?,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (FoodCategory?) -> Unit,
    onRestaurantClick: (String) -> Unit,
    onAddToCart: (MenuItem) -> Unit,
    onRemoveFromCart: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = language == Language.BN

    val filteredRestaurants = remember(searchQuery, restaurants, selectedCategory) {
        if (searchQuery.isBlank() && selectedCategory == null) emptyList()
        else {
            restaurants.filter { rest ->
                val matchesQuery = searchQuery.isBlank() ||
                    rest.nameEn.contains(searchQuery, ignoreCase = true) ||
                    rest.nameBn.contains(searchQuery, ignoreCase = true) ||
                    rest.cuisinesEn.any { it.contains(searchQuery, ignoreCase = true) } ||
                    rest.cuisinesBn.any { it.contains(searchQuery, ignoreCase = true) } ||
                    rest.area.contains(searchQuery, ignoreCase = true)

                val matchesCat = selectedCategory == null ||
                    rest.cuisinesEn.any { it.contains(selectedCategory.nameEn, ignoreCase = true) }

                matchesQuery && matchesCat
            }
        }
    }

    val filteredMenuItems = remember(searchQuery, menuItems, selectedCategory) {
        if (searchQuery.isBlank() && selectedCategory == null) emptyList()
        else {
            menuItems.filter { item ->
                val matchesQuery = searchQuery.isBlank() ||
                    item.nameEn.contains(searchQuery, ignoreCase = true) ||
                    item.nameBn.contains(searchQuery, ignoreCase = true) ||
                    item.descEn.contains(searchQuery, ignoreCase = true) ||
                    item.descBn.contains(searchQuery, ignoreCase = true)

                val matchesCat = selectedCategory == null || item.category == selectedCategory
                matchesQuery && matchesCat
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Input Field
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(if (isBn) "কাচ্চি, ইলিশ, সিঙ্গাড়া বা রেস্তোরাঁ খুঁজুন..." else "Search Biryani, Pitha, Kebab, Singara...")
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = HuzaGreen)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input_field")
            )
        }

        // Quick Category Filter Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(FoodCategory.values()) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelected(if (isSelected) null else cat) },
                        label = {
                            Text("${cat.iconEmoji} ${if (isBn) cat.nameBn else cat.nameEn}", fontSize = 12.sp)
                        }
                    )
                }
            }
        }

        if (searchQuery.isBlank() && selectedCategory == null) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔍", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isBn) "কী খেতে ইচ্ছে করছে আজ?" else "What are you craving today?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBn) "কাচ্চি বিরিয়ানি, ভুনা খিচুড়ি, ভাপা পিঠা, সর্ষে ইলিশ কিংবা স্পাইসি নাগা বার্গার লিখে সার্চ করুন।" else "Search for Kacchi Biryani, Bhuna Khichuri, Pitha, Shorshe Ilish, or Naga Burgers.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // Matching Menu Items Section
            if (filteredMenuItems.isNotEmpty()) {
                item {
                    Text(
                        text = if (isBn) "খাবার আইটেমসমূহ (${filteredMenuItems.size})" else "Dishes Found (${filteredMenuItems.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(filteredMenuItems) { item ->
                    val qty = cart.find { it.menuItem.id == item.id }?.quantity ?: 0
                    MenuItemCard(
                        menuItem = item,
                        language = language,
                        quantityInCart = qty,
                        onAddToCart = { onAddToCart(item) },
                        onRemoveFromCart = { onRemoveFromCart(item.id) }
                    )
                }
            }

            // Matching Restaurants Section
            if (filteredRestaurants.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isBn) "রেস্তোরাঁসমূহ (${filteredRestaurants.size})" else "Restaurants (${filteredRestaurants.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(filteredRestaurants) { rest ->
                    RestaurantCard(
                        restaurant = rest,
                        language = language,
                        onClick = { onRestaurantClick(rest.id) }
                    )
                }
            }

            if (filteredMenuItems.isEmpty() && filteredRestaurants.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBn) "\"$searchQuery\" এর জন্য কোনো ফলাফল পাওয়া যায়নি" else "No results found for \"$searchQuery\"",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
