package bookbot.service


import bookbot.model.{Book, BookId}
import bookbot.repository.BookRepository
import zio._

import java.time.{Instant, LocalDate, ZoneId}
import javax.sql.DataSource


final case class BookServiceLive(
                                  dataSource: DataSource,
                                  bookRepository: BookRepository,
                                  memberService: MemberService
                                ) extends BookService {

  import bookbot.QuillContext._

  override def create(memberTelegramId: Long, title: String, author: String, startDateInEpochSeconds: Int): Task[Book] = transaction {
    for {
      member <- memberService.getOrCreate(memberTelegramId)
      startDate <- toLocalDate(startDateInEpochSeconds)
      book <- bookRepository.create(member.id, title, author, startDate)
    } yield book
  }.provideEnvironment(ZEnvironment(dataSource))


  override def getForMember(memberTelegramId: Long): Task[List[Book]] = transaction {
    bookRepository.getForMember(memberTelegramId)
  }.provideEnvironment(ZEnvironment(dataSource))


  override def getCurrent(memberTelegramId: Long): Task[List[Book]] = transaction {
    bookRepository.getCurrent(memberTelegramId)
  }.provideEnvironment(ZEnvironment(dataSource))

  override def finish(id: BookId, endDateInEpochSeconds: Int): Task[Unit] = transaction {
    for {
      endDate <- toLocalDate(endDateInEpochSeconds)
      _ <- bookRepository.update(id, endDate = Option(Option(endDate)))
    } yield ()
  }.provideEnvironment(ZEnvironment(dataSource))

  private def toLocalDate(epochSeconds: Int): Task[LocalDate] = ZIO.attempt(
    Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()).toLocalDate
  )

  override def get(id: BookId): Task[Book] = transaction {
    for {
      bookOpt <- bookRepository.get(id)
      book <- bookOpt match {
        case Some(book) => ZIO.succeed(book)
        case None => ZIO.die(new RuntimeException("Book not found!")) //todo
      }
    } yield book
  }.provideEnvironment(ZEnvironment(dataSource))

}

object BookServiceLive {

  val layer: ZLayer[BookRepository with MemberService with DataSource, Nothing, BookService] = ZLayer.fromFunction(BookServiceLive.apply _)

}
