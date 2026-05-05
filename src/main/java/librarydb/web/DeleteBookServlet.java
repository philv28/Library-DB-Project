package librarydb.web;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

public class DeleteBookServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedIn") == null) {
            response.sendRedirect("/login");
            return;
        }

        try {
            Dotenv dotenv = Dotenv.load();
            String url      = dotenv.get("DB_URL");
            String user     = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            Connection conn = DriverManager.getConnection(url, user, password);

            String isbn = request.getParameter("ISBN");

            // Get the AuthorID before deleting
            PreparedStatement getAuthor = conn.prepareStatement("SELECT AuthorID FROM WrittenBy WHERE ISBN = ?");
            getAuthor.setString(1, isbn);
            ResultSet rsAuthor = getAuthor.executeQuery();
            int authorID = rsAuthor.next() ? rsAuthor.getInt("AuthorID") : 0;

            // Delete from WrittenBy first
            PreparedStatement ps1 = conn.prepareStatement("DELETE FROM WrittenBy WHERE ISBN = ?");
            ps1.setString(1, isbn);
            ps1.executeUpdate();

            // Delete the Author too
            if (authorID != 0) {
                PreparedStatement deleteAuthor = conn.prepareStatement("DELETE FROM Authors WHERE AuthorID = ?");
                deleteAuthor.setInt(1, authorID);
                deleteAuthor.executeUpdate();
            }

            // Delete from BookGenres
            PreparedStatement ps2 = conn.prepareStatement("DELETE FROM BookGenres WHERE ISBN = ?");
            ps2.setString(1, isbn);
            ps2.executeUpdate();

            // Delete from BookCopies
            PreparedStatement ps3 = conn.prepareStatement("DELETE FROM BookCopies WHERE ISBN = ?");
            ps3.setString(1, isbn);
            ps3.executeUpdate();

            // Delete from BookTitles last
            PreparedStatement ps4 = conn.prepareStatement("DELETE FROM BookTitles WHERE ISBN = ?");
            ps4.setString(1, isbn);
            ps4.executeUpdate();

            conn.close();
            response.sendRedirect("/books");

        } catch (Exception e) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<h3>Error deleting book.</h3>");
            out.println("<a href='/books'>Back</a><br><br>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
    }
}