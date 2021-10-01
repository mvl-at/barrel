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

package at.mvl.barrel.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "barrel")
data class BarrelConfigurationProperties(
    /** The LDAP configuration for mapping users and groups. */
    val ldap: LdapConfiguration = LdapConfiguration(),
    /** Role mappings. */
    val roles: RoleMappingConfiguration = RoleMappingConfiguration(),
    /** Security configuration. */
    val security: SecurityConfiguration = SecurityConfiguration()
) {
    data class LdapConfiguration(
        /** Search base where to start finding users. */
        val userSearchBase: String = "ou=Mitglieder",
        /** The search filter for users. */
        val userSearchFilter: String = "(uid={0})",
        /** Search base where to start finding groups/roles. */
        val groupSearchBase: String = "ou=Exekutive,ou=Divisionen",
        /** Group entry attribute which identifies user entries (their DN). */
        val groupSearchFilter: String = "(member={0})",
        /** Attribute to use for mapping the group name. */
        val groupRoleAttribute: String = "cn",
        /** Whether to perform a search over the whole subtree or not for groups. */
        val groupSearchSubtree: Boolean = true
    )

    data class RoleMappingConfiguration(
        /** Role for accessing members. */
        val memberAccess: String = "MITGLIEDVALIDIERER",
        /** Role for managing members. */
        val memberManager: String = "MITGLIEDVERWALTER",
        /** Role for accessing registers. */
        val registerAccess: String = "REGISTERVALIDIERER",
        /** Role for managing registers. */
        val registerManager: String = "REGISTERVERWALTER",
        /** Role for managing the archive. */
        val archive: String = "ARCHIVAR"
    )

    data class SecurityConfiguration(
        /** Jwt configuration. */
        val jwt: JwtConfiguration = JwtConfiguration(),
        /** Absolute paths for login (JwtAuthenticationFilter) */
        val loginPath: String = "/selfservice/login"
    ) {
        data class JwtConfiguration(
            /** Expiration delta in minutes. */
            val expiration: Long = 10,
            /** Expiration delta of the renewal token in minutes. */
            val expirationRenewal: Long = 60 * 24 * 7,
            /** Issuer of the token. */
            val issuer: String = "Barrel",
            /** Secret of the token to sign with. */
            val secret: String = "pleasereplaceme",
            /** Attribute in the token to check if it is a renewal token or not. */
            val renewalAttribute: String = "ren",
            /** Attribute name to indicate the last password change. */
            val passwordAttribute: String = "pwdd",
            /** Prefix to use for the token. */
            val prefix: String = "Bearer"
        )
    }
}
