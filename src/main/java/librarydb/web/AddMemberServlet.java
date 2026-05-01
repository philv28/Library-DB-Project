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

        out.println("""
            <html>
            <head>
                <title>Add Member</title>
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
                    
                    label {
                        display: block;
                        margin-top: 15px;
                        font-weight: bold;
                    }
                    
                    input, select {
                        width: 100%;
                        padding: 10px;
                        margin-top: 6px;
                        border: 1px solid #ccc;
                        border-radius: 6px;
                        box-sizing: border-box;
                    }
                    
                    button {
                        margin-top: 20px;
                        padding: 12px 16 px;
                        background: #2c3e50;
                        color: white;
                        border: none;
                        border-radius: 6px;
                        cursor: pointer;
                    }
                    
                    button: hover {
                        background: #1a252f;
                    }
                </style>
            </head>
            <body>
            <div class='container'>
                <h1>Add Member</h1>
            
                <nav>
                    <a href='/'>Home</a>
                    <a href='/members'>Members</a>
                    <a href-'/queries'>Queries</a>
                </nav>
                
                <form method='post' action=' /add-member'>
                    <label>Member ID</label>
                    <input type='number' name ='MemberID' min='1' required>
                    
                    <label>First Name</label>
                    <input type='text' name ='First Name' required>
                    
                    <label>Last Name</label>
                    <input type='text' name ='Last Name' required>
                    
                    <label>Address</label>
                    <input type='text' name ='Address' required>
                    
                    <label>Date of Birth</label>
                    <input type='date' name ='DateOfBirth required>
                    
                    <label>License ID</label>
                    <input type='text' name ='LicenseID' required>
                    
                    <label>Is Minor?</label>
                    <select name='IsMinor'>
                        <option value='false'>False</option>
                        <option value='true'>True</option>
                    </select>
                    
                    <button type='submit'>Add Member</button>
                </form>
            </div>
            </body>
            </html>
            """);
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