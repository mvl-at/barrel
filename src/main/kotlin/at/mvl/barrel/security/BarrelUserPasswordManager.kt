/*
 * Barrel, the backend of the Musikverein Leopoldsdorf.
 * Copyright (C) 2021  Richard Stöckl
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package at.mvl.barrel.security

import at.mvl.barrel.repositories.ldap.ExternalMemberRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ldap.NamingException
import org.springframework.ldap.OperationNotSupportedException
import org.springframework.ldap.core.LdapTemplate
import org.springframework.stereotype.Service
import java.util.stream.IntStream
import javax.naming.ldap.ExtendedRequest
import javax.naming.ldap.ExtendedResponse
import javax.naming.ldap.LdapContext

/**
 * @author Richard Stöckl
 *
 * An implementation of [LdapUserPasswordManager] which requires the [LDAP Password Modify Extended Operation](https://tools.ietf.org/html/rfc3062) enabled on the LDAP server.
 * @param ldapTemplate the LDAP template where to validate and change passwords
 * @param externalMemberRepository the repository where to fetch members from
 *
 */
@Service
class BarrelUserPasswordManager(
    @Autowired private val ldapTemplate: LdapTemplate,
    @Autowired private val externalMemberRepository: ExternalMemberRepository
) : LdapUserPasswordManager {

    private val logger: Logger = LoggerFactory.getLogger(BarrelUserPasswordManager::class.java)

    /**
     * Request a password modification for [username] with providing the old password.
     * Can also be used by normal users as far as enabled by the LDAP server.
     * @param username the username of the user whose password should be changed
     * @param oldPassword the old password to validate
     * @param newPassword the new password to use
     * @return 'true' if the validation succeed, 'false' otherwise
     * @throws IllegalArgumentException if no user with [username] can be found
     */
    override fun changePasswordChecked(username: String, oldPassword: String, newPassword: String): Boolean {
        logger.trace("changePassword({},[PROTECTED],[PROTECTED])", username)
        try {
            changePassword(PasswordModifyRequest(fullDn(username), oldPassword, newPassword))
        } catch (e: OperationNotSupportedException) {
            logger.info("Failed to change password due to wrong credentials: {}", e.message)
            return false
        }
        return true
    }

    /**
     * Request a password modification for [username] without providing the old password.
     * Can only be used by admins.
     * @param username the username of the user whose password should be changed
     * @param newPassword the new password to use
     * @throws IllegalArgumentException if no user with [username] can be found
     */
    override fun changePasswordUnchecked(username: String, newPassword: String) {
        logger.trace("changePassword({},[PROTECTED])", username)
        changePassword(PasswordModifyRequest(fullDn(username), newPassword))
    }

    /**
     * Perform a [passwordModifyRequest] on the LDAP server.
     * @param passwordModifyRequest the constructed [PasswordModifyRequest]
     */
    private fun changePassword(passwordModifyRequest: PasswordModifyRequest) {
        logger.trace("changePassword({})", passwordModifyRequest)
        ldapTemplate.executeReadWrite {
            if (it !is LdapContext) {
                logger.error("DirContext is no LdapContext, cannot run an extension request")
                return@executeReadWrite
            }
            val exResp = it.extendedOperation(passwordModifyRequest)
            logger.debug("Change password request returned result: {}", exResp)
        }
    }

    /**
     * Fetch the full DN from [username].
     * @param username the username which is mapped to the DN
     * @return the full DN of [username]
     * @throws IllegalArgumentException if no user with [username] can be found
     */
    private fun fullDn(username: String): String {
        logger.trace("fullDn({})", username)
        val member = externalMemberRepository.findByUsername(username)
        if (member == null) {
            logger.info("Cannot find user: {}", username)
            throw IllegalArgumentException("Invalid username")
        }
        try {
            return ldapTemplate.lookupContext(member.id).nameInNamespace
        } catch (e: NamingException) {
            logger.warn(
                "Found user {} with RDN {} in the repository but not in the LDAP template: {}",
                username,
                member.id,
                e.message
            )
            throw IllegalArgumentException("Cannot find user {} in the LDAP template", e)
        }
    }
}

private const val sequenceType: Byte = 48
private const val oid = "1.3.6.1.4.1.4203.1.11.1"
private const val userIdentityOctetType: Byte = -0x80
private const val oldPasswordOctetType: Byte = -0x7f
private const val newPasswordOctetType: Byte = -0x7e

/**
 * @author Richard Stöckl
 * An implementation of the [LDAP Password Modify Extended Operation](https://tools.ietf.org/html/rfc3062) client request.
 * The LDAP Server must support this extension.
 * This implementation does not support generating passwords as described by the extension.
 */
class PasswordModifyRequest : ExtendedRequest {

    private val dataList: MutableList<Byte> = mutableListOf()

    /**
     * Constructs a password modification request without providing the old password.
     * Can only be used by admins.
     * @param dn the full DN of the user whose password should be changed
     * @param newPassword the new password
     */
    constructor(dn: String, newPassword: String) {
        val structure = mutableListOf<Byte>()
        berEncode(userIdentityOctetType, dn.toByteArray(), structure)
        berEncode(newPasswordOctetType, newPassword.toByteArray(), structure)
        berEncode(sequenceType, structure.toByteArray(), dataList)
    }

    /**
     * Constructs a password modification request with providing the old password.
     * Can also be used by normal users as far as enabled by the LDAP server.
     * @param dn the full DN of the user whose password should be changed
     * @param oldPassword the old password which will be checked
     * @param newPassword the new password
     */
    constructor(dn: String, oldPassword: String, newPassword: String) {
        val structure = mutableListOf<Byte>()
        berEncode(userIdentityOctetType, dn.toByteArray(), structure)
        berEncode(oldPasswordOctetType, oldPassword.toByteArray(), structure)
        berEncode(newPasswordOctetType, newPassword.toByteArray(), structure)
        berEncode(sequenceType, structure.toByteArray(), dataList)
    }

    /**
     * The OID of this extension.
     * @return the OID
     */
    override fun getID(): String {
        return oid
    }

    /**
     * Encode this request.
     * Will always be the same as this request is immutable.
     * @return the encoded password modify request
     */
    override fun getEncodedValue(): ByteArray {
        return dataList.toByteArray()
    }

    override fun createExtendedResponse(oid: String?, data: ByteArray?, p2: Int, p3: Int): ExtendedResponse {
        return object : ExtendedResponse {
            override fun getID(): String? {
                return oid
            }

            override fun getEncodedValue(): ByteArray? {
                return data
            }

            override fun toString(): String {
                return "ExtendedResponse[oid=$oid,data=$data,$p2,$p3]"
            }
        }
    }

    /**
     * BER Encode a field [type], length and its [data] to [dataList].
     * Only implements a required subset and is *not* fully compatible to the [X.690](https://www.itu.int/rec/T-REC-X.690/) encodings specified by the ITU-T.
     * @param type the data type to encode
     * @param data the data/value to encode
     * @param dataList the target where to encode/append to
     */
    private fun berEncode(type: Byte, data: ByteArray, dataList: MutableList<Byte>) {
        dataList.add(type)
        val length = data.size
        if (length < 0x80) {
            dataList.add(length.toByte())
        } else {
            val extensions = length / 0x80
            IntStream.iterate(extensions) { i -> i - 1 }.limit(extensions.toLong())
                .mapToObj { arrayOf((0x80 + it).toByte(), ((length shr (8 * (it - 1))) and 0xff).toByte()) }
                .forEachOrdered { dataList.addAll(it) }
        }
        dataList.addAll(data.asIterable())
    }
}
