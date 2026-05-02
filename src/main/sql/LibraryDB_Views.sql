USE LibraryDB;

-- ================================================
-- MEMBER VIEWS
-- ================================================

-- View: Shows a member's personal account info and library card details
CREATE OR REPLACE VIEW MemberAccountView AS
SELECT
	m.MemberID, 
    m.FirstName,
    m.LastName,
    m.Email,
    m.Address, 
    m.DateOfBirth,
    lc.CardNumber,
    lc.Status AS CardStatus,
    lc.ExpireDate
FROM Members m
JOIN LibraryCards lc ON m.MemberID = lc.MemberID;

-- View: Shows a member's loan history including book title, dates, and current loan status
CREATE OR REPLACE VIEW MemberLoanView AS
SELECT
    m.MemberID,
    m.FirstName,
    m.LastName,
    bt.Title,
    bl.CheckoutDate,
    bl.DueDate,
    bl.ReturnDate,
    CASE 
        WHEN bl.ReturnDate IS NULL THEN 'Still Checked Out'
        ELSE 'Returned'
    END AS LoanStatus
FROM Members m
JOIN BookLoans bl ON m.MemberID = bl.MemberID
JOIN BookCopies bc ON bl.CopyID = bc.CopyID
JOIN BookTitles bt ON bc.ISBN = bt.ISBN;
    
-- View: Shows a member's late fees including amount, date assessed, and payment status
CREATE OR REPLACE VIEW MemberLateFeeView AS
SELECT 
	m.MemberID, 
    m.FirstName,
    m.LastName,
    bt.Title,
    lf.Amount,
    lf.DateAssessed,
    CASE
		WHEN lf.PaidStatus = TRUE THEN 'Paid'
        ELSE 'Unpaid'
	END AS PaymentStatus,
    lf.PaymentDate
FROM Members m
JOIN BookLoans bl ON m.MemberID = bl.MemberID
JOIN LateFees lf ON bl.LoanID = lf.LoanID
JOIN BookCopies bc ON bl.CopyID = bc.CopyID
JOIN BookTitles bt ON bc.ISBN = bt.ISBN;

-- View: Shows a member's computer reservations including scheduled and actual usage times
CREATE OR REPLACE VIEW MemberReservationView AS
SELECT
    m.MemberID,
    m.FirstName,
    m.LastName,
    c.ComputerNumber,
    c.Location,
    cr.StartTime,
    cr.EndTime,
    cr.UsageStart,
    cr.UsageEnd,
    CASE
        WHEN cr.UsageStart IS NULL THEN 'Not Started'
        WHEN cr.UsageEnd IS NULL THEN 'In Progress'
        ELSE 'Completed'
    END AS ReservationStatus
FROM Members m
JOIN ComputerReservations cr ON m.MemberID = cr.MemberID
JOIN Computers c ON cr.ComputerID = c.ComputerID;

-- ================================================
-- GUARDIAN VIEWS
-- ================================================

-- View: Shows a guardian's own personal account info and library card details
CREATE OR REPLACE VIEW GuardianAccountView AS
SELECT 
	m.MemberID,
    m.FirstName,
    m.Email,
    m.Address, 
    m.DateOfBirth,
    lc.CardNumber,
    lc.Status AS CardStatus,
    lc.ExpireDate
FROM Members m
JOIN LibraryCards lc ON m.MemberID = lc.MemberID
JOIN Guards g ON m.MemberID = g.GuardianID 
WHERE m.IsMinor = FALSE; 

-- View: Shows the minor's basic account info and library card details linked to their guardian
CREATE OR REPLACE VIEW GuardianMinorAccountView AS
SELECT 
	g.GuardianID,
    m.MemberID AS MinorID,
    m.FirstName AS MinorFirstName,
    m.LastName AS MinorLastName,
    m.Email AS MinorEmail,
    m.Address AS MinorAddress,
    m.DateOfBirth AS MinorDateOfBirth,
    lc.CardNumber AS MinorCardNumber,
    lc.Status AS MinorCardStatus,
    lc.ExpireDate AS MinorCardExpireDate
FROM Members m
JOIN LibraryCards lc ON m.MemberID = lc.MemberID
JOIN Guards g ON m.MemberID = g.MemberID
WHERE m.IsMinor = TRUE;

