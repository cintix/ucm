/*
 */
package dk.tv2.web.mvc.consumers;

import com.google.gson.Gson;
import dk.tv2.web.mvc.http.io.Consumer;

/**
 *
 * @author cix
 */
public class JSONConsumer extends Consumer{
    private final Gson json = new Gson();
    
    @Override
    public <T> T consume(Class<?> cls, byte[] data) {
        return (T) json.fromJson(new String(data,0,data.length),cls);
    }
    
}
