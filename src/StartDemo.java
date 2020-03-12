
import dk.tv2.web.mvc.http.HTTP;
import dk.tv2.web.mvc.services.HenrikService;
import dk.tv2.web.mvc.services.WebService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author migo
 */
public class StartDemo {

    public StartDemo() {
        
        HTTP.register(WebService.class);
        HTTP.register(HenrikService.class);

        HTTP.start(9090);

        int ticks = 0; 
        while (ticks < (6000)) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                ticks++;
            } catch (InterruptedException interruptedException) {
            }
        }
        HTTP.stop();
    }
    
    
    public static void main(String[] args) {
        new StartDemo();
    }
}
