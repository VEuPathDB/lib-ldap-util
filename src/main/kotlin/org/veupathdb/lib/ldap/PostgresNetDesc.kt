package org.veupathdb.lib.ldap

data class PostgresNetDesc(
    override val host: String,
    override val port: UShort,
    val dbname: String
): NetDesc {
    constructor(ldapResponse: String) : this(
        requireValue(ldapResponse, "host") { it },
        requireValue(ldapResponse, "port") { it.toUShort() },
        requireValue(ldapResponse, "dbname") { it }
    )
    constructor(propertyStrings: Array<String>) : this(
        requireValue(propertyStrings, "host") { it },
        requireValue(propertyStrings, "port") { it.toUShort() },
        requireValue(propertyStrings, "dbname") { it }
    )
    override val identifier: String = dbname
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
        .filter { it.size > 1 && it[0] == (LDAP.Constants.postgresConnectionParamKey + ":") }
        .map { it[1].split("=") }
        .filter { it.size > 1 && it[0] == propName }
        .map { converter(it[1]) }
        .first()
}
