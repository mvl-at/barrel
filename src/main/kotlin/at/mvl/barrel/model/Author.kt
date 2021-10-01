/* 
 * Barrel, the backend of the Musikverein Leopoldsdorf.
 * Copyright (C) 2021  Richard Stöckl
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

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
