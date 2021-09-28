package at.mvl.barrel.model

import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToMany

@Entity
@Schema(description = "An author of scores. Might be a composer or an arranger.")
class Author(
    @Id
    @Schema(description = "The id of this composer", example = "3", format = "int(64)", readOnly = true)
    var id: Long,

    @Schema(description = "Full name of this author", example = "Julius Fucik")
    var name: String,

    @Schema(description = "Scores which where composed by this author", readOnly = true)
    @ManyToMany var composerOf: Set<Score>,
    @ManyToMany var arrangerOf: Set<Score>
)
