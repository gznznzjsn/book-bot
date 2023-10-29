package bookbot.models

import zio._
import zio.json._

final case class Member(
                         id: MemberId,
                         telegramId: Long
                     )

object Member {

  def make(
            telegramId: Long
          ): UIO[Member] =
    MemberId.random.map(Member(_, telegramId))

  implicit val codec: JsonCodec[Member] = DeriveJsonCodec.gen[Member]

}