package bookbot.models.api

import zio.json._

/** Models the parameters of a post request that the client will send to the
 * server while removing the need for the request to handle generating an
 * PetId.
 */
final case class CreateBook(title: String, author: String)

/** Derives a JSON codec allowing the CreatePet request to be (de)serialized.
 */
object CreateBook {
  implicit val codec: JsonCodec[CreateBook] = DeriveJsonCodec.gen[CreateBook]
}
