package bookbot.service

import bookbot.models.{Book, BookId}
import zio.Task
import zio.macros.accessible

//@accessible
trait BookService {

  /** Creates a new Pet. */
  def create(title: String, author: String): Task[Book]

//  /** Retrieves a Pet from the database. */
//  def get(id: BookId): Task[Option[Book]]

}