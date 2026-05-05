package librarydb.web;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import io.github.cdimascio.dotenv.Dotenv;

public class DeleteMembersServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedIn") == null) {
            response.sendRedirect("/login");
            return;
        }

        String sql = "DELETE FROM Members WHERE MemberID = ?";

        try {
            Dotenv dotenv = Dotenv.load();
            String url      = dotenv.get("DB_URL");
            String user     = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement ps = conn.prepareStatement(sql);

            int memberID = Integer.parseInt(request.getParameter("MemberID"));
            ps.setInt(1, memberID);

            ps.executeUpdate();

            conn.close();

            response.sendRedirect("/members");

        } catch (Exception e) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            out.println("<h3>Error deleting member.</h3>");
            out.println("<a href='/members'>Back</a><br><br>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
    }
}
