package org.veupathdb.lib.ldap

data class LDAPHost(val host: String, val port: UShort) {
  companion object {
    @JvmStatic
    fun ofString(str: String): LDAPHost {
      val colonIndex = str.indexOf(':')

      if (colonIndex < 1)
        throw IllegalArgumentException("input string $str did not resemble a valid \"host:port\" string")

      return LDAPHost(str.substring(0, colonIndex), str.substring(colonIndex + 1).toUShort())
    }
  }
}
