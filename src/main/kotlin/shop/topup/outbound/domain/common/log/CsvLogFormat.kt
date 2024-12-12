package shop.topup.outbound.domain.common.log

import ch.qos.logback.classic.spi.ILoggingEvent
import org.apache.commons.text.StringEscapeUtils
import org.springframework.boot.logging.structured.StructuredLogFormatter
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

/**
 * csv log format: timestamp, level, group, name, server, user, remote, logger, message, pairs, stacktrace
 */
class CsvLogFormat : StructuredLogFormatter<ILoggingEvent> {
    override fun format(event: ILoggingEvent): String {
        val sb = StringBuilder()
        sb.append(event.instant).append(",")
        sb.append(event.level).append(",")
        // logger context, such as APPLICATION_NAME, APPLICATION_GROUP etc
        val loggerContext = event.loggerContextVO.propertyMap
        val applicationGroup = loggerContext["APPLICATION_GROUP"]
        if (applicationGroup != null) {
            sb.append(applicationGroup).append(",")
        } else {
            sb.append(",")
        }
        val applicationName = loggerContext["APPLICATION_NAME"]
        if (applicationName != null) {
            sb.append(applicationName).append(",")
        } else {
            sb.append(",")
        }
        sb.append(IP).append(",")
        // mdc context, such as user, traceId etc
        val mdc = event.mdcPropertyMap
        sb.append(mdc.getOrDefault("user", "")).append(",")
        sb.append(mdc.getOrDefault("remote", "")).append(",")
        sb.append(event.loggerName).append(",")
        // message
        sb.append(StringEscapeUtils.escapeCsv(event.message)).append(",")
        // key value pairs
        val keyValuePairs = event.keyValuePairs
        if (keyValuePairs != null && !keyValuePairs.isEmpty()) {
            val pairs = StringBuilder()
            pairs.append("{")
            for (keyValuePair in keyValuePairs) {
                pairs.append("\"").append(keyValuePair.key).append("\"").append(":")
                    .append("\"").append(keyValuePair.value).append("\"").append(",")
            }
            pairs.deleteCharAt(pairs.length - 1)
            pairs.append("}")
            sb.append(StringEscapeUtils.escapeCsv(pairs.toString()))
        }
        sb.append(",")
        // stack trace for exception
        val throwableProxy = event.throwableProxy
        if (throwableProxy != null) {
            val stackTrace = StringBuilder()
            for (stackTraceElementProxy in throwableProxy.stackTraceElementProxyArray) {
                stackTrace.append(stackTraceElementProxy.steAsString).append("\n")
            }
            sb.append(StringEscapeUtils.escapeCsv(stackTrace.toString()))
        }
        sb.append("\n")
        return sb.toString()
    }

    companion object {
        val IP: String = localHostLANAddress

        val localHostLANAddress: String
            get() {
                try {
                    var candidateAddress: InetAddress? = null
                    // Iterate all NICs (network interface cards)...
                    val ifaces: Enumeration<*> = NetworkInterface.getNetworkInterfaces()
                    while (ifaces.hasMoreElements()) {
                        val iface = ifaces.nextElement() as NetworkInterface
                        // Iterate all IP addresses assigned to each card...
                        val inetAddrs: Enumeration<*> = iface.inetAddresses
                        while (inetAddrs.hasMoreElements()) {
                            val inetAddr = inetAddrs.nextElement() as InetAddress
                            if (!inetAddr.isLoopbackAddress) {
                                if (inetAddr.isSiteLocalAddress) {
                                    // Found non-loopback site-local address. Return it immediately...
                                    return inetAddr.hostAddress
                                } else if (candidateAddress == null) {
                                    // Found non-loopback address, but not necessarily site-local.
                                    // Store it as a candidate to be returned if site-local address is not subsequently found...
                                    candidateAddress = inetAddr
                                    // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                                    // only the first. For subsequent iterations, candidate will be non-null.
                                }
                            }
                        }
                    }
                    if (candidateAddress != null) {
                        // We did not find a site-local address, but we found some other non-loopback address.
                        // Server might have a non-site-local address assigned to its NIC (or it might be running
                        // IPv6 which deprecates the "site-local" concept).
                        // Return this non-loopback candidate address...
                        return candidateAddress.hostAddress
                    }
                    // At this point, we did not find a non-loopback address.
                    // Fall back to returning whatever InetAddress.getLocalHost() returns...
                    val jdkSuppliedAddress = InetAddress.getLocalHost()
                    return jdkSuppliedAddress.hostAddress
                } catch (e: Exception) {
                    return "127.0.0.1"
                }
            }
    }
}
