package ru.scp.quiz

object ScpQuizConstants {

    const val DEFAULT_AVATAR_URL = "https://pp.userapi.com/c604519/v604519296/46a5/cRsRzeBpbGE.jpg"
    const val DEFAULT_FULL_NAME = "N/A"

    const val DEFAULT_PURCHASE_GIFT_TOKEN = "GIFT_PURCHASE_TOKEN"
    const val DEFAULT_PURCHASE_ORDER_ID= "GIFT_PURCHASE_ORDER_ID"

    object Path {
        const val AUTH = "auth"
        const val USER = "user"
        const val QUIZ = "quiz"
        const val TRANSLATIONS = "translations"
        const val PHRASES = "phrases"
        const val TRANSACTIONS = "transactions"
        const val LEADERBOARD = "leaderboard"
        const val IN_APP_PURCHASE = "inAppPurchase"
    }

    enum class SocialProvider {
        GOOGLE, FACEBOOK, VK
    }

    enum class ClientApp {
        ADMIN, GAME
    }

    enum class QuizTransactionType {
        NAME_WITH_PRICE, // 0
        NAME_NO_PRICE, // 1
        NAME_CHARS_REMOVED, // 2
        NUMBER_WITH_PRICE,  // 3
        NUMBER_NO_PRICE, // 4
        NUMBER_CHARS_REMOVED, // 5
        LEVEL_ENABLE_FOR_COINS, // 6
        ADV_WATCHED, // 7
        ADV_BUY_NEVER_SHOW, // 8
        UPDATE_SYNC, // 9
        // енум для синхронизации всех очков юзера при скачивании новой версии
        NAME_ENTERED_MIGRATION, // 10
        NUMBER_ENTERED_MIGRATION, // 11
        NAME_CHARS_REMOVED_MIGRATION, // 12
        NUMBER_CHARS_REMOVED_MIGRATION, // 13
        LEVEL_AVAILABLE_MIGRATION, // 14
        // енумы только для синхронизации с новой версией
        INAPP_PURCHASE,  //15
        // енум для покупки за деньги
        INAPP_PURCHASE_GIFT //16
        // енум для подарков юзерам
    }

    const val COINS_NAME_WITH_PRICE = -10
    const val COINS_NAME_NO_PRICE = 40
    const val COINS_NAME_CHARS_REMOVED = 10
    const val COINS_NUMBER_WITH_PRICE = -5
    const val COINS_NUMBER_NO_PRICE = 20
    const val COINS_NUMBER_CHARS_REMOVED = 10
    const val COINS_LEVEL_ENABLE_FOR_COINS = 5
    const val COINS_MIGRATION = 0
}
