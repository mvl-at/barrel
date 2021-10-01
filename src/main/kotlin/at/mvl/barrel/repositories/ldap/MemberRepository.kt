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

package at.mvl.barrel.repositories.ldap

import at.mvl.barrel.model.Member
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.ldap.repository.LdapRepository
import org.springframework.data.ldap.repository.Query
import org.springframework.data.rest.core.annotation.Description
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.security.access.prepost.PreAuthorize

/**
 * @author Richard Stöckl
 *
 * Repository for accessing members.
 * Suited for management and therefore required authentication for all methods.
 * Exposes confidential user data such as addresses, emails and phone numbers.
 */
@RepositoryRestResource
@Tag(name = "Member Repository", description = "Repository for all MVL members with extended data support")
@PreAuthorize("hasRole(@roleMap.roles().memberManager)")
interface MemberRepository : LdapRepository<Member> {

    /**
     * Find a member by its username.
     * @param username the username to search for
     * @return the [Member] with the corresponding username or 'null' if such member does not exist
     */
    fun findByUsername(username: String): Member?

    /**
     * Find all members which are part of the music corps.
     * @return a list of all members which are part of the music corps
     */
    @Query(base = "ou=Musik,ou=Mitglieder", value = "(objectClass=inetOrgPerson)")
    @RestResource(path = "/musicians", exported = true, description = Description("All musicians of this musikverein"))
    fun findAllMusicians(): List<Member>
}
