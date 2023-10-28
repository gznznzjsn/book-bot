package bookbot.models

import zio._
import zio.json.JsonCodec

import java.util.UUID

/** PetId is a wrapper for UUID.
 *
 * This is a merely a convenience to prevent us from passing the wrong ID type
 * along
 */
final case class UserId(id: UUID) extends AnyVal

object UserId {

  /** Generates a Random UUID and wraps it in the PetId type. */
  def random: UIO[UserId] = Random.nextUUID.map(UserId(_))

  /** Allows a UUID to be parsed from a string which is then wrapped in the
   * PetId type.
   */
  def fromString(id: String): Task[UserId] =
    ZIO.attempt {
      UserId(UUID.fromString(id))
    }

  /** Derives a codec allowing a UUID to be (de)serialized as an PetId. */
  implicit val codec: JsonCodec[UserId] = JsonCodec[UUID].transform(UserId(_), _.id)
}
