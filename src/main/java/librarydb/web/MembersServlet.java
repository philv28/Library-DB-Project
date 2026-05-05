package librarydb.web;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;


public class MembersServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedIn") == null) {
            response.sendRedirect("/login");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("""
            <html>
            <head>
                <title>Members</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 0;
                        background: #f4f6f8;
                        color: #222;
                    }
                    
                    .container {
                        max-width: 1100px;
                        margin: 40px auto;
                        background: white;
                        padding: 30px;
                        border-radius: 12px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.08);
                    }
                    
                    h1 {
                        margin-top: 0;
                        color: #2c3e50;
                    }
                    
                    nav {
                        margin-bottom: 25px;
                    }
                    
                     nav a {
                         display: inline-block;
                         margin-right: 10px;
                         padding: 10px 14px;
                         background: #2c3e50;
                         color: white;
                         text-decoration: none;
                         border-radius: 6px;
                     }
                    
                    nav a:hover {
                        background: #1a252f;
                    }
                    
                    .queryBox {
                        background: #fafafa;
                        padding: 20px;
                        margin-bottom: 25px;
                        border-radius: 8px;
                        border: 1px solid #ddd;
                    }
                    
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        background: white;
                        margin-top: 15px;
                    }
                    
                    th {
                        background: #2c3e50;
                        color: white;
                        padding: 10px;
                        text-align: left;
                    }
                    
                    td {
                        border-bottom: 1px solid #ddd;
                        padding: 10px;
                    }
                    
                    tr:hover {
                        background: #f1f1f1;
                    }
                    
                    pre {
                        background: #f0f0f0;
                        padding: 10px;
                        overflow-x: auto;
                        border-radius: 6px;
                    }
                    
                    .btn {
                        padding: 8px 12px;
                        background: #2c3e50;
                        color: white;
                        text-decoration: none;
                        border-radius: 5px;
                        display: inline-block;
                    }
                    
                    .btn:hover {
                        background: #1a252f;
                    }
                    
                    .btn-delete {
                        background: #c0392b;
                    }
                    
                    .btn-delete:hover {
                        background: #922b21;
                    }
                    
                    
                </style>
            </head>
            <body>
            <div class='container'>
            <h1>Members</h1>
            
            <nav>
                <a href='/'>Home</a>
                <a href='/add-member'>Add Member</a>
                <a href='/queries'>Queries</a>
                <a href='/logout'>Logout</a>
            </nav>
            """);

        try {
            Dotenv dotenv = Dotenv.load();
            String url      = dotenv.get("DB_URL");
            String user     = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

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
            out.println("<th>Actions</th>");
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

                out.println("<form method='get' action='/edit-member' style='display:inline; margin-right:6px;'>");
                out.println("<input type='hidden' name='MemberID' value='" + memberID + "'>");
                out.println("<button class='btn' type='submit'>Edit</button>");
                out.println("</form>");

                out.println("<form method='post' action='/delete-member' style='display:inline;'>");
                out.println("<input type='hidden' name='MemberID' value='" + memberID + "'>");
                out.println("<button class='btn btn-delete' type='submit' onclick=\"return confirm" + "('Confirm delete');\">Delete</button>");
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

        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}
