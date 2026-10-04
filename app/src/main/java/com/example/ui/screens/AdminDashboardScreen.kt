package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.HuzaAmber
import com.example.ui.theme.HuzaGreen

@Composable
fun AdminDashboardScreen(
    orders: List<Order>,
    restaurants: List<Restaurant>,
    menuItems: List<MenuItem>,
    language: Language,
    onBack: () -> Unit,
    onUpdateOrderStatus: (orderId: String, newStatus: OrderStatus) -> Unit,
    onAddRestaurant: (Restaurant) -> Unit,
    onAddMenuItem: (MenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val isBn = language == Language.BN

    var showAddRestaurantDialog by remember { mutableStateOf(false) }
    var showAddMenuItemDialog by remember { mutableStateOf(false) }

    val totalRevenue = orders.sumOf { it.total }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_dashboard_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isBn) "হুজাফুড অ্যাডমিন ও পার্টনার পোর্টাল" else "Admin & Restaurant Portal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isBn) "রিয়েল-টাইম কিচেন ও অর্ডার মনিটরিং" else "Real-time kitchen & menu operations",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Live Overview Stats Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = if (isBn) "মোট অর্ডার" else "Total Orders",
                    value = "${orders.size}",
                    icon = Icons.Default.ReceiptLong,
                    color = HuzaGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = if (isBn) "মোট আয় (BDT)" else "Revenue (৳)",
                    value = "৳${totalRevenue.toInt()}",
                    icon = Icons.Default.MonetizationOn,
                    color = HuzaAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = if (isBn) "সক্রিয় রেস্তোরাঁ" else "Restaurants",
                    value = "${restaurants.size}",
                    icon = Icons.Default.Storefront,
                    color = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = if (isBn) "মেনু আইটেম" else "Menu Dishes",
                    value = "${menuItems.size}",
                    icon = Icons.Default.RestaurantMenu,
                    color = Color(0xFF7C3AED),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Action Buttons: Add Restaurant & Add Menu Item
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showAddRestaurantDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HuzaGreen),
                    modifier = Modifier.weight(1f).height(46.dp).testTag("add_restaurant_dialog_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBn) "+ নতুন রেস্তোরাঁ" else "+ Add Restaurant", fontSize = 12.sp, color = Color.White)
                }

                Button(
                    onClick = { showAddMenuItemDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HuzaAmber),
                    modifier = Modifier.weight(1f).height(46.dp).testTag("add_menu_item_dialog_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBn) "+ নতুন মেনু আইটেম" else "+ Add Dish", fontSize = 12.sp, color = Color.Black)
                }
            }
        }

        // Live Order Management List
        item {
            Text(
                text = if (isBn) "সরাসরি অর্ডার পরিচালনা ও স্ট্যাটাস আপডেট" else "Manage Live Kitchen Orders",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(orders) { order ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Order #${order.id}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${order.restaurantNameEn} • ৳${order.total.toInt()}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (order.status) {
                                OrderStatus.PLACED -> Color(0xFFFEF3C7)
                                OrderStatus.PREPARING -> Color(0xFFDBEAFE)
                                OrderStatus.ON_THE_WAY -> Color(0xFFF3E8FF)
                                OrderStatus.DELIVERED -> Color(0xFFDCFCE7)
                            }
                        ) {
                            Text(
                                text = if (isBn) order.status.labelBn else order.status.labelEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = when (order.status) {
                                    OrderStatus.PLACED -> Color(0xFF92400E)
                                    OrderStatus.PREPARING -> Color(0xFF1E40AF)
                                    OrderStatus.ON_THE_WAY -> Color(0xFF6B21A8)
                                    OrderStatus.DELIVERED -> Color(0xFF166534)
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Customer: ${order.recipientName} (${order.recipientPhone})",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Address: ${order.deliveryAddress}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Advancement Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OrderStatus.values().forEach { st ->
                            val isCurrent = order.status == st
                            FilledTonalButton(
                                onClick = { onUpdateOrderStatus(order.id, st) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (isCurrent) HuzaGreen else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = when (st) {
                                        OrderStatus.PLACED -> "Placed"
                                        OrderStatus.PREPARING -> "Kitchen"
                                        OrderStatus.ON_THE_WAY -> "Rider"
                                        OrderStatus.DELIVERED -> "Done"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Restaurant Dialog Form
    if (showAddRestaurantDialog) {
        var nameEn by remember { mutableStateOf("") }
        var nameBn by remember { mutableStateOf("") }
        var district by remember { mutableStateOf("Kishoreganj") }
        var area by remember { mutableStateOf("Kishoreganj Sadar") }
        var cuisines by remember { mutableStateOf("Biryani, Kebab") }
        var deliveryFee by remember { mutableStateOf("25") }

        Dialog(onDismissRequest = { showAddRestaurantDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(if (isBn) "নতুন রেস্তোরাঁ যুক্ত করুন" else "Add New Restaurant", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    OutlinedTextField(value = nameEn, onValueChange = { nameEn = it }, label = { Text("Name (English)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = nameBn, onValueChange = { nameBn = it }, label = { Text("নাম (বাংলা)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = cuisines, onValueChange = { cuisines = it }, label = { Text("Cuisines (e.g. Biryani, Pitha)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = area, onValueChange = { area = it }, label = { Text("Area (e.g. Bhairab, Gulshan)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = deliveryFee, onValueChange = { deliveryFee = it }, label = { Text("Delivery Fee (৳)") }, modifier = Modifier.fillMaxWidth())

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddRestaurantDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (nameEn.isNotBlank()) {
                                    val r = Restaurant(
                                        id = "rest_${System.currentTimeMillis()}",
                                        nameEn = nameEn,
                                        nameBn = nameBn.ifBlank { nameEn },
                                        division = "Dhaka",
                                        district = district,
                                        area = area,
                                        cuisinesEn = cuisines.split(",").map { it.trim() },
                                        cuisinesBn = cuisines.split(",").map { it.trim() },
                                        rating = 4.8,
                                        reviewCount = 1,
                                        deliveryTimeMin = 20,
                                        deliveryTimeMax = 30,
                                        deliveryFee = deliveryFee.toDoubleOrNull() ?: 25.0,
                                        minOrder = 150.0,
                                        coverImageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&q=80",
                                        isSample = true
                                    )
                                    onAddRestaurant(r)
                                    showAddRestaurantDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HuzaGreen)
                        ) {
                            Text("Save", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Add Menu Item Dialog Form
    if (showAddMenuItemDialog) {
        var nameEn by remember { mutableStateOf("") }
        var nameBn by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("150") }
        var descEn by remember { mutableStateOf("") }
        var selectedCat by remember { mutableStateOf(FoodCategory.BIRYANI) }

        Dialog(onDismissRequest = { showAddMenuItemDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(if (isBn) "নতুন খাবার আইটেম যুক্ত করুন" else "Add New Menu Dish", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    OutlinedTextField(value = nameEn, onValueChange = { nameEn = it }, label = { Text("Dish Name (English)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = nameBn, onValueChange = { nameBn = it }, label = { Text("খাবারের নাম (বাংলা)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price in ৳ BDT") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = descEn, onValueChange = { descEn = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddMenuItemDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (nameEn.isNotBlank()) {
                                    val dish = MenuItem(
                                        id = "dish_${System.currentTimeMillis()}",
                                        restaurantId = restaurants.firstOrNull()?.id ?: "rest_1",
                                        nameEn = nameEn,
                                        nameBn = nameBn.ifBlank { nameEn },
                                        descEn = descEn.ifBlank { "Delicious authentic preparation" },
                                        descBn = "সুস্বাদু ঐতিহ্যবাহী পদ",
                                        price = price.toDoubleOrNull() ?: 150.0,
                                        category = selectedCat,
                                        isVeg = false,
                                        isBestseller = true,
                                        imageUrl = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500&q=80"
                                    )
                                    onAddMenuItem(dish)
                                    showAddMenuItemDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HuzaGreen)
                        ) {
                            Text("Save", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontWeight = FontWeight.Black, fontSize = 18.sp, color = color)
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
