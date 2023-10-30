package bookbot.model

import zio._
import zio.json.JsonCodec

import java.util.UUID

final case class MemberId(id: UUID) extends AnyVal

object MemberId {

  def random: UIO[MemberId] = Random.nextUUID.map(MemberId(_))

  def fromString(id: String): Task[MemberId] =
    ZIO.attempt {
      MemberId(UUID.fromString(id))
    }

  implicit val codec: JsonCodec[MemberId] = JsonCodec[UUID].transform(MemberId(_), _.id)

}
