package bookbot.service

import bookbot.model.Member
import bookbot.repository.MemberRepository
import zio._


final case class MemberServiceLive(
                                    repository: MemberRepository
                                  ) extends MemberService {


  override def getByTelegramId(telegramId: Long): Task[Member] =
    for {
      memberOpt <- repository.getByTelegramId(telegramId)
      member <- memberOpt match {
        case Some(member) => ZIO.succeed(member)
        case None => ZIO.die(new RuntimeException("Member not found!")) //todo
      }
    } yield member

  override def create(memberTelegramId: Long): Task[Member] =
    repository.create(memberTelegramId)

}

object MemberServiceLive {

  val layer: ZLayer[MemberRepository, Nothing, MemberService] = ZLayer.fromFunction(MemberServiceLive.apply _)

}