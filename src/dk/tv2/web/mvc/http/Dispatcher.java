package dk.tv2.web.mvc.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dk.tv2.web.mvc.annotation.Context;
import dk.tv2.web.mvc.annotation.Path;
import dk.tv2.web.mvc.annotation.PathParam;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author migo
 */
public class Dispatcher implements HttpHandler {

    private final Map<String, Rule> paths = new LinkedHashMap<>();
    private List<Class<?>> handlers = new LinkedList<>();

    public Dispatcher() {
    }

    public List<Class<?>> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<Class<?>> handlers) {
        this.handlers = handlers;
        for (Class c : handlers) {
            if (c.isAnnotationPresent(Context.class)) {
                Context context = (Context) c.getAnnotation(Context.class);
                setupPaths(context.value(), c);
            }
        }
        System.out.println(paths.keySet());
    }

    private void setupPaths(String context, Class<?> c) {
        String pattern = "^\\" + context.replaceAll("/", "\\/");
        Method[] methods = c.getMethods();
        for (Method method : methods) {
            String methodPattern = pattern;
            if (method.isAnnotationPresent(Path.class)) {
                Path path = method.getAnnotation(Path.class);
                methodPattern += path.value().replaceAll("/", "\\\\/");
                Parameter[] parameterTypes = method.getParameters();
                Map<String, Integer> parameterIndex = new LinkedHashMap<>();
                
                if (parameterTypes == null) {
                    parameterTypes = new Parameter[0];
                }

                for (int paramIndex = 0; paramIndex < parameterTypes.length; paramIndex++) {
                    Parameter param = parameterTypes[paramIndex];
                    if (param.isAnnotationPresent(PathParam.class)) {
                        String paramName = "${" + param.getAnnotation(PathParam.class).value() + "}";
                        methodPattern = methodPattern.replaceAll(Pattern.quote(paramName), ParamUtil.fromParamToRegex(param));
                        parameterIndex.put(paramName, paramIndex);
                    }
                }

                Rule rule = new Rule(context + path.value(), methodPattern, method, c, parameterIndex);
                if (parameterTypes.length > 0) {
                    paths.put(methodPattern, rule);
                } else {
                    paths.put(context + path.value(), rule);
                }
            }
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestedURL = exchange.getRequestURI().toString();
        Class requestedService = null;
        Rule rule = null;
        Method method = null;

        Response response;

        if (paths.containsKey(requestedURL)) {
            rule = paths.get(requestedURL);
        } else {
            for (String path : paths.keySet()) {
                try {
                    if (path.startsWith("^") && requestedURL.matches(path)) {
                        rule = paths.get(path);
                        break;
                    }
                } catch (Exception e) {
                }
            }
        }

        if (rule == null) {
            exchange.sendResponseHeaders(404, 0);
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
            return;
        }

        try {
            requestedService = rule.getService();
            method = rule.getMethod();
            if (requestedService != null) {
                int methodValuesCount = method.getParameterCount();
                Object newInstance = requestedService.getDeclaredConstructor().newInstance();
                Object[] params = new Object[methodValuesCount];

                String[] dataValues = requestedURL.split("/");
                String[] methodValues = rule.getContext().split("/");

                int fieldsToScan = dataValues.length;

                for (int index = 0; index < fieldsToScan; index++) {
                    if (methodValues[index].trim().startsWith("${")) {
                        int methodIndex = rule.getParameterIndex(methodValues[index].trim());
                        Parameter param = method.getParameters()[methodIndex];
                        params[methodIndex] = ParamUtil.fromStringToParam(param, dataValues[index]);
                    }
                }

                response = (Response) method.invoke(newInstance, params);
                Map<String, String> headers = response.getHeaders();

                for (String key : headers.keySet()) {
                    exchange.getResponseHeaders().add(key, headers.get(key));
                }
                exchange.sendResponseHeaders(response.getReponseCode(), response.getContent().length);
                exchange.getResponseBody().write(response.getContent());
                exchange.getResponseBody().flush();
                exchange.getResponseBody().close();
            }

        } catch (Exception ex) {
            Logger.getLogger(Dispatcher.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


}
