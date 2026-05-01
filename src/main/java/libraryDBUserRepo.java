import javax.sound.midi.SysexMessage;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class libraryDBUserRepo implements UserRepository {
    Dotenv dotenv = Dotenv.load();
    String url      = dotenv.get("DB_URL");
    String user     = dotenv.get("DB_USER");
    String password = dotenv.get("DB_PASSWORD");


    void save(String arg){
        System.out.println("yh");
    }

    // TODO: Take care of insertion here
    public void save() {
    }

    public void showUser(int memberID){
        String commandSQL = "SELECT * FROM Members WHERE memberID = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement ps = conn.prepareStatement(commandSQL);

            ps.setInt(1, memberID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                rs.getInt("MemberID");
                rs.getString("FirstName");
                rs.getString("LastName");
                rs.getString("Address");
                rs.getString("Email");
                rs.getDate("DateOfBirth");
                rs.getString("LicenseID");
                rs.getBoolean("IsMinor");

            }

        } catch (SQLException e) {
            System.out.println("Search failure or user does not exist!");
            System.exit(-1);
        }
    }


    // TODO: USE PreparedStatement.setInt() FOR PARAMS AND USE "?"
    public void newMember(String firstName, String lastName, int memberID, String dateOfBirth, String licenseID, boolean minorStatus, String email, String address){
        String commandSQL = "INSERT INTO members (MemberID, FirstName, LastName, Address, Email, DateOfBirth, LicenseID, MinorStatus) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password)){
            PreparedStatement ps = conn.prepareStatement(commandSQL);

            ps.setInt(1, memberID);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, address);
            ps.setString(5, email);
            ps.setDate(6, Date.valueOf(dateOfBirth));
            ps.setString(7, licenseID);
            ps.setBoolean(8, minorStatus);

            int rows = ps.executeUpdate();
            if (rows > 0){
                System.out.println("User Inserted");
            }

        } catch (SQLException e){
            System.out.println("User insert failed!");
            e.printStackTrace();
            System.exit(-1);
        }

    }

    // TODO: Add boilerplate try-catch to show query results
    public void overdueBookSearch() {
        String commandSQL = """
               SELECT
                        m.MemberID,
                        m.FirstName,
                        m.LastName,
                        bt.Title,
                        bl.CheckoutDate,
                        bl.DueDate,
                        DATEDIFF(CURDATE(), bl.DueDate) AS DaysOverdue
                    FROM Members m
                    JOIN BookLoans bl ON m.MemberID = bl.MemberID
                    JOIN BookCopies bc ON bl.CopyID = bc.CopyID
                    JOIN BookTitles bt ON bc.ISBN = bt.ISBN
                    WHERE bl.ReturnDate IS NULL
                    AND bl.DueDate < CURDATE();
        """;

        sqlCall(commandSQL);

    }

    public void currentlyCheckedOut(){
        String commandSQL = "SELECT"
        + "MemberID, "
        +       "FirstName," +
                "LastName, " +
                "Title, " +
                "CheckoutDate, " +
                 "DueDate " +
        "FROM MemberLoanView " +
        "WHERE LoanStatus = 'Still Checked Out'; ";


        sqlCall(commandSQL);
    }

    public void checkedOutPerMember(){
        String commandSQL = """
                SELECT
                    m.MemberID,
                    m.FirstName,
                    m.LastName,
                    COUNT(bl.LoanID) AS BooksCheckedOut,
                    CASE
                        WHEN COUNT(bl.LoanID) >= 5 THEN 'Limit Reached'
                        ELSE 'Can Borrow More'
                    END AS BorrowStatus
                FROM Members m
                JOIN BookLoans bl ON m.MemberID = bl.MemberID
                WHERE bl.ReturnDate IS NULL
                GROUP BY m.MemberID, m.FirstName, m.LastName;
                
                """;
            sqlCall(commandSQL);
    }

    // TODO: Need to refactor this code, SELECT should work but updating it may not.
     public void sqlCall (String commandLine){

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);

            PreparedStatement stmt = conn.prepareStatement(commandLine);
            ResultSet rs = stmt.executeQuery();
        } catch (Exception e) {
            System.out.println("Failed to insert new member!");
            e.printStackTrace();
        }
    }
}
