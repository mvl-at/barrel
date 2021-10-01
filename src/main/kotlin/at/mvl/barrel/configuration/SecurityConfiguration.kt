package at.mvl.barrel.configuration

import at.mvl.barrel.security.JwtAuthenticationFilter
import at.mvl.barrel.security.JwtAuthorizationFilter
import at.mvl.barrel.security.JwtTokenService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.ldap.core.support.BaseLdapPathContextSource
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator
import org.springframework.security.ldap.userdetails.LdapUserDetailsService
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
class SecurityConfiguration(
    @Autowired private val contextSource: BaseLdapPathContextSource,
    @Autowired private val barrelConfigurationProperties: BarrelConfigurationProperties
) : WebSecurityConfigurerAdapter() {

    private val logger: Logger = LoggerFactory.getLogger(SecurityConfiguration::class.java)

    override fun configure(http: HttpSecurity) {
        http.csrf {
            it.disable()
        }.addFilterAt(
            JwtAuthenticationFilter(
                authenticationManager(),
                barrelConfigurationProperties.security.loginPath,
                jwtTokenService()
            ),
            UsernamePasswordAuthenticationFilter::class.java
        ).addFilter(
            JwtAuthorizationFilter(
                authenticationManager(), jwtTokenService()
            )
        )
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        logger.trace("configure({})", auth)
        if (auth == null) {
            logger.warn("Unable to configure LDAP for the AuthenticationManager")
            return
        }
        auth.ldapAuthentication()
            .contextSource(contextSource)
            .userSearchBase(barrelConfigurationProperties.ldap.userSearchBase)
            .userSearchFilter(barrelConfigurationProperties.ldap.userSearchFilter)
            .groupSearchBase(barrelConfigurationProperties.ldap.groupSearchBase)
            .groupSearchFilter(barrelConfigurationProperties.ldap.groupSearchFilter)
            .groupRoleAttribute(barrelConfigurationProperties.ldap.groupRoleAttribute)
            .groupSearchSubtree(barrelConfigurationProperties.ldap.groupSearchSubtree)
    }

    @Bean
    fun ldapUserDetailsService(contextSource: BaseLdapPathContextSource): LdapUserDetailsService {
        logger.trace("ldapUserDetailsService({})", contextSource)
        val filter = FilterBasedLdapUserSearch(
            barrelConfigurationProperties.ldap.userSearchBase,
            barrelConfigurationProperties.ldap.userSearchFilter,
            contextSource
        )
        val authoritiesPopulator =
            DefaultLdapAuthoritiesPopulator(contextSource, barrelConfigurationProperties.ldap.groupSearchBase)
        authoritiesPopulator.setSearchSubtree(barrelConfigurationProperties.ldap.groupSearchSubtree)
        authoritiesPopulator.setGroupRoleAttribute(barrelConfigurationProperties.ldap.groupRoleAttribute)
        authoritiesPopulator.setGroupSearchFilter(barrelConfigurationProperties.ldap.groupSearchFilter)
        return LdapUserDetailsService(filter, authoritiesPopulator)
    }

    override fun userDetailsService(): UserDetailsService {
        return ldapUserDetailsService(contextSource)
    }

    override fun userDetailsServiceBean(): UserDetailsService {
        return userDetailsService()
    }

    @Bean
    fun jwtTokenService(): JwtTokenService {
        return JwtTokenService(barrelConfigurationProperties, userDetailsService())
    }
}
