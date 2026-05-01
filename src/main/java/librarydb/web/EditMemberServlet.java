package librarydb.web;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import io.github.cdimascio.dotenv.Dotenv;

public class EditMemberServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int memberID = Integer.parseInt(request.getParameter("MemberID"));

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("""
        <html>
        <head>
            <title>Edit Member</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 0; background: #f4f6f8; color: #222; }
                .container { max-width: 700px; margin: 40px auto; background: white; padding: 30px; border-radius: 12px; box-shadow: 0 2px 10px rgba(0,0,0,0.08); }
                h1 { margin-top: 0; color: #2c3e50; }
                nav { margin-bottom: 25px; }
                nav a { display: inline-block; margin-right: 10px; padding: 10px 14px; background: #2c3e50; color: white; text-decoration: none; border-radius: 6px; }
                nav a:hover { background: #1a252f; }
                label { display: block; margin-top: 15px; font-weight: bold; }
                input, select { width: 100%; padding: 10px; margin-top: 6px; border: 1px solid #ccc; border-radius: 6px; box-sizing: border-box; }
                button { margin-top: 20px; padding: 12px 16px; background: #2c3e50; color: white; border: none; border-radius: 6px; cursor: pointer; }
                button:hover { background: #1a252f; }
            </style>
        </head>
        <body>
        <div class='container'>
            <h1>Edit Member</h1>
            <nav>
                <a href='/'>Home</a>
                <a href='/members'>Members</a>
                <a href='/queries'>Queries</a>
            </nav>
        """);

        try {
            Dotenv dotenv = Dotenv.load();
            String url      = dotenv.get("DB_URL");
            String user     = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            Connection conn = DriverManager.getConnection(url, user, password);

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM Members WHERE MemberID = ?"
            );
            ps.setInt(1, memberID);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                boolean isMinor = rs.getBoolean("IsMinor");

                out.println("<form method='post' action='/edit-member'>");

                out.println("<label>Member ID</label>");
                out.println("<input type='number' name='MemberID' value='" + rs.getInt("MemberID") + "' readonly>");

                out.println("<label>First Name</label>");
                out.println("<input type='text' name='FirstName' value='" + rs.getString("FirstName") + "' required>");

                out.println("<label>Last Name</label>");
                out.println("<input type='text' name='LastName' value='" + rs.getString("LastName") + "' required>");

                out.println("<label>Address</label>");
                out.println("<input type='text' name='Address' value='" + rs.getString("Address") + "'>");

                out.println("<label>Email</label>");
                out.println("<input type='email' name='Email' value='" + rs.getString("Email") + "' required>");

                out.println("<label>Date of Birth</label>");
                out.println("<input type='date' name='DateOfBirth' value='" + rs.getDate("DateOfBirth") + "' required>");

                out.println("<label>License ID</label>");
                out.println("<input type='text' name='LicenseID' value='" + rs.getString("LicenseID") + "'>");

                out.println("<label>Is Minor?</label>");
                out.println("<select name='IsMinor'>");
                out.println("<option value='false'" + (!isMinor ? " selected" : "") + ">False</option>");
                out.println("<option value='true'" + (isMinor ? " selected" : "") + ">True</option>");
                out.println("</select>");

                out.println("<button type='submit'>Save Changes</button>");
                out.println("</form>");
            } else {
                out.println("<h3>Member not found.</h3>");
                out.println("<a href='/members'>Back to Members</a>");
            }

            conn.close();

        } catch (Exception e) {
            out.println("<h3>Error loading edit form.</h3>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }

        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String sql = "UPDATE Members SET FirstName=?, LastName=?, Address=?, Email=?, DateOfBirth=?, LicenseID=?, IsMinor=? WHERE MemberID=?";

        try {
            Dotenv dotenv = Dotenv.load();
            String url      = dotenv.get("DB_URL");
            String user     = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, request.getParameter("FirstName"));
            ps.setString(2, request.getParameter("LastName"));
            ps.setString(3, request.getParameter("Address"));
            ps.setString(4, request.getParameter("Email"));
            ps.setString(5, request.getParameter("DateOfBirth"));
            ps.setString(6, request.getParameter("LicenseID"));
            ps.setBoolean(7, Boolean.parseBoolean(request.getParameter("IsMinor")));
            ps.setInt(8, Integer.parseInt(request.getParameter("MemberID")));

            ps.executeUpdate();

            conn.close();
            response.sendRedirect("/members");

        } catch (Exception e) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            out.println("<h3>Error updating member.</h3>");
            out.println("<a href='/members'>Back to Members</a>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
    }
}