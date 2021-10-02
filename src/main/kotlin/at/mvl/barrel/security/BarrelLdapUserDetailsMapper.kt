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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper

/**
 * @author Richard Stöckl
 *
 * An [LdapUserDetailsMapper] which protects the password attribute.
 */
class BarrelLdapUserDetailsMapper : LdapUserDetailsMapper() {

    private val logger: Logger = LoggerFactory.getLogger(BarrelLdapUserDetailsMapper::class.java)

    /**
     * Do not map the password.
     *
     * @param passwordValue the password to omit
     * @return 'null'
     */
    override fun mapPassword(passwordValue: Any?): String? {
        logger.trace("mapPassword([PROTECTED])")
        return null
    }
}
