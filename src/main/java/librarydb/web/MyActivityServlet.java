package librarydb.web;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import io.github.cdimascio.dotenv.Dotenv;

public class MyActivityServlet extends HttpServlet {

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
                <title>My Activity</title>
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
                    h2 {
                        margin-top: 35px;
                        color: #2c3e50;
                        border-bottom: 2px solid #2c3e50;
                        padding-bottom: 6px;
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
                        margin-bottom: 25px;
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
                    .empty {
                        background: #f4f6f8;
                        padding: 12px;
                        border-radius: 6px;
                        margin-top: 12px;
                        color: #555;
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
                <h1>My Activity</h1>

                <nav>
                    <a href='/my-account'>My Account</a>
                    <a href='/logout'>Logout</a>
                </nav>
            """);

        try {
            Dotenv dotenv = Dotenv.load();

            String url = dotenv.get("DB_URL");
            String dbUser = dotenv.get("DB_USER");
            String dbPassword = dotenv.get("DB_PASSWORD");

            try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
                printLoansTable(out, conn, memberID);
                printFeesTable(out, conn, memberID);
                printReservationsTable(out, conn, memberID);
            }

        } catch (Exception e) {
            out.println("<div class='error'>Error loading activity information.</div>");
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

    private void printLoansTable(PrintWriter out, Connection conn, int memberID)
            throws SQLException {

        String sql = """
            SELECT *
            FROM MemberLoanView
            WHERE MemberID = ?
        """;

        out.println("<h2>Loans</h2>");

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberID);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean hasRows = false;

                out.println("<table>");
                out.println("<tr>");
                out.println("<th>Title</th>");
                out.println("<th>Checkout Date</th>");
                out.println("<th>Due Date</th>");
                out.println("<th>Return Date</th>");
                out.println("<th>Loan Status</th>");
                out.println("</tr>");

                while (rs.next()) {
                    hasRows = true;

                    Date returnDate = rs.getDate("ReturnDate");

                    out.println("<tr>");
                    out.println("<td>" + rs.getString("Title") + "</td>");
                    out.println("<td>" + rs.getDate("CheckoutDate") + "</td>");
                    out.println("<td>" + rs.getDate("DueDate") + "</td>");
                    out.println("<td>" + (returnDate == null ? "Not returned" : returnDate) + "</td>");
                    out.println("<td>" + rs.getString("LoanStatus") + "</td>");
                    out.println("</tr>");
                }

                out.println("</table>");

                if (!hasRows) {
                    out.println("<div class='empty'>No loans found.</div>");
                }
            }
        }
    }

    private void printFeesTable(PrintWriter out, Connection conn, int memberID)
            throws SQLException {

        String sql = """
            SELECT *
            FROM MemberLateFeeView
            WHERE MemberID = ?
        """;

        out.println("<h2>Fees</h2>");

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberID);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean hasRows = false;

                out.println("<table>");
                out.println("<tr>");
                out.println("<th>Title</th>");
                out.println("<th>Amount</th>");
                out.println("<th>Date Assessed</th>");
                out.println("<th>Payment Status</th>");
                out.println("<th>Payment Date</th>");
                out.println("</tr>");

                while (rs.next()) {
                    hasRows = true;

                    Date paymentDate = rs.getDate("PaymentDate");

                    out.println("<tr>");
                    out.println("<td>" + rs.getString("Title") + "</td>");
                    out.println("<td>$" + rs.getBigDecimal("Amount") + "</td>");
                    out.println("<td>" + rs.getDate("DateAssessed") + "</td>");
                    out.println("<td>" + rs.getString("PaymentStatus") + "</td>");
                    out.println("<td>" + (paymentDate == null ? "Not paid" : paymentDate) + "</td>");
                    out.println("</tr>");
                }

                out.println("</table>");

                if (!hasRows) {
                    out.println("<div class='empty'>No fees found.</div>");
                }
            }
        }
    }

    private void printReservationsTable(PrintWriter out, Connection conn, int memberID)
            throws SQLException {

        String sql = """
            SELECT *
            FROM MemberReservationView
            WHERE MemberID = ?
        """;

        out.println("<h2>Reservations</h2>");

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberID);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean hasRows = false;

                out.println("<table>");
                out.println("<tr>");
                out.println("<th>Computer Number</th>");
                out.println("<th>Location</th>");
                out.println("<th>Start Time</th>");
                out.println("<th>End Time</th>");
                out.println("<th>Reservation Status</th>");
                out.println("</tr>");

                while (rs.next()) {
                    hasRows = true;

                    out.println("<tr>");
                    out.println("<td>" + rs.getString("ComputerNumber") + "</td>");
                    out.println("<td>" + rs.getString("Location") + "</td>");
                    out.println("<td>" + rs.getTimestamp("StartTime") + "</td>");
                    out.println("<td>" + rs.getTimestamp("EndTime") + "</td>");
                    out.println("<td>" + rs.getString("ReservationStatus") + "</td>");
                    out.println("</tr>");
                }

                out.println("</table>");

                if (!hasRows) {
                    out.println("<div class='empty'>No reservations found.</div>");
                }
            }
        }
    }
}