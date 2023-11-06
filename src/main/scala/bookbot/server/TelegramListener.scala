package bookbot.server

import com.bot4s.telegram.api.BotBase

trait TelegramListener[B <: BotBase[_]] {

  def listen(bot: B): Unit

}
