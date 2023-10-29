package bookbot.models

import zio._
import zio.json._

import java.time.LocalDate

/** Pet defines what pieces of data a Pet is comprised of.
 *
 * This data type models what we expect to be defined in the database.
 */
final case class Book(
                       id: BookId,
                       memberId: MemberId,
                       title: String,
                       author: String,
                       startDate: LocalDate,
                       endDate: Option[LocalDate]
                     )

object Book {

  /** Uses the `random` method defined on our PetId wrapper to generate a random
   * ID and assign that to the Pet we are creating.
   */
  def make(
            memberId: MemberId,
            title: String,
            author: String,
            startDate: LocalDate,
            endDate: Option[LocalDate]
          ): UIO[Book] =
    BookId.random.map(Book(_, memberId, title, author, startDate, endDate))

  /** Derives a JSON codec for the Pet type allowing it to be (de)serialized.
   */
  implicit val codec: JsonCodec[Book] = DeriveJsonCodec.gen[Book]

}
