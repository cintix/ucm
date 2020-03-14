/*
 */
package dk.tv2.web.mvc.http.io;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author cix
 */
public class ContentType {
    private final List<String> contentTypes = new LinkedList<>(); 
    private final Consumer consumer;
    private final Producer producer;

    public ContentType(Consumer consumer, Producer producer, String[]... contenTypes) {
        this.contentTypes.addAll(contentTypes);
        this.consumer = consumer;
        this.producer = producer;
    }

    public List<String> getContentTypes() {
        return contentTypes;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public Producer getProducer() {
        return producer;
    }
    
    public boolean match(String contentType) {
        return contentTypes.contains(contentType);
    }

    @Override
    public String toString() {
        return "ContentType{" + "contentTypes=" + contentTypes + ", consumer=" + consumer + ", producer=" + producer + '}';
    }
    
}