-- View: Shows the restrictions applied to a guardian's minor
CREATE OR REPLACE VIEW GuardianMinorRestrictionsView AS
SELECT
    g.GuardianID,
    m.MemberID AS MinorID,
    m.FirstName AS MinorFirstName,
    m.LastName AS MinorLastName,
    mr.RestrictionID,
    mr.RestrictDeviceAccess,
    mr.UseTimeLimit,
    mr.Notes
FROM Members m
JOIN Guards g ON m.MemberID = g.MemberID
JOIN MinorRestrictions mr ON m.MemberID = mr.MemberID
WHERE m.IsMinor = TRUE;

-- View: Shows the minor's loan history including book title, dates, and current loan status
CREATE OR REPLACE VIEW GuardianMinorLoanView AS
SELECT
    g.GuardianID,
    m.MemberID AS MinorID,
    m.FirstName AS MinorFirstName,
    m.LastName AS MinorLastName,
    bt.Title,
    bl.CheckoutDate,
    bl.DueDate,
    bl.ReturnDate,
    CASE
        WHEN bl.ReturnDate IS NULL THEN 'Still Checked Out'
        ELSE 'Returned'
    END AS LoanStatus
FROM Members m
JOIN Guards g ON m.MemberID = g.MemberID
JOIN BookLoans bl ON m.MemberID = bl.MemberID
JOIN BookCopies bc ON bl.CopyID = bc.CopyID
JOIN BookTitles bt ON bc.ISBN = bt.ISBN
WHERE m.IsMinor = TRUE;

-- View: Shows the minor's computer reservations including scheduled and actual usage times
CREATE OR REPLACE VIEW GuardianMinorReservationView AS
SELECT
    g.GuardianID,
    m.MemberID AS MinorID,
    m.FirstName AS MinorFirstName,
    m.LastName AS MinorLastName,
    c.ComputerNumber,
    c.Location,
    cr.StartTime,
    cr.EndTime,
    cr.UsageStart,
    cr.UsageEnd,
    CASE
        WHEN cr.UsageStart IS NULL THEN 'Not Started'
        WHEN cr.UsageEnd IS NULL THEN 'In Progress'
        ELSE 'Completed'
    END AS ReservationStatus
FROM Members m
JOIN Guards g ON m.MemberID = g.MemberID
JOIN ComputerReservations cr ON m.MemberID = cr.MemberID
JOIN Computers c ON cr.ComputerID = c.ComputerID
WHERE m.IsMinor = TRUE;

-- ================================================
-- STAFF VIEWS
-- ================================================

-- View: Shows basic member info and library card status, excludes sensitive personal details
CREATE OR REPLACE VIEW StaffMemberView AS
SELECT
    m.MemberID,
    m.FirstName,
    m.LastName,
    m.Email,
    lc.CardNumber,
    lc.Status AS CardStatus,
    lc.ExpireDate,
    m.IsMinor
FROM Members m
JOIN LibraryCards lc ON m.MemberID = lc.MemberID;

-- View: Shows all active and past book loans across all members including book title and due dates
CREATE OR REPLACE VIEW StaffBookLoanView AS
SELECT
    m.MemberID,
    m.FirstName,
    m.LastName,
    bt.Title,
    bl.LoanID,
    bl.CheckoutDate,
    bl.DueDate,
    bl.ReturnDate,
    CASE
        WHEN bl.ReturnDate IS NULL AND bl.DueDate < CURDATE() THEN 'Overdue'
        WHEN bl.ReturnDate IS NULL THEN 'Still Checked Out'
        ELSE 'Returned'
    END AS LoanStatus
FROM Members m
JOIN BookLoans bl ON m.MemberID = bl.MemberID
JOIN BookCopies bc ON bl.CopyID = bc.CopyID
JOIN BookTitles bt ON bc.ISBN = bt.ISBN;

-- View: Shows all books and their current availability status including copy details
CREATE OR REPLACE VIEW StaffBookAvailabilityView AS
SELECT
    bt.ISBN,
    bt.Title,
    bt.Publisher,
    bt.PublicationYear,
    bc.CopyID,
    bc.CopyNumber,
    bc.Status AS CopyStatus,
    bc.AquisitionDate
