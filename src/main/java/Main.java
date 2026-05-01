import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

// Test the thing ig
public class Main {
    public static void main(String[] args) throws Exception {
        // Start Jetty on port 8080
        Server server = new Server(8080);


        // Set up servlet context
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Start server
        server.start();
        System.out.println("Server running at http://localhost:8080");
        server.join();
    }
}
