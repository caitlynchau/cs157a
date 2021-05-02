SELECT AVG(total) 
FROM (
    SELECT p1.customer.id, p1.customer.name, p1.customer.gender, p1.customer.age, SUM(p1.account.balance) as total
    FROM p1.customer 
    INNER JOIN p1.account ON p1.account.id = p1.customer.id 
    GROUP BY p1.customer.id, p1.customer.name, p1.customer.gender, p1.customer.age
    ORDER BY total DESC) reportA
WHERE reportA.age >= 10 AND reportA.age <= 18


String query = "SELECT p1.account.number, p1.account.balance FROM p1.account WHERE id = " + cusID;

SELECT AVG(p1.account.balance) as average
FROM p1.customer
INNER JOIN p1.account ON p1.account.id = p1.customer.id
GROUP BY p1.customer.id, p1.customer.age
HAVING p1.customer.age >= 10 AND p1.customer.age <= 18

SELECT *
FROM p1.customer
INNER JOIN p1.account ON p1.account.id = p1.customer.id