package librarydb.web;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class AddMemberServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Add Member</title></head>");
        out.println("<body style='font-family: Arial; margin: 30px;'>");

        out.println("<h1>Add Member</h1>");
        out.println("<a href='/'>Home</a> | <a href='/members'>Back to Members</a><br><br>");

        out.println("<form method='post' action='/add-member'>");
        out.println("Member ID: <input type='number' name='MemberID' required><br><br>");
        out.println("First Name: <input type='text' name='FirstName' required><br><br>");
        out.println("Last Name: <input type='text' name='LastName' required><br><br>");
        out.println("Address: <input type='text' name='Address' required><br><br>");
        out.println("Email: <input type='email' name='Email' required><br><br>");
        out.println("Date of Birth: <input type='date' name='DateOfBirth' required><br><br>");
        out.println("License ID: <input type='text' name='LicenseID' required><br><br>");
        out.println("Is Minor: ");
        out.println("<select name='IsMinor'>");
        out.println("<option value='false'>False</option>");
        out.println("<option value='true'>True</option>");
        out.println("</select><br><br>");
        out.println("<button type='submit'>Add Member</button>");
        out.println("</form>");

        out.println("</body>");
        out.println("</html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String sql = "INSERT INTO Members " +
                "(MemberID, FirstName, LastName, Address, Email, DateOfBirth, LicenseID, IsMinor) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            String url = "jdbc:mysql://localhost:3306/LibraryDB";
            String user = "root";
            String password = "rootpass";
            Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, Integer.parseInt(request.getParameter("MemberID")));
            ps.setString(2, request.getParameter("FirstName"));
            ps.setString(3, request.getParameter("LastName"));
            ps.setString(4, request.getParameter("Address"));
            ps.setString(5, request.getParameter("Email"));
            ps.setString(6, request.getParameter("DateOfBirth"));
            ps.setString(7, request.getParameter("LicenseID"));
            ps.setBoolean(8, Boolean.parseBoolean(request.getParameter("IsMinor")));

            ps.executeUpdate();
            conn.close();
            response.sendRedirect("/members");

        } catch (Exception e) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            out.println("<h3>Error adding member.</h3>");
            out.println("<a href='/add-member'>Try again</a><br><br>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
    }
}