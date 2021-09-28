package at.mvl.barrel.repositories

import at.mvl.barrel.model.Genre
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface GenreRepository : CrudRepository<Genre, Long>
