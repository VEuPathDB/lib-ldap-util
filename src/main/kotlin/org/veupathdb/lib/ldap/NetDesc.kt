package org.veupathdb.lib.ldap

enum class Platform {
    ORACLE, POSTGRES
}

interface NetDesc {
    val host: String
    val port: Int
    val identifier: String
    val platform: Platform
}
