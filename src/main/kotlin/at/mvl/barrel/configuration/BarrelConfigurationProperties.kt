package at.mvl.barrel.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "barrel")
data class BarrelConfigurationProperties(
    /** The LDAP configuration for mapping users and groups. */
    val ldap: LdapConfiguration = LdapConfiguration(),
    /** Role mappings. */
    val roles: RoleMappingConfiguration = RoleMappingConfiguration(),
    /** Security configuration. */
    val security: SecurityConfiguration = SecurityConfiguration()
) {
    data class LdapConfiguration(
        /** Search base where to start finding users. */
        val userSearchBase: String = "ou=Mitglieder",
        /** The search filter for users. */
        val userSearchFilter: String = "(uid={0})",
        /** Search base where to start finding groups/roles. */
        val groupSearchBase: String = "ou=Exekutive,ou=Divisionen",
        /** Group entry attribute which identifies user entries (their DN). */
        val groupSearchFilter: String = "(member={0})",
        /** Attribute to use for mapping the group name. */
        val groupRoleAttribute: String = "cn",
        /** Whether to perform a search over the whole subtree or not for groups. */
        val groupSearchSubtree: Boolean = true
    )

    data class RoleMappingConfiguration(
        /** Role for managing members. */
        val memberManager: String = "MITGLIEDVERWALTER"
    )

    data class SecurityConfiguration(
        /** Jwt configuration. */
        val jwt: JwtConfiguration = JwtConfiguration(),
        /** Absolute paths for login (JwtAuthenticationFilter) */
        val loginPath: String = "/selfservice/login"
    ) {
        data class JwtConfiguration(
            /** Expiration delta in minutes. */
            val expiration: Long = 10,
            /** Expiration delta of the renewal token in minutes. */
            val expirationRenewal: Long = 60 * 24 * 7,
            /** Issuer of the token. */
            val issuer: String = "Barrel",
            /** Secret of the token to sign with. */
            val secret: String = "pleasereplaceme",
            /** Attribute in the token to check if it is a renewal token or not. */
            val renewalAttribute: String = "ren",
            /** Attribute name to indicate the last password change. */
            val passwordAttribute: String = "pwdd",
            /** Prefix to use for the token. */
            val prefix: String = "Bearer"
        )
    }
}
