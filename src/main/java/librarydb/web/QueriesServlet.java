package librarydb.web;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import io.github.cdimascio.dotenv.Dotenv;

public class QueriesServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("""
            <html>
            <head>
                <title>Queries</title>
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
                    
                    .queryBox {
                        background: #fafafa;
                        padding: 20px;
                        margin-bottom: 25px;
                        border-radius: 8px;
                        border: 1px solid #ddd;
                        overflow-x: auto;
                    }
                    
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        background: white;
                        margin-top: 15px;
                        table-layout: auto;
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
                        word-break: break-word;
                    }
                    
                    tr:hover {
                        background: #f1f1f1;
                    }
                    
                    pre {
                        background: #f0f0f0;
                        padding: 10px;
                        overflow-x: auto;
                        border-radius: 6px;
                    }
                </style>
            </head>
            <body>
            <div class='container'>
            <h1>SQL Query</h1>
            
            <nav>
                <a href='/'>Home</a>
                <a href='/members'>Members</a>
            </nav>
            """);

        try {
            Dotenv dotenv = Dotenv.load();
            String url      = dotenv.get("DB_URL");
            String user     = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            Connection conn = DriverManager.getConnection(url, user, password);

            runQuery(out, conn,
                    "Query 1: Staff Book Loan View",
                    "Uses a view to show member loan history and loan status.",
                    "SELECT * FROM StaffBookLoanView");

            runQuery(out, conn,
                    "Query 2: Number of Copies Per Book",
                    "Uses a join and aggregation to count copies for each title.",
                    """
                    SELECT bt.Title, COUNT(bc.CopyID) AS TotalCopies
                    FROM BookTitles bt
                    JOIN BookCopies bc ON bt.ISBN = bc.ISBN
                    GROUP BY bt.Title
                    """);

            runQuery(out, conn,
                    "Query 3: Total Late Fees Per Member",
                    "Uses joins and aggregation to show total late fees per member.",
                    """
                    SELECT m.MemberID, m.FirstName, m.LastName, SUM(lf.Amount) AS TotalLateFees
                    FROM Members m
                    JOIN BookLoans bl ON m.MemberID = bl.MemberID
                    JOIN LateFees lf ON bl.LoanID = lf.LoanID
                    GROUP BY m.MemberID, m.FirstName, m.LastName
                    """);

            runQuery(out, conn,
                    "Query 4: Members With Unpaid Late Fees",
                    "Uses a subquery to find members who have unpaid late fees.",
                    """
                    SELECT MemberID, FirstName, LastName, Email
                    FROM Members
                    WHERE MemberID IN (
                        SELECT bl.MemberID
                        FROM BookLoans bl
                        JOIN LateFees lf ON bl.LoanID = lf.LoanID
                        WHERE lf.PaidStatus = FALSE
                    )
                    """);

            runQuery(out, conn,
                    "Query 5: Computer Reservation Schedule",
                    "Uses joins to show which members reserved which computers.",
                    """
                    SELECT cr.ReservationID, m.FirstName, m.LastName,
                           c.ComputerNumber, c.Location, cr.StartTime, cr.EndTime
                    FROM ComputerReservations cr
                    JOIN Members m ON cr.MemberID = m.MemberID
                    JOIN Computers c ON cr.ComputerID = c.ComputerID
                    ORDER BY cr.StartTime
                    """);

            conn.close();

        } catch (Exception e) {
            out.println("<h3>Error loading queries.</h3>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }

        out.println("</div>");
        out.println("</body></html>");
    }

    private void runQuery(PrintWriter out, Connection conn, String title, String description, String sql)
            throws SQLException {

        out.println("<div class='queryBox'>");
        out.println("<h2>" + title + "</h2>");
        out.println("<p>" + description + "</p>");
        out.println("<pre>" + sql + "</pre>");

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData meta = rs.getMetaData();

        out.println("<table>");
        out.println("<tr>");
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            out.println("<th>" + meta.getColumnName(i) + "</th>");
        }
        out.println("</tr>");

        while (rs.next()) {
            out.println("<tr>");
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                out.println("<td>" + rs.getString(i) + "</td>");
            }
            out.println("</tr>");
        }

        out.println("</table>");
        out.println("</div>");
    }
}
