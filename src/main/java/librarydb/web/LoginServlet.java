package librarydb.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import io.github.cdimascio.dotenv.Dotenv;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String error = request.getParameter("error");

        out.println("""
            <html>
            <head>
                <title>Login</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 0;
                        background: #f4f6f8;
                        color: #222;
                    }
                    .container {
                        max-width: 420px;
                        margin: 80px auto;
                        background: white;
                        padding: 30px;
                        border-radius: 12px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.08);
                    }
                    h1 {
                        margin-top: 0;
                        color: #2c3e50;
                        text-align: center;
                    }
                    label {
                        display: block;
                        margin-top: 15px;
                        font-weight: bold;
                    }
                    input {
                        width: 100%;
                        padding: 10px;
                        margin-top: 6px;
                        border: 1px solid #ccc;
                        border-radius: 6px;
                        font-size: 14px;
                        box-sizing: border-box;
                    }
                    .btn {
                        width: 100%;
                        margin-top: 22px;
                        padding: 10px 14px;
                        background: #2c3e50;
                        color: white;
                        border: none;
                        border-radius: 6px;
                        cursor: pointer;
                        font-size: 15px;
                    }
                    .btn:hover {
                        background: #1a252f;
                    }
                    .error {
                        background: #ffe5e5;
                        color: #b00020;
                        border: 1px solid #ffb3b3;
                        padding: 10px;
                        border-radius: 6px;
                        margin-bottom: 15px;
                        text-align: center;
                    }
                </style>
            </head>
            <body>
            <div class='container'>
                <h1>Employee Login</h1>
            """);

        if ("1".equals(error)) {
            out.println("<div class='error'>Invalid email or password. Please try again.</div>");
        }

        out.println("""
                <form method='post' action='/login'>
                    <label for='username'>Email</label>
                    <input type='email' id='username' name='username' required>

                    <label for='password'>Password</label>
                    <input type='password' id='password' name='password' required>

                    <button class='btn' type='submit'>Login</button>
                </form>
            </div>
            </body>
            </html>
            """);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String email = request.getParameter("username");
        String password = request.getParameter("password");

        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            response.sendRedirect("/login?error=1");
            return;
        }

        try {
            Dotenv dotenv = Dotenv.load();

            String url = dotenv.get("DB_URL");
            String dbUser = dotenv.get("DB_USER");
            String dbPassword = dotenv.get("DB_PASSWORD");

            String sql = """
                SELECT EmployeeID, Position, `password`
                FROM Employees
                WHERE Email = ?
            """;

            try (
                Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
                PreparedStatement stmt = conn.prepareStatement(sql)
            ) {
                stmt.setString(1, email);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int employeeID = rs.getInt("EmployeeID");
                        String position = rs.getString("Position");
                        String storedPassword = rs.getString("password");

                        if (password.equals(storedPassword)) {
                            HttpSession session = request.getSession();
                            session.setAttribute("loggedIn", true);
                            session.setAttribute("employeeID", employeeID);
                            session.setAttribute("position", position);

                            response.sendRedirect("/");
                            return;
                        }
                    }
                }
            }

            response.sendRedirect("/login?error=1");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("/login?error=1");
        }
    }
}