**Лабораторная работа 4. Часть 1. Реализация standalone-сервиса**

# Поиск с помощью standalone-реализации REST-сервиса

## Задание

> Таблица БД, а также код для работы с ней был взят из предыдущих работ без изменений. 

Выполнить задание из лабораторной работы 1, но с использованием REST-сервиса:

1. Реализовать возможность поиска по любым комбинациям полей с помощью REST-сервиса. Данные для поиска должны передаваться в метод сервиса в качестве аргументов.
2. Веб-сервис реализовать в виде standalone-приложения. 
3. Для демонстрации сервисов следует также разработать клиентское консольное приложение.

## Ход работы

За основу возьмем подготовленный в предыдущих работах код и базу данных. Таким образом в класс Student мы добавляем только аннотацию `@XmlRootElement`. Класс ConnectionUtil оставляем без изменений. В классе PostgreSQLDAO в методе getStudentsByFields принимаем один аргумент `name`, по которому будем производить поиск в БД и выводить результат перед реализацией поиска по любым комбинациям полей. 

В pom.xml добавляем зависимости:

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
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.2.20</version>
        </dependency>
    </dependencies>
```

В соответствии с методическим пособием создаем класс App. Затем добавляем класс StudentResource:

```java
@Path("/students")
@Produces({MediaType.APPLICATION_JSON})

public class StudentResource {
    @GET
    public LinkedHashSet<Student> getStudents(@QueryParam("name") String name) {
        LinkedHashSet<Student> students = new PostgreSQLDAO().getStudentsByFields(name);
        return students;
    }
}
```

Теперь при запросе в браузере (после записку сервиса, соответственно):

```http
http://localhost:8080/rest/students?name=Jim
```

Получаем следующий ответ в JSON:

```json
{"student":{"age":"26","mark":"good","name":"Jim","student_id":"234548","surname":"Carrey"}}
```

После этого приступаем к реализации поиска по комбинациям полей. Для поиска нам потребуется принимать массив значений от клиента, для чего будем использовать также аннотацию `@QueryParam`, но с параметрами типа `final List<String>`, где ключ `final` требуется для указания неизменяемого объекта на входе. В итоге, изменим класс `StudentResource` следующим образом:

```java
package com.labs;

import java.util.LinkedHashSet;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/students")
@Produces({MediaType.APPLICATION_JSON})

public class StudentResource {
    @GET
    public LinkedHashSet<Student> getStudents(@QueryParam("searchParams") final List<String> searchArgs) {
        System.out.println(searchArgs);
        return new PostgreSQLDAO().getStudentsByFields(searchArgs);
    }
}
```

Класс PostgreSQLDAO теперь должен иметь метод `getStudentsByFields()`, который в качестве аргумента принимает список строк `List<String> searchArgs` с параметрами для поиска по полям в таблице БД. Далее всё идёт аналогично лабораторной работе 1.



## Реализация клиентского приложения

Ввиду того, что REST-сервис не имеет WSDL-описания, генерацию артефактов в данном случае не провести. При разработке клиента потребуется полностью реализовать класс для обращений к REST-сервису.

Изначально реализуем также возможность получения данных по имени в качестве аргумента:

```java
package com.labs;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import java.util.List;
import javax.ws.rs.core.MediaType;

public class ClientApp {
    private static final String URL = "http://localhost:8080/rest/students";

    public static void main(String[] args) {
        Client client = Client.create();
        printList(getAllStudents(client, null));
        System.out.println();
        printList(getAllStudents(client, "Jim"));
    }

    private static List<Student> getAllStudents(Client client, String name) {
        WebResource webResource = client.resource(URL);
        if (name != null) {
            webResource = webResource.queryParam("name", name);
        }
        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            throw new IllegalStateException("Request failed");
        }
        GenericType<List<Student>> type = new GenericType<List<Student>>() {};
        return response.getEntity(type);
    }

    private static void printList(List<Student> students) {
        for (Student student : students) {
            System.out.println(student);
        }
    }
}
```

![image-20210606140530733](README.assets/image-20210606140530733.png)



После проверки (запуска сервиса, а затем клиента с заранее заданным параметром) переходим также к реализации поиска. 

Для реализации поиска мы должны запрашивать через консоль набор параметров для поиска и далее отправлять их с запросом к сервису. Для этого используем реализованный в лабораторной работе 1 консольный ввод аргументов через `Scanner` и далее из них также сформируем список `searchThis` и будем передавать сформированных список в метод `searchStudentsByFields(client, searchThis)`, который в итоге должен будет вернуть данные из ответа сервиса. Результат будем также выводить на консоль:

```java
for (Student student : searchStudentsByFields(client, searchThis)) {
            System.out.println(student);
        }
```



Реализация метода `searchStudentsByFields(client, searchThis)` представлена ниже с комментариями, где это необходимо:

```java
private static List<Student> searchStudentsByFields(Client client, List<String> searchParams) {
        WebResource webResource = client.resource(URL);
        if (searchParams != null) {
            // Передача набора параметров может производиться при помощи queryParams,
            // который на вход принимает структуру MultivaluedMap<String, String>,
            // которая в свою очередь состоит из ключа типа String и значения типа List<String>,
            // где в качестве списка передается массив параметров для поиска.
            MultivaluedMap<String, String> reqParams = new MultivaluedMapImpl();
            reqParams.put("searchParams", searchParams);
            webResource = webResource.queryParams(reqParams);

        }
        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            throw new IllegalStateException("Request failed");
        }
        GenericType<List<Student>> type = new GenericType<List<Student>>() {};
        return response.getEntity(type);
    }
```







