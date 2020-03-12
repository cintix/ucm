package dk.tv2.web.mvc.annotation;

import dk.tv2.web.mvc.annotation.enums.ContentTypes;
import dk.tv2.web.mvc.annotation.enums.Methods;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author migo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Path {
    public String value();
    public Methods method() default Methods.GET;    
    public ContentTypes contentType() default ContentTypes.PLAIN;    
}
