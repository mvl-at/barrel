package at.mvl.barrel.model

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Room(
    @Id var id: Long,
    var name: String
)
