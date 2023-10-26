import zio.{Scope, Task, ULayer, ZIO, ZIOAppArgs, ZLayer}

object ZioMain extends zio.ZIOAppDefault {

  val d = "daniel"
  val e = "e@nail.com"
  val u: User = User(d,e)
  val m = "msg"
  //horizontal composition
  val userBackendLayer: ULayer[UserDb.Service with UserEmailer.Service] = UserDb.live ++ UserEmailer.live

  //vertical
  object UserSubscription {
    class Service(notifier: UserEmailer.Service, database: UserDb.Service) {
      def subscribe(user: User): Task[User] = for {
        _ <- database.insert(user)
        _ <- notifier.notify(user, s"Welcome to app, ${user.name}!")
      } yield user
    }

    val live: ZLayer[UserDb.Service with UserEmailer.Service, Nothing, Service] = ZLayer {
      for {
        e <- ZIO.service[UserEmailer.Service]
        d <- ZIO.service[UserDb.Service]
      } yield new Service(e, d)
    }

    def subscribe(user: User): ZIO[Service, Throwable, User] =
      ZIO.serviceWithZIO(_.subscribe(user))

  }

  def notifyUserMainMethod(): Unit = {
    UserEmailer.notify(User(d, e), m)
      .provide(userBackendLayer)
      .exitCode
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    UserSubscription.subscribe(u)
      .provide(
        userBackendLayer,
        UserSubscription.live
      )

  }

  case class User(name: String, email: String)

  object UserEmailer {

    //service def
    trait Service {
      def notify(user: User, message: String): Task[Unit]
    }

    //service impl
    val live: ULayer[Service] = ZLayer.succeed(new Service {
      override def notify(user: User, message: String): Task[Unit] =
        ZIO.attempt(println(s"[User emailer] Sending $message to ${user.email}"))
    })

    //front-facing api
    def notify(user: User, message: String): ZIO[Service, Throwable, Unit] =
      ZIO.serviceWithZIO(_.notify(user, message))
  }

  object UserDb {
    trait Service {
      def insert(user: User): Task[Unit]
    }

    val live: ULayer[Service] = ZLayer.succeed(new Service {
      override def insert(user: User): Task[Unit] =
        ZIO.attempt(println(s"[UserDb] inserted ${user.email}"))
    })

    def insert(user: User): ZIO[Service, Throwable, Unit] =
      ZIO.serviceWithZIO(_.insert(user))

  }

}
