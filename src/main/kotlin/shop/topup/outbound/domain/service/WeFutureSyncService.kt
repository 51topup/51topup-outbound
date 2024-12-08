package shop.topup.outbound.domain.service

import org.jooq.DSLContext
import org.slf4j.Logger
import org.springframework.stereotype.Service
import shop.topup.admin.domain.common.jooq.tables.SupplierCatalog
import shop.topup.admin.domain.common.jooq.tables.SupplierGoods
import shop.topup.outbound.domain.common.dao.SupplierCatalogDAO
import shop.topup.outbound.domain.common.dao.SupplierGoodsDAO


@Service
class WeFutureSyncService(
    val dslContext: DSLContext,
    val supplierCatalogDAO: SupplierCatalogDAO,
    val supplierGoodsDAO: SupplierGoodsDAO,
    val weFutureService: WeFutureService
) {
    val sellerId = "35132"
    val secret = "831f54f132b2542016fca10cfe7cec2b"
    val log: Logger = org.slf4j.LoggerFactory.getLogger(WeFutureSyncService::class.java)

    fun syncGroups() {
        val catalogs = weFutureService.fetchGroups(sellerId, secret)
        log.error("Sync ${catalogs.size} catalogs from 1 ")
        catalogs.subList(0, 3).forEach {
            var catalog = supplierCatalogDAO.findBySupplier(1, it.groupid)
            if (catalog == null) {
                catalog = SupplierCatalog.SUPPLIER_CATALOG.newRecord().apply {
                    this.supplierId = 1
                    this.groupId = it.groupid
                }
            }
            catalog.name = it.groupname
            catalog.imgUrl = it.groupimgurl
            supplierCatalogDAO.save(catalog)
            log.info("Sync catalog: { id: ${catalog.groupId}, name:${catalog.name} }")
        }
    }

    fun syncGoods() {
        (0..33).forEach { i ->
            log.info("Sync goods: { page: $i }")
            val result = weFutureService.fetchItems(sellerId, secret, i)
            log.info("Sync goods: { page: $i, count: ${result.count} }")
            result.data.forEach {
                var goods = supplierGoodsDAO.findBySupplier(1, it.goodsid)
                if (goods == null) {
                    goods = SupplierGoods.SUPPLIER_GOODS.newRecord().apply {
                        this.supplierId = 1
                        this.supplierGoodsId = it.goodsid
                    }
                }
                goods.name = it.goodsname
                goods.mainPic = it.imgurl
                goods.price = it.goodsprice
                goods.status = it.goodsstatus
                goods.stock = it.stock
                goods.buyMinNum = it.buyminnum
                supplierGoodsDAO.save(goods)
                log.info("Sync goods: { id: ${goods.supplierGoodsId}, name:${goods.name} }")
            }
        }
    }

}
