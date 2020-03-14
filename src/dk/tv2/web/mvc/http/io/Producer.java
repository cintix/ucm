package dk.tv2.web.mvc.http.io;

/**
 *
 * @author migo
 */
public abstract class Producer {
    public abstract <T> byte[] produce(T object);
}
