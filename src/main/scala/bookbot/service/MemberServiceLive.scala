package bookbot.service

import bookbot.model.Member
import bookbot.repository.MemberRepository
import zio._


final case class MemberServiceLive(
                                    repository: MemberRepository
                                  ) extends MemberService {


  override def getOrCreate(telegramId: Long): Task[Member] =
    for {
      memberOpt <- repository.getByTelegramId(telegramId)
      member <- memberOpt match {
        case Some(member) => ZIO.succeed(member)
        case None => repository.create(telegramId)
      }
    } yield member

  override def create(telegramId: Long): Task[Member] =
    repository.create(telegramId)

}

object MemberServiceLive {

  val layer: ZLayer[MemberRepository, Nothing, MemberService] = ZLayer.fromFunction(MemberServiceLive.apply _)

}