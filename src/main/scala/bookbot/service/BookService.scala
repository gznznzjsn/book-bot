package bookbot.service

import bookbot.models.Book
import zio.Task

trait BookService {

  def create(userTelegramId: Long, title: String, author: String): Task[Book]

//  def get(id: BookId): Task[Option[Book]]

}