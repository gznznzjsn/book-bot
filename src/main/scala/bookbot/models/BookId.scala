package bookbot.models

import zio._
import zio.json.JsonCodec

import java.util.UUID

/** PetId is a wrapper for UUID.
  *
  * This is a merely a convenience to prevent us from passing the wrong ID type
  * along
  */
final case class BookId(id: UUID) extends AnyVal

object BookId {

  /** Generates a Random UUID and wraps it in the PetId type. */
  def random: UIO[BookId] = Random.nextUUID.map(BookId(_))

  /** Allows a UUID to be parsed from a string which is then wrapped in the
    * PetId type.
    */
  def fromString(id: String): Task[BookId] =
    ZIO.attempt {
      BookId(UUID.fromString(id))
    }

  /** Derives a codec allowing a UUID to be (de)serialized as an PetId. */
  implicit val codec: JsonCodec[BookId] = JsonCodec[UUID].transform(BookId(_), _.id)
}
