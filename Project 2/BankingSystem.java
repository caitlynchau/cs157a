import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

/**
 * Manage connection to database and perform SQL statements.
 * @author Caitlyn Chau
 * CS 157A
 * Project 2
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
	private static CallableStatement cstmt;
	private static ResultSet rs;

	public static int returnCode;
	public static String returnMsg;

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

		returnCode = -100;
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
	public static void newCustomer(String name, String gender, String age, String pin) 
	{
		try {
            cstmt = con.prepareCall("CALL P2.CUST_CRT(?,?,?,?,?,?,?)");
            cstmt.setString(1, name);
            cstmt.setString(2, gender);
            cstmt.setString(3, age);
			cstmt.setString(4, pin);
			cstmt.registerOutParameter(5, Types.INTEGER); 	// customer ID
			cstmt.registerOutParameter(6, Types.INTEGER);
			cstmt.registerOutParameter(7, Types.CHAR);
            cstmt.executeUpdate();

            int id = cstmt.getInt(5);
			returnCode = cstmt.getInt(6);
			returnMsg = cstmt.getString(7);
			System.out.println(returnMsg);
			if (returnCode == 1) System.out.println("Your ID is " + id);
        } catch (SQLException e) {
            System.out.println("Error: Could not create new customer. Invalid input.");
        }
	}

	public static boolean login(String idInput, String pinInput) {
		boolean success = false;
		try {
			cstmt = con.prepareCall("CALL P2.CUST_LOGIN(?,?,?,?,?)");
			cstmt.setString(1, idInput);
			cstmt.setString(2, pinInput);
			cstmt.registerOutParameter(3, Types.INTEGER);	 // valid
			cstmt.registerOutParameter(4, Types.INTEGER);
			cstmt.registerOutParameter(5, Types.CHAR);
			cstmt.executeUpdate();
			int valid = cstmt.getInt(3);
			returnCode = cstmt.getInt(4);
			returnMsg = cstmt.getString(5);
			System.out.println(returnMsg);
			if (valid == 1) success = true;
		} catch (SQLException e) {
			System.out.println("Error: Incorrect id or pin");
			returnCode = -100;
		} catch (NumberFormatException e) {
			System.out.println("Error: Incorrect id or pin");
			returnCode = -100;
		}
		return success;
	}

	/**
	 * Open a new account.
	 * @param id customer id
	 * @param type type of account
	 * @param amount initial deposit amount
	 */
	public static void openAccount(String id, String type, String amount) 
	{
		try {
			cstmt = con.prepareCall("CALL P2.ACCT_OPN(?,?,?,?,?,?)");
			cstmt.setString(1, id);
			cstmt.setString(2, amount);
			cstmt.setString(3, type);
			cstmt.registerOutParameter(4, Types.INTEGER);	 // account number
			cstmt.registerOutParameter(5, Types.INTEGER);
			cstmt.registerOutParameter(6, Types.CHAR);
			cstmt.executeUpdate();

			int accNum = cstmt.getInt(4);
			returnCode = cstmt.getInt(5);
			returnMsg = cstmt.getString(6);
			System.out.println(returnMsg);
			if (returnCode == 1) 
				System.out.println("The account number is " + accNum);
		}catch (SQLException e) {
			//e.printStackTrace();
			System.out.println("Error: Could not open new account");
			returnCode = -100;
		}
	}

	/**
	 * Close an account.
	 * @param accNum account number
	 */
	public static void closeAccount(String accNum) 
	{
		try {
		    CallableStatement cstmt = con.prepareCall("CALL p2.ACCT_CLS(?, ?, ?)");
			cstmt.setString(1, accNum);
			cstmt.registerOutParameter(2, Types.INTEGER);
			cstmt.registerOutParameter(3, Types.CHAR);
			cstmt.executeUpdate();
			returnCode = cstmt.getInt(2);
			returnMsg = cstmt.getString(3);
			System.out.println(returnMsg);
			if (returnCode != 1) System.out.println("Failed to close account # " + accNum);
		} catch (SQLException e) {
			System.out.println("Failed to close account # " + accNum);
		}
	}

	/**
	 * Deposit into an account.
	 * @param accNum account number
	 * @param amount deposit amount
	 */
	public static void deposit(String accNum, String amount) 
	{
		try {
		    cstmt = con.prepareCall("CALL p2.ACCT_DEP(?, ?, ?, ?)");
			cstmt.setString(1, accNum);
			cstmt.setString(2, amount);
			cstmt.registerOutParameter(3, Types.INTEGER);		
			cstmt.registerOutParameter(4, Types.INTEGER);
			cstmt.executeUpdate();

			returnCode = cstmt.getInt(3);
			returnMsg = cstmt.getString(4);
			System.out.println(returnMsg);
		} catch (SQLException e) {
			System.out.println("Error: failed to deposit");
		}
	}



	/**
	 * Withdraw from an account.
	 * @param accNum account number
	 * @param amount withdraw amount
	 */
	public static void withdraw(String accNum, String amount) 
	{
		//System.out.println(":: WITHDRAW - RUNNING");
		try {
		    CallableStatement cstmt = con.prepareCall("CALL p2.ACCT_WTH(?, ?, ?, ?)");
			cstmt.setString(1, accNum);
			cstmt.setString(2, amount);
			cstmt.registerOutParameter(3, Types.INTEGER);		
			cstmt.registerOutParameter(4, Types.INTEGER);
			cstmt.executeUpdate();
			returnCode = cstmt.getInt(3);
			returnMsg= cstmt.getString(4);
			System.out.println(returnMsg);
		} catch (SQLException e) {
			System.out.println("Error: failed to withdraw");
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
		try {
		    cstmt = con.prepareCall("CALL p2.ACCT_TRX(?, ?, ?, ?, ?)");
			cstmt.setString(1, srcAccNum);
			cstmt.setString(2, destAccNum);
			cstmt.setString(3, amount);
			cstmt.registerOutParameter(4, Types.INTEGER);		
			cstmt.registerOutParameter(5, Types.INTEGER);
			cstmt.executeUpdate();
			returnCode = cstmt.getInt(4);
			returnMsg = cstmt.getString(5);
			System.out.println(returnMsg);
		} catch (SQLException e) {
			System.out.println("Error: Failed to transfer");
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
			String query = "SELECT p2.account.number, p2.account.balance FROM p2.account WHERE id = " + id + " AND status = 'A'";
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
			returnCode = 0;
		} catch (SQLException e) {
			returnCode = -100;
		} catch (NumberFormatException e) {
			System.out.println(":: ACCOUNT SUMMARY - ERROR - INVALID ID");
			returnCode = -100;
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
			String query = "SELECT p2.customer.id, p2.customer.name, p2.customer.gender, p2.customer.age, SUM(p2.account.balance) as total FROM p2.customer INNER JOIN p2.account ON p2.account.id = p2.customer.id WHERE p2.account.status = 'A' GROUP BY p2.customer.id, p2.customer.name, p2.customer.gender, p2.customer.age ORDER BY total DESC";
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
			returnCode = 0;
		} catch (SQLException e) {
			// System.out.println(e.getMessage());
			// e.printStackTrace();
			returnCode = -100;
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
				"SELECT p2.customer.id, p2.customer.name, p2.customer.gender, p2.customer.age, SUM(p2.account.balance) as total\n" +
				"FROM p2.customer \n" +
				"INNER JOIN p2.account ON p2.account.id = p2.customer.id \n" +
				"GROUP BY p2.customer.id, p2.customer.name, p2.customer.gender, p2.customer.age \n" +
				"ORDER BY total DESC) reportA \n" +
			"WHERE reportA.age >= 10 AND reportA.age <= 18";
			rs = stmt.executeQuery(query);
			System.out.printf("%-11s \n", "AVERAGE");
			System.out.println("----------- ");
			while (rs.next()) {
				int avg = rs.getInt(1);
				System.out.printf("%11d \n", avg);
			}
			System.out.println(":: REPORT B - SUCCESS");
			returnCode = 0;
		} catch (SQLException e) {
			// System.out.println(e.getMessage());
			// e.printStackTrace();
			returnCode = -100;
		}
	}

	public static void addInterest(String cRate, String sRate) {
		try {
		    CallableStatement cstmt = con.prepareCall("CALL p2.ADD_INTEREST(?,?,?,?)");
			cstmt.setString(1, cRate);
			cstmt.setString(2, sRate);
			cstmt.registerOutParameter(3, Types.INTEGER);
			cstmt.registerOutParameter(4, Types.CHAR);
			cstmt.executeUpdate();
			returnCode = cstmt.getInt(3);
			returnMsg = cstmt.getString(4);
			System.out.println(returnMsg);
		} catch (SQLException e) {
			System.out.println("Error: interest failed");
		}
	}

	public static boolean validateID(String id) {
		boolean success = false;
		try {
			stmt = con.createStatement();
			String query = "SELECT COUNT(*) FROM p2.customer WHERE p2.customer.id = " + id;
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

	public static boolean hasAccounts(int id) {
		boolean hasAccts = false;
		try {
			stmt = con.createStatement();
			String query = "SELECT COUNT(*) FROM p2.account WHERE id = " + id;
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) hasAccts = true;
			}
		} catch (SQLException e) {
			hasAccts = false;
		} catch (NumberFormatException e) {
			hasAccts = false;
		}
		return hasAccts;
	}

	public static boolean validateAccNum(int number, int id) {
		boolean success = false;
		try {
			stmt = con.createStatement();
			String query = "SELECT COUNT(*) FROM p2.account WHERE p2.account.id = " + id + 
			" AND p2.account.number = " + number + " AND p2.account.status = 'A'";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) success = true;
			}
		} catch (SQLException e) {
			success = false;
		} catch (NumberFormatException e) {
			success = false;
		}
		return success;
	}

	public static boolean accountExists(int number) {
		boolean exists = false;
		try {
			stmt = con.createStatement();
			String query = "SELECT COUNT(*) FROM p2.account WHERE p2.account.number = " + number;
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) exists = true;
			}
			//System.out.println("num acc = " + numAccounts);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return exists;
	}
}
