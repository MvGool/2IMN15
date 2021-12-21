import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;

public class Server {
    public static void setupServer() {
        LeshanServerBuilder builder = new LeshanServerBuilder();
        LeshanServer server = builder.build();
        server.start();
    }

    public static void main(String args[]) {
        setupServer();
    }
}
