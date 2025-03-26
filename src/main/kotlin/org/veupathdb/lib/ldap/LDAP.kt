package org.veupathdb.lib.ldap

import com.unboundid.ldap.sdk.*
import org.slf4j.LoggerFactory

class LDAP(private val config: LDAPConfig) {

  private val log = LoggerFactory.getLogger(javaClass)

  object Constants {
    const val ORACLE_OBJECT_CLASS = "orclNetService"
    const val ORACLE_DESCRIPTION_KEY = "orclNetDescString"
    const val POSTGRES_OBJECT_CLASS = "PostgresServiceContext"
    const val POSTGRES_CONNECTION_PARAM_KEY = "pgConnectionParam"
  }

  private var ldapConnection: LDAPConnection? = null

  init {
    if (config.hosts.isEmpty())
      throw IllegalArgumentException("Passed the $javaClass constructor a config with 0 hosts entries")
  }

  fun requireSingularOracleNetDesc(commonName: String): OracleNetDesc {
    log.trace("requireSingularOracleNetDesc(commonName={})", commonName)
    return requireSingularNetDesc(lookupOracleNetDesc(commonName), "oracle net description", commonName)
  }

  fun lookupOracleNetDesc(commonName: String): List<OracleNetDesc> {
    log.trace("lookupOracleNetDesc(commonName={})", commonName)
    return performSearch(commonName, Constants.ORACLE_OBJECT_CLASS, Constants.ORACLE_DESCRIPTION_KEY)
      .map { OracleNetDesc(it.getAttribute(Constants.ORACLE_DESCRIPTION_KEY).value!!) }
  }

  fun requireSingularPostgresNetDesc(commonName: String): PostgresNetDesc {
    log.trace("requireSingularPostgresNetDesc(commonName={})", commonName)
    return requireSingularNetDesc(lookupPostgresNetDesc(commonName), "postgres net description", commonName)
  }

  fun lookupPostgresNetDesc(commonName: String): List<PostgresNetDesc> {
    log.trace("lookupPostgresNetDesc(commonName={})", commonName)
    return performSearch(commonName, Constants.POSTGRES_OBJECT_CLASS, Constants.POSTGRES_CONNECTION_PARAM_KEY)
      .map { PostgresNetDesc(it.getAttribute(Constants.POSTGRES_CONNECTION_PARAM_KEY).values!!) }
  }

  private fun performSearch(commonName: String, desiredObjectClass: String, desiredAttributeName: String): List<SearchResultEntry> {
    return getConnection()
      .search(SearchRequest(
        config.baseDN,
        SearchScope.SUB,
        Filter.createANDFilter(
          Filter.create("cn=$commonName"),
          Filter.create("objectClass=$desiredObjectClass")
        ),
        desiredAttributeName
      ))
      .searchEntries
  }

  fun requireSingularNetDesc(commonName: String): NetDesc {
    log.trace("requireSingularNetDesc(commonName={})", commonName)
    return requireSingularNetDesc(lookupNetDesc(commonName), "available db description", commonName)
  }

  fun lookupNetDesc(commonName: String): List<NetDesc> {
    return getConnection()
      .search(SearchRequest(
        config.baseDN,
        SearchScope.SUB,
        Filter.createANDFilter(
          Filter.create("cn=$commonName")
        )
      ))
      .searchEntries
      .map {
        val objClassValue = it.getAttribute("objectClass").values.first { value -> value != "top" }!!
        when(objClassValue) {
          Constants.POSTGRES_OBJECT_CLASS -> PostgresNetDesc(it.getAttribute(Constants.POSTGRES_CONNECTION_PARAM_KEY).values!!)
          Constants.ORACLE_OBJECT_CLASS -> OracleNetDesc(it.getAttribute(Constants.ORACLE_DESCRIPTION_KEY).value!!)
          else -> throw IllegalArgumentException("Object class $objClassValue is not supported.")
        }
      }
  }

  private fun getConnection(): LDAPConnection {
    log.trace("getConnection()")

    // Synchronized because this thing is gonna be called from who knows where.
    synchronized(this) {

      // If we've already got an LDAP connection
      if (ldapConnection != null) {

        // If the LDAP connection we've already got is still connected
        if (ldapConnection!!.isConnected)
        // then return it
          return ldapConnection!!
        // else, the LDAP connection we've already got is _not_ still connected
        else
        // then disregard it
          ldapConnection = null
      }

      log.debug("Attempting to establish a connection to a configured LDAP server")
      for (host in config.hosts) {
        log.trace("Trying to connect to {}:{}", host.host, host.port)

        try {
          ldapConnection =LDAPConnection(host.host, host.port.toInt())
            .also { log.debug("Connected to {}:{}", host.host, host.port) }
          break
        } catch (e: Throwable) {
          log.debug("Failed to connect to {}:{}", host.host, host.port)
        }
      }

      if (ldapConnection == null) {
        log.error("Failed to establish a connection to any configured LDAP server.")
        throw RuntimeException("Failed to establish a connection to any configured LDAP server.")
      }

      return ldapConnection!!
    }
  }

  private fun <T> requireSingularNetDesc(netDesc: List<T>, descName: String, commonName: String): T {
    if (netDesc.isEmpty())
      throw IllegalArgumentException("no ${descName}s found for common name $commonName")
    if (netDesc.size > 1)
      throw IllegalArgumentException("multiple ${descName}s found for common name $commonName")

    return netDesc[0]
  }
}
