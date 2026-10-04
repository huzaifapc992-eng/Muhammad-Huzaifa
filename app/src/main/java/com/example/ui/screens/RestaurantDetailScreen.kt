package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.CartItem
import com.example.data.model.FoodCategory
import com.example.data.model.Language
import com.example.data.model.MenuItem
import com.example.data.model.Restaurant
import com.example.ui.components.MenuItemCard
import com.example.ui.components.MiniCartBar
import com.example.ui.theme.HuzaAmber
import com.example.ui.theme.HuzaChiliRed
import com.example.ui.theme.HuzaGreen

@Composable
fun RestaurantDetailScreen(
    restaurant: Restaurant,
    allMenuItems: List<MenuItem>,
    cart: List<CartItem>,
    language: Language,
    onBack: () -> Unit,
    onAddToCart: (MenuItem) -> Unit,
    onRemoveFromCart: (String) -> Unit,
    onViewCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val isBn = language == Language.BN
    val context = LocalContext.current
    var selectedCategoryFilter by remember { mutableStateOf<FoodCategory?>(null) }

    // Filter items belonging to this restaurant (or show relevant regional menu items if sample)
    val restaurantMenu = remember(allMenuItems, restaurant, selectedCategoryFilter) {
        val baseItems = allMenuItems.filter {
            it.restaurantId == restaurant.id ||
            restaurant.cuisinesEn.any { c -> it.nameEn.contains(c, ignoreCase = true) || it.category.nameEn.contains(c, ignoreCase = true) }
        }.ifEmpty { allMenuItems.take(8) }

        if (selectedCategoryFilter != null) {
            baseItems.filter { it.category == selectedCategoryFilter }
        } else {
            baseItems
        }
    }

    val availableCategories = remember(allMenuItems, restaurant) {
        val base = allMenuItems.filter {
            it.restaurantId == restaurant.id ||
            restaurant.cuisinesEn.any { c -> it.nameEn.contains(c, ignoreCase = true) || it.category.nameEn.contains(c, ignoreCase = true) }
        }.ifEmpty { allMenuItems.take(8) }
        base.map { it.category }.distinct()
    }

    val cartItemCount = cart.sumOf { it.quantity }
    val cartSubtotal = cart.sumOf { it.menuItem.price * it.quantity }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("restaurant_detail_scroll"),
            contentPadding = PaddingValues(bottom = if (cartItemCount > 0) 90.dp else 24.dp)
        ) {
            // Header Image & Back Button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1B5E20), Color(0xFF0F766E))
                            )
                        )
                ) {
                    if (restaurant.coverImageUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(restaurant.coverImageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = restaurant.nameEn,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Scrim gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )

                    // Back Button & Sample badge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 40.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .testTag("restaurant_detail_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.65f)
                        ) {
                            Text(
                                text = if (isBn) "নমুনা রেস্তোরাঁ ডেটা" else "SAMPLE RESTAURANT",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Bottom info on image
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        val discount = if (isBn) restaurant.discountBadgeBn ?: restaurant.discountBadgeEn else restaurant.discountBadgeEn
                        if (discount != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = HuzaChiliRed,
                                modifier = Modifier.padding(bottom = 6.dp)
                            ) {
                                Text(
                                    text = discount,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Text(
                            text = if (isBn) restaurant.nameBn else restaurant.nameEn,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        )
                    }
                }
            }

            // Info Card details
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (isBn) restaurant.cuisinesBn.joinToString(" • ") else restaurant.cuisinesEn.joinToString(" • "),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Key Info chips: Rating, Delivery time, Delivery fee, Min order
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Rating
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = HuzaAmber.copy(alpha = 0.15f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = HuzaAmber, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("${restaurant.rating}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Text("${restaurant.reviewCount}+ ${if (isBn) "রিভিউ" else "reviews"}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            // Time
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = HuzaGreen, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("${restaurant.deliveryTimeMin}-${restaurant.deliveryTimeMax}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Text(if (isBn) "মিনিট" else "minutes", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            // Fee
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = HuzaGreen, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            if (restaurant.deliveryFee == 0.0) (if (isBn) "ফ্রি" else "Free") else "৳${restaurant.deliveryFee.toInt()}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (restaurant.deliveryFee == 0.0) HuzaGreen else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(if (isBn) "ডেলিভারি ফি" else "Delivery fee", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }
                }
            }

            // Menu Category Sticky Bar
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = if (isBn) "রেস্তোরাঁর খাবার তালিকা" else "Restaurant Menu",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedCategoryFilter == null,
                            onClick = { selectedCategoryFilter = null },
                            label = { Text(if (isBn) "সকল আইটেম" else "All Menu", fontSize = 12.sp) }
                        )

                        availableCategories.forEach { category ->
                            FilterChip(
                                selected = selectedCategoryFilter == category,
                                onClick = { selectedCategoryFilter = category },
                                label = { Text("${category.iconEmoji} ${if (isBn) category.nameBn else category.nameEn}", fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }

            // Menu Items List
            if (restaurantMenu.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBn) "এই ক্যাটাগরিতে কোনো খাবার পাওয়া যায়নি" else "No items found in this category",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(restaurantMenu) { item ->
                    val quantity = cart.find { it.menuItem.id == item.id }?.quantity ?: 0
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        MenuItemCard(
                            menuItem = item,
                            language = language,
                            quantityInCart = quantity,
                            onAddToCart = { onAddToCart(item) },
                            onRemoveFromCart = { onRemoveFromCart(item.id) }
                        )
                    }
                }
            }
        }

        // Floating Mini Cart Bar
        if (cartItemCount > 0) {
            MiniCartBar(
                totalItems = cartItemCount,
                totalPrice = cartSubtotal,
                language = language,
                onViewCartClick = onViewCart,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
