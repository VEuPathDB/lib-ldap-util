package org.veupathdb.lib.ldap

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("LDAPHost")
class LDAPHostTest {

  @Test
  @DisplayName("ofString(String)")
  fun t1() {
    val tgt = LDAPHost.ofString("something:1234")

    assertEquals("something", tgt.host)
    assertEquals(1234.toUShort(), tgt.port)
  }
}