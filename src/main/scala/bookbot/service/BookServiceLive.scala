package bookbot.service


import bookbot.models.Book
import zio._

import javax.sql.DataSource


final case class BookServiceLive(
                                  dataSource: DataSource,
                                  memberService: MemberService
                                ) extends BookService {

  import bookbot.QuillContext._

  /** `create` uses the Pet model's `make` method to create a new Pet. The Pet
   * is formatted into a query string, then inserted into the database using
   * `provideEnvironment` to provide the datasource to the effect returned by
   * `run`. The created Pet is returned.
   */
  override def create(memberTelegramId: Long, title: String, author: String): Task[Book] =
    for {
      memberOptional <- memberService.getByTelegramId(memberTelegramId)
      member <- memberOptional match {
        case Some(value) => ZIO.attempt(value)
        case None => memberService.create(memberTelegramId)
      } // is FP???
      book <- Book.make(member.id, title, author)
      _ <- run(query[Book].insertValue(lift(book))).provideEnvironment(ZEnvironment(dataSource))
    } yield book

  //  /** `get` uses `filter` to find a Pet in the database whose ID matches the one
  //   * provided and returns it.
  //   */
  //  override def get(id: PetId): Task[Option[Pet]] =
  //    run(query[Pet].filter(_.id == lift(id)))
  //      .provideEnvironment(ZEnvironment(dataSource))
  //      .map(_.headOption)

}

/** Here in the companion object we define the layer that provides the live
 * implementation of the PetService.
 */
object BookServiceLive {

  val layer: ZLayer[DataSource with MemberService, Nothing, BookServiceLive] = ZLayer.fromFunction(BookServiceLive.apply _)

}
