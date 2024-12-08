package shop.topup.outbound.demo

import com.github.database.rider.core.api.dataset.DataSet
import org.dbunit.database.DatabaseConnection
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.xml.FlatDtdDataSet
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import shop.topup.outbound.TestcontainersBaseTest
import java.io.FileOutputStream
import javax.sql.DataSource
import kotlin.jvm.Throws

@DataSet("/db/dataset/suppliers.xml")
class DataBaseTest : TestcontainersBaseTest() {
    @Autowired
    private lateinit var dataSource: DataSource

    @Test
    @Throws(Exception::class)
    fun generateDTD() {
        val connection: IDatabaseConnection = DatabaseConnection(dataSource.connection)
        FlatDtdDataSet.write(connection.createDataSet(), FileOutputStream("database.dtd"))
    }
}