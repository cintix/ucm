package dk.tv2.web.mvc.services;

import dk.tv2.web.mvc.annotation.Context;
import dk.tv2.web.mvc.annotation.Path;
import dk.tv2.web.mvc.annotation.PathParam;
import dk.tv2.web.mvc.http.io.Response;

/**
 *
 * @author migo
 */
@Context("/henrik")
public class HenrikService {

    @Path("/navn")
    public Response sayHenrik() {
        String message = "<h1>Hello I'm Henrik</h1>";
        return new Response().setContent(message);
    }

    @Path("/calendar/${dato}/events/${id}")
    public Response seeCalendarID(@PathParam("id") int id, @PathParam("dato") String dato) {
        String message = "<h2>" + dato + "</h2> #" + id + " arbejde 9-16";
        if (id == 39) {
            message = "<h2>" + dato + "</h2> #" + id + " nøgenbadning";
        }

        return new Response().setContent(message);
    }

    @Path("/age/${age}")
    public Response tellTime(@PathParam("age") int age) {
        String status = "Nope...";
        if (age == 39) {
            status = "I'm not ready to talk about it...";
        }
        String message = "<h1>Am I " + age + " old ? " + status + "</h1>";
        return new Response().setContent(message);
    }

}
