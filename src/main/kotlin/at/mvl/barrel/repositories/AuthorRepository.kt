package at.mvl.barrel.repositories

import at.mvl.barrel.model.Author
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface AuthorRepository : CrudRepository<Author, Long>
