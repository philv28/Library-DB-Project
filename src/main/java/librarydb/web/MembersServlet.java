package librarydb.web;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class MembersServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Members</title>");
        out.println("<style>");
        out.println("body { font-family: Arial; margin: 30px; background: #f7f7f7; }");
        out.println("table { border-collapse: collapse; background: white; }");
        out.println("th, td { border: 1px solid #ccc; padding: 8px; }");
        out.println("th { background: #eee; }");
        out.println("a { margin-right: 15px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<h1>Members</h1>");
        out.println("<a href='/'>Home</a>");
        out.println("<a href='/add-member'>Add Member</a>");
        out.println("<br><br>");

        try {
            String url = "jdbc:mysql://localhost:3306/LibraryDB";
            String user = "root";
            String password = "rootpass";
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Members");

            out.println("<table>");
            out.println("<tr>");
            out.println("<th>Member ID</th>");
            out.println("<th>First Name</th>");
            out.println("<th>Last Name</th>");
            out.println("<th>Address</th>");
            out.println("<th>Email</th>");
            out.println("<th>Date of Birth</th>");
            out.println("<th>License ID</th>");
            out.println("<th>Minor?</th>");
            out.println("<th>Actions</th");
            out.println("</tr>");

            while (rs.next()) {
                int memberID = rs.getInt("MemberID");
                out.println("<tr>");

                out.println("<td>" + memberID + "</td>");
                out.println("<td>" + rs.getString("FirstName") + "</td>");
                out.println("<td>" + rs.getString("LastName") + "</td>");
                out.println("<td>" + rs.getString("Address") + "</td>");
                out.println("<td>" + rs.getString("Email") + "</td>");
                out.println("<td>" + rs.getDate("DateOfBirth") + "</td>");
                out.println("<td>" + rs.getString("LicenseID") + "</td>");
                out.println("<td>" + rs.getBoolean("IsMinor") + "</td>");

                out.println("<td>");
                out.println("<form method='post' action='/delete-member' style='display:inline;'>");
                out.println("<input type='hidden' name='MemberID' value='" + memberID + "'>");
                out.println("<button type='submit'>Delete</button>");
                out.println("</form>");
                out.println("</td>");

                out.println("</tr>");
            }

            out.println("</table>");
            conn.close();

        } catch (Exception e) {
            out.println("<h3>Error loading members.</h3>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }

        out.println("</body>");
        out.println("</html>");
    }
}
