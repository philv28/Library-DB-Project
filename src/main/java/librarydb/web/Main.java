package librarydb.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class Main {
    public static void main(String[] args) throws Exception {
        // Start Jetty on port 8080
        Server server = new Server(8080);

        // Set up servlet context
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Register servlets
        context.addServlet(HomeServlet.class, "/");
        context.addServlet(MembersServlet.class, "/members");
        context.addServlet(AddMemberServlet.class, "/add-member");
        context.addServlet(EditMemberServlet.class, "/edit-member");
        context.addServlet(DeleteMembersServlet.class, "/delete-member");
        context.addServlet(QueriesServlet.class, "/queries");
        context.addServlet(BooksServlet.class, "/books");
        context.addServlet(TrendingBooksServlet.class, "/trending-books");
        context.addServlet(AddBookServlet.class, "/add-book");
        context.addServlet(DeleteBookServlet.class, "/delete-book");
        context.addServlet(EditBookServlet.class, "/edit-book");
        context.addServlet(LoginServlet.class, "/login");
        context.addServlet(LogoutServlet.class, "/logout");
        context.addServlet(MemberLoginServlet.class, "/member-login");
        context.addServlet(MyAccountServlet.class, "/my-account");
        context.addServlet(MyActivityServlet.class, "/my-activity");



        // Start server
        server.start();
        System.out.println("Server running at http://localhost:8080");
        server.join();
    }
}
