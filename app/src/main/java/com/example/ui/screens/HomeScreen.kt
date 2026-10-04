package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DistrictArea
import com.example.data.model.FoodCategory
import com.example.data.model.Language
import com.example.data.model.Restaurant
import com.example.ui.components.RestaurantCard
import com.example.ui.theme.HuzaAmber
import com.example.ui.theme.HuzaChiliRed
import com.example.ui.theme.HuzaGreen

@Composable
fun HomeScreen(
    currentLocation: DistrictArea,
    language: Language,
    restaurants: List<Restaurant>,
    selectedCategory: FoodCategory?,
    filterFreeDelivery: Boolean,
    filterOffersOnly: Boolean,
    filterMinRating: Double,
    sortBy: String,
    onCategorySelected: (FoodCategory?) -> Unit,
    onFilterFreeDeliveryChange: (Boolean) -> Unit,
    onFilterOffersChange: (Boolean) -> Unit,
    onFilterMinRatingChange: (Double) -> Unit,
    onSortByChange: (String) -> Unit,
    onRestaurantClick: (String) -> Unit,
    onSearchTrigger: () -> Unit,
    onLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = language == Language.BN
    val context = LocalContext.current

    // Filter and sort restaurants
    val filteredRestaurants = remember(restaurants, currentLocation, selectedCategory, filterFreeDelivery, filterOffersOnly, filterMinRating, sortBy) {
        var list = restaurants.filter { rest ->
            // Match current district or general sample
            rest.district.equals(currentLocation.district, ignoreCase = true) ||
            rest.area.contains(currentLocation.area, ignoreCase = true)
        }
        if (list.isEmpty()) {
            list = restaurants // fallback to show restaurants if specific area list is small
        }

        if (filterFreeDelivery) {
            list = list.filter { it.deliveryFee == 0.0 }
        }
        if (filterOffersOnly) {
            list = list.filter { it.discountBadgeEn != null }
        }
        if (filterMinRating > 0.0) {
            list = list.filter { it.rating >= filterMinRating }
        }

        when (sortBy) {
            "rating" -> list.sortedByDescending { it.rating }
            "delivery_time" -> list.sortedBy { it.deliveryTimeMin }
            "delivery_fee" -> list.sortedBy { it.deliveryFee }
            else -> list.sortedByDescending { it.isFeatured }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_scroll"),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Section & Search Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Banner Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = HuzaGreen),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Hero Image Background (Generated asset)
                        Image(
                            painter = painterResource(id = R.drawable.huzafood_hero_1791093858744),
                            contentDescription = "Bangladeshi Feast",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Gradient protection overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color.Black.copy(alpha = 0.75f),
                                            Color.Black.copy(alpha = 0.35f)
                                        )
                                    )
                                )
                        )

                        // Hero Content
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = HuzaAmber,
                                modifier = Modifier.padding(bottom = 6.dp)
                            ) {
                                Text(
                                    text = if (isBn) "কিশোরগঞ্জ ও ঢাকা এক্সপ্রেস ডেলিভারি" else "Kishoreganj & Dhaka Express",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Text(
                                text = if (isBn) "কাচ্চি, মাছের পদ কিংবা পিঠা,\nআপনার দরজায় পৌঁছে দেব আমরা!" else "Craving Biryani, Pitha or Ilish?\nDelivered fast to your door!",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White.copy(alpha = 0.25f))
                                    .clickable(onClick = onLocationClick)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = HuzaAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isBn) "${currentLocation.areaBn} (পরিবর্তন করতে ট্যাপ করুন)" else "${currentLocation.area} (Tap to change)",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar Trigger
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(onClick = onSearchTrigger)
                        .testTag("home_search_trigger")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = HuzaGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isBn) "খাবার বা রেস্তোরাঁ খুঁজুন (বিরিয়ানি, কাচ্চি, সিঙ্গাড়া)..." else "Search food or restaurant (Biryani, Pitha, Kebab)...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Promotional Offer Carousel
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isBn) "বিশেষ অফার ও ডিলসমূহ" else "Special Offers & Deals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        OfferPromoCard(
                            title = if (isBn) "প্রথম অর্ডারে ২০% ছাড়!" else "20% OFF FIRST ORDER",
                            subtitle = if (isBn) "কুপন কোড: HUZANEW" else "Use code: HUZANEW",
                            bgColor = HuzaGreen,
                            badge = if (isBn) "নতুন গ্রাহক" else "NEW USER"
                        )
                    }
                    item {
                        OfferPromoCard(
                            title = if (isBn) "কিশোরগঞ্জ ও ভৈরবে ফ্রি ডেলিভারি" else "FREE DELIVERY WEEKEND",
                            subtitle = if (isBn) "৳১৫০+ অর্ডারে সম্পূর্ণ ফ্রি" else "Free on orders above ৳150",
                            bgColor = HuzaAmber,
                            badge = if (isBn) "সীমিত সময়" else "LIMITED"
                        )
                    }
                    item {
                        OfferPromoCard(
                            title = if (isBn) "সরাসরি ৫০ টাকা ক্যাশ ডিসকাউন্ট" else "FLAT ৳50 DISCOUNT",
                            subtitle = if (isBn) "কোড প্রয়োগ করুন: HUZAFOOD" else "Use code: HUZAFOOD",
                            bgColor = HuzaChiliRed,
                            badge = if (isBn) "মেগা সেভার" else "MEGA SAVER"
                        )
                    }
                }
            }
        }

        // Food Categories Horizontal Bar
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "জনপ্রিয় খাবারের ক্যাটাগরি" else "Explore Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (selectedCategory != null) {
                        TextButton(onClick = { onCategorySelected(null) }) {
                            Text(if (isBn) "রিসেট করুন" else "Clear", color = HuzaGreen, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(FoodCategory.values()) { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, HuzaGreen) else null,
                            shadowElevation = if (isSelected) 3.dp else 1.dp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onCategorySelected(category) }
                                .testTag("category_chip_${category.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) HuzaGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = category.iconEmoji,
                                        fontSize = 24.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isBn) category.nameBn else category.nameEn,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Popular Restaurants near you
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isBn) "${currentLocation.areaBn} এর জনপ্রিয় রেস্তোরাঁ" else "Popular Near ${currentLocation.area}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                val featuredRestaurants = remember(restaurants, currentLocation) {
                    restaurants.filter { it.isFeatured }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(featuredRestaurants) { rest ->
                        Box(modifier = Modifier.width(280.dp)) {
                            RestaurantCard(
                                restaurant = rest,
                                language = language,
                                onClick = { onRestaurantClick(rest.id) }
                            )
                        }
                    }
                }
            }
        }

        // Filters and Sort Row
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "সকল রেস্তোরাঁ (${filteredRestaurants.size})" else "All Restaurants (${filteredRestaurants.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Sort chip indicator
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable {
                            val nextSort = when (sortBy) {
                                "relevance" -> "rating"
                                "rating" -> "delivery_time"
                                "delivery_time" -> "delivery_fee"
                                else -> "relevance"
                            }
                            onSortByChange(nextSort)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.FilterList, contentDescription = null, tint = HuzaGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (sortBy) {
                                    "rating" -> if (isBn) "রেটিং অনুযায়ী" else "Top Rated"
                                    "delivery_time" -> if (isBn) "দ্রুততম" else "Fastest"
                                    "delivery_fee" -> if (isBn) "ডেলিভারি ফি" else "Low Fee"
                                    else -> if (isBn) "জনপ্রিয়" else "Popular"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Horizontal Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filterFreeDelivery,
                        onClick = { onFilterFreeDeliveryChange(!filterFreeDelivery) },
                        label = { Text(if (isBn) "ফ্রি ডেলিভারি" else "Free Delivery", fontSize = 11.sp) },
                        leadingIcon = if (filterFreeDelivery) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null
                    )

                    FilterChip(
                        selected = filterOffersOnly,
                        onClick = { onFilterOffersChange(!filterOffersOnly) },
                        label = { Text(if (isBn) "বিশেষ অফার" else "Special Offers", fontSize = 11.sp) },
                        leadingIcon = if (filterOffersOnly) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null
                    )

                    FilterChip(
                        selected = filterMinRating >= 4.5,
                        onClick = { onFilterMinRatingChange(if (filterMinRating >= 4.5) 0.0 else 4.5) },
                        label = { Text(if (isBn) "৪.৫+ রেটিং" else "4.5+ Rating", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, tint = HuzaAmber, modifier = Modifier.size(14.dp)) }
                    )
                }
            }
        }

        // Restaurant List Items
        if (filteredRestaurants.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🍽️", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isBn) "এই ফিল্টারে কোনো রেস্তোরাঁ পাওয়া যায়নি" else "No restaurants found matching filters",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBn) "অনুগ্রহ করে অন্য ফিল্টার চেষ্টা করুন অথবা অন্য এলাকা নির্বাচন করুন।" else "Try clearing some filters or changing your delivery location.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredRestaurants) { rest ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    RestaurantCard(
                        restaurant = rest,
                        language = language,
                        onClick = { onRestaurantClick(rest.id) }
                    )
                }
            }
        }

        // Footer & Credits (MANDATORY REQUIREMENT)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Huza",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = HuzaGreen
                    )
                    Text(
                        text = "Food",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = HuzaAmber
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isBn) "রুচির ঠিকানা, আপনার দরজায়" else "Ruchi-r Thikana, Apnar Dorjay",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "Developed by Muhammad Huzaifa",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Serving Kishoreganj (Sadar, Bhairab, Nikli, Mithamoin) & Dhaka",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun OfferPromoCard(
    title: String,
    subtitle: String,
    bgColor: Color,
    badge: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(230.dp)
            .height(105.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.25f)
            ) {
                Text(
                    text = badge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
