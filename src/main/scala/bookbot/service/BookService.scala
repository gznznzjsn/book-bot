package bookbot.service

import bookbot.models.Book
import zio.Task

import java.time.LocalDate

trait BookService {

  def create(memberTelegramId: Long, title: String, author: String, startDate: LocalDate): Task[Book]

  def getForMember(memberTelegramId: Long): Task[List[Book]]

}