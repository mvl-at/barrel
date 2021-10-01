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
