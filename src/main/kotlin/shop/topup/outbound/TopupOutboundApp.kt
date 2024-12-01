package shop.topup.outbound

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TopupOutboundApp

fun main(args: Array<String>) {
    runApplication<TopupOutboundApp>(*args)
}
