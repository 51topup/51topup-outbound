package shop.topup.outbound.domain.service

import org.slf4j.Logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import shop.topup.admin.domain.common.jooq.tables.SupplierCatalog
import shop.topup.admin.domain.common.jooq.tables.SupplierGoods
import shop.topup.admin.domain.common.jooq.tables.records.SupplierCatalogRecord
import shop.topup.outbound.domain.common.dao.SupplierCatalogDAO
import shop.topup.outbound.domain.common.dao.SupplierDAO
import shop.topup.outbound.domain.common.dao.SupplierGoodsDAO


@Service
class WeFutureSyncService(
    val supplierDAO: SupplierDAO,
    val supplierCatalogDAO: SupplierCatalogDAO,
    val supplierGoodsDAO: SupplierGoodsDAO,
    val weFutureService: WeFutureService
) {
    val log: Logger = org.slf4j.LoggerFactory.getLogger(WeFutureSyncService::class.java)
    val supplierId: Long = 1

    @Scheduled(fixedRate = 60 * 60 * 6, initialDelay = 60 * 60 * 6) // 6 hours
    fun syncGroups() {
        val supplierApiInfo = supplierDAO.findApiInfo(supplierId)!!
        val catalogs = weFutureService.fetchGroups(supplierApiInfo.apiKey, supplierApiInfo.apiSecret)
        log.error("Sync ${catalogs.size} catalogs from 1 ")
        catalogs.forEach {
            var catalog = supplierCatalogDAO.findBySupplier(supplierId, it.groupid)
            if (catalog == null) {
                catalog = SupplierCatalog.SUPPLIER_CATALOG.newRecord().apply {
                    this.supplierId = supplierApiInfo.id
                    this.groupId = it.groupid
                }
            }
            catalog.name = it.groupname
            catalog.imgUrl = it.groupimgurl
            supplierCatalogDAO.save(catalog)
            log.info("Sync catalog: { id: ${catalog.groupId}, name:${catalog.name} }")
        }
    }

    @Scheduled(fixedRate = 60 * 60 * 6, initialDelay = 60 * 60 * 6) // 6 hours
    fun syncGoods() {
        val supplierApiInfo = supplierDAO.findApiInfo(supplierId)!!
        val catalogs: Map<Long, SupplierCatalogRecord> =
            supplierCatalogDAO.findAll(SupplierCatalog.SUPPLIER_CATALOG.SUPPLIER_ID.eq(supplierId))
                .associateBy { it.groupId }
        (0..33).forEach { i ->
            log.info("Sync goods: { page: $i }")
            val result = weFutureService.fetchItems(supplierApiInfo.apiKey, supplierApiInfo.apiSecret, i)
            log.info("Sync goods: { page: $i, count: ${result.count} }")
            result.data.forEach {
                var goods = supplierGoodsDAO.findBySupplier(supplierApiInfo.id, it.goodsid)
                if (goods == null) {
                    goods = SupplierGoods.SUPPLIER_GOODS.newRecord().apply {
                        this.supplierId = supplierId
                        this.supplierGoodsId = it.goodsid
                        this.supplierGroupId = it.goodsgroupid
                        this.type = it.goodstype
                        this.catalogId = catalogs[it.goodsgroupid]?.id ?: 0
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
            Thread.sleep(5000) // 5 seconds delay because 3 seconds delay required
        }
    }

}
