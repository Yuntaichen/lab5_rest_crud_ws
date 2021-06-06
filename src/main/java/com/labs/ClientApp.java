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

        List<String> searchThis = new ArrayList<String>();

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

        for (Student student : searchStudentsByFields(client, searchThis)) {
            System.out.println(student);
        }
    }

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

}