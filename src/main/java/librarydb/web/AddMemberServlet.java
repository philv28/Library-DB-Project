package librarydb.web;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.net.URLEncoder;
import java.time.LocalDate;

import io.github.cdimascio.dotenv.Dotenv;

public class AddMemberServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String memberError = request.getParameter("memberError");
        String emailError = request.getParameter("emailError");
        String dateError = request.getParameter("dateError");

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
                        padding: 12px 16px;
                        background: #2c3e50;
                        color: white;
                        border: none;
                        border-radius: 6px;
                        cursor: pointer;
                    }
                    
                    button:hover {
                        background: #1a252f;
                    }
                    
                    .error {
                        color: #c0392b;
                        font-weight: bold;
                        margin-top: 5px;
                    }

                </style>
            </head>
            <body>
            <div class='container'>
                <h1>Add Member</h1>
            
                <nav>
                    <a href='/'>Home</a>
                    <a href='/members'>Members</a>
                    <a href='/queries'>Queries</a>
                </nav>
                
                <form method='post' action='/add-member'>
                    <label>Member ID</label>
                    <input type='number' name ='MemberID' min='1' required>
            """);
        if (memberError != null) {
            out.println("<p class='error'>" + memberError + "</p>");
        }
        out.println("""
                    <label>First Name</label>
                    <input type='text' name ='FirstName' required>
                    
                    <label>Last Name</label>
                    <input type='text' name ='LastName' required>
                    
                    <label>Address</label>
                    <input type='text' name ='Address' required>
                    
                    <label>Date of Birth</label>
                    <input type='date' name='DateOfBirth' required>
            """);
        if (dateError != null) {
            out.print("<p class='error'>" + dateError + "</p>");
        }

        out.println("""        
                    <label>License ID</label>
                    <input type='text' name ='LicenseID' >

                    <label>Email</label>
                    <input type='email' name ='Email' required>
            """);
        if (emailError != null) {
            out.print("<p class='error'>" + emailError + "</p>");
        }
        out.print("""
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

        int memberID = Integer.parseInt(request.getParameter("MemberID"));

        String dobString = request.getParameter("DateOfBirth");
        LocalDate dob = LocalDate.parse(dobString);
        LocalDate today = LocalDate.now();

        if (dob.isAfter(today)) {
            response.sendRedirect("/add-member?MemberID=" + memberID + "&dateError=" +
                    URLEncoder.encode("Error: Cannot select future date", StandardCharsets.UTF_8));
            return;
        }

        String sql = "INSERT INTO Members " +
                "(MemberID, FirstName, LastName, Address, Email, DateOfBirth, LicenseID, IsMinor) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Dotenv dotenv = Dotenv.load();
            String url      = dotenv.get("DB_URL");
            String user     = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            Connection conn = DriverManager.getConnection(url, user, password);

            PreparedStatement check = conn.prepareStatement(
                    "SELECT MemberID FROM Members WHERE MemberID = ?"
            );
            check.setInt(1, memberID);

            ResultSet checkResult = check.executeQuery();

            if (checkResult.next()) {
                conn.close();
                response.sendRedirect("/add-member?memberError=" +
                        URLEncoder.encode("A member with that Member ID already exists",
                                StandardCharsets.UTF_8));
                return;
            }

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, memberID);
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

        } catch (SQLIntegrityConstraintViolationException e) {
            String message = e.getMessage();

            if (message.contains("PRIMARY")) {
                response.sendRedirect("/add-member?memberError=" +
                        URLEncoder.encode("Error: Member ID already exists", StandardCharsets.UTF_8));
                return;
            } else if(message.contains("Email")) {
                response.sendRedirect("/add-member?emailError=" +
                        URLEncoder.encode("Error: Email already exists", StandardCharsets.UTF_8));
                return;
            } else if (message.contains("Date of Birth")) {
                response.sendRedirect("/add-member?dateError=" +
                        URLEncoder.encode("Error: Cannot select future date", StandardCharsets.UTF_8));
                return;
            }

            response.sendRedirect("/add-member?memberError=" +
                    URLEncoder.encode("Error: Invalid or duplicate member information", StandardCharsets.UTF_8));

        } catch (Exception e) {
            String message = e.getMessage();

            if (message != null && (message.contains("Date") || message.contains("Birth") || message.contains("future"))) {
                response.sendRedirect("/add-member?dateError=" +
                        URLEncoder.encode("Error: Cannot select future date", StandardCharsets.UTF_8));
                return;
            }

            response.sendRedirect("/add-member?memberError=" +
                    URLEncoder.encode("Error: Member ID already exists.", StandardCharsets.UTF_8));;
        }
    }
}