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
                            body {
                                font-family: Arial, sans-serif;
                                margin: 0;
                                background: #f4f6f8;
                                color: #222;
                            }
                            
                            .container {
                                max-width: 900px;
                                margin: 60px auto;
                                background: white;
                                padding: 35px;
                                border-radius: 12px;
                                box-shadow: 0 2px 10px rgba(0,0,0,0.8);
                            }
                            
                            h1 {
                                margin-top: 0;
                                color: #2c3e50;
                            }
                            
                            p {
                                color: #555;
                            }
                            
                            .menu a {
                                display: block:
                                margin: 15px 0;
                                padding: 14px 18px;
                                background: #2c3e50;
                                color: white;
                                text-decoration: none;
                                border-radius: 6px;
                                width: 250px;
                            }
                            
                            .menu a:hover {
                                background: #1a252f;
                            }
                            
                            </style>
                    </head>
                    <body>
                    <div class='container'>
                        <h1>Library Database</h1>
                        <p>JDBC Web App</p>
                        <div class='menu'>
                            <a href='/members'>Manage Members</a>
                            <a href='/add-member'>Add Member</a>
                            <a href='/queries'>View Queries</a>
                            <a href='/books'>View Books</a>
                            <a href='/trending-books'>Trending Books</a>
                        </div>
                    </div>
                    </body>
                    </html>
                """);
    }
}
