package shop.topup.outbound.domain.service

import com.github.database.rider.core.api.dataset.DataSet
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import shop.topup.admin.domain.common.jooq.tables.SupplierCatalog
import shop.topup.admin.domain.common.jooq.tables.SupplierGoods
import shop.topup.outbound.domain.common.dao.SupplierCatalogDAO
import shop.topup.outbound.domain.common.dao.SupplierGoodsDAO
import shop.topup.workspace.TestcontainersBaseTest

@DataSet("/db/dataset/suppliers.xml")
class WeFutureSyncServiceTest : TestcontainersBaseTest() {

    @Autowired
    lateinit var weFutureSyncService: WeFutureSyncService

    @Autowired
    lateinit var catalogDAO: SupplierCatalogDAO

    @Autowired
    lateinit var supplierGoodsDAO: SupplierGoodsDAO

    @Test
    fun testSyncGroups() {
        weFutureSyncService.syncGroups()
        catalogDAO.findAll(SupplierCatalog.SUPPLIER_CATALOG.SUPPLIER_ID.eq(1)).forEach {
            println(it.name + " " + it.groupId)
        }
    }

    @Test
    fun testSyncGoods() {
        weFutureSyncService.syncGoods()
        supplierGoodsDAO.findAll(SupplierGoods.SUPPLIER_GOODS.SUPPLIER_ID.eq(1)).forEach {
            println(it.name + " " + it.supplierGoodsId)
        }
    }
}