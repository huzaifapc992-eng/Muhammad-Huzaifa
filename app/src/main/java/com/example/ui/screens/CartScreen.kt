package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.HuzaAmber
import com.example.ui.theme.HuzaChiliRed
import com.example.ui.theme.HuzaGreen

@Composable
fun CartScreen(
    cart: List<CartItem>,
    currentLocation: DistrictArea,
    language: Language,
    appliedPromo: PromoCode?,
    subtotal: Double,
    deliveryFee: Double,
    vat: Double,
    discount: Double,
    total: Double,
    userProfile: UserProfile,
    onAddToCart: (MenuItem) -> Unit,
    onRemoveFromCart: (String) -> Unit,
    onDeleteCartItem: (String) -> Unit,
    onClearCart: () -> Unit,
    onApplyPromo: (String) -> Boolean,
    onRemovePromo: () -> Unit,
    onChangeLocationClick: () -> Unit,
    onPlaceOrder: (deliveryAddress: String, phone: String, recipientName: String, paymentMethod: PaymentMethod, notes: String) -> Unit,
    onBrowseFoodClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = language == Language.BN

    var deliveryAddressInput by remember { mutableStateOf("House 14, Road 4, ${currentLocation.area}, ${currentLocation.district}") }
    var phoneInput by remember { mutableStateOf(userProfile.phone) }
    var nameInput by remember { mutableStateOf(userProfile.name) }
    var specialNotes by remember { mutableStateOf("") }
    var promoCodeInput by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.BKASH) }

    if (cart.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = HuzaGreen,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = if (isBn) "আপনার কার্ট খালি!" else "Your Cart is Empty",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isBn) "সুস্বাদু কাচ্চি বিরিয়ানি, খিচুড়ি, কিংবা ঐতিহ্যবাহী পিঠা অর্ডার করতে রেস্তোরাঁ খুঁজুন।" else "Explore restaurants to add delicious biryani, fish curry or local pitha to your order.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onBrowseFoodClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HuzaGreen),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("browse_food_empty_cart")
                ) {
                    Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBn) "খাবার ব্রাউজ করুন" else "Browse Restaurants",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("cart_screen_scroll"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBn) "আপনার অর্ডার কার্ট" else "Order Cart",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${cart.sumOf { it.quantity }} ${if (isBn) "টি খাবার আইটেম" else "items in cart"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = onClearCart) {
                    Text(if (isBn) "সব মুছুন" else "Clear All", color = HuzaChiliRed, fontSize = 12.sp)
                }
            }
        }

        // Cart Items List
        items(cart) { item ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBn) item.menuItem.nameBn else item.menuItem.nameEn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "৳${item.menuItem.price.toInt()} each",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "৳${(item.menuItem.price * item.quantity).toInt()}",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = HuzaGreen
                        )
                    }

                    // Stepper
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(4.dp)
                    ) {
                        IconButton(
                            onClick = { onRemoveFromCart(item.menuItem.id) },
                            modifier = Modifier.size(28.dp).testTag("cart_minus_${item.menuItem.id}")
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "${item.quantity}",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = { onAddToCart(item.menuItem) },
                            modifier = Modifier.size(28.dp).testTag("cart_plus_${item.menuItem.id}")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { onDeleteCartItem(item.menuItem.id) },
                        modifier = Modifier.size(32.dp).testTag("cart_delete_${item.menuItem.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = HuzaChiliRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Special Cooking Instruction
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EditNote, contentDescription = null, tint = HuzaGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) "রান্নাঘরে বিশেষ নির্দেশনা (ঐচ্ছিক)" else "Special Cooking Instructions (Optional)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = specialNotes,
                        onValueChange = { specialNotes = it },
                        placeholder = { Text(if (isBn) "যেমন: কম ঝাল দিন, সালাদে কাঁচা মরিচ দেবেন না..." else "e.g. Less spicy, extra salad, ring bell...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            }
        }

        // Delivery Address Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = HuzaGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBn) "ডেলিভারি ঠিকানা" else "Delivery Details",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        TextButton(onClick = onChangeLocationClick) {
                            Text(if (isBn) "এলাকা পরিবর্তন" else "Change Area", color = HuzaGreen, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = deliveryAddressInput,
                        onValueChange = { deliveryAddressInput = it },
                        label = { Text(if (isBn) "বাসা/রাস্তা ও বিস্তারিত ঠিকানা" else "Detailed Street & House Address") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text(if (isBn) "গ্রাহকের নাম" else "Recipient Name") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text(if (isBn) "ফোন নম্বর" else "Phone") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Promo Code Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isBn) "কুপন বা ভাউচার কোড" else "Promo Voucher Code",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (appliedPromo != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = HuzaGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(appliedPromo.code, fontWeight = FontWeight.Bold, color = HuzaGreen)
                                        Text(if (isBn) appliedPromo.descBn else appliedPromo.descEn, fontSize = 11.sp)
                                    }
                                }
                                TextButton(onClick = onRemovePromo) {
                                    Text(if (isBn) "বাতিল" else "Remove", color = HuzaChiliRed, fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = promoCodeInput,
                                onValueChange = { promoCodeInput = it.uppercase() },
                                placeholder = { Text("HUZANEW / HUZAFOOD") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("promo_code_input")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (onApplyPromo(promoCodeInput)) {
                                        promoCodeInput = ""
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HuzaGreen),
                                modifier = Modifier.height(52.dp).testTag("apply_promo_btn")
                            ) {
                                Text(if (isBn) "প্রয়োগ" else "Apply", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Payment Method Selector
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isBn) "মূল্য পরিশোধ পদ্ধতি (Payment Method)" else "Payment Method",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PaymentMethod.values().forEach { method ->
                        val isSelected = selectedPaymentMethod == method
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, HuzaGreen) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedPaymentMethod = method }
                                .testTag("payment_method_${method.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedPaymentMethod = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = HuzaGreen)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isBn) method.titleBn else method.titleEn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (isBn) method.subtitleBn else method.subtitleEn,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Order Summary Breakdown
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isBn) "মূল্য বিবরণী" else "Order Summary",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isBn) "খাবারের মোট মূল্য (Subtotal)" else "Subtotal", fontSize = 13.sp)
                        Text("৳${subtotal.toInt()}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isBn) "ডেলিভারি ফি" else "Delivery Fee", fontSize = 13.sp)
                        Text(
                            text = if (deliveryFee == 0.0) (if (isBn) "ফ্রি" else "Free") else "৳${deliveryFee.toInt()}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = if (deliveryFee == 0.0) HuzaGreen else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isBn) "ভ্যাট (৫%)" else "Government VAT (5%)", fontSize = 13.sp)
                        Text("৳${vat.toInt()}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    if (discount > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isBn) "ডিসকাউন্ট / ছাড়" else "Promo Discount", fontSize = 13.sp, color = HuzaGreen)
                            Text("-৳${discount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = HuzaGreen)
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBn) "সর্বমোট প্রদেয়" else "Total Payable",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "৳${total.toInt()}",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = HuzaGreen
                        )
                    }
                }
            }
        }

        // Place Order Action Button
        item {
            Button(
                onClick = {
                    onPlaceOrder(
                        deliveryAddressInput,
                        phoneInput,
                        nameInput,
                        selectedPaymentMethod,
                        specialNotes
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HuzaGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("place_order_button")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "অর্ডার কনফার্ম করুন" else "Place Order",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = "৳${total.toInt()} →",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
