package at.mvl.barrel.repositories

import at.mvl.barrel.model.Room
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface RoomRepository : CrudRepository<Room, Long>
