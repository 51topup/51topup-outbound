package shop.topup.outbound

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistrar
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container

@TestConfiguration(proxyBeanMethods = false)
class TestContainersConfiguration {

    companion object {
        @JvmStatic
        @Container
        val natsServerContainer: GenericContainer<*> =
            GenericContainer("nats:2.10.22-alpine")
                .withExposedPorts(4222)
                .waitingFor(Wait.forListeningPorts(4222))
                .apply {
                    start()
                }
    }

    @Bean
    fun dynamicPropertyRegistrar(): DynamicPropertyRegistrar {
        return DynamicPropertyRegistrar { registry: DynamicPropertyRegistry ->
            with(registry) {
                add("nats.spring.server") {
                    val port = natsServerContainer.getMappedPort(4222)
                    val host = natsServerContainer.host
                    String.format("nats://%s:%d", host, port)
                }
            }
        }
    }

    @Bean
    @ServiceConnection("redis")
    fun redisContainer(): GenericContainer<*> {
        return GenericContainer("redis:7.4.1-alpine").withExposedPorts(6379)
    }

    @Bean
    @ServiceConnection
    fun postgresqlContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer("postgres:17.1")
            .withDatabaseName("topup")
            .withUsername("topup")
            .withPassword("1234456")
            .withExposedPorts(5432)
    }


}
