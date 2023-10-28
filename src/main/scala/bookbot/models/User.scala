package bookbot.models

import zio._
import zio.json._

/** Pet defines what pieces of data a Pet is comprised of.
 *
 * This data type models what we expect to be defined in the database.
 */
final case class User(
                       id: UserId,
                       telegramId: Long
                     )

object User {

  /** Uses the `random` method defined on our PetId wrapper to generate a random
   * ID and assign that to the Pet we are creating.
   */
  def make(
            telegramId: Long

          ): UIO[User] =
    UserId.random.map(User(_, telegramId))

  /** Derives a JSON codec for the Pet type allowing it to be (de)serialized.
   */
  implicit val codec: JsonCodec[User] = DeriveJsonCodec.gen[User]

}