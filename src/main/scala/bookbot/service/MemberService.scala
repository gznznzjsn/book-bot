package bookbot.service

import bookbot.models.Member
import zio.Task

trait MemberService {

  def getByTelegramId(telegramId: Long): Task[Option[Member]]

  def create(memberTelegramId: Long): Task[Member]

}
