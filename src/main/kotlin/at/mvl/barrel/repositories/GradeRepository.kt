package at.mvl.barrel.repositories

import at.mvl.barrel.model.Grade
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface GradeRepository : CrudRepository<Grade, Long>
