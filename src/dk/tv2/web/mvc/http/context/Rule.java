package dk.tv2.web.mvc.http.context;

import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * @author migo
 */
public class Rule {

    private final String context;
    private final String pattern;
    private final Method method;
    private final Class service;
    private final Map<String,Integer> parameterIndex;
    
    public Rule(String context, String pattern, Method method, Class service, Map<String,Integer> parameterIndex) {
        this.context = context;
        this.pattern = pattern;
        this.method = method;
        this.service = service;
        this.parameterIndex = parameterIndex;
    }

    public String getPattern() {
        return pattern;
    }

    public Method getMethod() {
        return method;
    }

    public Class getService() {
        return service;
    }

    public String getContext() {
        return context;
    }

    public int getParameterIndex(String name) {
        return parameterIndex.get(name);
    }

    @Override
    public String toString() {
        return "Rule{" + "context=" + context + ", pattern=" + pattern + ", method=" + method + ", service=" + service + '}';
    }

}
