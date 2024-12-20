🚀 Сервис коротких ссылок с ограничением по времени

Сервис позволяет создавать короткие ссылки, которые действуют только 24 часа. После истечения этого времени ссылка удаляется при попытке редиректа. Данные хранятся в базе данных SQLite.

💡 Как работает сервис

Сервис работает через HTTP-клиент, например, curl на 127.0.0.1:10070.

    Все ссылки существуют только 24 часа.
    После этого они удаляются при попытке редиректа через метод goto, если прошло более 24 часов с момента создания.

У сервиса есть 4 метода для взаимодействия:

1️⃣ reg_short_link - регистрация пользователя, ссылки, получения uuid для дальнейших манипуляций, короткой ссылки и установка лимитов редиректов.
                    Также метод используется для напоминания этой информации.
   Пример использования: curl http://127.0.0.1:10070/reg_short_link/artem/5?https://music.yandex.ru/home - после метода в пути задается имя пользователя "artem", далее количество переходов для ссылки - 5, далее в query передается оригинальная ссылка.
   Пример ответа: "Пользователь с таким именем уже существует. uuid: **ac4afb9df4297ec9bbae3d6c10d97533**. Используйте uuid вместе с укороченной ссылкой **VDvxIBIN** в методе goto (http://127.0.0.1:10070/goto/VDvxIBIN?ac4afb9df4297ec9bbae3d6c10d97533) для редиректа на нужны ресурс: https://music.yandex.ru/home"

2️⃣ goto - метод редиректа на оригинальную ссылку.
   Пример использования: curl http://127.0.0.1:10070/goto/VDvxIBIN?ac4afb9df4297ec9bbae3d6c10d97533
   Пример ответа: "Переход на оригинальную ссылку https://music.yandex.ru/home успешно выполнен. Осталось 4 переходов"

3️⃣ links_list - позволяет получить актуальный лист созданных пользователем ссылок, их временем создания, количеством оставшихся лимитов редиректов.
   Пример использования: curl http://127.0.0.1:10070/links_list/ac4afb9df4297ec9bbae3d6c10d97533
   Пример ответа:
       Short Link: ZyK5emzk, Original Link: https://stackoverflow.com, Created time: 1733437935, Limit: 0
       Short Link: VDvxIBIN, Original Link: https://music.yandex.ru/home, Created time: 1733518356, Limit: 5

4️⃣ delete_link - позволяет удалить любую ссылку пользователя, например, когда лимит редиректов исчерпан, а время истечения наступит еще не скоро.
   Пример использования: http://127.0.0.1:10070/delete_link/VDvxIBIN
   Пример ответа: "Короткая ссылка VDvxIBIN была удалена."

💪🤓❤️