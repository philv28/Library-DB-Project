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
                </style>
            </head>
            <body>
            <div class='container'>
            <h1>Members</h1>
            
            <nav>
                <a href='/'>Home</a>
                <a href='/add-member'>Add Member</a>
                <a href='/queries'>Queries</a>
            </nav>
            """);

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
