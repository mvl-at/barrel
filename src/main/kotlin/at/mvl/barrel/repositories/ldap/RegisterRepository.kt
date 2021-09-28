package at.mvl.barrel.repositories.ldap

import at.mvl.barrel.model.Register
import org.springframework.data.ldap.repository.LdapRepository
import org.springframework.data.ldap.repository.Query
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface RegisterRepository : LdapRepository<Register> {

    @Query(base = "ou=Register,ou=Divisionen", value = "(objectClass=mvlGroup)")
    override fun findAll(): List<Register>

    @Query(base = "ou=Register,ou=Divisionen", value = "(objectClass=mvlGroup)")
    fun findRegisters(): List<Register>
}
