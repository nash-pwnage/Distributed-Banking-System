import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.StringTokenizer;


public class batchFileRead {

batchFileRead()

{
}

public  void configure() throws FileNotFoundException {
	try {
		File configFile = null;
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
		
		int[] ids = new int[numberOfServers];
		int[] ports = new int[numberOfServers];
		String[] hostnames = new String[numberOfServers];
		
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
					
					int numberOfAccounts = Integer.parseInt(st.nextToken());
					System.out.println("Number Of Accounts = "+numberOfAccounts);
					ln = br.readLine();
					st=new StringTokenizer(ln);
					
					// Skip Empty Lines and #
					while(ln.startsWith("#") || ln.length()<1) {
							ln= br.readLine();
							st=new StringTokenizer(ln);
					}
					
		
		HashMap<Integer,Integer> accountIDandServerID= new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> accountIDandBalance= new HashMap<Integer,Integer>();
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
}

