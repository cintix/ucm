package dk.tv2.web.mvc.services;

import dk.tv2.web.mvc.annotation.Context;
import dk.tv2.web.mvc.annotation.Path;
import dk.tv2.web.mvc.annotation.PathParam;
import dk.tv2.web.mvc.http.io.Response;
import java.util.Date;

/**
 *
 * @author migo
 */
@Context("/web")
public class WebService {

    @Path("/hello/${name}")
    public Response sayHello(@PathParam("name") String name) {
        String message = "<h1>Hello " + name + "</h1>";      
        return new Response().setContent(message);
    }

    @Path("/time")
    public Response tellTime() {
        String message = "<h1>Time is " + new Date() + "</h1>";
        return new Response().setContent(message);
    }

}
