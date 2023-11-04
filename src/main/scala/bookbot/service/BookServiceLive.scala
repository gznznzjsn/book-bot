package bookbot.service


import bookbot.model.{Book, BookId}
import bookbot.repository.BookRepository
import zio._

import java.time.{Instant, LocalDate, ZoneId}


final case class BookServiceLive(
                                  bookRepository: BookRepository,
                                  memberService: MemberService
                                ) extends BookService {

  override def create(memberTelegramId: Long, title: String, author: String, startDate: LocalDate): Task[Book] =
    for {
      memberOptional <- memberService.getByTelegramId(memberTelegramId)
      member <- memberOptional match {
        case Some(value) => ZIO.attempt(value)
        case None => memberService.create(memberTelegramId)
      }
      book <- bookRepository.create(member.id, title, author, startDate)
    } yield book

  override def getForMember(memberTelegramId: Long): Task[List[Book]] =
    bookRepository.getForMember(memberTelegramId)

  override def getCurrent(memberTelegramId: Long): Task[List[Book]] =
    bookRepository.getCurrent(memberTelegramId)

  override def finish(id: BookId, epochSeconds: Int): Task[Unit] = {
    for {
      endDate <- toLocalDate(epochSeconds)
      _ <- bookRepository.update(id, endDate = Option(Option(endDate)))
    } yield ()
  }

  private def toLocalDate(epochSeconds: Int): Task[LocalDate] = ZIO.attempt(
    Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()).toLocalDate
  )

  override def get(id: BookId): Task[Book] =
    for {
      bookOpt <- bookRepository.get(id)
      book <- bookOpt match {
        case Some(book) => ZIO.succeed(book)
        case None => ZIO.die(new RuntimeException("Book not found!")) //todo
      }
    } yield book
}

object BookServiceLive {

  val layer: ZLayer[BookRepository with MemberService, Nothing, BookService] = ZLayer.fromFunction(BookServiceLive.apply _)

}
