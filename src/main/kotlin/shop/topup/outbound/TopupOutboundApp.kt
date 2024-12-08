package shop.topup.outbound

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TopupOutboundApp

fun main(args: Array<String>) {
    runApplication<TopupOutboundApp>(*args)
}
