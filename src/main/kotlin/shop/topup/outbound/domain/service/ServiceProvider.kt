package shop.topup.outbound.domain.service

interface ServiceProvider {
    fun fetchBalance(userId: String, key: String): Double
}