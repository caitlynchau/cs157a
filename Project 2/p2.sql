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
    SET err_msg = 'Error: gender must be either M or F';
  ELSEIF p_age <= 0 THEN
    SET sql_code = -100;
    SET err_msg = 'Error: age must be an integer greater than 0';
  ELSEIF p_pin < 0 THEN
    SET sql_code = -100;
    SET err_msg = 'Error: PIN must be an integer greater than 0';
  ELSE
    
    -- insert new customer with information name, gender, age, and pin
    INSERT INTO p2.customer (name, gender, age, pin) values (p_name, p_gender, p_age, p2.encrypt(p_pin));
    set id = IDENTITY_VAL_LOCAL();
    
    -- new customer successful
    SET err_msg = 'Successfully created new customer';
    SET sql_code = 1;
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
    SET err_msg = 'Successfully logged in';
    SET sql_code = 1;
  ELSE
    SET valid = 0;
    SET err_msg = 'Error: Incorrect id or pin';
    SET sql_code = -500;
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

  IF p_id <= 0 THEN
    SET sql_code = -100;
    SET err_msg = 'Error: ID is not valid';
  ELSEIF p_balance < 0 THEN
    SET sql_code = -100;
    SET err_msg = 'Error: initial deposit must be an integer greater than 0';
  ELSEIF p_type != 'C' and p_type != 'S' THEN 
    SET sql_code = -100;
    SET err_msg = 'Error: type must be either C or S';
  ELSE 
    -- open new account for iser ID
    INSERT INTO p2.account (id, balance, type, status) values (p_id, p_balance, p_type, 'A');
    set number = IDENTITY_VAL_LOCAL();
    -- new account successful
    SET err_msg = 'Account successfuly created.';
    SET sql_code = 1;
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
    set err_msg = 'Error: Invalid account number';
  else
    update p2.account set status = 'I', balance = 0 where p2.account.number = p_number;
    set err_msg = 'Account successfully closed';
    set sql_code = 1;
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
  declare c_count cursor for select count(*) from p2.account where p2.account.number = p_number;
  declare c_balance cursor for select balance from p2.account where p2.account.number = p_number;
  declare c_status cursor for select status from p2.account where p2.account.number = p_number;

  open c_count;
  fetch c_count into accExists;
  close c_count;

  open c_balance;
  fetch c_balance into balance;
  close c_balance;

  open c_status;
  fetch c_status into accStatus;
  close c_status;

  -- check if valid account number or if account is inactive
  if accExists <= 0 then
    set sql_code = -100;
    set err_msg = 'Error: Invalid account number';
  -- check if account is inactive
  elseif accStatus != 'A' then
    set sql_code = -100;
    set err_msg = 'Error: Cannot deposit into an inactive account';
  -- check if deposit amount < 0
  elseif p_amt < 0 then
    set err_msg = 'Error: Amount must be an integer greater than 0';
    set sql_code = -100;
  else 
    -- successful deposit
    set p_amt = p_amt + balance; -- update balance
    update p2.account set p2.account.balance = p_amt where p2.account.number = p_number;
    set err_msg = 'Successfully deposited';
    set sql_code = 1;
  end if;
end@

-- Withdraw Procedure
create procedure P2.ACCT_WTH
(in p_number integer, in p_amt integer, out sql_code integer, out err_msg char(100))
language sql
begin

  declare accExists integer;
  declare currentBalance integer;
  declare accStatus char(1);
  declare c_count cursor for select count(*) from p2.account where p2.account.number = p_number;
  declare c_balance cursor for select balance from p2.account where p2.account.number = p_number;
  declare c_status cursor for select status from p2.account where p2.account.number = p_number;

  open c_count;
  fetch c_count into accExists;
  close c_count;

  open c_balance;
  fetch c_balance into currentBalance;
  close c_balance;

  open c_status;
  fetch c_status into accStatus;
  close c_status;

  -- check if valid account number
  if accExists <= 0 then
    set sql_code = -100;
    set err_msg = 'Error: Invalid account number';
  -- check if account is active
  elseif accStatus != 'A' then
    set sql_code = -100;
    set err_msg = 'Error: Cannot withdraw from an inactive account';
  -- check if withdraw amount < 0
  elseif p_amt < 0 then
    set err_msg = 'Error: Amount must be an integer greater than 0';
    set sql_code = -100;
  else 
    -- check if enough funds to withdraw
    set currentBalance = currentBalance - p_amt;
    if currentBalance < 0 then
      set err_msg = 'Error: Not enough funds to withdraw';
      set sql_code = -100;
    else
      -- successful withdraw
      update p2.account set p2.account.balance = currentBalance where p2.account.number = p_number;
      set err_msg = 'Successful withdraw';
      set sql_code = 1;
    end if;
  end if;
