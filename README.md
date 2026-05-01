# Library-DB-Project

## Running the Application

### Prerequisites
- Java 17+
- Maven
- MySQL 8+

### Setup

1. Make sure you have a LibraryDB running on your local device(This can be done with LibraryDB.sql)

3. Create a `.env` file in the project root with the following:

```env
DB_URL=jdbc:mysql://localhost:3306/LibraryDB?useSSL=false&allowPublicKeyRetrieval=true
DB_USER=your_mysql_username
DB_PASSWORD=your_mysql_password
```
3. Run the Main.java file and open to http://localhost:8080