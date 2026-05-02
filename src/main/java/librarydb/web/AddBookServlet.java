package librarydb.web;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

public class AddBookServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("""
            <html>
            <head>
                <title>Add Book</title>
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
                <h1>Add Book</h1>
                <nav>
                    <a href='/'>Home</a>
                    <a href='/books'>Books</a>
                </nav>
                <form method='post' action='/add-book'>
                    <label>ISBN</label>
                    <input type='text' name='ISBN' required>
                    
                    <label>Title</label>
                    <input type='text' name='Title' required>
                    
                    <label>Author First Name</label>
                    <input type='text' name='AuthorFirstName' required>
                    
                    <label>Author Last Name</label>
                    <input type='text' name='AuthorLastName' required>
                    
                    <label>Genre</label>
                    <input type='text' name='GenreName' required>
                    
                    <label>Publisher</label>
                    <input type='text' name='Publisher' required>
                    
                    <label>Publication Year</label>
                    <input type='number' name='PublicationYear' required>
                    
                    <label>Total Copies</label>
                    <input type='number' name='TotalCopies' min='1' required>
                    
                    <button type='submit'>Add Book</button>
                </form>
            </div>
            </body>
            </html>
            """);
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
            int totalCopies = Integer.parseInt(request.getParameter("TotalCopies"));

            // Insert into BookTitles
            PreparedStatement ps1 = conn.prepareStatement(
                    "INSERT INTO BookTitles (ISBN, Title, Publisher, PublicationYear) VALUES (?, ?, ?, ?)");
            ps1.setString(1, isbn);
            ps1.setString(2, request.getParameter("Title"));
            ps1.setString(3, request.getParameter("Publisher"));
            ps1.setInt(4, Integer.parseInt(request.getParameter("PublicationYear")));
            ps1.executeUpdate();

            // Auto-generate AuthorID
            ResultSet rsA = conn.createStatement().executeQuery("SELECT MAX(AuthorID) FROM Authors");
            int authorID = rsA.next() ? rsA.getInt(1) + 1 : 1;

            // Insert into Authors
            PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO Authors (AuthorID, FirstName, LastName) VALUES (?, ?, ?)");
            ps2.setInt(1, authorID);
            ps2.setString(2, request.getParameter("AuthorFirstName"));
            ps2.setString(3, request.getParameter("AuthorLastName"));
            ps2.executeUpdate();

            // Insert into WrittenBy
            PreparedStatement ps3 = conn.prepareStatement(
                    "INSERT INTO WrittenBy (ISBN, AuthorID) VALUES (?, ?)");
            ps3.setString(1, isbn);
            ps3.setInt(2, authorID);
            ps3.executeUpdate();

//            // Auto-generate GenreID
//            ResultSet rsG = conn.createStatement().executeQuery("SELECT MAX(GenreID) FROM Genres");
//            int genreID = rsG.next() ? rsG.getInt(1) + 1 : 1;

            // Check if genre already exists
            PreparedStatement checkGenre = conn.prepareStatement(
                    "SELECT GenreID FROM Genres WHERE GenreName = ?");
            checkGenre.setString(1, request.getParameter("GenreName"));
            ResultSet rsCheck = checkGenre.executeQuery();

            int genreID;
            if (rsCheck.next()) {
                // Genre already exists — use its ID
                genreID = rsCheck.getInt("GenreID");
            } else {
                // Genre doesn't exist — insert new one
                ResultSet rsG = conn.createStatement().executeQuery("SELECT MAX(GenreID) FROM Genres");
                genreID = rsG.next() ? rsG.getInt(1) + 1 : 1;

                PreparedStatement ps4 = conn.prepareStatement(
                        "INSERT INTO Genres (GenreID, GenreName) VALUES (?, ?)");
                ps4.setInt(1, genreID);
                ps4.setString(2, request.getParameter("GenreName"));
                ps4.executeUpdate();
            }


            // Insert into BookGenres
            PreparedStatement ps5 = conn.prepareStatement(
                    "INSERT INTO BookGenres (ISBN, GenreID) VALUES (?, ?)");
            ps5.setString(1, isbn);
            ps5.setInt(2, genreID);
            ps5.executeUpdate();

            // Auto-generate CopyID and insert copies
            ResultSet rsC = conn.createStatement().executeQuery("SELECT MAX(CopyID) FROM BookCopies");
            int copyID = rsC.next() ? rsC.getInt(1) + 1 : 1;

            for (int i = 0; i < totalCopies; i++) {
                PreparedStatement ps6 = conn.prepareStatement(
                        "INSERT INTO BookCopies (CopyID, CopyNumber, Status, AquisitionDate, ISBN) VALUES (?, ?, 'Available', CURDATE(), ?)");
                ps6.setInt(1, copyID + i);
                ps6.setInt(2, i + 1);
                ps6.setString(3, isbn);
                ps6.executeUpdate();
            }

            conn.close();
            response.sendRedirect("/books");

        } catch (Exception e) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<h3>Error adding book.</h3>");
            out.println("<a href='/add-book'>Try again</a><br><br>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
    }
}