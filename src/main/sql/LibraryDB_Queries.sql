-- Deliverable 5 Queries (Need min 2 that perform aggregations and min 3 that peform joins)

-- Return full book info of any trending books
SELECT * 
FROM BookTitles book
JOIN TrendingBooks trending
ON book.ISBN = trending.ISBN
;

-- Find highest trending book (FIX)
SELECT Title
FROM BookTitle book
JOIN TrendingBooks trending
ON book.ISBN = trending.ISBN
GROUP BY DemandScore
HAVING max(DemandScore);

-- Find how many books overdue member has
SELECT COUNT(*)
FROM Member m
JOIN BookLoan loan
ON loan.MemberID = m.memberID
JOIN LateFees fee
ON fee.LoanID = loan.LoanID

-- Find 

