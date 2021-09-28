package at.mvl.barrel.model

import org.springframework.ldap.odm.annotations.Attribute
import org.springframework.ldap.odm.annotations.Entry
import org.springframework.ldap.odm.annotations.Id
import javax.naming.Name
import javax.naming.ldap.LdapName

@Entry(
    base = "ou=Exekutive,ou=Divisionen",
    objectClasses = ["groupOfNames", "top"]
)
class ExecutiveRole(
    @Id
    var id: Name = LdapName("cn=nonexistent"),

    @Attribute(name = "cn") var name: String = "Voids",
    @Attribute(name = "cns") var nameSingular: String = "Void",
    @Attribute(name = "description") var description: String = "Empty group",

    @Attribute(name = "member") var members: List<String> = listOf()
)
