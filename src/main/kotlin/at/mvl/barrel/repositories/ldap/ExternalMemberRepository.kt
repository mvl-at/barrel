package at.mvl.barrel.repositories.ldap

import at.mvl.barrel.model.ExternalMember
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.ldap.repository.LdapRepository
import org.springframework.data.ldap.repository.Query
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.security.access.prepost.PreAuthorize
import javax.naming.Name

@Tag(name = "External Member Repository", description = "Repository which allows limited access to member data")
interface ExternalMemberRepository : LdapRepository<ExternalMember> {

    @Query(base = "ou=Mitglieder", value = "(listed=TRUE)")
    override fun findAll(): MutableList<ExternalMember>

    fun findByUsername(username: String): ExternalMember?

    @RestResource(exported = false)
    @PreAuthorize("false")
    override fun <S : ExternalMember?> save(entity: S): S

    @PreAuthorize("hasRole(@roleMap.roles().memberManager)")
    override fun delete(entity: ExternalMember)

    @PreAuthorize("hasRole(@roleMap.roles().memberManager)")
    override fun deleteAll(entities: MutableIterable<ExternalMember>)

    @PreAuthorize("hasRole(@roleMap.roles().memberManager)")
    override fun deleteAllById(ids: MutableIterable<Name>)

    @PreAuthorize("hasRole(@roleMap.roles().memberManager)")
    override fun deleteById(id: Name)
}
