package bookbot.service

import bookbot.QuillContext
import bookbot.model.Member
import bookbot.repository.MemberRepository
import zio._

import javax.sql.DataSource


final case class MemberServiceLive(
                                    repository: MemberRepository,
                                    dataSource: DataSource
                                  ) extends MemberService {

  private def transaction[A](op: ZIO[DataSource, Throwable, A]): Task[A] =
    QuillContext.transaction(op).provideEnvironment(ZEnvironment(dataSource))

  override def getOrCreate(telegramId: Long): Task[Member] = transaction {
    for {
      memberOpt <- repository.getByTelegramId(telegramId)
      member <- memberOpt match {
        case Some(member) => ZIO.succeed(member)
        case None => repository.create(telegramId)
      }
    } yield member
  }

  override def create(telegramId: Long): Task[Member] = transaction {
    repository.create(telegramId)
  }

}

object MemberServiceLive {

  val layer: ZLayer[MemberRepository with DataSource, Nothing, MemberService] = ZLayer.fromFunction(MemberServiceLive.apply _)

}