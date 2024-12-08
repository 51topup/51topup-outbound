package shop.topup.outbound.domain.common.dao

import com.github.database.rider.core.api.dataset.DataSet
import org.springframework.beans.factory.annotation.Autowired
import shop.topup.outbound.TestcontainersBaseTest
import kotlin.test.Test

@DataSet("/db/dataset/suppliers.xml")
class SupplierDAOTest : TestcontainersBaseTest() {
    @Autowired
    private lateinit var supplierDAO: SupplierDAO

    @Test
    fun testFindById() {
        val supplier = supplierDAO.findById(1)
        println(supplier)
    }
}