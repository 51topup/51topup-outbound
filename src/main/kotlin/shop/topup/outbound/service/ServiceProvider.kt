package shop.topup.outbound.service

interface ServiceProvider {
    fun fetchBalance(id: String, key: String): Double
}