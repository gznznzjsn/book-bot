package bookbot.repository

import bookbot.model.{Book, BookId, Member, MemberId}
import zio.{RIO, ZLayer}

import java.time.LocalDate
import javax.sql.DataSource

final case class BookRepositoryLive() extends BookRepository {

  import bookbot.QuillContext._

  override def create(memberId: MemberId, title: String, author: String, startDate: LocalDate): RIO[DataSource, Book] =
    for {
      book <- Book.make(memberId, title, author, startDate, None)
      _ <- run(query[Book].insertValue(lift(book)))
    } yield book

  override def get(id: BookId): RIO[DataSource, Option[Book]] =
    run(query[Book].filter(_.id == lift(id)))
      .map(_.headOption)


  override def getCurrent(memberTelegramId: Long): RIO[DataSource, List[Book]] = {
    run(
      query[Book]
        .filter(_.endDate.isEmpty)
        .join(query[Member]).on(_.memberId == _.id)
        .filter(_._2.telegramId == lift(memberTelegramId))
        .map(_._1)
    )
  }

  override def getForMember(memberTelegramId: Long): RIO[DataSource, List[Book]] = {
    run(
      query[Book]
        .join(query[Member]).on(_.memberId == _.id)
        .filter(_._2.telegramId == lift(memberTelegramId))
        .map(_._1)
    )
  }

  override def update(
                       id: BookId,
                       memberId: Option[MemberId],
                       title: Option[String],
                       author: Option[String],
                       startDate: Option[LocalDate],
                       endDate: Option[Option[LocalDate]]
                     ): RIO[DataSource, Unit] = {
    run(
      dynamicQuery[Book]
        .filter(_.id == lift(id))
        .update(
          setOpt(_.memberId, memberId),
          setOpt(_.title, title),
          setOpt(_.author, author),
          setOpt(_.startDate, startDate),
          setOpt(_.endDate, endDate)
        )
    )
      .unit
  }

}

object BookRepositoryLive {

  val layer: ZLayer[Any, Nothing, BookRepository] = ZLayer.fromFunction(BookRepositoryLive.apply _)

}
