package org.veupathdb.lib.ldap

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("LDAPHost")
class LdapResponseParseTest {

    @Test
    @DisplayName("ofString(String)")
    fun t1() {
        val ldapResponse =
"""
dn: cn=unidb,cn=PostgresContext,ou=Applications,dc=apidb,dc=org
pgConnectionParam: host=mydb.example.com
pgConnectionParam: port=5432
pgConnectionParam: dbname=mydbname
"""
        val desc = PostgresNetDesc(ldapResponse)
        Assertions.assertEquals("mydb.example.com", desc.host)
        Assertions.assertEquals(5432, desc.port)
        Assertions.assertEquals("mydbname", desc.dbname)
    }

    @Test
    fun oracleLookup() {
        val ldap = LDAP(LDAPConfig(listOf(LDAPHost.ofString("localhost:8389")), "ou=applications,dc=apidb,dc=org"))
        println("Performing lookup of known oracle DB")
        println(formatConnectionProps(ldap.requireSingularOracleNetDesc("toxo068n")))
    }

    @Test
    fun postgresLookup() {
        val ldap = LDAP(LDAPConfig(listOf(LDAPHost.ofString("localhost:8389")), "ou=applications,dc=apidb,dc=org"))
        println("Performing lookup of known postgres DB")
        println(formatConnectionProps(ldap.requireSingularPostgresNetDesc("unidb")))
    }

    @Test
    fun generalLookup() {
        val ldap = LDAP(LDAPConfig(listOf(LDAPHost.ofString("localhost:8389")), "ou=applications,dc=apidb,dc=org"))
        println("Performing generic lookup of known oracle and postgres DBs")
        println(formatConnectionProps(ldap.requireSingularNetDesc("toxo068n")))
        println(formatConnectionProps(ldap.requireSingularNetDesc("unidb")))
    }

    private fun formatConnectionProps(netDesc: NetDesc): String {
        return netDesc.host + ":" + netDesc.port + "/" + netDesc.identifier + " (" + netDesc.platform + ")"
    }
}
