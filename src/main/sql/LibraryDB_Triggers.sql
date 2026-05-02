-- Deliverable 5 Triggers (need min of 3)

-- Ensures a string of only whitespace is not entered as the name for members
DROP TRIGGER IF EXISTS emptyNameCheck;
DELIMITER %%
CREATE TRIGGER emptyNameCheck
BEFORE INSERT ON Members
FOR EACH ROW
BEGIN
	IF TRIM(NEW.FirstName) = '' THEN
		SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = "First name cannot be full of empty spaces!";
	END IF;
    
    IF TRIM(NEW.LastName) = '' THEN
		SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = "Last name cannot be full of empty spaces!";
	END IF;
    
    IF TRIM(NEW.Address) = '' OR TRIM(NEW.Email) = '' THEN
		SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = "Address or email field should not be full of whitespace!";
	END IF;
END %%
DELIMITER ;

-- Ensure book title is not empty string of whitespace
DROP TRIGGER IF EXISTS titleCheck;
DELIMITER %%
CREATE TRIGGER titleCheck
BEFORE INSERT ON BookTitles
FOR EACH ROW
BEGIN
	IF TRIM(NEW.Title) = '' OR TRIM(NEW.ISBN) = '' OR TRIM(NEW.Publisher) = '' THEN
		SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Title, ISBN, and/or Publisher fields cannot be only spaces';
	END IF;
END %%
DELIMITER ;

-- Ensure book title is not empty string of whitespace
DROP TRIGGER IF EXISTS genreNameCheck;
DELIMITER %%
CREATE TRIGGER genreNameCheck
BEFORE INSERT ON Genres
FOR EACH ROW
BEGIN
	IF TRIM(NEW.GenreNAME) = '' THEN
		SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = "Genre name shouldn't be full of whitespace!";
	END IF;
END %%
DELIMITER ;


-- Prevent a member from making a book request if they have a checked out book that is past due.
DROP TRIGGER IF EXISTS lateCheck;
DELIMITER %%
CREATE TRIGGER lateCheck
BEFORE INSERT ON BookLoans
FOR EACH ROW
BEGIN
	DECLARE pastDue INT;
    
    SELECT count(*)
    INTO pastDue 
    FROM BookLoans loan
    JOIN LateFees fees
    ON NEW.memberID = loan.memberID AND loan.LoanID = fees.LoanID;
    
    IF pastDue > 0 THEN
		SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = 'Member currently has a checked out book that is past due!';
    END IF;
END %%
DELIMITER ;

-- Ensure computer reservations do not overlap on the same computerID
DROP TRIGGER IF EXISTS reservationConflict;
DELIMITER %%
CREATE TRIGGER reservationConflict
BEFORE INSERT ON computerReservations
FOR EACH ROW
BEGIN
	IF NEW.StartTime > NEW.EndTime THEN
		SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = 'Please set StartTime to come before EndTime';
    END IF;
    
	IF EXISTS (
		SELECT *
		FROM computerReservations comp
		WHERE comp.ComputerID = NEW.ComputerID
        AND (StartTime > NEW.EndTime OR EndTime < New.StartTime) )
	THEN 
		SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'There is an overlap with the scheduling!'; 
    END IF;
END %%
DELIMITER ;

-- Apply restrictions to computer usage if member is a minor
DROP TRIGGER IF EXISTS restrictComp;
DELIMITER %%
CREATE TRIGGER restrictComp
BEFORE INSERT ON computerReservations
FOR EACH ROW
BEGIN
	DECLARE restriction INT;
    
    SELECT COUNT(*) 
    INTO restriction 
    FROM MinorRestrictions m
    WHERE NEW.MemberID = m.MemberID AND m.RestrictDeviceAccess = TRUE;
    
    IF restriction > 0 THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'Requester cannot make reservation, as they have restricted access!';
    END IF;
END %%
DELIMITER ;
