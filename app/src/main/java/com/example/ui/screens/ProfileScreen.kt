package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.data.model.UserProfile
import com.example.ui.theme.HuzaAmber
import com.example.ui.theme.HuzaChiliRed
import com.example.ui.theme.HuzaGreen

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    language: Language,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onLanguageToggle: () -> Unit,
    onOpenAdminDashboard: () -> Unit,
    onOpenAbout: () -> Unit,
    onViewOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = language == Language.BN
    var showAddressDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Info Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = HuzaGreen,
                            modifier = Modifier.size(60.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userProfile.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = userProfile.phone,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = userProfile.email,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (!userProfile.isLoggedIn) {
                            Button(
                                onClick = onLoginClick,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HuzaGreen)
                            ) {
                                Text(if (isBn) "লগইন" else "Login", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Developer Credit Banner (CRITICAL REQUIREMENT)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenAbout)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(HuzaGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Developed by Muhammad Huzaifa",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = if (isBn) "হুজাফুড বাংলাদেশ • কিশোরগঞ্জ ও ঢাকা" else "HuzaFood Bangladesh • Kishoreganj & Dhaka",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = HuzaGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Admin & Partner Portal Access (IMPORTANT REQUIREMENT)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = HuzaAmber.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenAdminDashboard)
                    .testTag("admin_dashboard_card_trigger")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = HuzaAmber,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.Black, modifier = Modifier.size(22.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBn) "রেস্তোরাঁ ও অ্যাডমিন ড্যাশবোর্ড" else "Restaurant & Admin Dashboard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isBn) "রেস্তোরাঁ যুক্ত করুন, খাবার মেনু সাজান ও অর্ডার পরিচালনা করুন" else "Manage restaurants, menu items & orders",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = HuzaAmber,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Account Options Section
        item {
            Text(
                text = if (isBn) "অ্যাকাউন্ট সেটিংস" else "Account & Preferences",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ProfileOptionItem(
                        icon = Icons.Default.ReceiptLong,
                        title = if (isBn) "অর্ডার হিস্টোরি" else "Order History",
                        subtitle = if (isBn) "অতীতের খাবারের অর্ডার দেখুন" else "View past deliveries",
                        onClick = onViewOrders
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    ProfileOptionItem(
                        icon = Icons.Default.Language,
                        title = if (isBn) "ভাষা পরিবর্তন (বাংলা / English)" else "Language (English / বাংলা)",
                        subtitle = if (isBn) "বর্তমান ভাষা: বাংলা" else "Current: English",
                        onClick = onLanguageToggle
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    ProfileOptionItem(
                        icon = Icons.Default.LocationOn,
                        title = if (isBn) "সংরক্ষিত ঠিকানা" else "Saved Addresses",
                        subtitle = "${userProfile.addresses.size} saved addresses",
                        onClick = { showAddressDialog = true }
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    ProfileOptionItem(
                        icon = Icons.Default.Info,
                        title = if (isBn) "হুজাফুড সম্পর্কে" else "About HuzaFood",
                        subtitle = if (isBn) "ভার্সন ১.০ • নিয়ম ও শর্তাবলী" else "Version 1.0 • Terms & Hotline",
                        onClick = onOpenAbout
                    )
                }
            }
        }

        // Logout Button if logged in
        if (userProfile.isLoggedIn) {
            item {
                OutlinedButton(
                    onClick = onLogoutClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = HuzaChiliRed),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isBn) "লগআউট করুন" else "Log Out", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = { Text(if (isBn) "সংরক্ষিত ঠিকানাসমূহ" else "Saved Delivery Addresses") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    userProfile.addresses.forEach { addr ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Home, contentDescription = null, tint = HuzaGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(addr, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddressDialog = false }) {
                    Text(if (isBn) "ঠিক আছে" else "Close")
                }
            }
        )
    }
}

@Composable
private fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HuzaGreen,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
    }
}
