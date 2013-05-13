
public class Account {

	int AccountNumber;
	int accountBalance;
	
	
	public int getAccountNumber() {
		return AccountNumber;
	}
	
	
	public void setAccountNumber(int accountNumber) {
		AccountNumber = accountNumber;
	}
	
	
	public int getAccountBalance() {
		return accountBalance;
	}
	
	
	public void setAccountBalance(int accountBalance) {
		this.accountBalance = accountBalance;
	}
	
	
	public Account(int accountNumber, int accountBalance) {
		super();
		AccountNumber = accountNumber;
		this.accountBalance = accountBalance;
	}
	
	public static int checkBalance() {
		return 0;
		
	}
	
	public static int Deposit()	{
		return 0;
		
	}
	
	public static int withdraw() {
		return 0;
		
	}
	
	
}
