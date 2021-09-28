package at.mvl.barrel.model

import org.springframework.ldap.odm.annotations.Attribute
import org.springframework.ldap.odm.annotations.Entry
import org.springframework.ldap.odm.annotations.Id
import javax.naming.Name
import javax.naming.ldap.LdapName

@Entry(
    base = "ou=Mitglieder",
    objectClasses = ["person", "inetOrgPerson", "top"]
)
class MemberPhoto(
    @Id var id: Name = LdapName("cn=nonexistent"),

    @Attribute(name = "uid") var username: String = "nobody",
    @Attribute(name = "jpegPhoto", type = Attribute.Type.BINARY) var photo: ByteArray? = null,
    @Attribute(name = "thumbnail", type = Attribute.Type.BINARY) var thumbnail: ByteArray? = null
) {
    companion object {
        val THUMBNAIL_WIDTH = 128
    }
}
