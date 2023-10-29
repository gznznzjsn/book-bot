package bookbot.models

import zio._
import zio.json.JsonCodec

import java.util.UUID

final case class BookId(id: UUID) extends AnyVal

object BookId {

  def random: UIO[BookId] = Random.nextUUID.map(BookId(_))

  def fromString(id: String): Task[BookId] =
    ZIO.attempt {
      BookId(UUID.fromString(id))
    }

  implicit val codec: JsonCodec[BookId] = JsonCodec[UUID].transform(BookId(_), _.id)

}
