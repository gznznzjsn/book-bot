package bookbot.service


import bookbot.models.{Book, Member}
import zio._

import javax.sql.DataSource


final case class BookServiceLive(
                                  dataSource: DataSource,
                                  memberService: MemberService
                                ) extends BookService {

  import bookbot.QuillContext._

  override def create(memberTelegramId: Long, title: String, author: String): Task[Book] =
    for {
      memberOptional <- memberService.getByTelegramId(memberTelegramId)
      member <- memberOptional match {
        case Some(value) => ZIO.attempt(value)
        case None => memberService.create(memberTelegramId)
      } // is FP???
      book <- Book.make(member.id, title, author)
      _ <- run(query[Book].insertValue(lift(book))).provideEnvironment(ZEnvironment(dataSource))
    } yield book

  override def getForMember(memberTelegramId: Long): Task[List[Book]] = {
    run(
      query[Book]
        .join(query[Member]).on(_.memberId == _.id)
        .filter(_._2.telegramId == lift(memberTelegramId))
        .map(_._1)
    )
      .provideEnvironment(ZEnvironment(dataSource))
  }
}

object BookServiceLive {

  val layer: ZLayer[DataSource with MemberService, Nothing, BookServiceLive] = ZLayer.fromFunction(BookServiceLive.apply _)

}
