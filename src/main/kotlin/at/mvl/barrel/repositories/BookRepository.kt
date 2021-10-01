package at.mvl.barrel.repositories

import at.mvl.barrel.model.Book
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

/**
 * @author Richard Stöckl
 *
 * Repository for managing books.
 */
@RepositoryRestResource
@PreAuthorize("hasRole(@roleMap.roles().archive)")
interface BookRepository : CrudRepository<Book, Long> {

    /**
     * Find all books and sort them by their name.
     * @return the sorted books
     */
    @PreAuthorize("isAuthenticated()")
    fun findByOrderByName(): List<Book>
}
