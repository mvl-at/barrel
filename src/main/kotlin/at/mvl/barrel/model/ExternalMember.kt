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

package at.mvl.barrel.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.ldap.odm.annotations.Attribute
import org.springframework.ldap.odm.annotations.Entry
import org.springframework.ldap.odm.annotations.Id
import javax.naming.Name
import javax.naming.ldap.LdapName

@Entry(
    base = "ou=Mitglieder",
    objectClasses = ["person", "inetOrgPerson", "top"]
)
class ExternalMember(
    @Id @JsonIgnore var id: Name = LdapName("cn=nonexistent"),

    @Attribute(name = "uid") var username: String = "nobody",
    @Attribute(name = "givenName") var firstName: String = "Nobody",
    @Attribute(name = "sn") var lastName: String = "Isit",
    @Attribute(name = "cn") var name: String = "Nobody Isit",
    @Attribute(name = "active") var active: Boolean = true,
    @Attribute(name = "joining") var joining: Int = 1900,
    @Attribute(name = "gender") var gender: Char = '*',
    @Attribute(name = "title") var titles: List<String>? = null,

    @Attribute(name = "official") var official: Boolean? = null,
    @Attribute(name = "listed") var listed: Boolean? = null,
) : Comparable<ExternalMember> {
    override fun compareTo(other: ExternalMember): Int {
        if (joining < other.joining) return -1
        if (joining > other.joining) return 1

        if (lastName < other.lastName) return -1
        if (lastName > other.firstName) return 1

        if (firstName < other.firstName) return -1
        if (firstName > other.firstName) return 1
        return 0
    }
}
