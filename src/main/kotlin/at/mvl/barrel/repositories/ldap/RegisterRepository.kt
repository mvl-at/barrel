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

import at.mvl.barrel.model.Register
import org.springframework.data.ldap.repository.LdapRepository
import org.springframework.data.ldap.repository.Query
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.ldap.query.LdapQuery
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.naming.Name

/**
 * @author Richard Stöckl
 *
 * Repository for managing registers.
 * Suited for public view as [Register] do not contain confidential data.
 */
@RepositoryRestResource
@PreAuthorize("hasRole(@roleMap.roles().registerManager)")
interface RegisterRepository : LdapRepository<Register> {

    @Query(base = "ou=Register,ou=Divisionen", value = "(objectClass=mvlGroup)")
    @PreAuthorize("permitAll()")
    override fun findAll(): List<Register>

    @Query(base = "ou=Register,ou=Divisionen", value = "(objectClass=mvlGroup)")
    @PreAuthorize("permitAll()")
    fun findRegisters(): List<Register>

    @PreAuthorize("permitAll()")
    override fun count(): Long

    @PreAuthorize("permitAll()")
    override fun existsById(id: Name): Boolean

    @PreAuthorize("permitAll()")
    override fun findAll(ldapQuery: LdapQuery): MutableIterable<Register>

    @PreAuthorize("permitAll()")
    override fun findAllById(names: MutableIterable<Name>): MutableList<Register>

    @PreAuthorize("permitAll()")
    override fun findById(id: Name): Optional<Register>

    @PreAuthorize("permitAll()")
    override fun findOne(ldapQuery: LdapQuery): Optional<Register>
}
