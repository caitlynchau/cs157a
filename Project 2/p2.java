import java.util.Scanner;

public class p2 {
	private Scanner scanner;

	public p2() {
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

	public void newCustomer() {
		String name="", gender="", age="", pin= "";
		scanner.nextLine(); // clear the buffer

		System.out.print("Enter your name: ");
		name = scanner.nextLine(); 

		System.out.print("[M]ale or [F]emale: ");
		gender = scanner.next().toUpperCase();

		System.out.print("Enter your age: ");
		age = scanner.next();

		System.out.print("Enter a PIN: ");
		pin = scanner.next();

		// insert new customer into database
		BankingSystem.newCustomer(name, gender, age, pin);

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
			boolean success = BankingSystem.login(id, pin);
			if (success) {
				customerMainMenu(Integer.valueOf(id));
			} else {
				mainMenu();
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
							"5. Transfer\n6. Account Summary\n7. Exit\n");
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
	}

	public void openAccountScreen(int currentID) {
		String id="", type="", amount="";

		System.out.print("Enter the customer ID: ");
		id = scanner.next();

		System.out.print("[C]hecking or [S]avings: ");
		type = scanner.next().toUpperCase();

		System.out.print("Enter the initial deposit: ");
		amount = scanner.next();

		// open new account
		BankingSystem.openAccount(id, type, amount);

		customerMainMenu(currentID);
	}

	public void closeAccountScreen(int currentID) {
		String accNum="";

		System.out.print("Enter the account number: ");
        accNum = scanner.next();

		BankingSystem.closeAccount(accNum);
		customerMainMenu(currentID);
	}

	public void depositScreen(int currentID) {
		String accNum="", amount="";
		boolean accValid = false;
        boolean amtValid = false;

		System.out.print("Enter the account number: ");
		accNum = scanner.next();

		System.out.print("Enter the amount to deposit: ");
        amount = scanner.next();
       
		BankingSystem.deposit(accNum, amount);

		customerMainMenu(currentID);
	}

	public void withdrawScreen(int currentID) {
		String accNum="", amount="";

		System.out.print("Enter the account number: ");
		accNum = scanner.next();

		System.out.print("Enter the amount to withdraw: ");
        amount = scanner.next();

		// withdraw amount
		BankingSystem.withdraw(accNum, amount);

		customerMainMenu(currentID);

        // if (!BankingSystem.hasAccounts(currentID)) {
        //     System.out.println("You have no accounts to withdraw from");
        // } else {
        //     // validate user account input
        //     while (!accValid) {
        //         try {
        //             System.out.print("Enter the account number: ");
        //             accNum = scanner.next();
        //             int accNumAsInt = Integer.valueOf(accNum); 
        //             if (accNumAsInt < 0) {
        //                 System.out.println("Error: Account number must be an integer greater than 0");
        //             } else if (!BankingSystem.validateAccNum(accNumAsInt, currentID)) {
        //                 System.out.println("Error: You can only withdraw from your active accounts");
        //             } else {
        //                 accValid = true;
        //             }
        //         } catch (NumberFormatException e) {
        //             System.out.println("Error: Account number must be an integer greater than 0");
        //         }
        //     }

        //     // validate amount input
        //     while (!amtValid) {
        //         try {
        //             System.out.print("Enter the amount to withdraw: ");
        //             amount = scanner.next();
        //             int amountAsInt = Integer.valueOf(amount);
        //             if (amountAsInt >= 0) {
        //                 amtValid = true;
        //             } else {
        //                 System.out.println("Error: withdraw must be an integer greater than 0");
        //             }
        //         } catch (NumberFormatException e) {
        //             System.out.println("Error: withdraw must be an integer greater than 0");
        //         }
        //     }

        //     // withdraw amount
        //     BankingSystem.withdraw(accNum, amount);
        //     int success = BankingSystem.returnCode;

        //     if (success == 0) {
        //         System.out.println("Successfully withdrew " + amount + " from account #" + accNum);
        //     }
        // }
	
	}

	public void transferScreen(int currentID) {

		String srcNum="", destNum="", amt="";
		// boolean srcValid = false;
		// boolean destValid = false;
		// boolean amtValid = false;


		System.out.print("Enter the source account number: ");
        srcNum = scanner.next();

		System.out.print("Enter the destination account number: ");
        destNum = scanner.next();

		System.out.print("Enter the amount to transfer: ");
		amt = scanner.next();

		BankingSystem.transfer(srcNum, destNum, amt);
		customerMainMenu(currentID);


		// // validate source account number
		// while (!srcValid) {
        //     try {
        //         System.out.print("Enter the source account number: ");
        //         srcNum = scanner.next();
		// 		int srcNumAsInt = Integer.valueOf(srcNum);
        //         if (srcNumAsInt < 0) {
        //             System.out.println("Error: Account number must be an integer greater than 0");
        //         } else if (!BankingSystem.validateAccNum(srcNumAsInt, currentID)) {
        //             System.out.println("Error: You can only transfer from your active accounts");
        //         } else {
        //             srcValid = true;
        //         }
        //     } catch (NumberFormatException e) {
        //         System.out.println("Error: Account number must be an integer greater than 0");
        //     }
		// }

		// // validate destination account numebr
		// // validate user account input
        // while (!destValid) {
        //     try {
        //         System.out.print("Enter the destination account number: ");
        //         destNum = scanner.next();
        //         int accNumAsInt = Integer.valueOf(destNum); 
        //         boolean accExists = BankingSystem.accountExists(accNumAsInt);
                
        //         if (accNumAsInt < 0) {
        //             System.out.println("Error: Account number must be an integer greater than 0");
        //         } else if (!accExists) {
        //             System.out.println("Error: Account number is not valid");
        //         }else {
        //             destValid = true;
        //         }
        //     } catch (NumberFormatException e) {
        //         System.out.println("Error: Account number must be an integer greater than 0");
        //     }
        // }

		// // validate amount
		// while (!amtValid) {
		// 	try {
		// 		System.out.print("Enter the amount to transfer: ");
		// 		amt = scanner.next();
		// 		int amountAsInt = Integer.valueOf(amt);
		// 		if (amountAsInt >= 0) {
		// 			amtValid = true;
		// 		} else {
		// 			System.out.println("Error: amount must be an integer greater than 0");
		// 		}
		// 	} catch (NumberFormatException e) {
		// 		System.out.println("Error: amount must be an integer greater than 0");
		// 	}
		// }

		// // transfer
		// BankingSystem.transfer(srcNum, destNum, amt);
		// int success = BankingSystem.returnCode;

		// if (success == 0) {
		// 	System.out.println("Successfully transfered " + amt + " from account #" + srcNum + " to #" + destNum);
		// }

		// customerMainMenu(currentID);
	}

	public void accountSummaryScreen(int currentID) {
		BankingSystem.accountSummary(currentID + "");
	}

	public void administratorMainMenu() {
		System.out.println("Administrator Main Menu");
		String input = "";
		boolean valid = false;

		//scanner.nextLine(); // clear the buffer

		while (!valid) {
			System.out.print("1. Account Summary for a Customer\n2. Report A: Customer Information with Total Balance in Decreasing Order\n3. Report B: Find the Average Total Balance Between Age Groups\n4. Add Interest to Accounts\n5. Exit\n");

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
							if (idAsInt > 0) {
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
                    interestScreen();
				case "5":
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
		//int success = BankingSystem.returnCode;

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
		//int success = BankingSystem.returnCode;

		administratorMainMenu();
		
	}

    public void interestScreen() {
        String cRate="", sRate="";
		scanner.nextLine(); // clear the buffer;
		
		System.out.print("Enter checking rate: ");
		cRate = scanner.nextLine();

		System.out.print("Enter savings rate: ");
		sRate = scanner.nextLine();
		
        BankingSystem.addInterest(cRate, sRate);

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
			p2 p = new p2();
			p.mainMenu();
			//BatchInputProcessor.run(argv[0]);
		}

		System.out.println(":: PROGRAM END");
	}

}