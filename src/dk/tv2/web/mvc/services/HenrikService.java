package dk.tv2.web.mvc.services;

import dk.tv2.web.mvc.annotation.Inject;
import dk.tv2.web.mvc.annotation.Path;
import dk.tv2.web.mvc.annotation.PathParam;
import dk.tv2.web.mvc.annotation.enums.Methods;
import dk.tv2.web.mvc.http.io.Request;
import dk.tv2.web.mvc.http.io.Response;
import java.util.Map;
import dk.tv2.web.mvc.annotation.ContextPath;
import dk.tv2.web.mvc.annotation.Model;
import dk.tv2.web.mvc.services.models.GreetingCard;

/**
 *
 * @author migo
 */
@ContextPath("/henrik")
public class HenrikService {

    @Inject()
    private Request request;

    @Path(value = "/navn", method = Methods.POST)
    public Response sayHenrik(@Model GreetingCard greetingCard) {
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
        
        if (request != null) {
            Map<String, String> post = request.getPost();
            for (String key : post.keySet()) {
                message += "\n<br/><p>POST-" + key + " : " + post.get(key) + "</p>";
            }
            Map<String, String> headers = request.getHeaders();
            for (String key : headers.keySet()) {
                message += "\n<br/><p>HEADER-" + key + " : " + headers.get(key) + "</p>";
            }
        }
        
        return new Response().setContent(message);
    }

}
