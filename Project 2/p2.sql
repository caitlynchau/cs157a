--
-- db2 -td"@" -f P2.clp
--
CONNECT TO CS157A@
--
--
DROP PROCEDURE P2.CUST_CRT@
DROP PROCEDURE P2.CUST_LOGIN@
DROP PROCEDURE P2.ACCT_OPN@
DROP PROCEDURE P2.ACCT_CLS@
DROP PROCEDURE P2.ACCT_DEP@
DROP PROCEDURE P2.ACCT_WTH@
DROP PROCEDURE P2.ACCT_TRX@
DROP PROCEDURE P2.ADD_INTEREST@
--
--

-- Create Customer Procedure
CREATE PROCEDURE P2.CUST_CRT
(IN p_name CHAR(15), IN p_gender CHAR(1), IN p_age INTEGER, IN p_pin INTEGER, OUT id INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
BEGIN
  IF p_gender != 'M' AND p_gender != 'F' THEN
    SET sql_code = -100;
    SET err_msg = 'Invalid gender';
  ELSEIF p_age <= 0 THEN
    SET sql_code = -100;
    SET err_msg = 'Invalid age';
  ELSEIF p_pin < 0 THEN
    SET sql_code = -100;
    SET err_msg = 'Invalid pin';
  ELSE
    
    -- insert new customer with information name, gender, age, and pin
    INSERT INTO p2.customer (name, gender, age, pin) values (p_name, p_gender, p_age, p2.encrypt(p_pin));
    set id = IDENTITY_VAL_LOCAL();
    
    -- new customer successful
    SET err_msg = '-';
    SET sql_code = 0;
  END IF;
END@

-- Customer Login Procedure
CREATE PROCEDURE P2.CUST_LOGIN
(IN p_id INTEGER, IN p_pin INTEGER, OUT valid INT, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
BEGIN
  DECLARE pin INTEGER;
  -- query for pin
  DECLARE c1 cursor for select p2.customer.pin from p2.customer where p2.customer.id = p_id;
  open c1;
  fetch c1 into pin;
  close c1;
  
  -- check if decrypted pin equals p_pin
  if p2.decrypt(pin) = p_pin THEN
    SET valid = 1;
    SET err_msg = '-';
    SET sql_code = 0;
  ELSE
    SET valid = 0;
    SET err_msg = 'Incorrect id or pin';
    SET sql_code = -100;
  END IF;

END@


-- Account Open Procedure
CREATE PROCEDURE P2.ACCT_OPN
(IN p_id INTEGER, IN p_balance INTEGER, IN p_type CHAR(1), OUT number INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
BEGIN

  DECLARE idExists INTEGER;

  DECLARE c1 CURSOR FOR SELECT COUNT(*) FROM p2.customer WHERE p2.customer.id = p_id;
  OPEN c1;
  FETCH c1 into idExists;
  CLOSE c1;

  IF idExists <= 0 THEN
    SET sql_code = -100;
    SET err_msg = 'Invalid customer id';
  ELSEIF p_balance < 0 THEN
    SET sql_code = -100;
    SET err_msg = 'Invalid balance';
  ELSEIF p_type != 'C' and p_type != 'S' THEN 
    SET sql_code = -100;
    SET err_msg = 'Invalid account type';
  ELSE 
    -- open new account for iser ID
    INSERT INTO p2.account (id, balance, type, status) values (p_id, p_balance, p_type, 'A');
    set number = IDENTITY_VAL_LOCAL();
    -- new account successful
    SET err_msg = '-';
    SET sql_code = 0;
  END IF;
END@


-- Close Account Procedure
create procedure P2.ACCT_CLS
(in p_number INTEGER, out sql_code integer, out err_msg char(100))
language sql
begin
  declare accExists integer;
  declare c1 cursor for select count(*) from p2.account where p2.account.number = p_number;
  open c1;
  fetch c1 into accExists;
  close c1;

  if accExists <= 0 then
    set sql_code = -100;
    set err_msg = 'Invalid account number';
  else
    update p2.account set status = 'I', balance = 0 where p2.account.number = p_number;
    set err_msg = '-';
    set sql_code = 0;
  end if;

end@

-- Deposit Procedure
create procedure P2.ACCT_DEP
(in p_number integer, in p_amt integer, out sql_code integer, out err_msg char(100))
language sql
begin

  declare accExists integer;
  declare balance integer;
  declare accStatus char(1);
  declare c1 cursor for select count(*) from p2.account where p2.account.number = p_number;
  declare c2 cursor for select balance from p2.account where p2.account.number = p_number;
  declare c3 cursor for select status from p2.account where p2.account.number = p_number;

  open c1;
  fetch c1 into accExists;
  close c1;

  open c2;
  fetch c2 into balance;
  close c2;

  open c3;
  fetch c3 into accStatus;
  close c3;

  -- check if valid account number or if account is invalid
  if accExists <= 0 or accStatus != 'A' then
    set sql_code = -100;
    set err_msg = 'Invalid account number';
  -- check if deposit amount < 0
  elseif p_amt < 0 then
    set err_msg = 'Invalid amount';
    set sql_code = -100;
  else 
    -- successful deposit
    set p_amt = p_amt + balance; -- update balance
    update p2.account set p2.account.balance = p_amt where p2.account.number = p_number;
    set err_msg = '-';
    set sql_code = 0;

  end if;
end@

-- Withdraw Procedure
create procedure P2.ACCT_WTH
(in p_number integer, in p_amt integer, out sql_code integer, out err_msg char(100))
language sql
begin

  declare accExists integer;
  declare balance integer;
  declare accStatus char(1);
  declare c1 cursor for select count(*) from p2.account where p2.account.number = p_number;
  declare c2 cursor for select balance from p2.account where p2.account.number = p_number;
  declare c3 cursor for select status from p2.account where p2.account.number = p_number;

  open c1;
  fetch c1 into accExists;
  close c1;

  open c3;
  fetch c3 into accStatus;
  close c3;

  open c2;
  fetch c2 into balance;
  close c2;

  -- check if valid account number or if account is invalid
  if accExists <= 0 or accStatus != 'A' then
    set sql_code = -100;
    set err_msg = 'Invalid account number';
  -- check if withdraw amount < 0
  elseif p_amt < 0 then
    set err_msg = 'Invalid amount';
    set sql_code = -100;
  else 
      -- check if enough funds to withdraw
    set balance = balance - p_amt;
    if balance < 0 then
      set err_msg = 'Not enough funds';
      set sql_code = -100;
    else
      -- successful withdraw
      update p2.account set p2.account.balance = balance where p2.account.number = p_number;
      set err_msg = '-';
      set sql_code = 0;
    end if;

  end if;
end@


--
TERMINATE@
--
--
