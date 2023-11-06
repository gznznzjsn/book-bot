package bookbot.server

import com.bot4s.telegram.api.BotBase

trait TelegramListener[F[_]] {

  def listen(bot: BotBase[F]): Unit

}
