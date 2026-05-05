package librarydb.web;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

public class BooksServlet extends HttpServlet {
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
                <title>Books</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; background: #f4f6f8; color: #222; }
                    .container { max-width: 1100px; margin: 40px auto; background: white; padding: 30px; border-radius: 12px; box-shadow: 0 2px 10px rgba(0,0,0,0.08); }
                    h1 { margin-top: 0; color: #2c3e50; }
                    nav { margin-bottom: 25px; }
                    nav a { display: inline-block; margin-right: 10px; padding: 10px 14px; background: #2c3e50; color: white; text-decoration: none; border-radius: 6px; }
                    nav a:hover { background: #1a252f; }
                    table { width: 100%; border-collapse: collapse; background: white; margin-top: 15px; }
                    th { background: #2c3e50; color: white; padding: 10px; text-align: left; }
                    td { border-bottom: 1px solid #ddd; padding: 10px; }
                    tr:hover { background: #f1f1f1; }
                    .btn { padding: 8px 12px; background: #2c3e50; color: white; border: none; border-radius: 5px; cursor: pointer; }
                    .btn:hover { background: #1a252f; }
                    .dropdown { position: relative; display: inline-block; }
                    .dropdown-menu { display: none; position: absolute; background: white; border: 1px solid #ccc; border-radius: 6px; z-index: 100; min-width: 120px; box-shadow: 0 2px 8px rgba(0,0,0,0.15); }
                    .dropdown:hover .dropdown-menu { display: block; }
                    .dropdown-item { display: block; width: 100%; padding: 10px 14px; background: none; border: none; text-align: left; cursor: pointer; font-size: 14px; }
                    .dropdown-item:hover { background: #f1f1f1; }
                </style>
            </head>
            <body>
            <div class='container'>
            <h1>Books</h1>

            <nav>
                <a href='/'>Home</a>
                <a href='/members'>Members</a>
                <a href='/add-member'>Add Member</a>
                <a href='/queries'>Queries</a>
                <a href='/trending-books'>View Trending Books</a>
                <a href='/add-book'>Add Book</a>
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

            ResultSet rs = stmt.executeQuery("""
                SELECT
                    bt.ISBN,
                    bt.Title,
                    bt.Publisher,
                    bt.PublicationYear,
                    GROUP_CONCAT(DISTINCT CONCAT(a.FirstName, ' ', a.LastName) SEPARATOR ', ') AS Authors,
                    GROUP_CONCAT(DISTINCT g.GenreName SEPARATOR ', ') AS Genres,
                    SUM(CASE WHEN bc.Status = 'Available' THEN 1 ELSE 0 END) AS AvailableCopies
                FROM BookTitles bt
                LEFT JOIN WrittenBy wb ON bt.ISBN = wb.ISBN
                LEFT JOIN Authors a ON wb.AuthorID = a.AuthorID
                LEFT JOIN BookGenres bg ON bt.ISBN = bg.ISBN
                LEFT JOIN Genres g ON bg.GenreID = g.GenreID
                LEFT JOIN BookCopies bc ON bt.ISBN = bc.ISBN
                GROUP BY bt.ISBN, bt.Title, bt.Publisher, bt.PublicationYear
                ORDER BY bt.Title
            """);

            out.println("<table>");
            out.println("<tr>");
            out.println("<th>ISBN</th>");
            out.println("<th>Title</th>");
            out.println("<th>Author(s)</th>");
            out.println("<th>Genre(s)</th>");
            out.println("<th>Publisher</th>");
            out.println("<th>Publication Year</th>");
            out.println("<th>Available Copies</th>");
            out.println("<th>Actions</th>");
            out.println("</tr>");

            while (rs.next()) {
                String isbn = rs.getString("ISBN");
                out.println("<tr>");
                out.println("<td>" + rs.getString("ISBN") + "</td>");
                out.println("<td>" + rs.getString("Title") + "</td>");
                out.println("<td>" + rs.getString("Authors") + "</td>");
                out.println("<td>" + rs.getString("Genres") + "</td>");
                out.println("<td>" + rs.getString("Publisher") + "</td>");
                out.println("<td>" + rs.getInt("PublicationYear") + "</td>");
                out.println("<td>" + rs.getInt("AvailableCopies") + "</td>");
                out.println("<td>");
                out.println("<div class='dropdown'>");
                out.println("<button class='btn dropdown-toggle'>Actions ▼</button>");
                out.println("<div class='dropdown-menu'>");
                out.println("<form method='get' action='/edit-book'>");
                out.println("<input type='hidden' name='ISBN' value='" + isbn + "'>");
                out.println("<button class='dropdown-item' type='submit'>Edit</button>");
                out.println("</form>");
                out.println("<form method='post' action='/delete-book'>");
                out.println("<input type='hidden' name='ISBN' value='" + isbn + "'>");
                out.println("<button class='dropdown-item' type='submit' onclick=\"return confirm('Confirm delete');\">Delete</button>");
                out.println("</form>");
                out.println("</div>");
                out.println("</div>");
                out.println("</td>");
                out.println("</tr>");
            }


            out.println("</table>");
            out.println("</div>"); 
            conn.close();

        } catch (Exception e) {
            out.println("<h3>Error loading books.</h3>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }

        out.println("</body>");
        out.println("</html>");
    }
}