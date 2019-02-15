# iso20022
Пример подписания документов стандарта ISO 20022

# Требования для запуска проекта
1. Скачать и установить JCP. Установка заключается в модификации JRE или JDK.
2. Перенести библиотеку JCPxml.jar из <path to patched JRE/JDK>/jre/lib/ext/JCPxml.jar в папку libs проекта (требования Криптопро для корректной работы xml подписания, подробнее в ЖТЯИ.00091-01 33 01. Руководство программиста.pdf)
3. Для тестов можно создать сертификат и ключ шифрования в HdImageStore Cryptopro JCP. Для этого можно зайти в интерфейс ControlPane и сгенерировать ключ оттуда. Идентификатор ключа записать в List<Certificate> certificates (LetterSignatureExample/PaymentSignatureExample/StatementSignatureExample).
 Для продакшена нужно перенести ключи и сертификат из хранилища *.pfx в хранилище HDImageStore (это будет папка с 6-ю файлами *.key), которое понимает Java c установленным КриптоПро (подробнее https://www.cryptopro.ru/forum2/default.aspx?g=posts&t=8271)
4. Запустить приложение, используя подготовленную JRE/JDK.