package bookbot.server

import com.bot4s.telegram.api.BotBase
import zio.Task

trait TelegramListener[B <: BotBase[Task]] {

  def listen(bot: B): Task[Unit]

}
