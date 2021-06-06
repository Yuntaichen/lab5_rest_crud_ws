**Лабораторная работа 5. Реализация CRUD с помощью REST-сервиса**

# Реализация CRUD с помощью REST-сервиса

## Задание

> Таблица БД, а также код для работы с ней был взят из предыдущих работ без изменений. 

Выполнить задание из лабораторной работы 2, но с использованием REST-сервиса:

1. В ранее разработанный веб-сервис необходимо добавить методы для создания (CREATE), изменения (UPDATE) и удаления (DELETE) записей из таблицы БД. 
2. Метод создания должен принимать значения полей новой записи, метод изменения - индентификатор изменяемой записи, методы обновления и удаления - статус операции. Необходимо вносить изменения только в standalone-реализацию сервиса. 
3. Соответствующим образом необходимо обновить клиентское приложение.

## Ход работы

В pom.xml оставляем зависимости:

```xml
<dependencies>
        <dependency>
            <groupId>com.sun.jersey</groupId>
            <artifactId>jersey-server</artifactId>
            <version>1.19.4</version>
        </dependency>
        <dependency>
            <groupId>com.sun.jersey</groupId>
            <artifactId>jersey-servlet</artifactId>
            <version>1.19.4</version>
        </dependency>
        <dependency>
            <groupId>com.sun.jersey</groupId>
            <artifactId>jersey-grizzly2</artifactId>
            <version>1.19.4</version>
        </dependency>
        <dependency>
            <groupId>com.sun.jersey</groupId>
            <artifactId>jersey-json</artifactId>
            <version>1.19.4</version>
        </dependency>
        <dependency>
            <groupId>com.sun.jersey</groupId>
            <artifactId>jersey-client</artifactId>
            <version>1.19.4</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.2.20</version>
        </dependency>
    </dependencies>
```

Основной код классов берем из лабораторной работы 4 части 1 (разработка standalone-реализации сервиса). 

![image-20210606230609815](README.assets/image-20210606230609815.png)



## Реализация клиентского приложения





