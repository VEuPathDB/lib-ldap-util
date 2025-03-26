package org.veupathdb.lib.ldap

import java.math.BigInteger

/*
 * The goal of this class is to store the values needed to construct an
 * Oracle DB connection string via direct setting (data class constructor)
 * or using the Oracle OCI value stored in LDAP, e.g.
 *
 * DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=mydb.example.com)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=myservicename)))
 */

private const val HOST_PREFIX = "(HOST="
private const val PORT_PREFIX = "(PORT="
private const val SERVICE_NAME_PREFIX = "(SERVICE_NAME="
private const val VALUE_SUFFIX = ')'

data class OracleNetDesc(
  override val host: String,
  override val port: Int,
  val serviceName: String
): NetDesc {
  constructor(string: String) : this(
    string.requireHostValue(),
    string.requirePortValue(),
    string.requireServiceNameValue(),
  )
  override val identifier: String get() = serviceName
  override val platform: Platform = Platform.ORACLE
}

private fun String.requireHostValue() = requireValue(HOST_PREFIX, "HOST")

private fun String.requirePortValue(): Int {
  val bi = try {
    BigInteger(requireValue(PORT_PREFIX, "PORT"))
  } catch (e: Throwable) {
    throw IllegalArgumentException("given orclNetDescString contained an invalid PORT value")
  }

  if (bi > BigInteger.valueOf(65535))
    throw IllegalArgumentException("given orclNetDescString contained a PORT value that was too large to be a valid port")
  if (bi < BigInteger.ZERO)
    throw IllegalArgumentException("given orclNetDescString contained a PORT value that was less than zero")

  return bi.toInt()
}

private fun String.requireServiceNameValue(): String = requireValue(SERVICE_NAME_PREFIX, "SERVICE_NAME")

/**
 * Looks for the passed prefix in this String, reads the following characters
 * until it sees VALUE_SUFFIX, and returns them.  If the string does not
 * contain a value in this format, an exception is thrown using the name argument.
 */
private fun String.requireValue(prefix: String, name: String): String {
  val start = indexOf(prefix)

  if (start < 0)
    throw IllegalArgumentException("given orclNetDescString did not contain a $name value")

  val end = indexOf(VALUE_SUFFIX, start)

  if (end < 0)
    throw IllegalArgumentException("malformed orclNetDescString value")

  val out = substring(start + prefix.length, end)

  if (out.isEmpty())
    throw IllegalArgumentException("given orclNetDescString contained an empty $name value")

  return out
}
