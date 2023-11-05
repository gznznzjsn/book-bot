package bookbot.service

import bookbot.model.Member
import zio.Task

trait MemberService {

  def getByTelegramId(telegramId: Long): Task[Member]

  def create(memberTelegramId: Long): Task[Member]

}
