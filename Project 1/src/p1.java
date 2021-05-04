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
		boolean success = BankingSystem.returnStatus;

		if (success) {
			// print out new customer's ID
			int id = BankingSystem.getNewID();
			System.out.println("Your ID is " + id);
		}

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
			administratorMainMenu();
		} else {
			// validate ID and PIN
			boolean success = BankingSystem.validatePIN(id, pin);
					
			if (success) {
				System.out.println("Successfully logged in");
				customerMainMenu(Integer.valueOf(id));
			} else {
				System.out.println("Error: Invalid ID or PIN");
			}
		}
	}


	public void customerMainMenu(int currentID) {
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
					openAccountScreen(currentID);
					//System.out.println("open account");
					break;
				case "2":
					valid = true;
					closeAccountScreen(currentID);
					break;
				case "3":
					valid = true;
					depositScreen(currentID);
					break;
				case "4":
					valid = true;
					withdrawScreen(currentID);
					break;
				case "5":
					valid = true;
					transferScreen(currentID);
					break;
				case "6":
					valid = true;
					accountSummaryScreen(currentID);
					customerMainMenu(currentID);
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

	public void openAccountScreen(int currentID) {
		String id="", type="", amount="";
		boolean idValid = false;
		boolean typeValid = false;
		boolean amountValid = false;

		// validate user ID input
		while (!idValid) {
			try {
				System.out.print("Enter the customer ID: ");
				id = scanner.next();
				int idAsInt = Integer.valueOf(id);
				if (idAsInt > 0 && BankingSystem.validateID(id)) {
					idValid = true;
				} else {
					System.out.println("Error: ID is not valid");
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: ID must be an integer greater than 0");
			}
		}
		

		// validate user type input
		while (!typeValid) {
			System.out.print("[C]hecking or [S]avings: ");
			type = scanner.next().toUpperCase();
			if (!type.equals("C") && !type.equals("S")) {
				System.out.println("Error: type must be either 'C' or 'S'");
			} else {
				typeValid = true;
			}
		}

		// validate user amount input
		while (!amountValid) {
			try {
				System.out.print("Enter the initial deposit: ");
				amount = scanner.next();
				int amountAsInt = Integer.valueOf(amount);
				if (amountAsInt >= 0) {
					amountValid = true;
				} else {
					System.out.println("Error: initial deposit must be an integer greater than 0");
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: initial deposit must be an integer greater than 0");
			}
		}

		// open new account
		BankingSystem.openAccount(id, type, amount);
		boolean success = BankingSystem.returnStatus;

		// print out account number
		if (success) {
			int accNum = BankingSystem.getNewAccountNumber();
			System.out.println("The account number is " + accNum);
		}

		customerMainMenu(currentID);
	}

	public void closeAccountScreen(int currentID) {
		String accNum="";
		boolean accValid = false;

		// validate account number input
		while (!accValid) {
			try {
				System.out.print("Enter the account number: (Q to quit) ");
				accNum = scanner.next();
				int accNumAsInt = 0; 
				
				if (accNum.equals("Q") || accNum.equals("q")) {
					break; // break from loop
				} else if ((accNumAsInt = Integer.valueOf(accNum)) > 0 && BankingSystem.validateAccNum(accNumAsInt, currentID)) {
					accValid = true;
				} else {
					System.out.println("Error: You may only close your own accounts");
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: Account number must be an integer greater than 0");
			}
		}

		BankingSystem.closeAccount(accNum);
		boolean success = BankingSystem.returnStatus;
		if (success) {
			System.out.println("Account #" + accNum + " successfully closed.");
		}

		customerMainMenu(currentID);
	}

	public void depositScreen(int currentID) {
		String accNum="", amount="";
		boolean accValid = false;
		boolean amtValid = false;

		// validate user account input
		while (!accValid) {
			try {
				System.out.print("Enter the account number: (Q to quit) ");
				accNum = scanner.next();
				int accNumAsInt = 0; 
				
				if (accNum.equals("Q") || accNum.equals("q")) {
					break; // break from loop
				} else if ((accNumAsInt = Integer.valueOf(accNum)) < 0 ) { 
					System.out.println("Error: Account number must be an integer greater than 0");
				} else if (BankingSystem.validateAccNum(accNumAsInt)) {
					accValid = true;
				}else {
					System.out.println("Error: Account number is not valid ");
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: Account number must be an integer greater than 0");
			}
		}

		while (!amtValid) {
			try {
				System.out.print("Enter the amount to deposit: ");
				amount = scanner.next();
				int amountAsInt = Integer.valueOf(amount);
				if (amountAsInt >= 0) {
					amtValid = true;
				} else {
					System.out.println("Error: deposit must be an integer greater than 0");
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: deposit must be an integer greater than 0");
			}
		}

		// deposit amount
		BankingSystem.deposit(accNum, amount);
		boolean success = BankingSystem.returnStatus;

		if (success) {
			System.out.println("Successfully deposited " + amount + " into account #" + accNum);
		}

		customerMainMenu(currentID);
	}

	public void withdrawScreen(int currentID) {
		String accNum="", amount="";
		boolean accValid = false;
		boolean amtValid = false;

		// validate user account input
		while (!accValid) {
			try {
				System.out.print("Enter the account number: (Q to quit) ");
				accNum = scanner.next();
				int accNumAsInt = 0; 
				
				if (accNum.equals("Q") || accNum.equals("q")) {
					break; // break from loop
				} else if ((accNumAsInt = Integer.valueOf(accNum)) > 0 && BankingSystem.validateAccNum(accNumAsInt, currentID)) {
					accValid = true;
				} else {
					System.out.println("Error: You may only withdraw from your own accounts");
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: Account number must be an integer greater than 0");
			}
		}

		// validate amount input
		while (!amtValid) {
			try {
				System.out.print("Enter the amount to withdraw: ");
				amount = scanner.next();
				int amountAsInt = Integer.valueOf(amount);
				if (amountAsInt >= 0) {
					amtValid = true;
				} else {
					System.out.println("Error: withdraw must be an integer greater than 0");
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: withdraw must be an integer greater than 0");
			}
		}

		// withdraw amount
		BankingSystem.withdraw(accNum, amount);
		boolean success = BankingSystem.returnStatus;

		if (success) {
			System.out.println("Successfully withdrew " + amount + " from account #" + accNum);
		}

		customerMainMenu(currentID);
	}

	public void transferScreen(int currentID) {

		String srcNum="", destNum="", amt="";
		boolean srcValid = false;
		boolean destValid = false;
		boolean amtValid = false;

		// validate source account number
		while (!srcValid) {
			try {
				System.out.print("Enter the source account number: (Q to quit) ");
				srcNum = scanner.next();
				int srcNumAsInt = 0; 
				
				if (srcNum.equals("Q") || srcNum.equals("q")) {
					break; // break from loop
				} else if ((srcNumAsInt = Integer.valueOf(srcNum)) > 0 && BankingSystem.validateAccNum(srcNumAsInt, currentID)) {
					srcValid = true;
				} else {
					System.out.println("Error: You may only transfer from your own accounts");
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: Account number must be an integer greater than 0");
			}
		}

		// validate destination account numebr
		while (!destValid) {
			try {
				System.out.print("Enter the destination account number: (Q to quit) ");
				destNum = scanner.next();
				int destNumAsInt = 0; 
				
				if (destNum.equals("Q") || destNum.equals("q")) {
					break; // break from loop
				} else if ((destNumAsInt = Integer.valueOf(destNum)) < 0 ) { 
					System.out.println("Error: Account number must be an integer greater than 0");
				} else if (BankingSystem.validateAccNum(destNumAsInt)) {
					destValid = true;
				}else {
					System.out.println("Error: Account number is not valid ");
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: Account number must be an integer greater than 0");
			}
		}

		// validate amount
		while (!amtValid) {
			try {
				System.out.print("Enter the amount to transfer: ");
				amt = scanner.next();
				int amountAsInt = Integer.valueOf(amt);
				if (amountAsInt >= 0) {
					amtValid = true;
				} else {
					System.out.println("Error: amount must be an integer greater than 0");
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: amount must be an integer greater than 0");
			}
		}

		// transfer
		BankingSystem.transfer(srcNum, destNum, amt);
		boolean success = BankingSystem.returnStatus;

		if (success) {
			System.out.println("Successfully transfered " + amt + " from account #" + srcNum + " to " + destNum);
		}

		customerMainMenu(currentID);
	}

	public void accountSummaryScreen(int currentID) {
		BankingSystem.accountSummary(currentID + "");
		boolean success = BankingSystem.returnStatus;

		//customerMainMenu(currentID);
	}

	public void administratorMainMenu() {
		System.out.println("Administrator Main Menu");
		String input = "";
		boolean valid = false;

		//scanner.nextLine(); // clear the buffer

		while (!valid) {
			System.out.print("1. Account Summary for a Customer\n2. Report A: Customer Information with Total Balance in Decreasing Order\n3. Report B: Find the Average Total Balance Between Age Groups\n4.Exit\n");

			input = scanner.next();
			switch(input) {
				case "1":
					valid = true;

					boolean idValid = false;
					String id = "";
					int idAsInt = 0;

					while (!idValid) {
						try {
							System.out.print("Enter the customer ID: ");
							id = scanner.next();
							idAsInt = Integer.valueOf(id);
							if (idAsInt > 0 && BankingSystem.validateID(id)) {
								idValid = true;
							} else {
								System.out.println("Error: ID is not valid");
							}
						} catch (NumberFormatException e) {
							System.out.println("Error: ID must be an integer greater than 0");
						}
					}
					accountSummaryScreen(idAsInt);
					administratorMainMenu();
					break;
				case "2":
					valid = true;
					reportAScreen();
					break;
				case "3":
					valid = true;
					reportBScreen();
					break;
				case "4":
					valid = true;
					mainMenu();
					break;
				default:
					System.out.println("Error: invalid input.");
			}
		}
	}

	public void reportAScreen() {
		BankingSystem.reportA();
		boolean success = BankingSystem.returnStatus;

		administratorMainMenu();
	}

	public void reportBScreen() {
		String min="", max="";
		int minAsInt = 0;
		int maxAsInt = 0;
		boolean minValid = false;
		boolean maxValid = false;

		// validate min
		while (!minValid) {
			try {
				System.out.print("Enter the minimum age: ");
				min = scanner.next();
				minAsInt = Integer.parseInt(min);
				if (minAsInt >= 0) {
					minValid = true;
				} else {
					System.out.println("Error: age must be an integer greater than 0. ");
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: age must be an integer greater than 0. ");
			}
		}

		// validate max
		while (!maxValid) {
			try {
				System.out.print("Enter the maximum age: ");
				max = scanner.next();
				maxAsInt = Integer.parseInt(max);
				if (maxAsInt < 0) {
					System.out.println("Error: age must be an integer greater than 0. ");
				} else if (maxAsInt < minAsInt) {
					System.out.println("Error: maximum age must be less than minimum age (" + min + ")");
				} 
				else {
					maxValid = true;
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: age must be an integer greater than 0. ");
			}
		}

		// get report
		BankingSystem.reportB(min, max);
		boolean success = BankingSystem.returnStatus;

		administratorMainMenu();
		
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

}
