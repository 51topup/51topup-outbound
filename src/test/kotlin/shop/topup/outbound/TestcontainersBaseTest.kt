package shop.topup.outbound

import com.github.database.rider.core.api.configuration.DBUnit
import com.github.database.rider.core.api.configuration.Orthography
import com.github.database.rider.spring.api.DBRider
import org.springframework.context.annotation.Import
import org.testcontainers.junit.jupiter.Testcontainers

@Import(TestContainersConfiguration::class)
@Testcontainers
@DBRider
@DBUnit(schema = "public", caseInsensitiveStrategy = Orthography.LOWERCASE)
abstract class TestcontainersBaseTest : ProjectBaseTestCase()
