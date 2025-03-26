package org.veupathdb.lib.ldap

data class PostgresNetDesc(
    override val host: String,
    override val port: Int,
    val dbname: String
): NetDesc {
    constructor(ldapResponse: String) : this(
        requireValue(ldapResponse, "host") { it },
        requireValue(ldapResponse, "port") { it.toInt() },
        requireValue(ldapResponse, "dbname") { it }
    )
    constructor(propertyStrings: Array<String>) : this(
        requireValue(propertyStrings, "host") { it },
        requireValue(propertyStrings, "port") { it.toInt() },
        requireValue(propertyStrings, "dbname") { it }
    )
    override val identifier: String get() = dbname
    override val platform: Platform = Platform.POSTGRES
}

fun <T> requireValue(propertyStrings: Array<String>, propName: String, converter: (s: String) -> T): T {
    return propertyStrings
        .map { it.split("=") }
        .filter {it.size > 1 && it[0] == propName }
        .map { converter(it[1]) }
        .first()
}

fun <T> requireValue(ldapResponse: String, propName: String, converter: (s: String) -> T): T {
    return ldapResponse.splitToSequence("\n")
        .map { it.split(" ") }
        .filter { it.size > 1 && it[0] == (LDAP.Constants.POSTGRES_CONNECTION_PARAM_KEY + ":") }
        .map { it[1].split("=") }
        .filter { it.size > 1 && it[0] == propName }
        .map { converter(it[1]) }
        .first()
}
