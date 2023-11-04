package bookbot.service

import bookbot.model.{Book, BookId}
import zio.Task

trait BookService {

  def create(memberTelegramId: Long, title: String, author: String, startDateInEpochSeconds: Int): Task[Book]

  def get(id: BookId): Task[Book]

  def getForMember(memberTelegramId: Long): Task[List[Book]]

  def getCurrent(memberTelegramId: Long): Task[List[Book]]

  def finish(id: BookId, endDateInEpochSeconds: Int): Task[Unit]

}