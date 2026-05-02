package librarydb.web;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

public class EditBookServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String isbn = request.getParameter("ISBN");

        try {
            Dotenv dotenv = Dotenv.load();
            String url      = dotenv.get("DB_URL");
            String user     = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            Connection conn = DriverManager.getConnection(url, user, password);

            // Get book info
            PreparedStatement ps1 = conn.prepareStatement("SELECT * FROM BookTitles WHERE ISBN = ?");
            ps1.setString(1, isbn);
            ResultSet rs1 = ps1.executeQuery();

            // Get author info
            PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT a.AuthorID, a.FirstName, a.LastName FROM Authors a JOIN WrittenBy wb ON a.AuthorID = wb.AuthorID WHERE wb.ISBN = ?");
            ps2.setString(1, isbn);
            ResultSet rs2 = ps2.executeQuery();

            // Get genre info
            PreparedStatement ps3 = conn.prepareStatement(
                    "SELECT g.GenreID, g.GenreName FROM Genres g JOIN BookGenres bg ON g.GenreID = bg.GenreID WHERE bg.ISBN = ?");
            ps3.setString(1, isbn);
            ResultSet rs3 = ps3.executeQuery();

            // Get total copies
            PreparedStatement ps4 = conn.prepareStatement(
                    "SELECT COUNT(CopyID) AS TotalCopies FROM BookCopies WHERE ISBN = ?");
            ps4.setString(1, isbn);
            ResultSet rs4 = ps4.executeQuery();

            if (rs1.next()) {
                String title = rs1.getString("Title");
                String publisher = rs1.getString("Publisher");
                int year = rs1.getInt("PublicationYear");

                int authorID = 0;
                String authorFirst = "";
                String authorLast = "";
                if (rs2.next()) {
                    authorID = rs2.getInt("AuthorID");
                    authorFirst = rs2.getString("FirstName");
                    authorLast = rs2.getString("LastName");
                }

                int genreID = 0;
                String genreName = "";
                if (rs3.next()) {
                    genreID = rs3.getInt("GenreID");
                    genreName = rs3.getString("GenreName");
                }

                int totalCopies = 0;
                if (rs4.next()) {
                    totalCopies = rs4.getInt("TotalCopies");
                }

                out.println("""
                    <html>
                    <head>
                        <title>Edit Book</title>
                        <style>
                            body { font-family: Arial, sans-serif; margin: 0; background: #f4f6f8; color: #222; }
                            .container { max-width: 1100px; margin: 40px auto; background: white; padding: 30px; border-radius: 12px; box-shadow: 0 2px 10px rgba(0,0,0,0.08); }
                            h1 { margin-top: 0; color: #2c3e50; }
                            nav { margin-bottom: 25px; }
                            nav a { display: inline-block; margin-right: 10px; padding: 10px 14px; background: #2c3e50; color: white; text-decoration: none; border-radius: 6px; }
                            nav a:hover { background: #1a252f; }
                            label { display: block; margin-top: 15px; font-weight: bold; }
                            input { width: 100%; padding: 10px; margin-top: 6px; border: 1px solid #ccc; border-radius: 6px; box-sizing: border-box; }
                            button { margin-top: 20px; padding: 12px 16px; background: #2c3e50; color: white; border: none; border-radius: 6px; cursor: pointer; }
                            button:hover { background: #1a252f; }
                        </style>
                    </head>
                    <body>
                    <div class='container'>
                        <h1>Edit Book</h1>
                        <nav>
                            <a href='/'>Home</a>
                            <a href='/books'>Books</a>
                        </nav>
                """);

                out.println("<form method='post' action='/edit-book'>");
                out.println("<input type='hidden' name='ISBN' value='" + isbn + "'>");
                out.println("<input type='hidden' name='AuthorID' value='" + authorID + "'>");
                out.println("<input type='hidden' name='GenreID' value='" + genreID + "'>");

                out.println("<label>ISBN</label>");
                out.println("<input type='text' value='" + isbn + "' disabled>");

                out.println("<label>Title</label>");
                out.println("<input type='text' name='Title' value='" + title + "' required>");

                out.println("<label>Author First Name</label>");
                out.println("<input type='text' name='AuthorFirstName' value='" + authorFirst + "' required>");

                out.println("<label>Author Last Name</label>");
                out.println("<input type='text' name='AuthorLastName' value='" + authorLast + "' required>");

                out.println("<label>Genre</label>");
                out.println("<input type='text' name='GenreName' value='" + genreName + "' required>");

                out.println("<label>Publisher</label>");
                out.println("<input type='text' name='Publisher' value='" + publisher + "' required>");

                out.println("<label>Publication Year</label>");
                out.println("<input type='number' name='PublicationYear' value='" + year + "' required>");

                out.println("<label>Total Copies</label>");
                out.println("<input type='number' name='TotalCopies' value='" + totalCopies + "' required>");

                out.println("<button type='submit'>Save Changes</button>");
                out.println("</form>");
                out.println("</div></body></html>");
            }

            conn.close();

        } catch (Exception e) {
            out.println("<h3>Error loading book.</h3>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            Dotenv dotenv = Dotenv.load();
            String url      = dotenv.get("DB_URL");
            String user     = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            Connection conn = DriverManager.getConnection(url, user, password);

            String isbn = request.getParameter("ISBN");
            int authorID = Integer.parseInt(request.getParameter("AuthorID"));
            int genreID = Integer.parseInt(request.getParameter("GenreID"));
            int newTotalCopies = Integer.parseInt(request.getParameter("TotalCopies"));

            // Update BookTitles
            PreparedStatement ps1 = conn.prepareStatement(
                    "UPDATE BookTitles SET Title = ?, Publisher = ?, PublicationYear = ? WHERE ISBN = ?");
            ps1.setString(1, request.getParameter("Title"));
            ps1.setString(2, request.getParameter("Publisher"));
            ps1.setInt(3, Integer.parseInt(request.getParameter("PublicationYear")));
            ps1.setString(4, isbn);
            ps1.executeUpdate();

            // Update or Insert Author
            if (authorID == 0) {
                ResultSet rsA = conn.createStatement().executeQuery("SELECT MAX(AuthorID) FROM Authors");
                int newAuthorID = rsA.next() ? rsA.getInt(1) + 1 : 1;

                PreparedStatement ps2 = conn.prepareStatement(
                        "INSERT INTO Authors (AuthorID, FirstName, LastName) VALUES (?, ?, ?)");
                ps2.setInt(1, newAuthorID);
                ps2.setString(2, request.getParameter("AuthorFirstName"));
                ps2.setString(3, request.getParameter("AuthorLastName"));
                ps2.executeUpdate();

                PreparedStatement ps3 = conn.prepareStatement(
                        "INSERT INTO WrittenBy (ISBN, AuthorID) VALUES (?, ?)");
                ps3.setString(1, isbn);
                ps3.setInt(2, newAuthorID);
                ps3.executeUpdate();
            } else {
                PreparedStatement ps2 = conn.prepareStatement(
                        "UPDATE Authors SET FirstName = ?, LastName = ? WHERE AuthorID = ?");
                ps2.setString(1, request.getParameter("AuthorFirstName"));
                ps2.setString(2, request.getParameter("AuthorLastName"));
                ps2.setInt(3, authorID);
                ps2.executeUpdate();
            }

            // Update or Insert Genre
            if (genreID == 0) {
                // Check if genre name already exists
                PreparedStatement checkGenre = conn.prepareStatement(
                        "SELECT GenreID FROM Genres WHERE GenreName = ?");
                checkGenre.setString(1, request.getParameter("GenreName"));
                ResultSet rsCheck = checkGenre.executeQuery();

                int newGenreID;
                if (rsCheck.next()) {
                    // Genre already exists — just use its ID
                    newGenreID = rsCheck.getInt("GenreID");
                } else {
                    // Genre doesn't exist — insert new one
                    ResultSet rsG = conn.createStatement().executeQuery("SELECT MAX(GenreID) FROM Genres");
                    newGenreID = rsG.next() ? rsG.getInt(1) + 1 : 1;

                    PreparedStatement ps4 = conn.prepareStatement(
                            "INSERT INTO Genres (GenreID, GenreName) VALUES (?, ?)");
                    ps4.setInt(1, newGenreID);
                    ps4.setString(2, request.getParameter("GenreName"));
                    ps4.executeUpdate();
                }

                PreparedStatement ps5 = conn.prepareStatement(
                        "INSERT INTO BookGenres (ISBN, GenreID) VALUES (?, ?)");
                ps5.setString(1, isbn);
                ps5.setInt(2, newGenreID);
                ps5.executeUpdate();
            } else {
                PreparedStatement ps4 = conn.prepareStatement(
                        "UPDATE Genres SET GenreName = ? WHERE GenreID = ?");
                ps4.setString(1, request.getParameter("GenreName"));
                ps4.setInt(2, genreID);
                ps4.executeUpdate();
            }

            // Update Total Copies
            PreparedStatement ps6 = conn.prepareStatement(
                    "SELECT COUNT(CopyID) AS CurrentCopies FROM BookCopies WHERE ISBN = ?");
            ps6.setString(1, isbn);
            ResultSet rs = ps6.executeQuery();
            int currentCopies = rs.next() ? rs.getInt("CurrentCopies") : 0;

            if (newTotalCopies > currentCopies) {
                ResultSet rsC = conn.createStatement().executeQuery("SELECT MAX(CopyID) FROM BookCopies");
                int copyID = rsC.next() ? rsC.getInt(1) + 1 : 1;
                for (int i = 0; i < newTotalCopies - currentCopies; i++) {
                    PreparedStatement ps7 = conn.prepareStatement(
                            "INSERT INTO BookCopies (CopyID, CopyNumber, Status, AquisitionDate, ISBN) VALUES (?, ?, 'Available', CURDATE(), ?)");
                    ps7.setInt(1, copyID + i);
                    ps7.setInt(2, currentCopies + i + 1);
                    ps7.setString(3, isbn);
                    ps7.executeUpdate();
                }
            } else if (newTotalCopies < currentCopies) {
                PreparedStatement ps7 = conn.prepareStatement(
                        "DELETE FROM BookCopies WHERE ISBN = ? AND Status = 'Available' LIMIT ?");
                ps7.setString(1, isbn);
                ps7.setInt(2, currentCopies - newTotalCopies);
                ps7.executeUpdate();
            }

            conn.close();
            response.sendRedirect("/books");

        } catch (Exception e) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<h3>Error updating book.</h3>");
            out.println("<a href='/books'>Back</a><br><br>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
    }
}