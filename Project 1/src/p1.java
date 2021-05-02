public class p1 {
    public static void main(String argv[]) {
		System.out.println(":: PROGRAM START");
		
		if (argv.length < 1) {
			System.out.println("Need database properties filename");
		} else {
			BankingSystem.init(argv[0]);
			BankingSystem.testConnection();
			System.out.println();
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
