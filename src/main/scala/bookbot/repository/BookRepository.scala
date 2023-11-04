package bookbot.repository

import bookbot.model.{Book, BookId, MemberId}
import zio.Task

import java.time.LocalDate

trait BookRepository {

  def create(memberId: MemberId, title: String, author: String, startDate: LocalDate): Task[Book]

  def getCurrent(memberTelegramId: Long): Task[List[Book]]

  def getForMember(memberTelegramId: Long): Task[List[Book]]

  def update(
              id: BookId,
              memberId: Option[MemberId] = None,
              title: Option[String] = None,
              author: Option[String] = None,
              startDate: Option[LocalDate] = None,
              endDate: Option[Option[LocalDate]] = None
            ): Task[Unit]

}





