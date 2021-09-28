package at.mvl.barrel.repositories.ldap

import at.mvl.barrel.model.Member
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.ldap.repository.LdapRepository
import org.springframework.data.ldap.repository.Query
import org.springframework.data.rest.core.annotation.Description
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.security.access.prepost.PreAuthorize

@RepositoryRestResource
@Tag(name = "Member Repository", description = "Repository for all MVL members with extended data support")
@PreAuthorize("hasRole(@roleMap.roles().memberManager)")
interface MemberRepository : LdapRepository<Member> {

    fun findByUsername(username: String): Member?

    @Query(base = "ou=Musik,ou=Mitglieder", value = "(objectClass=inetOrgPerson)")
    @RestResource(path = "/musicians", exported = true, description = Description("All musicians of this musikverein"))
    fun findAllMusicians(): List<Member>
}
