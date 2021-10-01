package at.mvl.barrel.repositories

import at.mvl.barrel.model.ScoreLocation
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

/**
 * @author Richard Stöckl
 *
 * Repository for managing score locations.
 */
@RepositoryRestResource
@PreAuthorize("hasRole(@roleMap.roles().archive)")
interface ScoreLocationRepository : CrudRepository<ScoreLocation, Long>
