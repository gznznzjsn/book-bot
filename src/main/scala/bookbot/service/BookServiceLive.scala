package bookbot.service


import bookbot.models.Book
import zio._
import zio.metrics._

import javax.sql.DataSource

/** PetServiceLive is a service which provides the "live" implementation of the
 * PetService. This implementation uses a DataSource, which will concretely be
 * a connection pool.
 */
final case class BookServiceLive(
                                  dataSource: DataSource
                                ) extends BookService {

  // QuillContext needs to be imported here to expose the methods in the QuillContext object.

  import bookbot.QuillContext._

  /** `create` uses the Pet model's `make` method to create a new Pet. The Pet
   * is formatted into a query string, then inserted into the database using
   * `provideEnvironment` to provide the datasource to the effect returned by
   * `run`. The created Pet is returned.
   */
  override def create(title: String, author: String): Task[Book] =
    for {
      book <- Book.make(title, author)
      _ <- run(query[Book].insertValue(lift(book))).provideEnvironment(ZEnvironment(dataSource))
      _ <- ZIO.attempt(println(s"Book '${book.title} by ${book.author}' is created"))
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

  val layer: ZLayer[DataSource, Nothing, BookServiceLive] = ZLayer.fromFunction(BookServiceLive.apply _)

}
