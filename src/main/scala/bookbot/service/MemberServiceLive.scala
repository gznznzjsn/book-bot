package bookbot.service

import bookbot.models.Member
import zio._


import javax.sql.DataSource

final case class MemberServiceLive(
                       dataSource: DataSource
                     ) extends MemberService {

  import bookbot.QuillContext._

  override def getByTelegramId(telegramId: Long): Task[Option[Member]] =
    run(query[Member].filter(_.telegramId == lift(telegramId)))
      .provideEnvironment(ZEnvironment(dataSource))
      .map(_.headOption)

  override def create(memberTelegramId: Long): Task[Member] =
    for {
      member <- Member.make(memberTelegramId)
      _ <- run(query[Member].insertValue(lift(member))).provideEnvironment(ZEnvironment(dataSource))
    } yield member

}

object MemberServiceLive {

  val layer: ZLayer[DataSource, Nothing, MemberServiceLive] = ZLayer.fromFunction(MemberServiceLive.apply _)

}