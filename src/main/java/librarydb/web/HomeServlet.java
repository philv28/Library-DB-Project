package librarydb.web;

import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.PrintWriter;

public class HomeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("""
                    <html>
                    <head>
                        <title>LibraryDB Website</title>
                        <style>
                            body { font-family: Arial; margin: 40px; background: #f7f7f7; }
                            .card { background: white; padding: 25px; border-radius: 10px; width: 600px; }
                            a { display: block; margin: 12px 0; font-size: 18px; }
                        </style>
                    </head>
                    <body>
                        <div class='card'>
                            <h1>Library Database</h1>
                            <p>Deliverable 5 JDBC Web App</p>

                            <a href='/members'>Manage Members</a>
                            <a href='/add-member'>Add Member</a>
                            <a href='/queries'>View SQL Query Showcase</a>
                        </div>
                    </body>
                    </html>
                """);
    }
}
