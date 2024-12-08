package shop.topup.outbound.domain.common.dao

import ch.martinelli.oss.jooqspring.JooqDAO
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import shop.topup.admin.domain.common.jooq.tables.Supplier
import shop.topup.admin.domain.common.jooq.tables.SupplierApiInfo
import shop.topup.admin.domain.common.jooq.tables.SupplierCatalog
import shop.topup.admin.domain.common.jooq.tables.SupplierGoods
import shop.topup.admin.domain.common.jooq.tables.records.SupplierApiInfoRecord
import shop.topup.admin.domain.common.jooq.tables.records.SupplierCatalogRecord
import shop.topup.admin.domain.common.jooq.tables.records.SupplierGoodsRecord
import shop.topup.admin.domain.common.jooq.tables.records.SupplierRecord

@Repository
class SupplierDAO(@Autowired dslContext: DSLContext) :
    JooqDAO<Supplier, SupplierRecord, Long>(dslContext, Supplier.SUPPLIER) {

    fun findApiInfo(supplierId: Long): SupplierApiInfoRecord? {
        return dslContext
            .selectFrom(SupplierApiInfo.SUPPLIER_API_INFO)
            .where(SupplierApiInfo.SUPPLIER_API_INFO.ID.eq(supplierId))
            .fetchOne()
    }

}

@Repository
class SupplierCatalogDAO(@Autowired dslContext: DSLContext) :
    JooqDAO<SupplierCatalog, SupplierCatalogRecord, Long>(dslContext, SupplierCatalog.SUPPLIER_CATALOG) {

    fun findBySupplier(supplierId: Long, supplierGroupId: Long): SupplierCatalogRecord? {
        return dslContext.selectFrom(SupplierCatalog.SUPPLIER_CATALOG)
            .where(
                SupplierCatalog.SUPPLIER_CATALOG.SUPPLIER_ID.eq(supplierId),
                SupplierCatalog.SUPPLIER_CATALOG.GROUP_ID.eq(supplierGroupId)
            )
            .fetchOne()
    }

}

@Repository
class SupplierGoodsDAO(@Autowired dslContext: DSLContext) :
    JooqDAO<SupplierGoods, SupplierGoodsRecord, Long>(dslContext, SupplierGoods.SUPPLIER_GOODS) {


    fun findBySupplier(supplerId: Long, goodsId: Long): SupplierGoodsRecord? {
        return dslContext.selectFrom(SupplierGoods.SUPPLIER_GOODS)
            .where(
                SupplierGoods.SUPPLIER_GOODS.SUPPLIER_ID.eq(supplerId),
                SupplierGoods.SUPPLIER_GOODS.SUPPLIER_GOODS_ID.eq(goodsId)
            )
            .fetchOne()
    }
}