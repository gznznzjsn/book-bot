package bookbot.repository

import bookbot.model.Member
import zio.{Task, ZEnvironment, ZLayer}

import javax.sql.DataSource

final case class MemberRepositoryLive(
                                       dataSource: DataSource
                                     ) extends MemberRepository {

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

object MemberRepositoryLive {

  val layer: ZLayer[DataSource, Nothing, MemberRepository] = ZLayer.fromFunction(MemberRepositoryLive.apply _)


}
