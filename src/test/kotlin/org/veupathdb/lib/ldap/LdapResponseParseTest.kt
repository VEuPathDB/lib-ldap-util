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
pgConnectionParam: host=dbserver.example.com
pgConnectionParam: port=5432
pgConnectionParam: dbname=mydbname
"""
        val desc = PostgresNetDesc(ldapResponse)
        Assertions.assertEquals("dbserver.example.com", desc.host)
        Assertions.assertEquals(5432.toUShort(), desc.port)
        Assertions.assertEquals("mydbname", desc.dbname)
    }

    //@Test
    fun oracleLookup() {
        val ldap = LDAP(LDAPConfig(listOf(LDAPHost.ofString("localhost:8389")), "ou=applications,dc=apidb,dc=org"))
        val response = ldap.requireSingularOracleNetDesc("toxo068n").toNetDesc()
        println(response.host + ":" + response.port + "/" + response.identifier + " (" + response.platform + ")")
    }

    //@Test
    fun postgresLookup() {
        val ldap = LDAP(LDAPConfig(listOf(LDAPHost.ofString("localhost:8389")), "ou=applications,dc=apidb,dc=org"))
        val response = ldap.requireSingularPostgresNetDesc("unidb").toNetDesc()
        println(response.host + ":" + response.port + "/" + response.identifier + " (" + response.platform + ")")
    }

    //@Test
    fun generalLookup() {
        val ldap = LDAP(LDAPConfig(listOf(LDAPHost.ofString("localhost:8389")), "ou=applications,dc=apidb,dc=org"))
        println(ldap.lookupNetDesc("toxo068n").toString());
        println(ldap.lookupNetDesc("unidb").toString());
    }
}
