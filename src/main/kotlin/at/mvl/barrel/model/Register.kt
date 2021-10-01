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
    base = "ou=Divisionen",
    objectClasses = ["mvlGroup", "groupOfNames", "top"]
)
class Register(
    @Id
    @JsonIgnore
    var id: Name = LdapName("cn=nonexistent"),

    @Attribute(name = "cn") var name: String = "Nobodies",
    @Attribute(name = "cns") var nameSingular: String = "Nobody",

    @Attribute(name = "member") @JsonIgnore var allMembers: List<String> = listOf(),
    @Transient var members: MutableList<ExternalMember>? = null
) : Comparable<Register> {
    override fun compareTo(other: Register): Int {
        if (name < other.name) return -1
        if (name > other.name) return 1
        return 0
    }
}
