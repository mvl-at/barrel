package at.mvl.barrel.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.ldap.odm.annotations.Attribute
import org.springframework.ldap.odm.annotations.Entry
import org.springframework.ldap.odm.annotations.Id
import javax.naming.Name
import javax.naming.ldap.LdapName

@Entry(
    base = "ou=Divisionen",
    objectClasses = ["mvlGroup", "groupOfNames", "top"]
)
class Register(
    @Id
    @JsonIgnore
    var id: Name = LdapName("cn=nonexistent"),

    @Attribute(name = "cn") var name: String = "Nobodies",
    @Attribute(name = "cns") var nameSingular: String = "Nobody",

    @Attribute(name = "member") @JsonIgnore var allMembers: List<String> = listOf(),
    @Transient var members: MutableList<ExternalMember>? = null
) : Comparable<Register> {
    override fun compareTo(other: Register): Int {
        if (name < other.name) return -1
        if (name > other.name) return 1
        return 0
    }
}
