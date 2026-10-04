package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.theme.HuzaAmber
import com.example.ui.theme.HuzaGreen

@Composable
fun OrderTrackingScreen(
    order: Order,
    language: Language,
    onBack: () -> Unit,
    onViewAllOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val isBn = language == Language.BN

    // Pulsing animation for active step
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("order_tracking_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top App Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isBn) "লাইভ অর্ডার ট্র্যাকিং" else "Live Order Tracking",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ID: ${order.id} • ${if (isBn) order.restaurantNameBn else order.restaurantNameEn}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Live Status Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(HuzaGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (order.status) {
                                OrderStatus.PLACED -> Icons.Default.ReceiptLong
                                OrderStatus.PREPARING -> Icons.Default.OutdoorGrill
                                OrderStatus.ON_THE_WAY -> Icons.Default.DeliveryDining
                                OrderStatus.DELIVERED -> Icons.Default.CheckCircle
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBn) order.status.labelBn else order.status.labelEn,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = if (order.status == OrderStatus.DELIVERED) {
                                if (isBn) "ধন্যবাদ! আপনার খাবার উপভোগ করুন" else "Order delivered! Enjoy your meal"
                            } else {
                                if (isBn) "আনুমানিক ডেলিভারি সময়: ২০-২৫ মিনিট" else "Estimated delivery: 20-25 mins"
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // Live Simulated Delivery Map Graphic
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Custom Canvas Map Drawing
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Draw background terrain
                        drawRect(color = Color(0xFFF1F5F9))

                        // Draw road network
                        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)

                        // Main route line from Restaurant to Home
                        drawLine(
                            color = Color(0xFFCBD5E1),
                            start = Offset(40f, h * 0.75f),
                            end = Offset(w - 40f, h * 0.25f),
                            strokeWidth = 14f
                        )
                        drawLine(
                            color = Color(0xFF16A34A),
                            start = Offset(40f, h * 0.75f),
                            end = Offset(w - 40f, h * 0.25f),
                            strokeWidth = 6f,
                            pathEffect = pathEffect
                        )

                        // Cross roads
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(w * 0.4f, 0f),
                            end = Offset(w * 0.45f, h),
                            strokeWidth = 10f
                        )
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(w * 0.7f, 0f),
                            end = Offset(w * 0.65f, h),
                            strokeWidth = 10f
                        )
                    }

                    // Restaurant Pin (Left)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 24.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = HuzaAmber,
                            shadowElevation = 4.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = "Restaurant",
                                tint = Color.Black,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(18.dp)
                            )
                        }
                        Text(
                            text = if (isBn) "রেস্তোরাঁ" else "Kitchen",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Moving Rider Marker (Center)
                    val riderProgress = when (order.status) {
                        OrderStatus.PLACED -> 0.15f
                        OrderStatus.PREPARING -> 0.35f
                        OrderStatus.ON_THE_WAY -> 0.70f
                        OrderStatus.DELIVERED -> 0.95f
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 40.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(x = (riderProgress * 120 - 60).dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = HuzaGreen.copy(alpha = if (order.status == OrderStatus.ON_THE_WAY) pulseAlpha else 1f),
                                shadowElevation = 6.dp
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TwoWheeler,
                                    contentDescription = "Rider",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(22.dp)
                                )
                            }
                            Text(
                                text = if (isBn) "হুজা রাইডার" else "Huza Rider",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HuzaGreen
                            )
                        }
                    }

                    // Delivery Destination Pin (Right)
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 24.dp, top = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFDC2626),
                            shadowElevation = 4.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(18.dp)
                            )
                        }
                        Text(
                            text = if (isBn) "আপনার ঠিকানা" else "Your Door",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Timeline Steps
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBn) "অর্ডার অগ্রগতির ধাপসমূহ" else "Order Progress Steps",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val steps = OrderStatus.values()
                    steps.forEachIndexed { index, status ->
                        val isCompleted = order.status.stepIndex >= status.stepIndex
                        val isCurrent = order.status == status

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Step indicator circle
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCompleted -> HuzaGreen
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBn) status.labelBn else status.labelEn,
                                    fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (isCurrent) HuzaGreen else if (isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (isCurrent) {
                                    Text(
                                        text = if (isBn) "বর্তমান অবস্থান" else "In Progress...",
                                        fontSize = 11.sp,
                                        color = HuzaGreen,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        if (index < steps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 13.dp)
                                    .width(2.dp)
                                    .height(20.dp)
                                    .background(if (order.status.stepIndex > status.stepIndex) HuzaGreen else MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                    }
                }
            }
        }

        // Assigned Rider Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = HuzaGreen)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Karim Mia (করিম মিয়া)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (isBn) "হুজাফুড ভেরিফায়েড রাইডার • ৫.০★" else "HuzaFood Verified Rider • 5.0★",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = { /* simulated call */ },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HuzaGreen),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBn) "কল করুন" else "Call", fontSize = 12.sp)
                    }
                }
            }
        }

        // Order Summary items list
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isBn) "অর্ডারকৃত খাবারের তালিকা" else "Ordered Items",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.quantity}x ${if (isBn) item.menuItem.nameBn else item.menuItem.nameEn}",
                                fontSize = 13.sp
                            )
                            Text(
                                text = "৳${(item.menuItem.price * item.quantity).toInt()}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isBn) "সর্বমোট (পেমেন্ট: ${order.paymentMethod.titleBn})" else "Total (${order.paymentMethod.titleEn})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("৳${order.total.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = HuzaGreen)
                    }
                }
            }
        }

        // Back to home action
        item {
            Button(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = if (isBn) "হোমে ফিরে যান" else "Back to Home",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
