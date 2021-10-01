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
    @PreAuthorize("true")
    override fun findAll(): List<Register>

    @Query(base = "ou=Register,ou=Divisionen", value = "(objectClass=mvlGroup)")
    @PreAuthorize("true ")
    fun findRegisters(): List<Register>

    @PreAuthorize("true")
    override fun count(): Long

    @PreAuthorize("true")
    override fun existsById(id: Name): Boolean

    @PreAuthorize("true")
    override fun findAll(ldapQuery: LdapQuery): MutableIterable<Register>

    @PreAuthorize("true")
    override fun findAllById(names: MutableIterable<Name>): MutableList<Register>

    @PreAuthorize("true")
    override fun findById(id: Name): Optional<Register>

    @PreAuthorize("true")
    override fun findOne(ldapQuery: LdapQuery): Optional<Register>
}