FROM BookTitles bt
JOIN BookCopies bc ON bt.ISBN = bc.ISBN;

-- View: Shows all computer reservations across all members including usage times and status
CREATE OR REPLACE VIEW StaffComputerReservationView AS
SELECT
    m.MemberID,
    m.FirstName,
    m.LastName,
    c.ComputerID,
    c.ComputerNumber,
    c.Location,
    cr.ReservationID,
    cr.StartTime,
    cr.EndTime,
    cr.UsageStart,
    cr.UsageEnd,
    CASE
        WHEN cr.UsageStart IS NULL THEN 'Not Started'
        WHEN cr.UsageEnd IS NULL THEN 'In Progress'
        ELSE 'Completed'
    END AS ReservationStatus
FROM Members m
JOIN ComputerReservations cr ON m.MemberID = cr.MemberID
JOIN Computers c ON cr.ComputerID = c.ComputerID;

-- View: Shows all late fees across all members including payment status and amount owed
CREATE OR REPLACE VIEW StaffLateFeeView AS
SELECT
    m.MemberID,
    m.FirstName,
    m.LastName,
    bt.Title,
    bl.LoanID,
    lf.LateFeeID,
    lf.Amount,
    lf.DateAssessed,
    CASE
        WHEN lf.PaidStatus = TRUE THEN 'Paid'
        ELSE 'Unpaid'
    END AS PaymentStatus,
    lf.PaymentDate
FROM Members m
JOIN BookLoans bl ON m.MemberID = bl.MemberID
JOIN LateFees lf ON bl.LoanID = lf.LoanID
JOIN BookCopies bc ON bl.CopyID = bc.CopyID
JOIN BookTitles bt ON bc.ISBN = bt.ISBN;

-- ================================================
-- MANAGER VIEWS
-- ================================================

-- View: Shows full member details including sensitive personal info for manager access
CREATE OR REPLACE VIEW ManagerMemberView AS
SELECT
    m.MemberID,
    m.FirstName,
    m.LastName,
    m.Email,
    m.Address,
    m.DateOfBirth,
    m.LicenseID,
    m.IsMinor,
    lc.CardNumber,
    lc.Status AS CardStatus,
    lc.IssueDate,
    lc.ExpireDate
FROM Members m
JOIN LibraryCards lc ON m.MemberID = lc.MemberID;

-- View: Shows all book orders including delivery status and the employee who placed the order
CREATE OR REPLACE VIEW ManagerBookOrderView AS
SELECT
    bo.OrderID,
    bo.DeliveryNo,
    bt.Title,
    bt.ISBN,
    bo.PublicationWarehouse,
    bo.OrderDate,
    bo.ExpectedDate,
    bo.DeliveryStatus,
    e.EmployeeID,
    e.FirstName AS EmployeeFirstName,
    e.LastName AS EmployeeLastName
FROM BookOrders bo
JOIN BookTitles bt ON bo.ISBN = bt.ISBN
JOIN Employees e ON bo.EmployeeID = e.EmployeeID;

-- View: Shows staff employee accounts only, excludes manager and super manager accounts
CREATE OR REPLACE VIEW ManagerStaffAccountView AS
SELECT
    e.EmployeeID,
    e.FirstName,
    e.LastName,
    e.Email,
    e.PhoneNumber,
    e.Address,
    e.HireDate,
    e.Position
FROM Employees e
WHERE e.Position IN ('Librarian', 'Assistant');

-- View: Shows all book requests made by members including request type and book details
CREATE OR REPLACE VIEW ManagerBookRequestView AS
SELECT
    br.RequestID,
    m.MemberID,
    m.FirstName,
    m.LastName,
    bt.Title,
    bt.ISBN,
    br.RequestDate,
    br.RequestType
FROM BookRequests br
JOIN Members m ON br.MemberID = m.MemberID
JOIN BookTitles bt ON br.ISBN = bt.ISBN;

-- View: Shows all trending books with demand scores and publisher details
CREATE OR REPLACE VIEW ManagerTrendingBooksView AS
SELECT
    tb.TrendingID,
    bt.ISBN,
    bt.Title,
    bt.Publisher,
    bt.PublicationYear,
    tb.DateMarked,
    tb.DemandScore
