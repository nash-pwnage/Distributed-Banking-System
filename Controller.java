import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;


public class Controller extends Thread {
	
	int[] ports, ids;
	String[] hostnames;
	int controllerId;
	int controllerPort;
	String controllerHostname;
	HashMap<Integer,Integer> sendCountVectorSummation;
	ServerSocket controllerSocket;

	public int[] getPorts() {
		return ports;
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




	public String[] getHostnames() {
		return hostnames;
	}




	public void setHostnames(String[] hostnames) {
		this.hostnames = hostnames;
	}




	public int getControllerId() {
		return controllerId;
	}




	public void setControllerId(int controllerId) {
		this.controllerId = controllerId;
	}




	public int getControllerPort() {
		return controllerPort;
	}




	public void setControllerPort(int controllerPort) {
		this.controllerPort = controllerPort;
	}


	

	public Controller(int[] ports, int[] ids, String[] hostnames,
			int controllerId, int controllerPort) {
		super();
		this.ports = ports;
		this.ids = ids;
		this.hostnames = hostnames;
		this.controllerId = controllerId;
		this.controllerPort = controllerPort;
		try {
			this.controllerHostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		try {
			controllerSocket = new ServerSocket(this.getControllerPort());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.sendCountVectorSummation = new HashMap<Integer, Integer>();
		//initialize send count map
		for(int id : ids)
		{
			sendCountVectorSummation.put(id, 0);
		}
		
	}

	public String getControllerHostname()
	{
		return controllerHostname;
	}
	
	
	


	public void run()
	{
		try {
			
			
			int replyCount = 0;
			
			//phase 2 when all clients start replying to freeze messages with piggybacked send count vectors
			ArrayList<Integer> serversThatSentFreezeReplies =  new ArrayList<Integer>();
			
			
				while(replyCount < hostnames.length){
				Socket serverIncoming = controllerSocket.accept();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				ObjectInputStream in = new ObjectInputStream(serverIncoming.getInputStream());
				Message m = (Message) in.readObject();
				if(!(m.messageType == 14) ||(serversThatSentFreezeReplies.contains(m.getServerId())))
				{
					continue;
				}
				else
				{
					System.out.println(" Received freeze reply message " + m);
					serversThatSentFreezeReplies.add(m.getServerId());
					replyCount++;
					HashMap<Integer,Integer> sendCountVector = m.getSendCounts();
					
					for(int serverId : sendCountVector.keySet())
					{
						int tempVal = sendCountVectorSummation.get(serverId);
						tempVal += sendCountVector.get(serverId);
						sendCountVectorSummation.put(serverId, tempVal);
						System.out.println("Size of send vector from server "+ m.getServerId() + " " +sendCountVectorSummation == null? null : sendCountVectorSummation.size());
						for(int serverid : sendCountVectorSummation.keySet())
						{
							System.out.println("send count sum : server|sendCount " + sendCountVectorSummation.get(serverid));
						}
						
					}
				}
				
				
			}//end of phase 2 while
				
				System.out.println("Size of summation vector = " + sendCountVectorSummation == null? null : sendCountVectorSummation.size());
				for(int serverid : sendCountVectorSummation.keySet())
				{
					System.out.println("send count sum : server|sendCount " + sendCountVectorSummation.get(serverid));
				}
				
			replyCount = 0;
				
			//start phase 3 : controller sends the target send counts to the servers
			for(int i =0; i<ids.length; i++)
			{
				Socket s = new Socket(hostnames[i], ports[i]);
				ObjectOutputStream o = new ObjectOutputStream(s.getOutputStream());
				Message m = new Message(15,-1,-1,-1,-1);
				m.setTargetSendCount(sendCountVectorSummation.get(ids[i]));
				m.setControllerHostname(getControllerHostname());
				m.setControllerPort(getControllerPort());
				m.setControllerId(getControllerId());
				o.writeObject(m);
				System.out.println("Outgoing message to server "+ ids[i] + "> " + hostnames[i] + " : " + ports[i] );
				System.out.println(m);
			}//end of phase 3
			
			
			//start phase 4: 
			//phase 4: when all clients start replying to target send count messages with piggybacked send count vectors, received count, and amount in all accounts;
			ArrayList<Integer> serversThatSentTotalAmounts =  new ArrayList<Integer>();
			HashMap<Integer,HashMap<Integer,Integer>> vClocks = new HashMap<Integer,HashMap<Integer,Integer>>(); 
			HashMap<Integer,Integer> receiveCounts = new HashMap<Integer,Integer>(); 
				replyCount = 0;
				int totalAmount = 0;
				while(replyCount < hostnames.length){
					Socket serverIncoming = controllerSocket.accept();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					ObjectInputStream in = new ObjectInputStream(serverIncoming.getInputStream());
					Message m = (Message) in.readObject();
					System.out.println("Received Message 16: " + m);
					if(!(m.messageType == 16) ||(serversThatSentTotalAmounts.contains(m.getServerId())))
					{
						continue;
					}
					else
					{
						serversThatSentTotalAmounts.add(m.getServerId());
						replyCount++;
						totalAmount = totalAmount + m.getAmount();
						System.out.println("TCA message received : " + m);
						vClocks.put(m.serverId,m.vClock);
						receiveCounts.put(m.serverId, m.receiveCount);
						
					}
					 
					
					
				
				}//end of phase 4 while
				
				//consistency check
				System.out.println("Receive count at the end of phase 4 = " + receiveCounts);
				System.out.println("Total amount in System : "  + totalAmount);
				for(int id : ids){
				System.out.println("Vector clock of "+ id + vClocks.get(id));
				}
				
				boolean flag = true;
				for(int id1 : ids){
					
					int ownEntry = vClocks.get(id1).get(id1);
					for(int id2 : ids){
						int theirEntry = vClocks.get(id2).get(id1);
						if(theirEntry>ownEntry)
						{
							flag = false;
						}
					}
				}
				if(flag == false)
				{
					System.out.println("The system snapshot is inconsistent");
				}
				if(flag == true)
				{
					System.out.println("The system snapshot is consistent");
				}
				
				
				////start phase 5: 
				//phase 5: when all clients start replying to target send count messages with piggybacked send count vectors, received count, and amount in all accounts;
				
				System.out.println("Waiting for receive count check messages to prove transit less ness");
				
				ArrayList<Integer> serversThatSentCheckReceiveCounts =  new ArrayList<Integer>();
				
				HashMap<Integer,Integer> checkReceiveCounts = new HashMap<Integer,Integer>(); 
					replyCount = 0;
					while(replyCount < hostnames.length){
						Socket serverIncoming = controllerSocket.accept();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						ObjectInputStream in = new ObjectInputStream(serverIncoming.getInputStream());
						Message m = (Message) in.readObject();
						System.out.println("Received Message 19: " + m);
						if(!(m.messageType == 19) ||(serversThatSentCheckReceiveCounts.contains(m.getServerId())))
						{
							continue;
						}
						else
						{
							serversThatSentCheckReceiveCounts.add(m.getServerId());
							replyCount++;
							System.out.println("check receive count message received : " + m);
							checkReceiveCounts.put(m.serverId, m.receiveCount);
							
						}
					}//end of phase 5 while
				
				
				
				System.out.println("Receive count at the end of phase 5 = " + checkReceiveCounts);
				
				
				boolean flag2 = true;
				for(int id : ids)
				{
					if(receiveCounts.get(id)!=checkReceiveCounts.get(id))
					{
						flag = false;
					}
				}
				System.out.println("transit less? " + flag2);
				
			//start phase 6
			//phase 6 : when controller send unfreeze messages to all the servers
				
				for(int i =0; i<ids.length; i++)
				{
					Socket s = new Socket(hostnames[i], ports[i]);
					ObjectOutputStream o = new ObjectOutputStream(s.getOutputStream());
					Message m = new Message(17,-1,-1,-1,-1);
					m.setControllerHostname(getControllerHostname());
					m.setControllerPort(getControllerPort());
					m.setControllerId(getControllerId());
					o.writeObject(m);
					System.out.println("Outgoing message to server "+ ids[i] + "> " + hostnames[i] + " : " + ports[i] );
					System.out.println(m);
				}//end of phase 6
				
				
				
				
				
			
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				controllerSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
