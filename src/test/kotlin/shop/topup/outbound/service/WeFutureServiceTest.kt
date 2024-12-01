package shop.topup.outbound.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import kotlin.test.Test

class WeFutureServiceTest {
    val weFutureService = WeFutureService(jacksonMapperBuilder().apply {
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }.build())
    val sellerId = "35132"
    val secret = "xxx"

    @Test
    fun fetchBalance() {
        val balance = weFutureService.fetchBalance(sellerId, secret)
        println(balance)
    }

    @Test
    fun fetchGroups() {
        val groups = weFutureService.fetchGroups(sellerId, secret)
        println(groups)
    }

    @Test
    fun testFetchItems() {
        val items = weFutureService.fetchItems(sellerId, secret, 0)
        println(items)
    }

    @Test
    fun fetchItemDetail() {
        val item = weFutureService.fetchItemDetail(sellerId, secret, 413353)
        println(item)
    }
}