FROM TrendingBooks tb
JOIN BookTitles bt ON tb.ISBN = bt.ISBN
ORDER BY tb.DemandScore DESC;

-- ================================================
-- SUPER MANAGER VIEWS
-- ================================================

-- View: Shows full details of all members with no restrictions
CREATE OR REPLACE VIEW SuperManagerAllMembersView AS
SELECT
    m.*,
    lc.LibraryCardID,
    lc.CardNumber,
    lc.IssueDate,
    lc.ExpireDate,
    lc.Status AS CardStatus
FROM Members m
JOIN LibraryCards lc ON m.MemberID = lc.MemberID;

-- View: Shows full details of all employees including salaries and all positions with no restrictions
CREATE OR REPLACE VIEW SuperManagerAllEmployeesView AS
SELECT
    e.*,
    m.FirstName AS MemberFirstName,
    m.LastName AS MemberLastName,
    m.Email AS MemberEmail
FROM Employees e
LEFT JOIN Members m ON e.MemberID = m.MemberID;

-- View: Shows full details of all loans across all members with no restrictions
CREATE OR REPLACE VIEW SuperManagerAllLoansView AS
SELECT
    bl.*,
    m.FirstName AS MemberFirstName,
    m.LastName AS MemberLastName,
    bt.Title AS BookTitle,
    bc.CopyNumber,
    e.FirstName AS EmployeeFirstName,
    e.LastName AS EmployeeLastName,
    CASE
        WHEN bl.ReturnDate IS NULL AND bl.DueDate < CURDATE() THEN 'Overdue'
        WHEN bl.ReturnDate IS NULL THEN 'Still Checked Out'
        ELSE 'Returned'
    END AS LoanStatus
FROM BookLoans bl
JOIN Members m ON bl.MemberID = m.MemberID
JOIN BookCopies bc ON bl.CopyID = bc.CopyID
JOIN BookTitles bt ON bc.ISBN = bt.ISBN
JOIN Employees e ON bl.EmployeeID = e.EmployeeID;

-- View: Shows full details of all late fees across all members with no restrictions
CREATE OR REPLACE VIEW SuperManagerAllFeesView AS
SELECT
    lf.*,
    m.MemberID,
    m.FirstName AS MemberFirstName,
    m.LastName AS MemberLastName,
    bt.Title AS BookTitle,
    CASE
        WHEN lf.PaidStatus = TRUE THEN 'Paid'
        ELSE 'Unpaid'
    END AS PaymentStatus
FROM LateFees lf
JOIN BookLoans bl ON lf.LoanID = bl.LoanID
JOIN Members m ON bl.MemberID = m.MemberID
JOIN BookCopies bc ON bl.CopyID = bc.CopyID
JOIN BookTitles bt ON bc.ISBN = bt.ISBN;

-- View: Shows full details of all book orders with no restrictions
CREATE OR REPLACE VIEW SuperManagerAllOrdersView AS
SELECT
    bo.*,
    bt.Title AS BookTitle,
    bt.Publisher,
    bt.PublicationYear,
    e.FirstName AS EmployeeFirstName,
    e.LastName AS EmployeeLastName,
    e.Position AS EmployeePosition
FROM BookOrders bo
JOIN BookTitles bt ON bo.ISBN = bt.ISBN
JOIN Employees e ON bo.EmployeeID = e.EmployeeID;

-- View: Shows full details of all computer reservations across all members with no restrictions
CREATE OR REPLACE VIEW SuperManagerAllReservationsView AS
SELECT
    cr.*,
    m.FirstName AS MemberFirstName,
    m.LastName AS MemberLastName,
    c.ComputerNumber,
    c.Location,
    c.Status AS ComputerStatus,
    CASE
        WHEN cr.UsageStart IS NULL THEN 'Not Started'
        WHEN cr.UsageEnd IS NULL THEN 'In Progress'
        ELSE 'Completed'
    END AS ReservationStatus
FROM ComputerReservations cr
JOIN Members m ON cr.MemberID = m.MemberID
JOIN Computers c ON cr.ComputerID = c.ComputerID;
