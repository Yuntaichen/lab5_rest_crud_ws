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

    @POST
    public String createStudent(
            @QueryParam("studentName") String name,
            @QueryParam("studentSurname") String surname,
            @QueryParam("studentAge") String age,
            @QueryParam("studentId") String studentId,
            @QueryParam("studentMark") String mark) {
        return new PostgreSQLDAO().createStudent(name, surname, age, studentId, mark);
    }

    @DELETE
    public String deleteStudent(@QueryParam("rowId") String rowId) {
        return new PostgreSQLDAO().deleteStudent(rowId);
    }

}