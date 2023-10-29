package bookbot.service

import bookbot.models.{Book, BookId}
import zio.Task

import java.time.LocalDate

trait BookService {

  def create(memberTelegramId: Long, title: String, author: String, startDate: LocalDate): Task[Book]

  def getForMember(memberTelegramId: Long): Task[List[Book]]

  def getCurrent(memberTelegramId: Long): Task[List[Book]]

  def finish(id: BookId, endDate: LocalDate): Task[Book]

}