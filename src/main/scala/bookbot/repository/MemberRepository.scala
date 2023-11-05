package bookbot.repository

import bookbot.model.Member
import zio.Task

trait MemberRepository {

  def getByTelegramId(telegramId: Long): Task[Option[Member]]

  def create(memberTelegramId: Long): Task[Member]

}
