# Тестовое задание

## Стек
Java 17, PostgreSQL, Spring, Lombok, Spring Data JPA, Hibernate

## Инструкция в запуску
1. Собрать проект командой mvn clean package
2. Запуск  Docker Compose командой docker-compose up -d  (или docker-compose up --build для деталиции логов)
(при необходимости проверить логи контейнеров командой docker-compose docker-compose ps)
3. Открыть bash в среде разработки и выполнить следующие команды: 
*id требуется ввести именно в формате UUID(можно сгенерировать или использовать уже имеющий)
deposit:
 curl -X POST http://localhost:8080/api/v1/wallets \
  -H "Content-Type: application/json" \
  -d '{
    "walletId": "00000000-0000-0000-0000-000000000003",
    "operationType": "DEPOSIT",
    "amount": 1000
  }'

withdraw:
curl -X POST http://localhost:8080/api/v1/wallets \
  -H "Content-Type: application/json" \
  -d '{
    "walletId": "00000000-0000-0000-0000-000000000003",
    "operationType": "WITHDRAW",
    "amount": 500
  }'

check_balance:
curl http://localhost:8080/api/v1/wallets/00000000-0000-0000-0000-000000000003 

## Техническое задание

Напишите приложение, которое по REST принимает запрос вида
POST api/v1/wallet
{
valletId: UUID,
operationType: DEPOSIT or WITHDRAW,
amount: 1000
}

после выполнять логику по изменению счета в базе данных
также есть возможность получить баланс кошелька

GET api/v1/wallets/{WALLET_UUID}
стек:
java 8-17
Spring 3
Postgresql

Должны быть написаны миграции для базы данных с помощью liquibase

Обратите особое внимание проблемам при работе в конкурентной среде (1000 RPS по
одному кошельку). Ни один запрос не должен быть не обработан (50Х error)

Предусмотрите соблюдение формата ответа для заведомо неверных запросов, когда
кошелька не существует, не валидный json, или недостаточно средств.

приложение должно запускаться в докер контейнере, база данных тоже, вся система
должна подниматься с помощью docker-compose

предусмотрите возможность настраивать различные параметры как на стороне

приложения так и базы данных без пересборки контейнеров.

эндпоинты должны быть покрыты тестами.
