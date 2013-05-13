import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class SocketAcceptor extends Thread {
ArrayList<Integer> id; // Had to be changed from int to AL
int port[];
ArrayList<String> hostName;
//Socket socket[];
int lport;
HashMap<Integer, Socket> sockets;
HashMap<String,Integer> recievedSockets;
ServerSocket ss;


public HashMap<Integer, Socket> getSockets() {
	return sockets;
}

public void setSockets(HashMap<Integer, Socket> sockets) {
	this.sockets = sockets;
}

public SocketAcceptor(int listenPort, ArrayList<String> rhostnames, ArrayList<Integer> rid, ServerSocket s1)
{
	sockets= new HashMap<Integer, Socket>();
	this.ss=s1;
	id=rid;
	hostName=rhostnames;
	lport=listenPort;
	recievedSockets=new HashMap<String,Integer>();
	int i=0;
	
	for(String hostname: rhostnames)
	{
		int tempId = rid.get(i);
		i++;
		recievedSockets.put(hostname, tempId);
		System.out.println("Accept from "+hostname+" : "+id);
	}
}

public void run()
{
	
	//	System.out.println("Address "+ss.getInetAddress()+"local addr= "+ss.getLocalSocketAddress());
		System.out.println("SocketAcceptor New Serversocket extablished on port no: "+lport);
		System.out.println("SocketAcceptor Listening....");
	
	int countAcceptedHosts=0;
	while (countAcceptedHosts < id.size())
	{
		 
		try {
			Socket s = ss.accept();
			countAcceptedHosts++;
		String recievedHostname=s.getInetAddress().getHostName();
		System.out.println("Connected successfully to "+recievedHostname);
		if (recievedSockets.get(recievedHostname)!=null)
		{	int id= recievedSockets.get(recievedHostname);
			sockets.put(id,s);
			System.out.println("Added a new entry from "+id+" to reciever hashmap 2");
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception Ocurred");
			e.printStackTrace();
		}
			
	}
}

}
