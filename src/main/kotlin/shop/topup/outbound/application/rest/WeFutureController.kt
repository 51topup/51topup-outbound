package shop.topup.outbound.application.rest

import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.server.ServerWebExchange

@Controller
@RequestMapping("/wf")
class WeFutureController {

    @PostMapping(value = ["/order/callback"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    @ResponseBody
    suspend fun orderCallBack(serverWebExchange: ServerWebExchange): String {
        val formData: MultiValueMap<String, String> = serverWebExchange.formData.awaitFirst()
        val request = OrderCallbackRequest(
            orderno = formData.getFirst("orderno")!!,
            outorderno = formData.getFirst("outorderno")!!,
            userid = formData.getFirst("userid")!!,
            status = formData.getFirst("status")!!.toInt(),
            refundstatus = formData.getFirst("refundstatus")?.toInt() ?: 0,
            money = formData.getFirst("money")!!.toDouble(),
            refundmoney = formData.getFirst("refundmoney")?.toDouble() ?: 0.0,
            receipt = formData.getFirst("receipt"),
            refundreceipt = formData.getFirst("refundreceipt"),
            create_time = formData.getFirst("create_time")!!.toLong(),
            update_time = formData.getFirst("update_time")!!.toLong(),
            timestamp = formData.getFirst("timestamp")!!.toLong(),
            sign = formData.getFirst("sign")!!
        )
        print(request.orderno)
        return "ok"
    }
}

data class OrderCallbackRequest(
    val orderno: String,
    val outorderno: String,
    val userid: String,
    val status: Int,
    val refundstatus: Int,
    val money: Double,
    val refundmoney: Double = 0.0,
    val receipt: String?,
    val refundreceipt: String?,
    val create_time: Long,
    val update_time: Long,
    val timestamp: Long,
    val sign: String
)