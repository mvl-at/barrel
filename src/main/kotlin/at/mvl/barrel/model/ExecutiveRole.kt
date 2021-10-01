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

import org.springframework.ldap.odm.annotations.Attribute
import org.springframework.ldap.odm.annotations.Entry
import org.springframework.ldap.odm.annotations.Id
import javax.naming.Name
import javax.naming.ldap.LdapName

@Entry(
    base = "ou=Exekutive,ou=Divisionen",
    objectClasses = ["groupOfNames", "top"]
)
class ExecutiveRole(
    @Id
    var id: Name = LdapName("cn=nonexistent"),

    @Attribute(name = "cn") var name: String = "Voids",
    @Attribute(name = "cns") var nameSingular: String = "Void",
    @Attribute(name = "description") var description: String = "Empty group",

    @Attribute(name = "member") var members: List<String> = listOf()
)
