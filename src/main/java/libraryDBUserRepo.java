import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class libraryDBUserRepo implements UserRepository {

    String url = "[INSERT URL HERE]";
    String user = "root";
    String password = "[INSERT PASSWORD HERE]";

    void save(String arg){
        System.out.println("yh");
    }

    // TODO: Take care of insertion here
    public void save() {
    }

    public void showUser(int MemberID){
        String commandSQL = String.format("SELECT * FROM members WHERE MemberID = %d", MemberID);
        sqlCall(commandSQL);
    }


    // TODO: USE PreparedStatement.setInt() FOR PARAMS AND USE "?"
    public void newMember(String firstName, String lastName, int memberID, String dateOfBirth, String licenseID, boolean minorStatus, String email, String address){
    }

    // For these queries, do we even need parameters like this?
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
