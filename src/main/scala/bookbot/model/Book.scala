package bookbot.model

import zio._
import zio.json._

import java.time.LocalDate

final case class Book(
                       id: BookId,
                       memberId: MemberId,
                       title: String,
                       author: String,
                       startDate: LocalDate,
                       endDate: Option[LocalDate]
                     )

object Book {

  def make(
            memberId: MemberId,
            title: String,
            author: String,
            startDate: LocalDate,
            endDate: Option[LocalDate]
          ): UIO[Book] =
    BookId.random.map(Book(_, memberId, title, author, startDate, endDate))

  implicit val codec: JsonCodec[Book] = DeriveJsonCodec.gen[Book]

}
