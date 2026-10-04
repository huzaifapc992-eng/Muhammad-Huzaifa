package com.example.data.model

enum class Language {
    EN, BN
}

data class DistrictArea(
    val division: String,
    val divisionBn: String,
    val district: String,
    val districtBn: String,
    val area: String,
    val areaBn: String
)

enum class FoodCategory(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val iconEmoji: String
) {
    BIRYANI("biryani", "Biryani", "বিরিয়ানি", "🍲"),
    KHICHURI("khichuri", "Bhuna Khichuri", "ভুনা খিচুড়ি", "🍛"),
    PITHA("pitha", "Pitha", "পিঠা", "🥟"),
    FAST_FOOD("fast_food", "Fast Food", "ফাস্ট ফুড", "🍟"),
    BURGER("burger", "Burger", "বার্গার", "🍔"),
    PIZZA("pizza", "Pizza", "পিজ্জা", "🍕"),
    CHINESE("chinese", "Chinese", "চাইনিজ", "🍜"),
    FISH("fish", "Fish Items", "মাছের পদ", "🐟"),
    SNACKS("snacks", "Snacks", "স্ন্যাকস", "🥟"),
    BREAKFAST("breakfast", "Breakfast", "সকালের নাস্তা", "🍳"),
    DESSERT("dessert", "Desserts", "মিষ্টি ও দই", "🍧"),
    DRINKS("drinks", "Drinks", "পানীয়", "🥤")
}

data class MenuItem(
    val id: String,
    val restaurantId: String,
    val nameEn: String,
    val nameBn: String,
    val descEn: String,
    val descBn: String,
    val price: Double,
    val category: FoodCategory,
    val isVeg: Boolean,
    val isBestseller: Boolean,
    val imageUrl: String = "",
    val rating: Double = 4.8
)

data class Restaurant(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val division: String,
    val district: String,
    val area: String,
    val cuisinesEn: List<String>,
    val cuisinesBn: List<String>,
    val rating: Double,
    val reviewCount: Int,
    val deliveryTimeMin: Int,
    val deliveryTimeMax: Int,
    val deliveryFee: Double, // 0.0 means free
    val minOrder: Double,
    val coverImageUrl: String = "",
    val discountBadgeEn: String? = null,
    val discountBadgeBn: String? = null,
    val isSample: Boolean = true,
    val isFeatured: Boolean = false
)

data class CartItem(
    val menuItem: MenuItem,
    var quantity: Int,
    val specialInstructions: String = ""
)

enum class OrderStatus(val stepIndex: Int, val labelEn: String, val labelBn: String) {
    PLACED(0, "Order Placed", "অর্ডার গৃহীত"),
    PREPARING(1, "Preparing in Kitchen", "রান্নাঘরে প্রস্তুত হচ্ছে"),
    ON_THE_WAY(2, "Rider on the Way", "রাইডার খাবার নিয়ে আসছেন"),
    DELIVERED(3, "Delivered", "খাবার ডেলিভারি সম্পন্ন")
}

enum class PaymentMethod(val id: String, val titleEn: String, val titleBn: String, val subtitleEn: String, val subtitleBn: String) {
    CASH_ON_DELIVERY("cod", "Cash on Delivery", "ক্যাশ অন ডেলিভারি", "Pay cash upon arrival", "খাবার পেয়ে নগদ মূল্য দিন"),
    BKASH("bkash", "bKash", "বিকাশ", "Instant 1.5% cashback", "দ্রুত ও নিরাপদ বিকাশ পেমেন্ট"),
    NAGAD("nagad", "Nagad", "নগদ", "Special promotional discount", "নগদ পে ওয়ালেট পেমেন্ট"),
    CARD("card", "Debit / Credit Card", "কার্ড পেমেন্ট", "Visa / Mastercard / AMEX", "যেকোনো ডেবিট বা ক্রেডিট কার্ড")
}

data class PromoCode(
    val code: String,
    val discountPercent: Double = 0.0,
    val flatDiscount: Double = 0.0,
    val minOrder: Double = 0.0,
    val descEn: String,
    val descBn: String
)

data class Order(
    val id: String,
    val restaurantId: String,
    val restaurantNameEn: String,
    val restaurantNameBn: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val vat: Double,
    val discount: Double,
    val total: Double,
    val deliveryAddress: String,
    val district: String,
    val area: String,
    val recipientPhone: String,
    val recipientName: String,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus,
    val placedAtTimestamp: Long,
    val estimatedMinutes: Int = 30,
    val notes: String = ""
)

data class UserProfile(
    val name: String = "Huzaifa Customer",
    val phone: String = "+880 1700-123456",
    val email: String = "huzaifa@example.com",
    val isLoggedIn: Boolean = true,
    val addresses: List<String> = listOf(
        "House 14, Road 4, Kishoreganj Sadar",
        "Flat 3B, Lake View Tower, Dhanmondi, Dhaka"
    )
)
