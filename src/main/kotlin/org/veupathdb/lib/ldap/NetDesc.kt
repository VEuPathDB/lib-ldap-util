package org.veupathdb.lib.ldap

enum class Platform {
    ORACLE, POSTGRES
}

interface NetDesc {
    val host: String
    val port: UShort
    val identifier: String
    val platform: Platform
}
