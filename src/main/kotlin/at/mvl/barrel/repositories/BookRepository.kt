package at.mvl.barrel.repositories

import at.mvl.barrel.model.Book
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@RepositoryRestResource(path = "books")
interface BookRepository : CrudRepository<Book, Long> {

    @PreAuthorize("permitAll()")
    override fun findAll(): MutableIterable<Book>
}
