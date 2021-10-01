/* 
 * Barrel, the backend of the Musikverein Leopoldsdorf.
 * Copyright (C) 2021  Richard Stöckl
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package at.mvl.barrel.configuration

import at.mvl.barrel.model.ExternalMember
import at.mvl.barrel.repositories.ldap.ExternalMemberRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.core.convert.support.GenericConversionService
import org.springframework.data.ldap.repository.config.EnableLdapRepositories
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.odm.core.impl.DefaultObjectDirectoryMapper
import org.springframework.ldap.odm.typeconversion.impl.ConversionServiceConverterManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct
import javax.naming.ldap.LdapName

/**
 * @author Richard Stöckl
 *
 * This class is intended to configure and extend basic LDAP features.
 * This includes only additional features such as converter registration but not required settings such as configuring the server source(s).
 */
@Configuration
@EnableLdapRepositories(basePackages = ["at.mvl.barrel.repositories.ldap.**"])
class LdapConfiguration(
    @Autowired private val ldap: LdapTemplate,
    @Autowired private val memberRepository: ExternalMemberRepository
) {

    private val logger: Logger = LoggerFactory.getLogger(LdapConfiguration::class.java)

    /**
     * Register all LDAP data type converters required in this application.
     * @return the configured conversion service
     */
    @PostConstruct
    fun conversionService(): DefaultConversionService {
        val odm = ldap.objectDirectoryMapper
        val conversionService = DefaultConversionService()
        if (odm !is DefaultObjectDirectoryMapper) {
            logger.warn("ODM is not DefaultObjectDirectoryMapper, cannot add custom converters")
            return conversionService
        }
        registerConverter(
            conversionService,
            GeneralizedTimeToLocalDateConverter(),
            String::class.java,
            LocalDate::class.java
        )
        registerConverter(
            conversionService,
            LocalDateToGeneralizedTimeConverter(),
            LocalDate::class.java,
            String::class.java
        )
        registerConverter(
            conversionService,
            DnToExternalMemberConverter(memberRepository),
            String::class.java,
            ExternalMember::class.java
        )
        registerConverter(
            conversionService,
            ExternalMemberToStringConverter(),
            ExternalMember::class.java,
            String::class.java
        )
        odm.setConverterManager(ConversionServiceConverterManager(conversionService))
        ldap.objectDirectoryMapper = odm
        return conversionService
    }

    /**
     * Register a converter to a conversion service and produce a log message.
     * @param conversionService the conversion service where the converter should be registered to
     * @param converter the converter to register at the conversionService
     * @param from the source class type
     * @param to the target class type
     */
    private fun registerConverter(
        conversionService: GenericConversionService,
        converter: Converter<*, *>,
        from: Class<*>,
        to: Class<*>
    ) {
        logger.debug("Register converter {}: {} -> {}", converter.javaClass, from, to)
        conversionService.addConverter(converter)
    }
}

/**
 * Converter which converts an LDAP GeneralizedTime string to an instance of [LocalDate]
 */
class GeneralizedTimeToLocalDateConverter : Converter<String, LocalDate> {

    private val logger: Logger = LoggerFactory.getLogger(GeneralizedTimeToLocalDateConverter::class.java)
    private val format = DateTimeFormatter.ofPattern("yyyyMMddHHmmssX")

    override fun convert(source: String): LocalDate? {
        logger.trace("convert({})", source)
        return LocalDate.parse(source, format)
    }
}

/**
 * Converter which converts an instance of [LocalDate] to an LDAP GeneralizedTime string
 */
class LocalDateToGeneralizedTimeConverter : Converter<LocalDate, String> {

    private val logger: Logger = LoggerFactory.getLogger(LocalDateToGeneralizedTimeConverter::class.java)
    private val format = DateTimeFormatter.ofPattern("yyyyMMddHHmmssX")

    override fun convert(source: LocalDate): String? {
        logger.trace("convert({})", source)
        return source.format(format)
    }
}

/**
 * Converter which converts an LDAP DN to an [ExternalMember].
 * Therefore, a [ExternalMemberRepository] is required in order to fetch the [ExternalMember] from an LDAP Server.
 *
 * @param memberRepository the repository where the DN is looked up
 */
class DnToExternalMemberConverter(private val memberRepository: ExternalMemberRepository) :
    Converter<String, ExternalMember> {
    override fun convert(source: String): ExternalMember? {
        if (source == "") {
            return null
        }
        return memberRepository.findById(LdapName(source)).orElse(null)
    }
}

/**
 * Converter which converts an [ExternalMember] to a [String].
 * This converter just extracts the DN of the [ExternalMember] and returns it.
 */
class ExternalMemberToStringConverter : Converter<ExternalMember, String> {
    override fun convert(source: ExternalMember): String {
        return source.id.toString()
    }
}
