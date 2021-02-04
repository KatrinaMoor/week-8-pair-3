SELECT *
FROM users;

SELECT *
FROM accounts;

SELECT *
FROM transfers;

SELECT *
FROM transfer_types;

SELECT *
FROM transfer_statuses;

SELECT u.username
    , a.balance
FROM users AS u
JOIN accounts AS a
    ON u.user_id = a.user_id;

BEGIN TRANSACTION;

INSERT INTO transfers(
    transfer_type_id
    , transfer_status_id
    , account_from
    , account_to
    , amount)
VALUES (
    2
    , 2
    , 1
    , 2
    , 200.00
    );

UPDATE accounts
SET balance = balance - 200.00
WHERE user_id = 1;

UPDATE accounts
SET balance = balance + 200.00
WHERE user_id = 2;

SELECT u.username
    , a.balance
FROM users AS u
JOIN accounts AS a
    ON u.user_id = a.user_id;

SELECT *
FROM transfers;

ROLLBACK;