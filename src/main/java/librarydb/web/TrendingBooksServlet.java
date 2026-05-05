package librarydb.web;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class TrendingBooksServlet extends HttpServlet {

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
            <title>Trending Books</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    margin: 0; background: #f4f6f8; color: #222;
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
            </style>
        </head>
        <body>
        <div class='container'>
            <h1>Trending Books</h1>

            <nav>
                <a href='/'>Home</a>
                <a href='/members'>Members</a>
                <a href='/queries'>Queries</a>
                <a href='/books'>View Books</a>
                <a href='/logout'>Logout</a>
            </nav>

            <p>Shows books ranked by how many times they have been borrowed.</p>
        """);

        String sql =
                "SELECT bt.ISBN, bt.Title, COUNT(bl.LoanID) AS TimesBorrowed " +
                        "FROM BookTitles bt " +
                        "JOIN BookCopies bc ON bt.ISBN = bc.ISBN " +
                        "JOIN BookLoans bl ON bc.CopyID = bl.CopyID " +
                        "GROUP BY bt.ISBN, bt.Title " +
                        "ORDER BY TimesBorrowed DESC";

        try {
            Dotenv dotenv = Dotenv.load();
            String url      = dotenv.get("DB_URL");
            String user     = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            Connection conn = DriverManager.getConnection(url, user, password);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            out.println("<table>");
            out.println("<tr>");
            out.println("<th>ISBN</th>");
            out.println("<th>Title</th>");
            out.println("<th>Times Borrowed</th>");
            out.println("</tr>");

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getString("ISBN") + "</td>");
                out.println("<td>" + rs.getString("Title") + "</td>");
                out.println("<td>" + rs.getInt("TimesBorrowed") + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");

            conn.close();

        } catch (Exception e) {
            out.println("<h3>Error loading trending books.</h3>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }

        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}