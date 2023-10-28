package bookbot.models

import zio._
import zio.json.JsonCodec

import java.util.UUID

/** PetId is a wrapper for UUID.
 *
 * This is a merely a convenience to prevent us from passing the wrong ID type
 * along
 */
final case class MemberId(id: UUID) extends AnyVal

object MemberId {

  /** Generates a Random UUID and wraps it in the PetId type. */
  def random: UIO[MemberId] = Random.nextUUID.map(MemberId(_))

  /** Allows a UUID to be parsed from a string which is then wrapped in the
   * PetId type.
   */
  def fromString(id: String): Task[MemberId] =
    ZIO.attempt {
      MemberId(UUID.fromString(id))
    }

  /** Derives a codec allowing a UUID to be (de)serialized as an PetId. */
  implicit val codec: JsonCodec[MemberId] = JsonCodec[UUID].transform(MemberId(_), _.id)
}
