# Telegram-бот для управления читательским дневником
### Основные функции
* Хранение и обновление списка прочитанных книг.
* Добавление к книгам цитат и комментариев.
* Редактирование списка чтения.
### Как использовать
_Бот реагирует на команды как в личных сообщениях, так и в общих беседах._  
* Добавить книгу в список читаемых на данный момент - `начал "Название книги" Имя автора` или иное сообщение, удовлетворяющее `\s*[Нн]ачала?\s*['"«]([а-яА-Я\s]+)['"»]\s*([а-яА-Я\s]+)\s*`.  
* Пометить книгу как прочитанную - `прочитал` или иное сообщение, удовлетворяющее `\s*([Пп]рочитал|[Зз]акончил)а?\s*`. Если у вас только одна книга, которую вы читаете в данный момент, то бот поймет, что именно ее вы прочитали. Если вы читаете несколько одновременно, то бот предложит выбрать какую именно вы закончили. Если же текущих книг нет, то бот вас уведомит об этом.
* Посмотреть список книг, которые вы читаете на данный момент - `текущие` или иное сообщение, удовлетворяющее `\s*[Тт]екущ(ие|ая)\s*`.
* Посмотреть список всех книг - `все` или иное сообщение, удовлетворяющее `\s*[Вв]се\s*`.
### Технологии, использованные при разработке
* [Scala](https://www.scala-lang.org/) - основной язык разработки.
* [ZIO](https://zio.dev/) - фреймворк, обеспечивающий экономное использование ресурсов и написание кода в функциональном стиле.
* [bot4s.telegram](https://github.com/bot4s/telegram) - обертка над Telegram API для взаимодействия с Telegram.
* [ZIO Quill](https://zio.dev/zio-quill/) - библиотека для общения с базой данных посредством DSL (Domain Specific Language).
* [PostgreSQL](https://www.postgresql.org/) - СУБД, хранящая в себе все данные пользователей.
* [Flyway](https://flywaydb.org/) - инструмент для версионирования и миграции базы данных.
    