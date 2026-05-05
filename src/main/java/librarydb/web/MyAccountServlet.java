package librarydb.web;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import io.github.cdimascio.dotenv.Dotenv;

public class MyAccountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedIn") == null
                || !"member".equals(session.getAttribute("userType"))) {
            response.sendRedirect("/member-login");
            return;
        }

        int memberID = (int) session.getAttribute("memberID");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("""
            <html>
            <head>
                <title>My Account</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 0;
                        background: #f4f6f8;
                        color: #222;
                    }
                    .container {
                        max-width: 900px;
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
                    .account-card {
                        border: 1px solid #ddd;
                        border-radius: 10px;
                        overflow: hidden;
                        margin-top: 15px;
                    }
                    .row {
                        display: flex;
                        border-bottom: 1px solid #eee;
                    }
                    .row:last-child {
                        border-bottom: none;
                    }
                    .label {
                        width: 220px;
                        background: #f4f6f8;
                        font-weight: bold;
                        padding: 12px;
                        color: #2c3e50;
                    }
                    .value {
                        flex: 1;
                        padding: 12px;
                    }
                    .error {
                        background: #ffe5e5;
                        color: #b00020;
                        border: 1px solid #ffb3b3;
                        padding: 10px;
                        border-radius: 6px;
                        margin-top: 15px;
                    }
                </style>
            </head>
            <body>
            <div class='container'>
                <h1>My Account</h1>

                <nav>
                    <a href='/my-activity'>My Activity</a>
                    <a href='/logout'>Logout</a>
                </nav>
            """);

        try {
            Dotenv dotenv = Dotenv.load();

            String url = dotenv.get("DB_URL");
            String dbUser = dotenv.get("DB_USER");
            String dbPassword = dotenv.get("DB_PASSWORD");

            String sql = """
                SELECT *
                FROM MemberAccountView
                WHERE MemberID = ?
            """;

            try (
                Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
                PreparedStatement stmt = conn.prepareStatement(sql)
            ) {
                stmt.setInt(1, memberID);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        out.println("<div class='account-card'>");

                        out.println("<div class='row'><div class='label'>Member ID</div><div class='value'>"
                                + rs.getInt("MemberID") + "</div></div>");

                        out.println("<div class='row'><div class='label'>First Name</div><div class='value'>"
                                + rs.getString("FirstName") + "</div></div>");

                        out.println("<div class='row'><div class='label'>Last Name</div><div class='value'>"
                                + rs.getString("LastName") + "</div></div>");

                        out.println("<div class='row'><div class='label'>Email</div><div class='value'>"
                                + rs.getString("Email") + "</div></div>");

                        out.println("<div class='row'><div class='label'>Address</div><div class='value'>"
                                + rs.getString("Address") + "</div></div>");

                        out.println("<div class='row'><div class='label'>Date of Birth</div><div class='value'>"
                                + rs.getDate("DateOfBirth") + "</div></div>");

                        out.println("<div class='row'><div class='label'>Card Number</div><div class='value'>"
                                + rs.getString("CardNumber") + "</div></div>");

                        out.println("<div class='row'><div class='label'>Card Status</div><div class='value'>"
                                + rs.getString("CardStatus") + "</div></div>");

                        out.println("<div class='row'><div class='label'>Expiration Date</div><div class='value'>"
                                + rs.getDate("ExpireDate") + "</div></div>");

                        out.println("</div>");
                    } else {
                        out.println("<div class='error'>No account information found.</div>");
                    }
                }
            }

        } catch (Exception e) {
            out.println("<div class='error'>Error loading account information.</div>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }

        out.println("""
            </div>
            </body>
            </html>
            """);
    }
}