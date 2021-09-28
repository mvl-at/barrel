package at.mvl.barrel.repositories

import at.mvl.barrel.model.Score
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface ScoreRepository : CrudRepository<Score, Long>
