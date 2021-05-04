import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manage connection to database and perform SQL statements.
 */
public class BankingSystem {
	// Connection properties
	private static String driver;
	private static String url;
	private static String username;
	private static String password;
	
	// JDBC Objects
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;

	/**
	 * Initialize database connection given properties file.
	 * @param filename name of properties file
	 */
	public static void init(String filename) {
		try {
			Properties props = new Properties();						// Create a new Properties object
			FileInputStream input = new FileInputStream(filename);	// Create a new FileInputStream object using our filename parameter
			props.load(input);										// Load the file contents into the Properties object
			driver = props.getProperty("jdbc.driver");				// Load the driver
			url = props.getProperty("jdbc.url");						// Load the url
			username = props.getProperty("jdbc.username");			// Load the username
			password = props.getProperty("jdbc.password");			// Load the password
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test database connection.
	 */
	public static void testConnection() {
		System.out.println(":: TEST - CONNECTING TO DATABASE");
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			// con.close();
			System.out.println(":: TEST - SUCCESSFULLY CONNECTED TO DATABASE");
		} catch (Exception e) {
			System.out.println(":: TEST - FAILED CONNECTED TO DATABASE");
			e.printStackTrace();
		}
	}

	/**
	 * Create a new customer.
	 * @param name customer name
	 * @param gender customer gender
	 * @param age customer age
	 * @param pin customer pin
	 */
	public static boolean newCustomer(String name, String gender, String age, String pin) 
	{
		
		System.out.println(":: CREATE NEW CUSTOMER - RUNNING");
		
		// Validate user input for batch input processing
		int ageAsInt = 0, pinAsInt = 0;
		try {
			ageAsInt = Integer.parseInt(age);
		} catch (NumberFormatException e) {
			System.out.println(":: CREATE NEW CUSTOMER - ERROR - INVALID AGE");
			return false;
		}

		try {
			pinAsInt = Integer.parseInt(pin);
		} catch (NumberFormatException e) {
			System.out.println(":: CREATE NEW CUSTOMER - ERROR - INVALID PIN");
			return false;
		}
		
		try {
			stmt = con.createStatement();
			String query = "INSERT INTO p1.customer(NAME, GENDER, AGE, PIN) VALUES ('" 
			+ name + "', '" + gender + "', " + ageAsInt + ", " + pinAsInt + ")"; 
			// System.out.println(query);
			stmt.execute(query);
			System.out.println(":: CREATE NEW CUSTOMER - SUCCESS");
			return true;
		} catch (SQLException e) {
			// System.out.println(e.getMessage());
			// e.printStackTrace();
			System.out.println(":: CREATE NEW CUSTOMER - ERROR - INVALID INPUT");
			return false;
		} 
		
	}

	/**
	 * Open a new account.
	 * @param id customer id
	 * @param type type of account
	 * @param amount initial deposit amount
	 */
	public static boolean openAccount(String id, String type, String amount) 
	{
		System.out.println(":: OPEN ACCOUNT - RUNNING");

		String active = "A";
		try {
			stmt = con.createStatement();
			String query = "INSERT INTO p1.account(ID, BALANCE, TYPE, STATUS) VALUES (" 
			+ id + ", " + amount + ", '" + type + "', '" + active + "')"; 
			// System.out.println(query);
			stmt.execute(query);
			System.out.println(":: OPEN ACCOUNT - SUCCESS");
			return true;
		}catch (SQLException e) {
			// System.out.println(e.getMessage());
			// e.printStackTrace();
			System.out.println(":: OPEN ACCOUNT - ERROR - INVALID INPUT");
			return false;
		}
		
	}

	/**
	 * Close an account.
	 * @param accNum account number
	 */
	public static boolean closeAccount(String accNum) 
	{
		System.out.println(":: CLOSE ACCOUNT - RUNNING");
		try {
	        stmt = con.createStatement(); 
			String query = "UPDATE p1.account SET p1.account.status = 'I' AND SET p1.account.balance = 0 WHERE number = " + accNum;
			// DELETE FROM p1.account WHERE number = '" + accNum + "'";
			// System.out.println(query);
			stmt.execute(query);
			System.out.println(":: CLOSE ACCOUNT - SUCCESS");
			return true;
		} catch (SQLException e) {
			// System.out.println("Exception in main()");
			// e.printStackTrace();
			return false;
		}
		
	}


	/**
	 * Deposit into an account.
	 * @param accNum account number
	 * @param amount deposit amount
	 */
	public static void deposit(String accNum, String amount) 
	{
		System.out.println(":: DEPOSIT - RUNNING");
		try {
			int inputAmt = Integer.valueOf(amount);
			stmt = con.createStatement(); 
			String query1 = "SELECT balance FROM p1.account WHERE number = " + accNum;
			// System.out.println(query1);
			rs = stmt.executeQuery(query1);
			int currentBalance = 0;
			while(rs.next()) {
				currentBalance = rs.getInt("balance");
			}
			inputAmt += currentBalance;
	        stmt = con.createStatement(); 
			String query2 = "UPDATE p1.account SET balance = " + inputAmt + " WHERE number = " + accNum;
			// System.out.println(query2);
			stmt.execute(query2);
			System.out.println(":: DEPOSIT - SUCCESS");
		} catch (SQLException e) {
			System.out.println("Exception in main()");
			//e.printStackTrace();
		} catch (NumberFormatException e) {
			System.out.println(":: DEPOSIT - ERROR - INVALID AMOUNT");
		}
		
	}


	/**
	 * Withdraw from an account.
	 * @param accNum account number
	 * @param amount withdraw amount
	 */
	public static void withdraw(String accNum, String amount) 
	{
		System.out.println(":: WITHDRAW - RUNNING");
		try {
			int inputAmt = Integer.valueOf(amount);
			stmt = con.createStatement(); 
			String query1 = "SELECT balance FROM p1.account WHERE number = " + accNum;
			// System.out.println(query1);
			rs = stmt.executeQuery(query1);
			int currentBalance = 0;
			while(rs.next()) {
				currentBalance = rs.getInt("balance");
			}
			currentBalance -= inputAmt;
			if (currentBalance < 0) {
				System.out.println(":: WITHDRAW - ERROR - NOT ENOUGH FUNDS");
				return;
			} else {
				stmt = con.createStatement(); 
				String query2 = "UPDATE p1.account SET balance = " + currentBalance + " WHERE number = " + accNum;
				// System.out.println(query2);
				stmt.execute(query2);
				System.out.println(":: WITHDRAW - SUCCESS");
			}	
		} catch (SQLException e) {
			System.out.println("Exception in main()");
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.out.println(":: WITHDRAW - ERROR - INVALID AMOUNT");
			//e.printStackTrace();
		}
	}


	/**
	 * Transfer amount from source account to destination account. 
	 * @param srcAccNum source account number
	 * @param destAccNum destination account number
	 * @param amount transfer amount
	 */
	public static void transfer(String srcAccNum, String destAccNum, String amount) 
	{
		System.out.println(":: TRANSFER - RUNNING");
		try {
			int transfer = Integer.valueOf(amount);
			//Current balance of srcAcc
			stmt = con.createStatement(); 
			String selectAcc1 = "SELECT balance FROM p1.account WHERE number = " + srcAccNum;
			rs = stmt.executeQuery(selectAcc1);
			int balance1 = 0;
			while(rs.next()) {
				balance1 = rs.getInt("balance");
			}
			//Current balance of destAcc
			stmt = con.createStatement(); 
			String selectAcc2 = "SELECT balance FROM p1.account WHERE number = " + destAccNum;
			rs = stmt.executeQuery(selectAcc2);
			int balance2 = 0;
			while(rs.next()) {
				balance2 = rs.getInt("balance");
			}
			
			//Update new balances
			balance1 -= transfer;
			balance2 += transfer;
			if (balance1 < 0) {
				System.out.println(":: TRANSFER - ERROR - NOT ENOUGH FUNDS");
				return;
			} else {
				//Withdraw from srcAcc
				stmt = con.createStatement(); 
				String updateAcc1 = "UPDATE p1.account SET balance = " + balance1 + " WHERE number = " + srcAccNum;
				// System.out.println(updateAcc1);
				stmt.execute(updateAcc1);
	
				//Deposit from destAcc
				stmt = con.createStatement(); 
				String updateAcc2 = "UPDATE p1.account SET balance = " + balance2 + " WHERE number = " + destAccNum;
				// System.out.println(updateAcc2);
				stmt.execute(updateAcc2);

				System.out.println(":: TRANSFER - SUCCESS");
			}
		
		} catch (SQLException e) {
			System.out.println("Exception in main()");
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.out.println(":: TRANSFER - ERROR - INVALID AMOUNT");
		}	
	}

	/**
	 * Display account summary.
	 * @param cusID customer ID
	 */
	public static void accountSummary(String cusID) 
	{
		System.out.println(":: ACCOUNT SUMMARY - RUNNING");
		try {
			int total = 0;
			int id = Integer.valueOf(cusID);
			stmt = con.createStatement();
			String query = "SELECT p1.account.number, p1.account.balance FROM p1.account WHERE id = " + id;
			// System.out.println(query);
			rs = stmt.executeQuery(query);

			System.out.printf("%-11s %-11s \n", "NUMBER", "BALANCE");
			System.out.println("----------- ----------- ");
			while (rs.next()) {
				int accNum = rs.getInt(1);
				int balance = rs.getInt(2);
				total += balance;
				System.out.printf("%11d %11d \n", accNum, balance);
			}
			System.out.println("-----------------------");
			System.out.printf("%-11s %11d\n", "TOTAL", total);
			System.out.println(":: ACCOUNT SUMMARY - SUCCESS");
		} catch (SQLException e) {
			System.out.println("Exception in main()");

		} catch (NumberFormatException e) {
			System.out.println(":: ACCOUNT SUMMARY - ERROR - INVALID ID");
		}
	}

	/**
	 * Display Report A - Customer Information with Total Balance in Decreasing Order.
	 */
	public static void reportA() 
	{
		System.out.println(":: REPORT A - RUNNING");
		try {
			stmt = con.createStatement();
			String query = "SELECT p1.customer.id, p1.customer.name, p1.customer.gender, p1.customer.age, SUM(p1.account.balance) as total FROM p1.customer INNER JOIN p1.account ON p1.account.id = p1.customer.id WHERE p1.account.status = 'A' GROUP BY p1.customer.id, p1.customer.name, p1.customer.gender, p1.customer.age ORDER BY total DESC";
			// System.out.println(query);
			rs = stmt.executeQuery(query);

			System.out.printf("%-11s %-15s %6s %-11s %-11s \n", "ID", "NAME", "GENDER", "AGE", "TOTAL");
			System.out.println("----------- --------------- ------ ----------- ----------- ");
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String gender = rs.getString(3);
				int age = rs.getInt(4);
				int tot = rs.getInt(5);

				System.out.printf("%11s %-15s %-6s %11s %11s \n", id, name, gender, age, tot);
			}
			System.out.println(":: REPORT A - SUCCESS");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Display Report B - Customer Information with Total Balance in Decreasing Order.
	 * @param min minimum age
	 * @param max maximum age
	 */
	public static void reportB(String min, String max) 
	{
		System.out.println(":: REPORT B - RUNNING");
		try {
			stmt = con.createStatement();
			String query = "SELECT AVG(total) \n" +
			"FROM ( \n" +
				"SELECT p1.customer.id, p1.customer.name, p1.customer.gender, p1.customer.age, SUM(p1.account.balance) as total\n" +
				"FROM p1.customer \n" +
				"INNER JOIN p1.account ON p1.account.id = p1.customer.id \n" +
				"GROUP BY p1.customer.id, p1.customer.name, p1.customer.gender, p1.customer.age \n" +
				"ORDER BY total DESC) reportA \n" +
			"WHERE reportA.age >= 10 AND reportA.age <= 18";
			rs = stmt.executeQuery(query);
			System.out.printf("%-11s \n", "AVERAGE");
			System.out.println("----------- ");
			while (rs.next()) {
				int avg = rs.getInt(1);
				System.out.printf("%11d \n", avg);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		System.out.println(":: REPORT B - SUCCESS");
	}

	public static boolean validatePIN(String idInput, String pinInput) {
		boolean success = false;
		try {
			stmt = con.createStatement();
			String query = "SELECT p1.customer.pin FROM p1.customer WHERE p1.customer.id = " + idInput;
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				int pinQuery = rs.getInt(1);
				if (Integer.valueOf(pinInput) == pinQuery) {
					success = true;
				}
			}
		} catch (SQLException e) {
			//e.printStackTrace();
			success = false;
		} catch (NumberFormatException e) {
			success = false;
		}
		return success;
	}

	public static int getNewID() {
		int id = 0;
		try {
			stmt = con.createStatement();
			String query = "SELECT MAX(p1.customer.id) FROM p1.customer";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				id = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}

	public static int getNewAccountNumber() {
		int accNum = 0;
		try {
			stmt = con.createStatement();
			String query = "SELECT MAX(p1.account.number) FROM p1.account";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				accNum = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return accNum;
	}

	public static boolean validateID(String id) {
		boolean success = false;
		try {
			stmt = con.createStatement();
			String query = "SELECT COUNT(*) FROM p1.account WHERE p1.account.id = " + id;
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {
					success = true;
				}
			}
		} catch (SQLException e) {
			success = false;
		} catch (NumberFormatException e) {
			success = false;
		}

		return success;
	}

	public static boolean validateAccNum(int number, int id) {
		boolean success = false;
		try {
			stmt = con.createStatement();
			String query = "SELECT COUNT(*) FROM p1.account WHERE p1.account.id = " + id + " AND p1.account.number = " + number;
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {
					success = true;
				}
			}
		} catch (SQLException e) {
			success = false;
		} catch (NumberFormatException e) {
			success = false;
		}

		return success;
	}

	public static boolean validateAccNum(int number) {
		boolean success = false;
		try {
			stmt = con.createStatement();
			String query = "SELECT COUNT(*) FROM p1.account WHERE p1.account.number = " + number;
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {
					success = true;
				}
			}
		} catch (SQLException e) {
			success = false;
		} catch (NumberFormatException e) {
			success = false;
		}
		return success;
	}
}
