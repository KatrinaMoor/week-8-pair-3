SELECT *
FROM accounts;

SELECT *
FROM transfer_status;

--we need to create--
SELECT *
FROM transfer_types;

SELECT *
FROM users;

--we need to create--
SELECT *
FROM transfers;


SELECT t.transfer_id
    , t.account_from
    , t.account_to
    , t.amount
    , u.username

FROM transfers as t
JOIN accounts as a
ON t.account_to = a.account_id
JOIN users as u
ON u.user_id = a.user_id;

SELECT t.transfer_id
    , t.account_from
    , t.account_to
    , t.amount
    , u.username

FROM transfers as t
JOIN accounts as a
ON t.account_to = a.account_id
JOIN users as u
ON u.user_id = a.user_id;
