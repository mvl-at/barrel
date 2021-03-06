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

import at.mvl.barrel.model.MemberPhoto
import org.springframework.data.ldap.repository.LdapRepository
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.security.access.prepost.PreAuthorize
import javax.naming.Name

/**
 * @author Richard Stöckl
 *
 * Repository for accessing member photos.
 * Suited for public view as this repository uses [MemberPhoto].
 * This repository should not be used to delete resources, use [MemberRepository] for that purpose instead.
 */
interface MemberPhotoRepository : LdapRepository<MemberPhoto> {

    fun findByUsername(username: String): MemberPhoto?

    @PreAuthorize("hasRole(@roleMap.roles().memberManager)")
    override fun <S : MemberPhoto?> save(entity: S): S

    @PreAuthorize("hasRole(@roleMap.roles().memberManager)")
    override fun <S : MemberPhoto?> saveAll(entities: MutableIterable<S>): MutableList<S>

    /**
     * @see MemberRepository.delete
     */
    @Deprecated(
        message = "Partial member view should not be used for resource modification.",
        replaceWith = ReplaceWith("MemberRepository.delete"),
        level = DeprecationLevel.ERROR
    )
    @RestResource(exported = false)
    @PreAuthorize("denyAll()")
    override fun delete(entity: MemberPhoto)

    /**
     * @see MemberRepository.deleteAll
     */
    @Deprecated(
        message = "Partial member view should not be used for resource modification.",
        replaceWith = ReplaceWith("MemberRepository.deleteAll"),
        level = DeprecationLevel.ERROR
    )
    @RestResource(exported = false)
    @PreAuthorize("denyAll()")
    override fun deleteAll()

    /**
     * @see MemberRepository.deleteAll
     */
    @Deprecated(
        message = "Partial member view should not be used for resource modification.",
        replaceWith = ReplaceWith("MemberRepository.deleteAll"),
        level = DeprecationLevel.ERROR
    )
    @RestResource(exported = false)
    @PreAuthorize("denyAll()")
    override fun deleteAll(entities: MutableIterable<MemberPhoto>)

    /**
     * @see MemberRepository.deleteAllById
     */
    @Deprecated(
        message = "Partial member view should not be used for resource modification.",
        replaceWith = ReplaceWith("MemberRepository.deleteAllById"),
        level = DeprecationLevel.ERROR
    )
    @RestResource(exported = false)
    @PreAuthorize("denyAll()")
    override fun deleteAllById(ids: MutableIterable<Name>)

    /**
     * @see MemberRepository.deleteById
     */
    @Deprecated(
        message = "Partial member view should not be used for resource modification.",
        replaceWith = ReplaceWith("MemberRepository.deleteById"),
        level = DeprecationLevel.ERROR
    )
    @RestResource(exported = false)
    @PreAuthorize("denyAll()")
    override fun deleteById(id: Name)
}
