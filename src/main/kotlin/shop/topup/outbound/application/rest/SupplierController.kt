package shop.topup.outbound.application.rest

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import shop.topup.outbound.domain.service.WeFutureSyncService

@RestController
@RequestMapping("/supplier")
class SupplierController(val weFutureSyncService: WeFutureSyncService) {
    @RequestMapping("/syncWf")
    fun syncWf(): String {
        weFutureSyncService.syncGroups()
        weFutureSyncService.syncGoods()
        return "success"
    }
}