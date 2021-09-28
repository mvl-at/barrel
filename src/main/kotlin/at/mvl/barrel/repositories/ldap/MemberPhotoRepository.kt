package at.mvl.barrel.repositories.ldap

import at.mvl.barrel.model.MemberPhoto
import org.springframework.data.ldap.repository.LdapRepository

interface MemberPhotoRepository : LdapRepository<MemberPhoto> {

    fun findByUsername(username: String): MemberPhoto?
}
