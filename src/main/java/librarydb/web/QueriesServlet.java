package librarydb.web;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

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
                    body { font-family: Arial; margin: 30px; background: #f7f7f7; }
                    table { border-collapse: collapse; background: white; margin-bottom: 30px; }
                    th, td { border: 1px solid #ccc; padding: 8px; }
                    th { background: #eee; }
                    .queryBox { background: white; padding: 20px; margin-bottom: 25px; border-radius: 8px; }
                    pre { background: #f0f0f0; padding: 10px; overflow-x: auto; }
                </style>
            </head>
            <body>
            <h1>SQL Query Showcase</h1>
            <a href='/'>Home</a>
        """);

        try {
            String url = "jdbc:mysql://localhost:3306/LibraryDB";
            String user = "root";
            String password = "rootpass";
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
