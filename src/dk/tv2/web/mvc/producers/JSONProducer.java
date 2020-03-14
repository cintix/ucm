/*
 */
package dk.tv2.web.mvc.producers;

import com.google.gson.Gson;
import dk.tv2.web.mvc.http.io.Producer;

/**
 *
 * @author cix
 */
public class JSONProducer extends Producer {
    private final Gson json = new Gson();
    
    @Override
    public <T> byte[] produce(T object) {
        String data = json.toJson(object);
        return data.getBytes();
    }
    
}
