package dk.tv2.web.mvc.http;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author migo
 */
public class HTTP {

    private static final List<Class<?>> registeredClasses = new LinkedList<>();
    private static int port = 8080;
    private static HTTP instance;

    private final Dispatcher dispatcher = new Dispatcher();
    private volatile boolean running = false;
    private HttpServer server;

    private HTTP() {

    }

    public static void register(Class<?> cls) {
        if (!registeredClasses.contains(cls)) {
            registeredClasses.add(cls);
        }
    }

    public static void stop() {
        if (instance != null) {
            instance.internalStop();
        }
    }

    public static void start() {
        start(port);
    }

    public static void start(int port) {
        HTTP.port = port;
        instance = initilizeService();
    }

    private static HTTP initilizeService() {
        HTTP http = new HTTP();
        http.internalStart(HTTP.port);
        return http;
    }

    public void internalStop() {
        try {
            server.stop(0);
        } catch (Exception e) {
        }
    }

    public void internalStart(int port) {
        try {
            dispatcher.setHandlers(registeredClasses);
            server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
            server.createContext("/", dispatcher);
            server.setExecutor(null);
            server.start();
        } catch (IOException ex) {
            Logger.getLogger(HTTP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
