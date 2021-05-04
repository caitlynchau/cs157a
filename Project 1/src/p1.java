import java.util.ArrayList;
import java.util.Scanner;

public class p1 {
	private Scanner scanner;

	public p1() {
		scanner = new Scanner(System.in);
		
	}

	public void mainMenu() {
		System.out.println("Welcome to the Self Services Banking System! - Main Menu");
		String input = "";
		boolean valid = false;
		while (!valid) {
			System.out.println("1. New Customer\n2. Customer Login\n3. Exit");
			input = scanner.next();

			switch (input) {
				case "1":
					valid = true;
					newCustomer();
					break;
				case "2":
					valid = true;
					customerLogin();
					break;
				case "3":
					valid = true;
					System.out.println("Goodbye!");
					break;
				default:
					System.out.println("Invalid input."); // input was not 1, 2, or 3
			}
		}
	}

	// Add a new customer to database. Prompt for name, gender, age, and PIN
	public void newCustomer() {
		String name="", gender="", age="", pin= "";
		boolean nameValid = false;
		boolean genderValid = false;
		boolean ageValid = false;
		boolean pinValid = false;
		scanner.nextLine(); // clear the buffer

		// validate user name input
		while (!nameValid) {
			System.out.print("Enter your name: ");
			name = scanner.nextLine(); //nextLine?
			if (name.length() > 0 && name.length() <= 15) {
				nameValid = true;
			} else {
				System.out.println("Error: name must be between 0 to 15 characters. ");
			}
		}
		
		// validate user gender input
		while (!genderValid) {
			System.out.print("[M]ale or [F]emale: ");
			gender = scanner.next().toUpperCase();
			if (gender.equals("M") || gender.equals("F")) {
				genderValid = true;
			} else {
				System.out.println("Error: gender must be either 'M' or 'F'");
			}
		}

		// validate user age input
		while (!ageValid) {
			try {
				System.out.print("Enter your age: ");
				age = scanner.next();
				int ageAsInt = Integer.parseInt(age);
				if (ageAsInt >= 0) {
					ageValid = true;
				} else {
					System.out.println("Error: age must be an integer greater than 0. ");
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: age must be an integer greater than 0. ");

			}
		}

		// validate user PIN input
		while (!pinValid) {
			try {
				System.out.print("Enter a PIN: ");
				pin = scanner.next();
				int pinAsInt = Integer.parseInt(pin);
				if (pinAsInt >= 0) {
					pinValid = true;
				} else {
					System.out.println("Error: PIN must be an integer greater than 0. ");
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: PIN must be an integer greater than 0. ");

			}
		}

		// insert new customer into database
		BankingSystem.newCustomer(name, gender, age, pin);

		// print out new customer's ID
		int id = BankingSystem.getNewID();
		System.out.println("Your ID is " + id);

		// return to main menu
		mainMenu();
	}

	public void customerLogin() {
		String id="", pin="";
		scanner.nextLine(); // clear the buffer;
		
		System.out.print("Enter ID: ");
		id = scanner.nextLine();

		System.out.print("Enter PIN: ");
		pin = scanner.nextLine();

		if (id.equals("0") && pin.equals("0")) {
			// call administratorMainMenu();
		}

		// validate ID and PIN
		boolean success = BankingSystem.validatePIN(id, pin);
		
		if (success) {
			System.out.println("Successfully logged in");
			customerMainMenu();
		} else {
			System.out.println("Error: Invalid ID or PIN");
		}
	}


	public void customerMainMenu() {
		System.out.println("Customer Main Menu");
		String input = "";
		boolean valid = false;

		//scanner.nextLine(); // clear the buffer

		while (!valid) {
			System.out.print("1. Open Account\n2. Close Account\n3. Deposit\n4. Withdraw\n" +
							"5. Transfer\n6. Account Summary\n7.Exit\n");
			input = scanner.next();
			switch(input) {
				case "1":
					valid = true;
					// openAccountScreen();
					System.out.println("open account");
					break;
				case "2":
					valid = true;
					// closeAccount();
					System.out.println("close account");
					break;
				case "3":
					valid = true;
					// deposit();
					System.out.println("deposit");
					break;
				case "4":
					valid = true;
					// withdraw();
					System.out.println("withdraw");
					break;
				case "5":
					valid = true;
					// transfer();
					System.out.println("transfer");
					break;
				case "6":
					valid = true;
					// accountSummary();
					System.out.println("account summary");
					break;
				case "7":
					valid = true;
					mainMenu();
					break;
				default:
					System.out.println("Error: invalid input.");
			}
		}
		// mainMenu();
	}

	public void openAccountScreen() {
		String id="", type="", amount="";
		
	}

	public void closeAccountScreen() {

	}

	public void depositScreen() {

	}

	public void withdrawScreen() {

	}

	public void transferScreen() {

	}

	public void accountSummaryScreen() {

	}

	public void administratorMainMenu() {

	}


	public static void main(String argv[]) {
		System.out.println(":: PROGRAM START");
		
		if (argv.length < 1) {
			System.out.println("Need database properties filename");
		} else {
			BankingSystem.init(argv[0]);
			BankingSystem.testConnection();
			System.out.println();
			p1 p = new p1();
			p.mainMenu();
			//BatchInputProcessor.run(argv[0]);
		}

		System.out.println(":: PROGRAM END");
	}


    /*
    prompt user for withdraw amt
        call checkBalance()
        while balance - amt < 0
            keep prompting user
        otherwise call withdraw()


    */
}
