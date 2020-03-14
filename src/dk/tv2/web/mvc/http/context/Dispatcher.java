package dk.tv2.web.mvc.http.context;

import dk.tv2.web.mvc.http.io.Response;
import dk.tv2.web.mvc.http.utils.ParamUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dk.tv2.web.mvc.annotation.Inject;
import dk.tv2.web.mvc.annotation.Path;
import dk.tv2.web.mvc.annotation.PathParam;
import dk.tv2.web.mvc.http.io.Request;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import dk.tv2.web.mvc.annotation.ContextPath;

/**
 *
 * @author migo
 */
public class Dispatcher implements HttpHandler {

    private final Map<String, Rule> paths = new LinkedHashMap<>();
    private final Map<Class<?>, List<Field>> injectionMap = new LinkedHashMap<>();
    private List<Class<?>> handlers = new LinkedList<>();

    public Dispatcher() {
    }

    public List<Class<?>> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<Class<?>> handlers) {
        this.handlers = handlers;
        for (Class c : handlers) {
            if (c.isAnnotationPresent(ContextPath.class)) {
                ContextPath context = (ContextPath) c.getAnnotation(ContextPath.class);
                injectionMap.put(c, getFieldsWithAnnotation(Inject.class, c));
                setupPaths(context.value(), c);
            }
        }
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
                    paths.put(path.method().name() + ":" + methodPattern, rule);
                } else {
                    paths.put(path.method().name() + ":" + context + path.value(), rule);
                }
            }
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpMethod = exchange.getRequestMethod().toUpperCase();
        String requestedURL = exchange.getRequestURI().toString();
        Class requestedService = null;
        Rule rule = null;
        Method method = null;

        Response response;

        if (paths.containsKey(httpMethod + ":" + requestedURL)) {
            rule = paths.get(httpMethod + ":" + requestedURL);
        } else {
            for (String path : paths.keySet()) {
                String regex = path;
                try {
                    if (path.startsWith(httpMethod + ":")) {
                        regex = path.substring(httpMethod.length() + 1);
                        if (regex.startsWith("^") && requestedURL.matches(regex)) {
                            rule = paths.get(path);
                            break;
                        }
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

                List<Field> fields = injectionMap.get(requestedService);
                for (Field field : fields) {
                    Class<?> type = field.getType();
                    if (type == Request.class) {
                        field.setAccessible(true);
                        field.set(newInstance, new Request(exchange));
                    }
                }

                if (methodValuesCount > 0) {
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

    /**
     *
     * @param annotation
     * @param cls
     * @return
     */
    private List<Field> getFieldsWithAnnotation(Class annotation, Class<?> cls) {
        List<Field> injectionFields = new LinkedList<>();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(annotation)) {
                Class<?> type = field.getType();
                if (type == Request.class) {
                    injectionFields.add(field);
                }
            }
        }
        if (!cls.getSuperclass().getName().equals("java.lang.Object")) {
            List<Field> childFields = getFieldsWithAnnotation(annotation, cls.getSuperclass());
            injectionFields.addAll(childFields);
        }
        return injectionFields;
    }

}
