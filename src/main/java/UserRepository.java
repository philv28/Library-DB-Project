/*
* Method bodies for implementing queries and CRUD operations will be defined here.
* */
public interface UserRepository {
    void sqlCall(String commandLine);

    void save();

    void showUser(int MemberID);

    void newMember(String firstName, String lastName, int memberID, String dateOfBirth, String licenseID, boolean minorStatus, String email, String address);

    void overdueBookSearch();

    void currentlyCheckedOut();

    void checkedOutPerMember();
}
