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

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.ldap.odm.annotations.Attribute
import org.springframework.ldap.odm.annotations.Entry
import org.springframework.ldap.odm.annotations.Id
import java.time.LocalDate
import javax.naming.Name
import javax.naming.ldap.LdapName

@Entry(
    base = "ou=Mitglieder",
    objectClasses = ["person", "inetOrgPerson", "top"]
)
@Schema(name = "Member")
class Member(
    @Id
    @Schema(hidden = true)
    var id: Name = LdapName("cn=nonexistent"),

    @Attribute(name = "uid")
    @Schema(
        description = "The uid and username of this member, used in the DN",
        example = "oli"
    )
    var username: String = "nobody",

    @Attribute(name = "givenName")
    @Schema(
        description = "The first name of this member",
        example = "Oliver"
    )
    var firstName: String = "Nobody",

    @Attribute(name = "sn")
    @Schema(description = "The last name of this member", example = "Petschk")
    var lastName: String = "Isit",

    @Attribute(name = "cn")
    @Schema(
        description = "The composed, full name of this member, may be used for display",
        example = "Oliver Petschk"
    )
    var name: String = "Nobody Isit",

    @Attribute(name = "active")
    @Schema(
        description = "Whether this member is active or not",
        example = "true",
        defaultValue = "true"
    )
    var active: Boolean = true,

    @Attribute(name = "birthday")
    @Schema(
        description = "The date of birth of this member",
        example = "1999-02-01"
    )
    var birthday: LocalDate = LocalDate.now(),

    @Attribute(name = "joining")
    @Schema(
        description = "The year this member has joined",
        example = "2007"
    )
    var joining: Int = 1900,

    @Attribute(name = "gender")
    @Schema(
        description = "Gender of this member",
        example = "w", oneOf = []
    )
    var gender: Char = '*',

    @Attribute(name = "mail")
    @Schema(description = "Mail addresses of this member", example = "[\"o.p@example.org\"]")
    var mailAddresses: List<String> = listOf(),

    @Attribute(name = "title")
    @Schema(
        description = "All internal titles and functions belonging to this member",
        example = "[\"Beirat\", \"Ehrenkapellmeister\"]"
    )
    var titles: List<String>? = null,

    @Attribute(name = "mobile")
    @Schema(
        description = "All mobiles numbers of this member",
        example = "[\"+43 664 9182756\"]"
    )
    var mobiles: List<String> = listOf(),

    @Attribute(name = "c")
    @Schema(
        description = "Country code of the members address",
        example = "AT"
    )
    var countryCode: String? = null,

    @Attribute(name = "st")
    @Schema(
        description = "Province of the members address",
        example = "Niederösterreich"
    ) var province: String? = null,

    @Attribute(name = "postalCode")
    @Schema(
        description = "Postal code of the members address",
        example = "2285"
    )
    var postalCode: String? = null,

    @Attribute(name = "l")
    @Schema(
        description = "Locality/city of the members address",
        example = "Leopoldsdorf im Marchfelde"
    )
    var locality: String? = null,

    @Attribute(name = "street")
    @Schema(
        description = "Street of the members address",
        example = "Hauptplatz"
    )
    var street: String? = null,

    @Attribute(name = "houseIdentifier")
    @Schema(
        description = "House number of the members address",
        example = "1a"
    )
    var houseIdentifier: String? = null,

    @Attribute(name = "official")
    @Schema(
        description = "Whether this member is officially listed at the NÖBV or not",
        example = "true"
    )
    var official: Boolean? = null,

    @Attribute(name = "listed")
    @Schema(
        description = "Whether this member is listed at the website or not",
        example = "true"
    )
    var listed: Boolean? = null,

    @Attribute(name = "wa")
    @Schema(
        description = "Whether this member has whatsapp or not",
        example = "true"
    )
    var whatsapp: Boolean? = null,

    @Attribute(name = "memberOf")
    @Schema(
        description = "The groups where this member belongs to",
        example = "[]"
    )
    var groups: List<String> = listOf()
)
