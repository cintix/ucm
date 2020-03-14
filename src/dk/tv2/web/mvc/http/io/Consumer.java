package dk.tv2.web.mvc.http.io;

/**
 *
 * @author migo
 */
public abstract class Consumer {
    public abstract <T> T consume(Class<?> cls, byte[] data);
}
