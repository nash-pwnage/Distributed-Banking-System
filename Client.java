import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Client {


	String configFile;
	// A hashmap to store sending connection sockets and recieving connection sockets
	int ports[], ids[];
	int myPort,myId;
	String[] hostnames;
	String myhostname;
	int numberOfAccounts;
	HashMap<Integer,Integer> accountIDandServerID;
	HashMap<Integer,Integer> accountIDandBalance;
	
	public Client(String configFile, int myPort) {
		super();
		this.configFile = configFile;
		this.myPort = myPort;
	}
	
	public  void configure() throws FileNotFoundException {
		try {
			BufferedReader br = new BufferedReader(new FileReader(configFile));
			
			//read first line
			String ln= br.readLine();
			StringTokenizer st = new StringTokenizer(ln);
						
			// if empty, skip lines
			if(st.countTokens()==0||ln.startsWith("#")){
					ln= br.readLine();
					st=new StringTokenizer(ln);
			}
					
			int numberOfServers = Integer.parseInt(st.nextToken());
			System.out.println("Number of servers = " + numberOfServers);
			// go to next line
			ln = br.readLine();
			st= new StringTokenizer(ln);
			//System.out.println("183: ln =" +ln);
			
			// if empty, skip lines
			while(ln.startsWith("#")||ln.length()<1){
				ln= br.readLine();
				st=new StringTokenizer(ln);
			}
			
			ids = new int[numberOfServers];
			ports = new int[numberOfServers];
			hostnames = new String[numberOfServers];
			
			for (int i = 0; i < numberOfServers; i++) {
				st = new StringTokenizer(ln);
				ids[i] = Integer.parseInt(st.nextToken());
				hostnames[i]=st.nextToken();
				ports[i] = Integer.parseInt(st.nextToken());
				/*if (myhostname.equals(hostnames[i]) && myPort == ports[i]) {
					myId = ids[i];
					}*/
				System.out.println("added node id = " + ids[i] + " hostname = "
						+ hostnames[i] + " port = " + ports[i]);
				ln=br.readLine();
				}
	
			// Skip Empty Lines
						while(ln.length()<1||ln.startsWith("#")){
								ln= br.readLine();
								st=new StringTokenizer(ln);
						}
						
						numberOfAccounts = Integer.parseInt(st.nextToken());
						System.out.println("Number Of Accounts = "+numberOfAccounts);
						ln = br.readLine();
						st=new StringTokenizer(ln);
						
						// Skip Empty Lines and #
						while(ln.startsWith("#") || ln.length()<1) {
								ln= br.readLine();
								st=new StringTokenizer(ln);
						}
						
			
			accountIDandServerID= new HashMap<Integer,Integer>();
			accountIDandBalance= new HashMap<Integer,Integer>();
			int acno=0,serno=0,bal=0;
			
			for (int i = 0; i < numberOfAccounts; i++) {
				st = new StringTokenizer(ln);
				StringTokenizer st1 = new StringTokenizer(ln,"(), ");
				while(st1.hasMoreElements())
				{				
				acno=Integer.parseInt(st1.nextToken());
				serno= Integer.parseInt(st1.nextToken());
				bal= Integer.parseInt(st1.nextToken());
				//System.out.println("  acno= "+acno+"  serno = "+serno+" bal= "+bal);
				accountIDandBalance.put(acno, bal);
				accountIDandServerID.put(acno, serno);
				System.out.println("Added  acno= "+acno+"  serno = "+serno+" bal= "+bal);
				}
				ln=br.readLine();
			}
		
		} catch(Exception e){};
		
		
	}
	
	public void showMenu()
	{
		System.out.println();
		System.out.println();
		System.out.println("--------------------------------------------------------------");
		System.out.println("Please Enter Choice:");
		System.out.println("1. Check Balance of an Account");
		System.out.println("2. Deposit Money in Account");
		System.out.println("4. Withdraw Money from an Account");
		System.out.println("6. Transfer Money from one Account to another");
		System.out.println("8. Determine the Total amount of Money in the Banking System");
		//System.out.println("9. Print All Vectors");
		System.out.println("666. Exit ");
	}
	
	public Message getChoice()
	{	
		Message m=null;
		int a=0;
		Scanner reader = new Scanner(System.in);
		
		showMenu();
		a = reader.nextInt();
		if(a==666)
		{
			return null;
		}
		int b=-1;
		if(a<=9)
		{
			System.out.println("Please enter the Account Number");
			b = reader.nextInt();
			int serverId=accountIDandServerID.get(b);
			System.out.println("Server Number= "+accountIDandServerID.get(b));
			
			// finding the ID number in ids[]
			
			int i=0;
			while(ids[i]!=serverId){
				i++;				
			}
			
			
			//System.out.println("To connect to Hostname "+hostnames[i]+" At port no "+ports[i]);
			
			
				//Message m=null;
				if(b!=-1)
					switch (a) {
						case 1: {
						m = new Message(0,b,accountIDandBalance.get(b),-1,-1);
						break;
						}
						case 2: {
						System.out.println("Enter Amount to Deposit: ");
						int x = reader.nextInt();
						m = new Message(2,b,accountIDandBalance.get(b),-1,x);
						break;
						}
						case 4: {
						System.out.println("Enter Amount to Withdraw: ");
						int x = reader.nextInt();
						m = new Message(4,b,accountIDandBalance.get(b),-1,x);
						break;
						}
						case 6: {
						System.out.println("Enter Payee Account Number: ");
						int xpayee = reader.nextInt();
						System.out.println("Enter Amount to Transfer to Payee: ");
						int x = reader.nextInt();
						m = new Message(6,b,accountIDandBalance.get(b),xpayee,x);
						break;
						}
						case 8: {
							//create freeze message
							m = new Message(13,-1,-1,-1,-1);
						break;
						}
					}
					
		}
		
		
		
	
		return m;
	}
	
	public void executeUserChoice(Message m)
	{
		
		if(m.getMessageType() != 13)
		{
			int b = m.myAccountno;
			int serverId=accountIDandServerID.get(b);
			System.out.println("Server Number= "+accountIDandServerID.get(b));
			
			
			// finding the ID number in ids[]
			
			int i=0;
			while(ids[i]!=serverId){
				i++;				
			}
			
			ObjectInputStream in=null;
			ObjectOutputStream out=null;
			
			
			Socket s;
			try {
				s = new Socket(hostnames[i],ports[i]);
				System.out.println("New socket connection to "
						+ hostnames[i] + " At port " + ports[i]);
				out = new ObjectOutputStream(s.getOutputStream());
				in = new ObjectInputStream(s.getInputStream());
				System.out.println("output stream being successfully established with "+i);
				if(m!=null)out.writeObject(m);
				
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			Message reply=null;
			try {
				reply = (Message) in.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("--------------------------------------------------------------");
			System.out.println();
			System.out.println();
			
			
			
				switch (reply.messageType) {
					case 1: {// Balance check reply
						
					System.out.println("Account Number: "+reply.myAccountno+" Balance: $"+reply.balance);
					break;
					}
					case 3: {// Deposit Reply
						
					System.out.println("Amount Deposited: $"+reply.amount);
					System.out.println("Account Number: "+reply.myAccountno+" Current Balance: $"+reply.balance);
					break;
					}
					case 5: {// Withdrawal Reply
					if(reply.messageType==5){
					System.out.println("Amount Withdrawn: $"+reply.amount);
					System.out.println("Account Number: "+reply.myAccountno+" Balance: $"+reply.balance);
					}
					break;
					}
					case 51:{
					// Withdrawal failed
					System.out.println("Transaction Failed: Insufficient Funds");
					System.out.println("Withdrawal Requested= $"+reply.amount);
					System.out.println("Account Number: "+reply.myAccountno+" Current Balance: $"+reply.balance);
					
					break;
					}
					case 61:{
					System.out.println("Transaction Failed: Insufficient funds in Payer Account "+reply.myAccountno);
					System.out.println("Withdrawal Requested= $"+reply.amount);
					System.out.println("Account Number: "+reply.myAccountno+" Current Balance: $"+reply.balance);
					
					break;
					}
					case 71:{
					System.out.println("Transaction Failed: Insufficient funds in Payer Account "+reply.myAccountno);
					System.out.println("Transfer Requested= $"+reply.amount);
					System.out.println("Account Number: "+reply.myAccountno+" Current Balance: $"+reply.balance);
					
					break;
					}
					case 7:{
					System.out.println("Transaction Succesfull");
					System.out.println("Transferred $"+reply.amount+" from Account Number "+reply.myAccountno+" to Account Number "+reply.payeeAccountno);
					break;
					}
		
				}
				
				
				
				
				
		}
		else//start controller and send freeze messages : phase 1
		{
			Socket s = null;
			Controller controller = new Controller(ports, ids, hostnames,this.myId, 9999);
			controller.start();
			
			for(int i =0; i<ids.length; i++)
			{
				
				try {
					s = new Socket(hostnames[i], ports[i]);
				} catch (UnknownHostException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				ObjectOutputStream o = null;
				try {
					o = new ObjectOutputStream(s.getOutputStream());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Message freeze = new Message(13,-1,-1,-1,-1);
				freeze.setControllerHostname(controller.getControllerHostname());
				freeze.setControllerPort(controller.getControllerPort());
				freeze.setControllerId(controller.getControllerId());
				
				try {
					o.writeObject(freeze);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Outgoing freeze message to server "+ ids[i] + "> " + hostnames[i] + " : " + ports[i] );
				System.out.println(freeze);
			}
			
			
			try {
				controller.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
			
		
	}
	
	
	public void processBatchFile(File f)
	{
		System.out.println("Entered the funcn");
		Message m=null;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(f));		
		//read first line
		String ln= br.readLine();
		StringTokenizer st = new StringTokenizer(ln);
					
		// if empty, skip lines
		while(st.countTokens()==0||ln.startsWith("#")){
				ln= br.readLine();
				st=new StringTokenizer(ln);
				System.out.println("LN: "+ln);
		}
		//System.out.println("LN2: "+ln);		
		int numberOfTasks = Integer.parseInt(st.nextToken());
		System.out.println("Number of Tasks = " + numberOfTasks);
		// go to next line
		ln = br.readLine();
		st= new StringTokenizer(ln);
		//System.out.println("183: ln =" +ln);
		
		// if empty, skip lines
		while(ln.startsWith("#")||ln.length()<1){
			ln= br.readLine();
			st=new StringTokenizer(ln);
		}
		
		Scanner reader = new Scanner(System.in);
		
		
		for(int j=0;j<numberOfTasks;j++){
			st= new StringTokenizer(ln,"),(");
			String opString=st.nextToken();
			String myAccNumberString=st.nextToken();
			String payeeAccountNumberString= st.nextToken();;
			String amountString=st.nextToken();
			if(opString.equals("T")){
				
				m = new Message(6,Integer.parseInt(myAccNumberString),accountIDandBalance.get(Integer.parseInt(myAccNumberString)),Integer.parseInt(payeeAccountNumberString),Integer.parseInt(amountString));
			}
			
			
			if(opString.equals("W")){
							
				m = new Message(4,Integer.parseInt(myAccNumberString),accountIDandBalance.get(Integer.parseInt(myAccNumberString)),-1,Integer.parseInt(amountString));
			}
			
			
			if(opString.equals("D")){
				
				
				m = new Message(2,Integer.parseInt(myAccNumberString),accountIDandBalance.get(Integer.parseInt(myAccNumberString)),-1,Integer.parseInt(amountString));
			}
			
			
			if(opString.equals("I")){
				
				m = new Message(0,Integer.parseInt(myAccNumberString),accountIDandBalance.get(Integer.parseInt(myAccNumberString)),-1,-1);
			}
			
			if(opString.equals("S")){
				m = new Message(13,-1,-1,-1,-1);
			}
			
			
			//Send and Recieve M,reply
			
			executeUserChoice(m);
			
			
			ln=br.readLine();
		}
			} catch (Exception e){ e.printStackTrace();
			System.out.println("Error at catch 394");};
		
	}
	
	public static void main(String argv[]) {
		
		if (argv.length != 3) {
			System.out.println("Insufficient arguments");
			return;
		}
		
		String file = argv[0];
		int id = Integer.parseInt(argv[1]);
		int port = Integer.parseInt(argv[2]);
		
		Client c = new Client(file, port);
		c.myId = id;
		
		try {
			c.configure();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File Not Foundd....!!");
			e.printStackTrace();
		}
		
		boolean isBatchEnabled=false;
		while(true)
		{
			System.out.println("Enter mode : B=Batch I=Interactive E=Exit");
			Scanner reader = new Scanner(System.in);
			String mode = reader.next();
			
		
		if(mode.equalsIgnoreCase("I")){
			Message M1= c.getChoice();
			c.executeUserChoice(M1);
			//System.out.println("Message= "+M1.toString());				
		}
		else if(mode.equalsIgnoreCase("B"))
		{
		System.out.println("Enter Batch filename");
		Scanner reader1 = new Scanner(System.in);
		String fileName=reader1.next();
		File f= new File(fileName);
		c.processBatchFile(f);
		System.out.println("Exiting");
		
		}
		else if(mode.equalsIgnoreCase("E"))
		{
			System.exit(0);
		}
	  }
	}
	
	
}

