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

/**
 * @author Richard Stöckl
 *
 * Service to change passwords on an LDAP server.
 */
interface LdapUserPasswordManager {

    /**
     * Change the password of [username] with validating the old one.
     * @param username the username of the user whose password should be changed
     * @param oldPassword the old password to validate
     * @param newPassword the new password to use
     * @return 'true' if the validation succeed, 'false' otherwise
     */
    fun changePasswordChecked(username: String, oldPassword: String, newPassword: String): Boolean

    /**
     * Change the password of [username].
     * @param username the username of the user whose password should be changed
     * @param newPassword the new password to use
     */
    fun changePasswordUnchecked(username: String, newPassword: String)
}
