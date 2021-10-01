package at.mvl.barrel.repositories

import at.mvl.barrel.model.Grade
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

/**
 * @author Richard Stöckl
 *
 * Repository for managing grades.
 */
@RepositoryRestResource
@PreAuthorize("hasRole(@roleMap.roles().archive)")
interface GradeRepository : CrudRepository<Grade, Long>
