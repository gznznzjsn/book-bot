package bookbot.repository

import bookbot.model.{Book, BookId, Member, MemberId}
import zio.{Task, ZEnvironment, ZLayer}

import java.time.LocalDate
import javax.sql.DataSource

final case class BookRepositoryLive(
                                     dataSource: DataSource
                                   ) extends BookRepository {

  import bookbot.QuillContext._

  override def create(memberId: MemberId, title: String, author: String, startDate: LocalDate): Task[Book] =
    for {
      book <- Book.make(memberId, title, author, startDate, None)
      _ <- run(query[Book].insertValue(lift(book))).provideEnvironment(ZEnvironment(dataSource))
    } yield book

  override def getCurrent(memberTelegramId: Long): Task[List[Book]] = {
    run(
      query[Book]
        .filter(_.endDate.isEmpty)
        .join(query[Member]).on(_.memberId == _.id)
        .filter(_._2.telegramId == lift(memberTelegramId))
        .map(_._1)
    )
      .provideEnvironment(ZEnvironment(dataSource))
  }

  override def getForMember(memberTelegramId: Long): Task[List[Book]] = {
    run(
      query[Book]
        .join(query[Member]).on(_.memberId == _.id)
        .filter(_._2.telegramId == lift(memberTelegramId))
        .map(_._1)
    )
      .provideEnvironment(ZEnvironment(dataSource))
  }

  override def update(
                       id: BookId,
                       memberId: Option[MemberId],
                       title: Option[String],
                       author: Option[String],
                       startDate: Option[LocalDate],
                       endDate: Option[Option[LocalDate]]
                     ): Task[Unit] = {
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
      .provideEnvironment(ZEnvironment(dataSource))
      .unit
  }

}

object BookRepositoryLive {

  val layer: ZLayer[DataSource, Nothing, BookRepository] = ZLayer.fromFunction(BookRepositoryLive.apply _)

}
