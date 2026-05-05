DROP DATABASE IF EXISTS LibraryDB;
CREATE DATABASE LibraryDB;
USE LibraryDB;

CREATE TABLE Members (
	MemberID INT PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    Address VARCHAR(100),
    Email VARCHAR(100) NOT NULL UNIQUE,
    DateOfBirth DATE NOT NULL,
    LicenseID VARCHAR(50),
    IsMinor BOOLEAN NOT NULL DEFAULT FALSE,
    CHECK (MemberID > 0)
);

CREATE TABLE Employees (
	EmployeeID INT PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    Address VARCHAR(100),
    PhoneNumber VARCHAR(20) NOT NULL UNIQUE,
    Email VARCHAR(100) NOT NULL UNIQUE,
    Salary DECIMAL(10,2) NOT NULL,
    HireDate DATE NOT NULL,
    Position VARCHAR(50) NOT NULL,
    MemberID INT UNIQUE,
    FOREIGN KEY (MemberID) REFERENCES Members(MemberID)
		ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE TABLE Guards (
	MemberID INT NOT NULL,
    GuardianID INT NOT NULL,
    PRIMARY KEY (MemberID, GuardianID),
    FOREIGN KEY (MemberID) REFERENCES Members(MemberID)
		ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (GuardianID) REFERENCES Members(MemberID)
		ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE LibraryCards (
	LibraryCardID INT PRIMARY KEY,
    CardNumber VARCHAR(50) NOT NULL UNIQUE,
    IssueDate DATE NOT NULL,
    ExpireDate DATE NOT NULL,
    Status VARCHAR(30) NOT NULL DEFAULT 'Active',
    MemberID INT NOT NULL UNIQUE,
    FOREIGN KEY (MemberID) REFERENCES Members(MemberID)
		ON DELETE CASCADE
        ON UPDATE CASCADE,
	CHECK (ExpireDate > IssueDate)
);

CREATE TABLE MinorRestrictions (
	RestrictionID INT PRIMARY KEY,
    MemberID INT NOT NULL,
    GuardianID INT NOT NULL,
    RestrictDeviceAccess BOOLEAN NOT NULL DEFAULT FALSE,
    UseTimeLimit VARCHAR(50) NOT NULL,
    Notes VARCHAR(100),
    FOREIGN KEY (MemberID) REFERENCES Members(MemberID)
		ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (GuardianID) REFERENCES Members(MemberID)
		ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE Computers(
	ComputerID INT PRIMARY KEY,
    ComputerNumber VARCHAR(20) NOT NULL UNIQUE,
    Status VARCHAR(30) NOT NULL DEFAULT 'Available',
    Location VARCHAR(100) NOT NULL
);

CREATE TABLE ComputerReservations (
	ReservationID INT PRIMARY KEY,
    MemberID INT NOT NULL,
    ComputerID INT NOT NULL,
    StartTime DATETIME NOT NULL,
    EndTime DATETIME NOT NULL,
    UsageStart DATETIME DEFAULT NULL,
    UsageEnd DATETIME DEFAULT NULL,
    FOREIGN KEY (MemberID) REFERENCES Members(MemberID)
		ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (ComputerID) REFERENCES Computers(ComputerID)
		ON DELETE CASCADE
        ON UPDATE CASCADE,
	CHECK (EndTime > StartTime),
    CHECK (UsageEnd IS NULL OR UsageStart IS NULL OR UsageEnd > UsageStart)
);

CREATE TABLE BookTitles (
	ISBN VARCHAR(50) PRIMARY KEY,
    Title VARCHAR(100) NOT NULL,
    Publisher VARCHAR(100) NOT NULL,
    PublicationYear INT NOT NULL
);

CREATE TABLE BookCopies (
	CopyID INT PRIMARY KEY,
    CopyNumber INT NOT NULL,
    Status VARCHAR(30) NOT NULL DEFAULT 'Available',
    AquisitionDate DATE NOT NULL,
    ISBN VARCHAR(50) NOT NULL,
    FOREIGN KEY (ISBN) REFERENCES BookTitles(ISBN)
		ON DELETE CASCADE
        ON UPDATE CASCADE,
	UNIQUE (ISBN, CopyNumber)
);

CREATE TABLE Authors (
	AuthorID INT PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50)
);

CREATE TABLE WrittenBy (
	ISBN VARCHAR(50) NOT NULL,
    AuthorID INT NOT NULL,
    PRIMARY KEY (ISBN, AuthorID),
    FOREIGN KEY (ISBN) REFERENCES BookTitles(ISBN)
		ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (AuthorID) REFERENCES Authors(AuthorID)
		ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Genres (
	GenreID INT PRIMARY KEY,
    GenreName VARCHAR(50) NOT NULL UNIQUE 
);

CREATE TABLE BookGenres (
	ISBN VARCHAR(50) NOT NULL,
    GenreID INT NOT NULL,
    PRIMARY KEY (ISBN, GenreID),
    FOREIGN KEY (ISBN) REFERENCES BookTitles(ISBN)
		ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (GenreID) REFERENCES Genres(GenreID)
		ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE TrendingBooks (
	TrendingID INT PRIMARY KEY,
    ISBN VARCHAR(50) NOT NULL,
    DateMarked DATE NOT NULL,
    DemandScore DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (ISBN) REFERENCES BookTitles(ISBN)
		ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE BookRequests (
	RequestID INT PRIMARY KEY,
    RequestDate DATE NOT NULL,
    RequestType VARCHAR(50) NOT NULL, 
    MemberID INT NOT NULL,
    ISBN VARCHAR(50) NOT NULL,
    FOREIGN KEY (MemberID) REFERENCES Members(MemberID)
		ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (ISBN) REFERENCES BookTitles(ISBN)
		ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE BookLoans (
	LoanID INT PRIMARY KEY,
    DueDate DATE NOT NULL,
    MemberID INT NOT NULL,
    EmployeeID INT,
    CheckoutDate DATE NOT NULL,
    ReturnDate DATE DEFAULT NULL,
    CopyID INT NOT NULL,
    FOREIGN KEY (MemberID) REFERENCES Members(MemberID)
		ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID)
		ON DELETE SET NULL
        ON UPDATE CASCADE,
    FOREIGN KEY (CopyID) REFERENCES BookCopies(CopyID)
		ON DELETE RESTRICT
        ON UPDATE CASCADE,
	CHECK (DueDate >= CheckoutDate),
    CHECK (ReturnDate IS NULL OR ReturnDate >= CheckoutDate)
);

CREATE TABLE LateFees (
	LateFeeID INT PRIMARY KEY,
    LoanID INT NOT NULL,
    Amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    DateAssessed DATE NOT NULL,
    PaidStatus BOOLEAN NOT NULL DEFAULT FALSE,
    PaymentDate DATE DEFAULT NULL,
    FOREIGN KEY (LoanID) REFERENCES BookLoans(LoanID)
		ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE BookOrders (
	OrderID INT PRIMARY KEY,
    DeliveryNo VARCHAR(50) NOT NULL UNIQUE,
    ISBN VARCHAR(50) NOT NULL,
    PublicationWarehouse VARCHAR(100)NOT NULL,
    OrderDate DATE NOT NULL,
    ExpectedDate DATE NOT NULL,
    DeliveryStatus VARCHAR(50) NOT NULL DEFAULT 'Pending',
    EmployeeID INT,
    FOREIGN KEY (ISBN) REFERENCES BookTitles(ISBN)
		ON DELETE RESTRICT
        ON UPDATE CASCADE,
    FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID)
		ON DELETE SET NULL
        ON UPDATE CASCADE
);

INSERT INTO Members VALUES
(1, 'John', 'Smith', '101 Main St', 'john.smith@email.com', '2000-05-12', 'D12345', FALSE),
(2, 'Emily', 'Smith', '101 Main St', 'emily.smith@email.com', '2014-08-20', NULL, TRUE),
(3, 'Sarah', 'Johnson', '202 Oak Ave', 'sarah.j@email.com', '1998-11-03', 'D67890', FALSE),
(4, 'David', 'Miller', '700 Pine Rd', 'david.miller@email.com', '1999-8-12', 'C89472', FALSE),
(5, 'Michael', 'Lee', '303 River St', 'michael.lee@email.com', '2001-07-19', 'L44556', FALSE),
(6, 'Olivia', 'Lee', '303 River St', 'olivia.clark@email.com', '2013-10-08', NULL, TRUE),
(7, 'Benjamin', 'Lee', '303 River St', 'benjamin.lee@email.com', '2015-8-14', NULL, TRUE),
(8, 'Jason', 'Smith', '101 Main St', 'jason.smith@email.com', '2016-06-14', NULL, TRUE),
(9, 'Alexander', 'Smith', '101 Main St', 'alexander.smith@email.com' , '2016-06-14', NULL, TRUE);

-- EMPLOYEES
INSERT INTO Employees VALUES
(101, 'Alice', 'Brown', '500 Market St', '319-555-1001', 'alice.brown@library.com', 55000.00, '2023-01-15', 'Librarian', NULL),
(102, 'David', 'Miller', '700 Pine Rd', '319-555-1002', 'david.miller@library.com', 42000.00, '2024-03-01', 'Assistant', 4),
(103, 'Olivia', 'White', '600 River Rd', '319-555-1003', 'olivia.white@library.com', 60000.00, '2022-06-10', 'Manager', NULL),
(104, 'James', 'Hall', '800 Elm St', '319-555-1004', 'james.hall@library.com', 42000.00, '2024-04-10', 'Assistant', NULL),
(105, 'Michael', 'Lee', '303 River St', '319-555-1005', 'michale.lee@library.com', 50000.00, '2023-09-15', 'Technician', 5);

-- GUARDS
INSERT INTO Guards VALUES
(2, 1),
(8, 1),
(9, 1),
(6, 5),
(7, 5);

-- LIBRARY CARDS
INSERT INTO LibraryCards VALUES
(1001, 'LC1001', '2025-01-01', '2030-01-01', 'Active', 1),
(1002, 'LC1002', '2025-01-01', '2030-01-01', 'Active', 2),
(1003, 'LC1003', '2025-01-01', '2030-01-01', 'Active', 3),
(1004, 'LC1004', '2024-03-01', '2029-01-01', 'Active', 4),
(1005, 'LC1005', '2023-09-15', '2028-01-01', 'Active', 5),
(1006, 'LC1006', '2025-01-01', '2030-01-01', 'Active', 6),
(1007, 'LC1007', '2025-01-01', '2030-01-01', 'Active', 7),
(1008, 'LC1008', '2025-01-01', '2030-01-01', 'Active', 8),
(1009, 'LC1009', '2025-01-01', '2030-01-01', 'Active', 9);

-- MINOR RESTRICTIONS
INSERT INTO MinorRestrictions VALUES
(201, 2, 1, FALSE, '2 hours/day', 'Standard minor restrictions apply'),
(202, 6, 5, TRUE, '2 hours/day', 'Guardian approval required for device access'),
(203, 7, 5, TRUE, '2 hours/day', 'Guardian approval required for devicce access'),
(204, 8, 1, FALSE, '2 hours/day', 'Standard minor restrictions apply'),
(205, 9, 1, FALSE, '2 hours/day', 'Standard minor restrictions apply');

SELECT * FROM MinorRestrictions;

-- COMPUTERS
INSERT INTO Computers VALUES
(301, 'PC-01', 'Available',   'Main Floor'),
(302, 'PC-02', 'In Use',      'Main Floor'),
(303, 'PC-03', 'Available',   'Main Floor'),
(304, 'PC-04', 'Maintenance', 'Main Floor'),

(305, 'PC-05', 'Available',   'Second Floor'),
(306, 'PC-06', 'In Use',      'Second Floor'),
(307, 'PC-07', 'Available',   'Second Floor'),

(308, 'PC-08', 'Available',   'Basement'),
(309, 'PC-09', 'In Use',      'Basement'),
(310, 'PC-10', 'Out of Order','Basement');

SELECT * FROM Members;
SELECT * FROM Computers;

-- COMPUTER RESERVATIONS
INSERT INTO ComputerReservations VALUES
(401, 1, 301, '2026-04-17 08:00:00', '2026-04-17 09:00:00', '2026-04-17 08:03:00', '2026-04-17 08:57:00'),
(402, 2, 301, '2026-04-18 10:00:00', '2026-04-18 11:00:00', '2026-04-18 10:05:00', '2026-04-18 10:54:00'),
(403, 3, 301, '2026-04-19 13:00:00', '2026-04-19 14:00:00', '2026-04-19 13:02:00', '2026-04-19 13:58:00'),
(404, 4, 302, '2026-04-17 09:00:00', '2026-04-17 10:00:00', '2026-04-17 09:04:00', '2026-04-17 09:51:00'),
(405, 5, 302, '2026-04-18 12:00:00', '2026-04-18 13:00:00', '2026-04-18 12:06:00', '2026-04-18 12:55:00'),
(406, 6, 302, '2026-04-20 15:00:00', '2026-04-20 16:00:00', '2026-04-20 15:03:00', '2026-04-20 15:57:00'),
(407, 7, 303, '2026-04-17 08:30:00', '2026-04-17 09:30:00', '2026-04-17 08:34:00', '2026-04-17 09:25:00'),
(408, 8, 303, '2026-04-19 11:00:00', '2026-04-19 12:00:00', '2026-04-19 11:07:00', '2026-04-19 11:56:00'),
(409, 9, 303, '2026-04-21 14:00:00', '2026-04-21 15:00:00', '2026-04-21 14:10:00', '2026-04-21 14:53:00'),
(410, 1, 304, '2026-04-18 10:30:00', '2026-04-18 11:30:00', '2026-04-18 10:32:00', '2026-04-18 11:24:00'),
(411, 3, 304, '2026-04-20 13:30:00', '2026-04-20 14:30:00', '2026-04-20 13:35:00', '2026-04-20 14:20:00'),
(412, 5, 305, '2026-04-17 09:00:00', '2026-04-17 10:00:00', '2026-04-17 09:01:00', '2026-04-17 09:59:00'),
(413, 6, 305, '2026-04-19 12:30:00', '2026-04-19 13:30:00', '2026-04-19 12:33:00', '2026-04-19 13:21:00'),
(414, 7, 305, '2026-04-22 16:00:00', '2026-04-22 17:00:00', '2026-04-22 16:05:00', '2026-04-22 16:58:00');

SELECT * FROM ComputerReservations;

-- BOOK TITLES
INSERT INTO BookTitles VALUES
('9780547928227', 'The Hobbit', 'Houghton Mifflin', 1937),
('9780439708180', 'Harry Potter and the Sorcerer''s Stone', 'Scholastic', 1997),
('9780064404990', 'The Lion, the Witch and the Wardrobe', 'HarperCollins', 1950),
('9780590353427', 'A Wrinkle in Time', 'Square Fish', 1962),
('9780061120084', 'Charlotte''s Web', 'HarperCollins', 1952),
('9780553380163', 'A Brief History of Time', 'Bantam', 1988),
('9781426217787', 'National Geographic Atlas of the World', 'National Geographic', 2019),
('9780812974492', 'Team of Rivals', 'Simon & Schuster', 2005),
('9780143110439', 'The Republic', 'Penguin Classics', 2007),
('9780143039433', 'Meditations', 'Penguin Classics', 2006),
('9780131103627', 'The C Programming Language', 'Prentice Hall', 1988),
('9780262033848', 'Introduction to Algorithms', 'MIT Press', 2009),
('9780134685991', 'Effective Java', 'Addison-Wesley', 2018),
('9781491950357', 'Designing Data Intensive Applications', 'OReilly', 2017),
('9780062316110', 'Sapiens', 'Harper', 2015);

-- BOOK COPIES
INSERT INTO BookCopies VALUES
(501, 1, 'Available',   '2025-02-01', '9780547928227'),
(502, 2, 'Checked Out', '2025-02-05', '9780547928227'),
(503, 1, 'Checked Out', '2025-02-10', '9780439708180'),
(504, 2, 'Checked Out', '2025-02-12', '9780439708180'),
(505, 3, 'Available',   '2025-02-15', '9780439708180'),
(506, 1, 'Available',   '2025-03-01', '9780064404990'),
(507, 2, 'Checked Out', '2025-03-03', '9780064404990'),
(508, 1, 'Checked Out', '2025-03-10', '9780590353427'),
(509, 2, 'Available',   '2025-03-12', '9780590353427'),
(510, 1, 'Available',   '2025-03-20', '9780061120084'),
(511, 2, 'Checked Out', '2025-03-22', '9780061120084'),
(512, 1, 'Available',   '2025-04-01', '9780553380163'),
(513, 1, 'Checked Out', '2025-04-03', '9781426217787'),
(514, 1, 'Available',   '2025-04-05', '9780812974492'),
(515, 1, 'Checked Out', '2025-04-07', '9780143110439'),
(516, 1, 'Available',   '2025-04-08', '9780143039433'),
(517, 1, 'Available',   '2025-04-09', '9780131103627'),
(518, 1, 'Checked Out', '2025-04-10', '9780262033848'),
(519, 1, 'Available',   '2025-04-11', '9780134685991'),
(520, 1, 'Checked Out', '2025-04-12', '9781491950357'),
(521, 1, 'Checked Out', '2025-04-13', '9780062316110'),
(522, 2, 'Available',   '2025-04-14', '9780062316110');

-- AUTHORS
INSERT INTO Authors VALUES
(601, 'J.R.R.', 'Tolkien'),
(602, 'J.K.', 'Rowling'),
(603, 'C.S.', 'Lewis'),
(604, 'Madeleine', 'LEngle'),
(605, 'E.B.', 'White'),
(606, 'Stephen', 'Hawking'),
(607, 'National', 'Geographic'),
(608, 'Doris', 'Kearns Goodwin'),
(609, 'Plato', ''),
(610, 'Marcus', 'Aurelius'),
(611, 'Brian', 'Kernighan'),
(612, 'Thomas', 'Cormen'),
(613, 'Joshua', 'Bloch'),
(614, 'Martin', 'Kleppmann'),
(615, 'Yuval Noah', 'Harari');

-- WRITTEN BY
INSERT INTO WrittenBy VALUES
('9780547928227', 601),
('9780439708180', 602),
('9780064404990', 603),
('9780590353427', 604),
('9780061120084', 605),
('9780553380163', 606),
('9781426217787', 607),
('9780812974492', 608),
('9780143110439', 609),
('9780143039433', 610),
('9780131103627', 611),
('9780262033848', 612),
('9780134685991', 613),
('9781491950357', 614),
('9780062316110', 615);

-- GENRES
INSERT INTO Genres VALUES
(701, 'Fantasy'),
(702, 'Children'),
(703, 'Science'),
(704, 'Geography'),
(705, 'History'),
(706, 'Politics'),
(707, 'Philosophy'),
(708, 'Programming'),
(709, 'Computer Science'),
(710, 'Space');

-- BOOK GENRE
INSERT INTO BookGenres VALUES
('9780547928227', 701),
('9780439708180', 701),
('9780439708180', 702),
('9780064404990', 701),
('9780064404990', 702),
('9780590353427', 702),
('9780590353427', 710),
('9780061120084', 702),
('9780553380163', 710),
('9780553380163', 703),
('9781426217787', 704),
('9780812974492', 705),
('9780812974492', 706),
('9780143110439', 706),
('9780143110439', 707),
('9780143039433', 707),
('9780131103627', 708),
('9780262033848', 709),
('9780134685991', 708),
('9781491950357', 709),
('9780062316110', 705);

-- TRENDING BOOKS
INSERT INTO TrendingBooks VALUES
(801, '9780439708180', '2026-04-10', 9.50),
(802, '9780547928227', '2026-04-11', 9.20),
(803, '9780553380163', '2026-04-12', 8.90),
(804, '9780812974492', '2026-04-13', 8.75),
(805, '9780062316110', '2026-04-14', 9.10);

-- BOOK REQUESTS
INSERT INTO BookRequests VALUES
(901, '2026-04-16', 'Hold', 2, '9780439708180'),
(902, '2026-04-16', 'Hold', 6, '9780547928227'),
(903, '2026-04-17', 'Purchase Suggestion', 1, '9780553380163'),
(904, '2024-04-18', 'Hold', 3, '9780812974492'),
(905, '2024-04-19', 'Renewal', 5, '9780062316110');

-- BOOK LOANS
INSERT INTO BookLoans VALUES
(1001, '2026-04-25', 2, 101, '2026-04-11', NULL, 502),
(1002, '2026-04-26', 6, 102, '2026-04-12', NULL, 504),
(1003, '2026-04-20', 3, 103, '2026-04-05', '2026-04-18', 507),
(1004, '2026-04-22', 4, 104, '2026-04-08', '2026-04-21', 509),
(1005, '2026-04-30', 5, 105, '2026-04-16', NULL, 515);

-- LATE FEES
INSERT INTO LateFees VALUES
(1101, 1003, 4.00, '2026-04-20', TRUE, '2026-04-21'),
(1102, 1004, 2.50, '2026-04-22', TRUE, '2026-04-23'),
(1103, 1001, 3.00, '2026-04-26', FALSE, NULL),
(1104, 1002, 3.50, '2026-04-27', FALSE, NULL),
(1105, 1005, 1.50, '2026-05-01', FALSE, NULL);

-- BOOK ORDERS
INSERT INTO BookOrders VALUES
(1201, 'DEL-001', '9780439708180', 'Scholastic Warehouse', '2026-04-01', '2026-04-20', 'Shipped', 101),
(1202, 'DEL-002', '9780553380163', 'Bantam Warehouse', '2026-04-03', '2026-04-22', 'Pending', 102),
(1203, 'DEL-003', '9781426217787', 'National Geographic Warehouse', '2026-04-05', '2026-04-25', 'Delivered', 103),
(1204, 'DEL-004', '9780812974492', 'Simon Warehouse', '2026-04-07', '2026-04-27', 'Pending', 104),
(1205, 'DEL-005', '9780062316110', 'Harper Warehouse', '2026-04-09', '2026-04-29', 'Shipped', 105);



-- VIEW: Used by predefined query for checked out books
CREATE OR REPLACE VIEW MemberLoanView AS
SELECT
    m.MemberID,
    m.FirstName,
    m.LastName,
    bt.Title,
    bl.CheckoutDate,
    bl.DueDate,
    CASE
        WHEN bl.ReturnDate IS NULL THEN 'Still Checked Out'
        ELSE 'Returned'
    END AS LoanStatus
FROM Members m
JOIN BookLoans bl ON m.MemberID = bl.MemberID
JOIN BookCopies bc ON bl.CopyID = bc.CopyID
JOIN BookTitles bt ON bc.ISBN = bt.ISBN;


SHOW TABLES;
SELECT * FROM Members;
SELECT * FROM MinorRestrictions;
SELECT * FROM Computers;
SELECT * FROM ComputerReservations;

-- Query: Shows the number of books currently checked out per member (max allowed is 5)
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

-- Query: Shows the total unpaid late fees owed per member
SELECT
    m.MemberID,
    m.FirstName,
    m.LastName,
    SUM(lf.Amount) AS TotalUnpaidFees
FROM Members m
JOIN BookLoans bl ON m.MemberID = bl.MemberID
JOIN LateFees lf ON bl.LoanID = lf.LoanID
WHERE lf.PaidStatus = FALSE
GROUP BY m.MemberID, m.FirstName, m.LastName;

-- Query: Uses MemberLoanView to find all currently checked out books
SELECT
    MemberID,
    FirstName,
    LastName,
    Title,
    CheckoutDate,
    DueDate
FROM MemberLoanView
WHERE LoanStatus = 'Still Checked Out';

-- Query: Finds all overdue books with member name and book title
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

-- Query: Finds members who have more loans than the average member
SELECT
    m.MemberID,
    m.FirstName,
    m.LastName,
    COUNT(bl.LoanID) AS TotalLoans
FROM Members m
JOIN BookLoans bl ON m.MemberID = bl.MemberID
GROUP BY m.MemberID, m.FirstName, m.LastName
HAVING COUNT(bl.LoanID) > (
    SELECT AVG(LoanCount)
    FROM (
        SELECT COUNT(LoanID) AS LoanCount
        FROM BookLoans
        GROUP BY MemberID
    ) AS LoanCounts
);

-- Query: Finds books that have never been loaned out
SELECT
    bt.ISBN,
    bt.Title,
    bt.Publisher,
    bt.PublicationYear
FROM BookTitles bt
WHERE bt.ISBN NOT IN (
    SELECT DISTINCT bc.ISBN
    FROM BookCopies bc
    JOIN BookLoans bl ON bc.CopyID = bl.CopyID
);

-- Query: Finds members who have unpaid fees higher than the average unpaid fee
SELECT
    m.MemberID,
    m.FirstName,
    m.LastName,
    SUM(lf.Amount) AS TotalUnpaidFees
FROM Members m
JOIN BookLoans bl ON m.MemberID = bl.MemberID
JOIN LateFees lf ON bl.LoanID = lf.LoanID
WHERE lf.PaidStatus = FALSE
GROUP BY m.MemberID, m.FirstName, m.LastName
HAVING SUM(lf.Amount) > (
    SELECT AVG(TotalFees)
    FROM (
        SELECT SUM(Amount) AS TotalFees
        FROM LateFees
        WHERE PaidStatus = FALSE
        GROUP BY LoanID
    ) AS FeeTotals
);



-- Deliverable 5(6.) Phil

-- Function that Calculates Unpaid fees for a member
DELIMITER //

CREATE FUNCTION GetTotalUnpaidFees(p_MemberID INT)
RETURNS DECIMAL(12,2)
DETERMINISTIC
BEGIN
    DECLARE totalFees DECIMAL(12,2);

    SELECT COALESCE(SUM(lf.Amount), 0)
    INTO totalFees
    FROM LateFees lf
    JOIN BookLoans bl ON lf.LoanID = bl.LoanID
    WHERE bl.MemberID = p_MemberID
      AND lf.PaidStatus = FALSE;

    RETURN totalFees;
END //

DELIMITER ;

-- Test query for function
SELECT GetTotalUnpaidFees(1) AS TotalUnpaidFees;

-- Procedure that shows active loans for a member
DELIMITER //

CREATE PROCEDURE GetActiveLoansByMember(IN p_MemberID INT)
BEGIN
    SELECT
        m.MemberID,
        m.FirstName,
        m.LastName,
        bt.Title,
        bl.CheckoutDate,
        bl.DueDate
    FROM Members m
    JOIN BookLoans bl ON m.MemberID = bl.MemberID
    JOIN BookCopies bc ON bl.CopyID = bc.CopyID
    JOIN BookTitles bt ON bc.ISBN = bt.ISBN
    WHERE m.MemberID = p_MemberID
      AND bl.ReturnDate IS NULL;
END //

DELIMITER ;

-- test query for procedure
CALL GetActiveLoansByMember(1);

-- login implementation
ALTER TABLE Employees ADD COLUMN password VARCHAR(255);

UPDATE Employees SET password = 'password123' WHERE EmployeeID = 101;
UPDATE Employees SET password = 'password123' WHERE EmployeeID = 102;
UPDATE Employees SET password = 'password123' WHERE EmployeeID = 103;
UPDATE Employees SET password = 'password123' WHERE EmployeeID = 104;
UPDATE Employees SET password = 'password123' WHERE EmployeeID = 105;

ALTER TABLE Members ADD COLUMN password VARCHAR(255);

UPDATE Members SET password = 'password123' WHERE MemberID = 1;
UPDATE Members SET password = 'password123' WHERE MemberID = 2;
UPDATE Members SET password = 'password123' WHERE MemberID = 3;
UPDATE Members SET password = 'password123' WHERE MemberID = 4;
UPDATE Members SET password = 'password123' WHERE MemberID = 5;
UPDATE Members SET password = 'password123' WHERE MemberID = 6;
UPDATE Members SET password = 'password123' WHERE MemberID = 7;
UPDATE Members SET password = 'password123' WHERE MemberID = 8;
UPDATE Members SET password = 'password123' WHERE MemberID = 9;