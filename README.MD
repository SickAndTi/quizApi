1) Переменная PGDATA -> data postgres
2)./pg_ctl start -> C:\Program Files\PostgreSQL\10\bin
3) pg_hba.conf -> # IPv4 local connections:
                 host    all             all             127.0.0.1/32            md5
                 host    all             all             *            md5
4) Для винды надо создать переменную среды PGCLIENTENCODING -> win1251 (устанавливает кодировку для программы `psql`)
5) Также надо изменить кодировку для `cmd`, добавив команду при запуске оной в реестре (HKEY_CURRENT_USER\Software\Microsoft\Command Processor and add String value Autorun = chcp 1251) 
путь реестра не всегда дефолтный , корневая может быть другой , например HKEY_LOCAL_MACHINE\ . Искать просто Command Proccessor по всему реестру.
6) Для `gradle` задачи `bootRun` и `bootWar` требуются такие аргументы: `-Dspring.profiles.active=dev -Psuffix=`, где `dev` - текущий профиль, а `suffix` - дополнение к имени `war` файла.
7) Чтобы русский текст работал через git-bash на винде надо создать файл настроек в папке юзера (типа `C:/Users/username`) так (`touch .bashrc`) и в него прописать локали:

```
export LC_ALL=ru_RU.CP1251
export LANG=ru_RU.CP1251
export LANGUAGE=ru_RU.CP1251
```