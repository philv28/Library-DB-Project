package librarydb.web;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class DeleteMembersServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String sql = "DELETE FROM Members WHERE MemberID = ?";

        try {
            String url = "jdbc:mysql://localhost:3306/LibraryDB";
            String user = "root";
            String password = "rootpass";
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
