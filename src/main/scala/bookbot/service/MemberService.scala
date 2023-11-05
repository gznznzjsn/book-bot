package bookbot.service

import bookbot.model.Member
import zio.Task

trait MemberService {

  def getOrCreate(telegramId: Long): Task[Member]

  def create(telegramId: Long): Task[Member]

}
