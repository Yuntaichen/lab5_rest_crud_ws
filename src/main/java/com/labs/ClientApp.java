package com.labs;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import java.util.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

public class ClientApp {
    private static final String URL = "http://localhost:8080/rest/students";

    public static void main(String[] args) {
        Client client = Client.create();

        // Консольный выбор CRUD метода
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose CRUD method (input CREATE, READ, UPDATE or DELETE), or input 'exit' for exit:");
        String chosenMethod;
        do {
            chosenMethod = scanner.nextLine();
            // проверим строку на наличие аргумента: если строка не является пустой и не состоит из пробелов, то
            // проверяем на наличие одной из возможных операций
            if (chosenMethod != null && !chosenMethod.trim().isEmpty()) {

                switch (chosenMethod) {
                    case ("CREATE"):
                        createStudent(client);
                        System.out.println("That's it! You can choose another CRUD method or input 'exit' for exit");
                        break;
                    case ("READ"):
                        searchStudentsByFields(client);
                        System.out.println("That's it! You can choose another CRUD method or input 'exit' for exit");
                        break;
                    case ("UPDATE"):
//                        updateStudentRowById(studentService);
                        System.out.println("That's it! You can choose another CRUD method or input 'exit' for exit");
                        break;
                    case ("DELETE"):
                        deleteStudent(client);
                        System.out.println("That's it! You can choose another CRUD method or input 'exit' for exit");
                        break;
                    case ("exit"):
                        System.out.println("Bye-Bye!");
                        break;
                    default:
                        System.out.println("You can input just CREATE, READ, UPDATE or DELETE!");
                        System.out.println("Try again or use 'exit' for exit.");
                        break;
                }
            }
        } while (!Objects.equals(chosenMethod, "exit"));

        scanner.close();

    }

    private static void deleteStudent(Client client) {
        // Консольный ввод аргументов
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input rowId (integer): ");
        String rowId = scanner.nextLine();

        try {
            Integer.parseInt(rowId.trim());

            WebResource webResource = client.resource(URL);
            webResource = webResource.queryParam("rowId", rowId);
            ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
            if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
                throw new IllegalStateException("Request failed");
            }
            System.out.println(response.getStatus());

        } catch (NumberFormatException ex) {
            System.out.println("Incorrect rowId value! Input just one integer.");
        }
    }

    private static void createStudent(Client client) {

        // Консольный ввод аргументов
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input name: ");
        String name = scanner.nextLine();
        System.out.print("Input surname: ");
        String surname = scanner.nextLine();
        System.out.print("Input age (integer): ");
        String age = scanner.nextLine();
        System.out.print("Input student_id (integer): ");
        String studentId = scanner.nextLine();
        System.out.print("Input mark: ");
        String mark = scanner.nextLine();

        // проверим ввод на наличие значений: строка не является пустой и не состоит из пробелов
        if ((name != null && !name.trim().isEmpty())  &&
                (surname != null && !surname.trim().isEmpty()) &&
                (age != null && !age.trim().isEmpty()) &&
                (studentId != null && !studentId.trim().isEmpty()) &&
                (mark != null && !mark.trim().isEmpty())) {
            try {
                Integer.parseInt(age.trim());
                Integer.parseInt(studentId.trim());

                WebResource webResource = client.resource(URL);

                webResource = webResource.queryParam("studentName", name).queryParam("studentSurname",
                        surname).queryParam("studentAge", age).queryParam("studentId",
                        studentId).queryParam("studentMark", mark);

                ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
                if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
                    throw new IllegalStateException("Request failed");
                }
                System.out.println(response.getStatus());

            } catch (NumberFormatException ex) {
                System.out.println("Incorrect age or studentId value!");
            }
        }
        else {
            System.out.println("Your request is incorrect!");
        }
    }

    private static void searchStudentsByFields(Client client) {
        List<String> searchThis = new ArrayList<>();
        // Консольный ввод аргументов (по аналогии с лаб. раб. 1)
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input arguments for search (one line = one argument, input 'exit' for exit): ");
        String given_arg;
        do {
            given_arg = scanner.nextLine();
            // проверим строку на наличие аргумента: если строка не является пустой и не состоит из пробелов, то
            // добавляем аргумент в массив
            if (given_arg != null && !given_arg.trim().isEmpty()) {
                searchThis.add(given_arg);
            }
        } while (!Objects.equals(given_arg, "exit"));

        WebResource webResource = client.resource(URL);
        // Передача набора параметров может производиться при помощи queryParams,
        // который на вход принимает структуру MultivaluedMap<String, String>,
        // которая в свою очередь состоит из ключа типа String и значения типа List<String>,
        // где в качестве списка передается массив параметров для поиска.
        MultivaluedMap<String, String> reqParams = new MultivaluedMapImpl();
        reqParams.put("searchParams", searchThis);
        webResource = webResource.queryParams(reqParams);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            throw new IllegalStateException("Request failed");
        }
        GenericType<List<Student>> type = new GenericType<List<Student>>() {};

        for (Student student : response.getEntity(type)) {
            System.out.println(student);
        }
    }
}