package bookbot.repository

import bookbot.model.{Book, BookId, MemberId}
import zio.RIO

import java.time.LocalDate
import javax.sql.DataSource

trait BookRepository {

  def create(memberId: MemberId, title: String, author: String, startDate: LocalDate): RIO[DataSource, Book]

  def get(id: BookId): RIO[DataSource, Option[Book]]

  def getCurrent(memberTelegramId: Long): RIO[DataSource, List[Book]]

  def getForMember(memberTelegramId: Long): RIO[DataSource, List[Book]]

  def update(
              id: BookId,
              memberId: Option[MemberId] = None,
              title: Option[String] = None,
              author: Option[String] = None,
              startDate: Option[LocalDate] = None,
              endDate: Option[Option[LocalDate]] = None
            ): RIO[DataSource, Unit]

}





