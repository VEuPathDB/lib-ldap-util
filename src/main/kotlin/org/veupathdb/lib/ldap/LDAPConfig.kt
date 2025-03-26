package org.veupathdb.lib.ldap

data class LDAPConfig(val hosts: Collection<LDAPHost>, val baseDN: String)
