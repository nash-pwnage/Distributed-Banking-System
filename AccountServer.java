import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class AccountServer {

	File configurationFile;
	String configFile;
	ArrayList<Integer> listOfAccounts;
	ArrayList<Integer> listOfServers;
	int numberOfServers;
	HashMap<Integer,Integer> accountIDandServerID;
	HashMap<Integer,Integer> accountIDandBalance;
	HashMap<String,Integer> hostNamesServerID;
	// A hashmap to store sending connection sockets and recieving connection sockets
	int ports[], ids[];
	int myPort,myId;
	String[] hostnames;
	String myhostname;
	int numberOfAccounts ;
	ArrayList<Integer> recieveFrom;
	ArrayList<Integer> sendTo;
	ArrayList<String> rHostnames;
	ArrayList<String> sHostnames;
	ArrayList<Integer> sendPorts;
	ArrayList<Integer> recievePorts;
	ServerSocket ss;
	HashMap<Integer, ObjectOutputStream> outputStreams ;
	HashMap<Integer, ObjectInputStream> inputStreams ;
	ClientManager cm;
	int sendVector[];
	int receiveVector[];
	
	
	boolean frozen;
	int receiveCount;
	HashMap<Integer,Integer> sendCountMap;
	HashMap<Integer,ControllerEntry> controllers;
	HashMap<Integer,Integer> vClock;
	
	public void addEvent()
	{
		int temp = vClock.get(myId);
		temp++;
		vClock.put(myId, temp);
	}
	
	public void mergeVector(HashMap<Integer,Integer> vClock2)
	{
		System.out.println("Merging");
		for(int id : vClock.keySet())
		{
			int templocal = vClock.get(id);
			int tempOther = vClock2.get(id);
			vClock.put(id, Math.max(templocal, tempOther));
		}
	}
	
	public void printVector(HashMap<Integer,Integer> vClock2)
	{
		for(int id : vClock2.keySet())
		{
			System.out.println("vc " + id + " = " + vClock2.get(id));
		}
	}
	
	
	
	
	public void listControllers(String purpose)
	{
		System.out.println("Displaying current controllers : " + purpose);
		for(int controllerId : controllers.keySet())
		{
			System.out.println("Controller : " + controllers.get(controllerId));
			 
		}
	}
	
	public AccountServer(String configFile, int myPort) {
		super();
		this.configFile = configFile;
		this.myPort = myPort;
		this.frozen = false;
		this.receiveCount = 0;
		sendCountMap = new HashMap<Integer,Integer>();
		controllers=new HashMap<Integer,ControllerEntry>();
				
		
	}

	
	
	public HashMap<Integer, Integer> getSendCountMap() {
		return sendCountMap;
	}



	public void setSendCountMap(HashMap<Integer, Integer> sendCountMap) {
		this.sendCountMap = sendCountMap;
	}



	public String[] getHostnames() {
		return hostnames;
	}



	public void setHostnames(String[] hostnames) {
		this.hostnames = hostnames;
	}



	public File getConfigurationFile() {
		return configurationFile;
	}


	public void setConfigurationFile(File configurationFile) {
		this.configurationFile = configurationFile;
	}


	public String getConfigFile() {
		return configFile;
	}


	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}


	public ArrayList<Integer> getListOfAccounts() {
		return listOfAccounts;
	}


	public void setListOfAccounts(ArrayList<Integer> listOfAccounts) {
		this.listOfAccounts = listOfAccounts;
	}


	public ArrayList<Integer> getListOfServers() {
		return listOfServers;
	}


	public void setListOfServers(ArrayList<Integer> listOfServers) {
		this.listOfServers = listOfServers;
	}


	public HashMap<Integer, Integer> getAccountIDandServerID() {
		return accountIDandServerID;
	}


	public void setAccountIDandServerID(
			HashMap<Integer, Integer> accountIDandServerID) {
		this.accountIDandServerID = accountIDandServerID;
	}


	public HashMap<String, Integer> getHostNamesServerID() {
		return hostNamesServerID;
	}


	public void setHostNamesServerID(HashMap<String, Integer> hostNamesServerID) {
		this.hostNamesServerID = hostNamesServerID;
	}

	public int[] getPorts() {
		return ports;
	}

	public int getTotalAmount() {
		int total=0;
		for(int amt: accountIDandBalance.values())
		{
			System.out.println("total = total + " + amt);
			total+=amt;
		}
		return total;
	}
	
	public void setPorts(int[] ports) {
		this.ports = ports;
	}


	public int[] getIds() {
		return ids;
	}



	public void setIds(int[] ids) {
		this.ids = ids;
	}


	public int getMyPort() {
		return myPort;
	}


	public void setMyPort(int myPort) {
		this.myPort = myPort;
	}


	public int getMyId() {
		return myId;
	}


	public void setMyId(int myId) {
		this.myId = myId;
	}


	

	public String getMyhostname() {
		return myhostname;
	}




	public void setMyhostname(String myhostname) {
		this.myhostname = myhostname;
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
					
			numberOfServers = Integer.parseInt(st.nextToken());
			System.out.println("Number of servers = " + numberOfServers);
			// go to next line
			ln = br.readLine();
			st= new StringTokenizer(ln);
			//System.out.println("183: ln =" +ln);
			
			
			
			// Initializing Vectors
			
			sendVector= new int[numberOfServers];
			receiveVector= new int[numberOfServers];
			sendVector[myId]=0;
			receiveVector[myId]=0;
			
			
			
			
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
			
			System.out.println("Line= "+ln );
			
			
			accountIDandServerID= new HashMap<Integer,Integer>();
			accountIDandBalance= new HashMap<Integer,Integer>();
			int acno=0,serno=0,bal=0;
			
			//self identification
			String myHostname = InetAddress.getLocalHost().getHostName();
			for(int i2 =0; i2<ids.length; i2++) {
				if(hostnames[i2].equals(myHostname)) myId=i2;
			}
			System.out.println("Self identification. my id = " + myId);
			
			
			for (int i = 0; i < numberOfAccounts; i++) {
				st = new StringTokenizer(ln);
				StringTokenizer st1 = new StringTokenizer(ln,"(), ");
				while(st1.hasMoreElements())
				{				
					acno=Integer.parseInt(st1.nextToken());
					serno= Integer.parseInt(st1.nextToken());
					bal= Integer.parseInt(st1.nextToken());
					System.out.println("  acno= "+acno+"  serno = "+serno+" bal= "+bal);
					if(serno == myId){
					accountIDandBalance.put(acno, bal);
					System.out.println("Added  acno= "+acno+"  serno = "+serno+" bal= "+bal);
					}
					accountIDandServerID.put(acno, serno);
				}
				ln=br.readLine();
			}
			
			
			//initialize send count map
			for(int id : ids)
			{
				sendCountMap.put(id, 0);
			}
			//initialize fidge mattern v clock
			vClock = new HashMap<Integer,Integer>();
			for(int id : ids)
			{
				vClock.put(id, 0);
			}

			
			
		} catch (NumberFormatException n) {
			System.out.println("Cannot Read Config File : expected number");
			n.printStackTrace();
		} catch (FileNotFoundException fne) {
			System.out.println("Cannot Read Config File : file not found");
			fne.printStackTrace();
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		
	}
	
	public void findRecieveNodes()
	{
		try {
			
			//Find own ID 
			String myHostname = InetAddress.getLocalHost().getHostName();
			for(int i =0; i<ids.length; i++) {
			if(hostnames[i].equals(myHostname)) myId=i;
			else continue;
			}
			
			System.out.println("My Index is = "+myId);
			
			recieveFrom= new ArrayList<Integer>();
			sendTo= new ArrayList<Integer>();
			
			// Partitioning the Given nodes into Sending and Recieving
			
			//counters for rhostname and shostname
			
			
			sHostnames= new ArrayList<String>();
			rHostnames= new ArrayList<String>();
			recievePorts = new ArrayList<Integer>();
			sendPorts= new ArrayList<Integer>();
			
			
			for(int i=0;i<ids.length;i++)
			{
				if(ids[i]<myId){
					//Send
					sendTo.add(ids[i]);
					sHostnames.add(hostnames[i]);
					sendPorts.add(ports[i]);
					System.out.println("Added to SendList: "+ids[i]);
				}
				if(ids[i]>myId){
					//Recieve
					recieveFrom.add(ids[i]);
					rHostnames.add(hostnames[i]);
					recievePorts.add(ports[i]);
					System.out.println("Added to RecList: "+ids[i]);
				}
			}
			
		} catch (UnknownHostException e) {
			System.out.println("Error determining Hostname");
			e.printStackTrace();
		}
		
	}
	
		public static void main(String argv[]) {
			
			if (argv.length != 2) {
				System.out.println("Insufficient arguments");
				return;
			}
			
			String file = argv[0];
			int port = Integer.parseInt(argv[1]);
			AccountServer n = new AccountServer(file, port);
			try {
				n.configure();
				n.findRecieveNodes();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("File Not Foundd....!!");
				e.printStackTrace();
			}
			
			SocketAcceptor sAcceptor = null;
			try {
				n.ss = new ServerSocket(n.myPort);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("Error initializing Global server socket");
				e1.printStackTrace();
			}
			// if receive list not empty start socket acceptor thread
			if (!n.recieveFrom.isEmpty()) {
				sAcceptor = new SocketAcceptor(port,n.rHostnames, n.recieveFrom, n.ss);
				System.out
						.println("Socket acceptor Initialized with listening port= "
								+ port+ "at ID = "+n.myId);
				sAcceptor.start();
				System.out.println("Acceptor thread started at = "+n.myId);
			}
			
			// Sending out requests....
		//	System.out.println("send: "+n.send.length+" shostnames: "+n.sHostnames.length+" sports "+n.sendports.length);
			
			
			// Sending Requests part
			
			HashMap<Integer, Socket> sendersockets = new HashMap<Integer, Socket>();
			
			Scanner reader = new Scanner(System.in);
			System.out.println("Press Enter Before sending out...");
			int a = reader.nextInt();
			
			
			int senderSuccess = 0;
			int k = 0;
			

			//for( int i: n.send)
			
				//System.out.println("send : " + i);
			
			
			if (!n.sendTo.isEmpty()) 
			while (senderSuccess < n.sendTo.size()) {
					try {
						System.out.println("Port no:"+n.sendPorts.get(k));
						Socket s = new Socket(n.sHostnames.get(k), n.sendPorts.get(k));
						System.out.println("New socket connection to "
								+ n.sHostnames.get(k) + " At port " + n.sendPorts.get(k));
						senderSuccess++;
						sendersockets.put(n.sendTo.get(k),s);
						k++;
						} catch (IOException e) {
						
							System.out.println("Connection to " + n.sHostnames.get(k)
								+ ": " + n.sendPorts.get(k));
							e.printStackTrace();
						}
					
			}
			
			HashMap<Integer, Socket> directoryMap = new HashMap<Integer,Socket>();
			n.outputStreams = new HashMap<Integer, ObjectOutputStream>();
			n.inputStreams = new HashMap<Integer, ObjectInputStream>();
			directoryMap.putAll(sendersockets);
			
			try {
				if(sAcceptor!=null){
					sAcceptor.join();
					directoryMap.putAll(sAcceptor.getSockets());}
				else
					System.out.println("not waiting for acceptor thread as there exists none.");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.err.println("ERROR: Interrupted while waiting for server to return to main");
			}
			
			
			for(int i : directoryMap.keySet())
			{
				//System.out.println("output stream being extracted with client with id " + i);
				try {
					
					ObjectOutputStream out = new ObjectOutputStream(directoryMap.get(i).getOutputStream());
					System.out.println("output stream being successfully established with "+i);
					n.outputStreams.put(i, out);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.err.println("could not establish output streams with id " + i);
				}
			}
			
			for(int i : directoryMap.keySet())
			{
				//System.out.println("input stream being extracted with client with id " + i);
				try {
					ObjectInputStream in = new ObjectInputStream(directoryMap.get(i).getInputStream());
					System.out.println("input stream being successfully established with "+i);
					n.inputStreams.put(i, in);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.err.println("could not establish input streams with id " + i);
				}
			}
			
			
			System.out.println("Inputstream Contents :"+n.inputStreams.values().toString());
			//HashMap<Integer,Listener> listenerMap= new HashMap<Integer,Listener>();
			
			
			n.cm= new ClientManager(n.myPort, n.myId,n.ss,n);
			n.cm.start();
			System.out.println("CM STARTAAAD");
			
			// for each inputstream add a listener
			for(int i : n.inputStreams.keySet())
			{
				Listener l=new Listener(n.myId, i, n.inputStreams.get(i), n.outputStreams, n,n.cm);
				l.start();
				System.out.println("Adding a listener at id "+n.myId+" for id "+i+" Input stream = "+n.inputStreams.get(i).toString());
			}
	}
}
