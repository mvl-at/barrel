package at.mvl.barrel.model

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Book(
    @Id var id: Long,
    var name: String,
    var annotation: String
)
