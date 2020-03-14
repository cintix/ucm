
import dk.tv2.web.mvc.http.Server;
import dk.tv2.web.mvc.services.HenrikService;
import dk.tv2.web.mvc.services.WebService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author migo
 */
public class StartDemo {

    public StartDemo() {
        
        Server.register(WebService.class);
        Server.register(HenrikService.class);

        Server.start(9090);

        int ticks = 0; 
        while (ticks < (60000)) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                ticks++;
            } catch (InterruptedException interruptedException) {
            }
        }
        Server.stop();
    }
    
    
    public static void main(String[] args) {
        new StartDemo();
    }
}
