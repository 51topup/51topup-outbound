package shop.topup.outbound.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED
import org.springframework.stereotype.Service
import org.springframework.util.DigestUtils
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient

/**
 * service for http://by.wefutureidea.com/home/user/uscenter.html
 */
@Service
class WeFutureService(val objectMapper: ObjectMapper) : ServiceProvider {
    val restClient = RestClient.builder()
        .baseUrl("http://aa.wefutureidea.com").build();

    override fun fetchBalance(id: String, secret: String): Double {
        val map = LinkedMultiValueMap<String, String>();
        map.add("userid", id)
        fillSignature(map, secret)
        val jsonText = restClient.post().uri("/dockapi/index/userinfo")
            .contentType(APPLICATION_FORM_URLENCODED)
            .body(map)
            .retrieve()
            .body(String::class.java)!!
        val result = objectMapper.readValue<Result<WfSellerInfo>>(jsonText);
        return result.data.money
    }

    fun fetchGroups(id: String, secret: String): List<WfGroup> {
        val map = LinkedMultiValueMap<String, String>();
        map.add("userid", id)
        fillSignature(map, secret)
        val jsonText = restClient.post().uri("/dockapi/v2/getallgoodsgroup")
            .contentType(APPLICATION_FORM_URLENCODED)
            .body(map)
            .retrieve()
            .body(String::class.java)!!
        println(jsonText)
        val result = objectMapper.readValue<Result<List<WfGroup>>>(jsonText);
        return result.data
    }

    /**
     * page start with 0
     */
    fun fetchItems(id: String, secret: String, page: Int): WfItemsResult {
        val map = LinkedMultiValueMap<String, String>()
        map.add("userid", id)
        map.add("page", page.toString())
        fillSignature(map, secret)
        val jsonText = restClient.post().uri("/dockapi/v2/getallgoods")
            .contentType(APPLICATION_FORM_URLENCODED)
            .body(map)
            .retrieve()
            .body(String::class.java)!!
        println(jsonText)
        return objectMapper.readValue<WfItemsResult>(jsonText)
    }

    fun fetchItemDetail(id: String, secret: String, goodsId: Int): WfItemDetail {
        val map = LinkedMultiValueMap<String, String>()
        map.add("userid", id)
        map.add("goodsid", goodsId.toString())
        fillSignature(map, secret)
        val objectNode = restClient.post().uri("/dockapi/v2/goodsdetails.html")
            .contentType(APPLICATION_FORM_URLENCODED)
            .body(map)
            .retrieve()
            .body(ObjectNode::class.java)!!
        return objectMapper.convertValue<WfItemDetail>(objectNode.get("goodsdetails"))
    }

    fun placeOrder(request: PlaceOrderRequest, secret: String): PlaceOrderResult {
        val map = objectMapper.convertValue<LinkedMultiValueMap<String, String>>(request)
        map.add("userid", request.userid)
        map.add("goodsid", request.goodsid.toString())
        map.add("buynum", request.buynum.toString())
        map.add("outorderno", request.outorderno)
        map.add("attach", request.attach)
        fillSignature(map, secret)
        return restClient.post().uri("/dockapi/index/buy")
            .contentType(APPLICATION_FORM_URLENCODED)
            .body(map)
            .retrieve()
            .body(PlaceOrderResult::class.java)!!
    }

    fun fetchOrderDetail(id: String, secret: String, orderNo: String): OrderDetailResult {
        val map = LinkedMultiValueMap<String, String>()
        map.add("userid", id)
        map.add("orderno", orderNo)
        fillSignature(map, secret)
        return restClient.post().uri("/dockapi/index/orderinfo")
            .contentType(APPLICATION_FORM_URLENCODED)
            .body(map)
            .retrieve()
            .body(OrderDetailResult::class.java)!!
    }

    fun fillSignature(map: LinkedMultiValueMap<String, String>, secret: String) {
        var pairs = mutableListOf<String>()
        map.keys.sorted().forEach { key ->
            map.getFirst(key)?.let {
                pairs.add("$key=$it")
            }
        }
        val signedText = pairs.joinToString("&") + secret
        val signature = DigestUtils.md5DigestAsHex(signedText.toByteArray())
        map.add("sign", signature)
    }
}

data class Result<T>(val code: Int, val msg: String, val data: T)
data class WfSellerInfo(val money: Double, val creditquota: Double, val group_id: Int)
data class WfGroup(
    val groupid: Int,
    val groupname: String,
    val groupaliasname: String?,
    val groupimgurl: String,
    val brandid: Int,
    val brandname: String,
    val brandimgurl: String,
)

data class WfItemsResult(
    val code: Int, val msg: String,
    val nowpage: Int,
    val allpage: Int,
    val count: Int,
    val data: List<WfItem>
)

data class WfItem(
    val goodsid: Int,
    val imgurl: String,
    val goodsname: String,
    val goodsprice: Double,
    val goodsstatus: Int,
    val goodstype: Int,
    val stock: Int,
    val buyminnum: Int,
    val goodsgroupid: Int,
    val attach: List<Note>?
)

data class Note(val title: String, val tip: String?)

data class WfItemDetail(
    val id: Int,
    val groupname: String,
    val groupimgurl: String,
    val brandname: String,
    val brandimgurl: String,
    val brandid: Int,
    val attachgroupid: Int, //充值字段模版ID
    val goodsname: String,
    val goodsgroupid: Int,
    val stock: Int,
    val salesvolume: Int,
    val goodsprice: Double,
    val marketprice: Double,
    val goodsstatus: Int,
    val buyminnum: Int,
    val buymaxnum: Int,
    val tiptext: String,
    val imgurl: String,
    val goodstype: Int,
    val goodstypetext: String?,
    val msgboxtip: String?,
    val details: String,
    val attach: List<Note>?
)

data class PlaceOrderRequest(
    val userid: String,
    val goodsid: Int,
    val buynum: Int,
    val outorderno: String,
    val attach: String,
    val callbackurl: String?
)

data class PlaceOrderResult(
    val code: Int,
    val msg: String,
    val orderno: String,
    val outorderno: String,
    val money: Double, //订单金额
    val buynum: Double,
    val cardlist: List<String>?,
)

data class OrderDetailResult(
    val code: Int,
    val msg: String,
    val aftersales: List<String>,
    val cardlist: List<String>,
    val data: OrderDetail
)

data class OrderDetail(
    val orderno: String,
    val outorderno: String,
    val dockapiorderno: String?,
    val money: Double,
    val buynum: Int,
    val goodsprice: Double,
    val goodsid: Int,
    val status: Int, //订单状态 0=已付款 1=已提取 2=未付款 3=进行中 4=撤回 5=充值成功
    val refundmoney: Double,
    val refundstatus: Int, //0=未退款 1=已退款
    val payrefundspeed: Int,
    val banstatus: Int, //0=正常 1=禁用订单
    val mobile: String,
    val receipt: String,
    val create_time: Long,
    val update_time: Long,
)