end@


-- Transfer Procedure
create procedure P2.ACCT_TRX
(in src_acct integer, in dest_acct integer, in amt integer, out sql_code integer, out err_msg char(100))
language sql
begin
  declare withdrawErrorCode integer;
  declare depositErrorCode integer;
  declare withdrawMsg char(100) default '';
  declare depositMsg char(100) default '';

  declare srcAccExists integer;
  declare destAccExists integer;
  declare srcAccStatus char(1);
  declare destAccStatus char(1);

  declare c1 cursor for select count(*) from p2.account where p2.account.number = src_acct;
  declare c2 cursor for select status from p2.account where p2.account.number = src_acct;
  declare c3 cursor for select count(*) from p2.account where p2.account.number = dest_acct;
  declare c4 cursor for select status from p2.account where p2.account.number = dest_acct;
  
  open c1;
  fetch c1 into srcAccExists;
  close c1;

  open c2;
  fetch c2 into srcAccStatus;
  close c2;

  open c3;
  fetch c3 into destAccExists;
  close c3;

  open c4;
  fetch c4 into destAccStatus;
  close c4;

  -- check if transfer amount < 0
  if amt < 0 then
    set err_msg = 'Error: amount must be an integer greater than 0';
    set sql_code = -100;
  else
    -- try to withdraw
    call P2.ACCT_WTH(src_acct, amt, withdrawErrorCode, withdrawMsg);
    if withdrawErrorCode = 1 then
      -- successful withdraw
      call P2.ACCT_DEP(dest_acct, amt, depositErrorCode, depositMsg);
      if depositErrorCode = 1 then
        set err_msg = 'Transfer successful';
        set sql_code = 1;
      else 
        set err_msg = 'Error: Transfer unsuccessful';
        set sql_code = -100;
      end if;
    else 
      -- unsuccessful withdraw
      set err_msg = 'Error: Transfer unsuccessful';
      set sql_code = -100;
    end if;
  end if;
end@

-- Add Interest Procedure
create procedure P2.ADD_INTEREST
(in s_rate decfloat, in c_rate decfloat, out sql_code integer, out err_msg char(100)) 
language sql
begin
  if s_rate <= 0 then
    set sql_code = -100;
    set err_msg = 'Error: Invalid savings interest rate';
  elseif c_rate <= 0 then
    set sql_code = -100;
    set err_msg = 'Error: Invalid checking interest rate';
  else
    update p2.account set balance = balance+(balance * s_rate) where status = 'A' and type = 'S';
    update p2.account set balance = balance+(balance * c_rate) where status = 'A' and type = 'C';
    set err_msg = 'Add interest successful';
    set sql_code = 1;
  end if;
end@

-- create procedure P2.ACCT_EXISTS
-- (in number integer, out exists integer, out sql_code integer, out err_msg char(100))
-- language sql
-- begin
--   declare count integer;
--   declare cursor c1 for select count(*) from p2.account where p2.account.number = number;
--   open c1;
--   fetch c1 into count;
--   close c1;
--   if count > 0 then
--     set exists = 1; -- true
--   else 
--     set exists = 0;
--   end if;
--   set error_msg = 'acct exists';
--   set sql_code = 1;
-- end@

--
TERMINATE@
--
--
