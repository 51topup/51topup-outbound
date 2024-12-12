package shop.topup.outbound.domain.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import org.apache.commons.text.StringEscapeUtils
import kotlin.test.Test

class WeFutureServiceTest {
    val weFutureService = WeFutureService(jacksonMapperBuilder().apply {
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }.build())
    val sellerId = "35132"
    val secret = "831f54f132b2542016fca10cfe7cec2b"

    @Test
    fun fetchBalance() {
        val balance = weFutureService.fetchBalance(sellerId, secret)
        println(balance)
    }

    @Test
    fun fetchGroups() {
        val groups = weFutureService.fetchGroups(sellerId, secret)
        println("groupname,groupaliasname,groupid,groupimgurl,brandid,brandname,brandimgurl")
        groups.forEach {
            val groupname = StringEscapeUtils.escapeCsv(it.groupname)
            val groupaliasname = StringEscapeUtils.escapeCsv(it.groupaliasname)
            val brandname = StringEscapeUtils.escapeCsv(it.brandname)
            println("${groupname},${groupaliasname},${it.groupid},${it.groupimgurl},${it.brandid},${brandname},${it.brandimgurl}")
        }
    }

    @Test
    fun testFetchItems() {
        println("goodsid,imgurl,goodsname,goodsprice,goodsstatus,goodstype,stock,buyminnum,goodsgroupid")
        (0..33).forEach { i ->
            val result = weFutureService.fetchItems(sellerId, secret, i)
            result.data.forEach {
                val goodsname = StringEscapeUtils.escapeCsv(it.goodsname)
                println("${it.goodsid},${it.imgurl},${goodsname},${it.goodsprice},${it.goodsstatus},${it.goodstype},${it.stock},${it.buyminnum},${it.goodsgroupid}")
            }
            Thread.sleep(10000)
        }

    }

    @Test
    fun fetchItemDetail() {
        val item = weFutureService.fetchItemDetail(sellerId, secret, 1931722)
        println(ObjectMapper().writeValueAsString(item))
    }

    @Test
    fun fetchOrderDetail() {
        val order = weFutureService.fetchOrderDetail(sellerId, secret, "D202411261902396962173519")
        println(ObjectMapper().writeValueAsString(order))
    }

    @Test
    fun testPlaceOrder() {
        val request = PlaceOrderRequest(
            userid = sellerId,
            goodsid = 1931722,
            buynum = 1,
            outorderno = "123456789",
            attach = "[{\"attachtype\":1,\"value\":\"18667135137\"}]",
        )
        val result = weFutureService.placeOrder(request, secret);
        println(result)
    }
}