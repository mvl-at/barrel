package at.mvl.barrel.repositories.ldap

import at.mvl.barrel.model.ExternalMember
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.ldap.repository.LdapRepository
import org.springframework.data.ldap.repository.Query
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.security.access.prepost.PreAuthorize
import javax.naming.Name

/**
 * @author Richard Stöckl
 *
 * Repository for accessing all members.
 * Suited for public view as this repository uses [ExternalMember].
 * This repository should not be used to modify resources, use [MemberRepository] for that purpose instead.
 */
@Tag(name = "External Member Repository", description = "Repository which allows limited access to member data")
interface ExternalMemberRepository : LdapRepository<ExternalMember> {

    /**
     * Finds all members which are 'listed'.
     *
     * @return a list of the listed members
     */
    @Query(base = "ou=Mitglieder", value = "(listed=TRUE)")
    override fun findAll(): MutableList<ExternalMember>

    /**
     * Find a member with the exact given username.
     *
     * @param username the username of the member
     * @return a member with the provided username or 'null' if such member does not exist
     */
    fun findByUsername(username: String): ExternalMember?

    /**
     * @see MemberRepository.save
     */
    @Deprecated(
        message = "Partial member view should not be used for resource modification.",
        replaceWith = ReplaceWith("MemberRepository.save"),
        level = DeprecationLevel.ERROR
    )
    @RestResource(exported = false)
    @PreAuthorize("false")
    override fun <S : ExternalMember?> save(entity: S): S

    /**
     * @see MemberRepository.saveAll
     */
    @Deprecated(
        message = "Partial member view should not be used for resource modification.",
        replaceWith = ReplaceWith("MemberRepository.saveAll"),
        level = DeprecationLevel.ERROR
    )
    @RestResource(exported = false)
    @PreAuthorize("false")
    override fun <S : ExternalMember?> saveAll(entities: MutableIterable<S>): MutableList<S>

    /**
     * @see MemberRepository.delete
     */
    @Deprecated(
        message = "Partial member view should not be used for resource modification.",
        replaceWith = ReplaceWith("MemberRepository.delete"),
        level = DeprecationLevel.ERROR
    )
    @RestResource(exported = false)
    @PreAuthorize("false")
    override fun delete(entity: ExternalMember)

    /**
     * @see MemberRepository.deleteAll
     */
    @Deprecated(
        message = "Partial member view should not be used for resource modification.",
        replaceWith = ReplaceWith("MemberRepository.deleteAll"),
        level = DeprecationLevel.ERROR
    )
    @RestResource(exported = false)
    @PreAuthorize("false")
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
    @PreAuthorize("false")
    override fun deleteAll(entities: MutableIterable<ExternalMember>)

    /**
     * @see MemberRepository.deleteAllById
     */
    @Deprecated(
        message = "Partial member view should not be used for resource modification.",
        replaceWith = ReplaceWith("MemberRepository.deleteAllById"),
        level = DeprecationLevel.ERROR
    )
    @RestResource(exported = false)
    @PreAuthorize("false")
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
    @PreAuthorize("false")
    override fun deleteById(id: Name)
}
