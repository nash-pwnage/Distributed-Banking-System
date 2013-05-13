import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;


public class ClientManager extends Thread {
	
	ServerSocket ss;
	AccountServer parentServer;
	int lport;
	int id;
	ObjectOutputStream out;
	ClientManager(int myport, int myId, ServerSocket s1, AccountServer a)
	{
		this.parentServer= a;
		this.ss = s1;
		this.lport=myport;
		this.id=myId;
	}
	
	public void run()
	{
	while(true) {
		try {
			System.out.println("Waiting for accept");
			Socket s = ss.accept();
			System.out.println("socket S = "+s.toString());
			ObjectInputStream in = new ObjectInputStream(s.getInputStream());
			out = new ObjectOutputStream(s.getOutputStream());
			System.out.println("input stream being successfully established with "+id);
			Message m;
			try {
				m = (Message)in.readObject();
			//System.out.println("M: "+m.toString());
			Message mreply=null;
			switch (m.messageType){
			case 0:{ // Check balance
				int temp= parentServer.accountIDandBalance.get(m.myAccountno);
				//System.out.println("Your Account Balance is: $"+temp);
				parentServer.addEvent();
				parentServer.printVector(parentServer.vClock);
				mreply=new Message(1,m.myAccountno,temp,-1,-1);
				break;
				}
			
			case 2:{ // Deposit
				int temp= parentServer.accountIDandBalance.get(m.myAccountno);
				if(m.amount>0){
					parentServer.addEvent();
					parentServer.printVector(parentServer.vClock);
					parentServer.accountIDandBalance.put(m.myAccountno,(temp+m.amount));
				}
				mreply=new Message(3,m.myAccountno,parentServer.accountIDandBalance.get(m.myAccountno),-1,m.amount);
				break;
				}
			
			case 4:{ // Withdrawal
				int temp= parentServer.accountIDandBalance.get(m.myAccountno);
				if(m.amount>0&&((parentServer.accountIDandBalance.get(m.myAccountno)-m.amount)>0)){ 
					parentServer.addEvent();
					parentServer.printVector(parentServer.vClock);
					parentServer.accountIDandBalance.put(m.myAccountno,(temp-m.amount));
				mreply=new Message(5,m.myAccountno,parentServer.accountIDandBalance.get(m.myAccountno),-1,m.amount);}
				else {
				mreply=new Message(51,m.myAccountno,(parentServer.accountIDandBalance.get(m.myAccountno)),-1,m.amount);
				}
				break;
				}
		
			case 6:{ // Transfer
				if(!parentServer.frozen){
				int temp= parentServer.accountIDandBalance.get(m.myAccountno);
				
				if(m.amount>0&&((parentServer.accountIDandBalance.get(m.myAccountno)-m.amount)>0)){ 
					parentServer.accountIDandBalance.put(m.myAccountno,(temp-m.amount));
				
					// Do the transfer
				System.out.println("Handling message :" + m);
				parentServer.addEvent();
				parentServer.printVector(parentServer.vClock);
				int payeeServerNumber=parentServer.accountIDandServerID.get(m.payeeAccountno);
					if(payeeServerNumber==parentServer.myId){
						temp= parentServer.accountIDandBalance.get(m.payeeAccountno);
						parentServer.accountIDandBalance.put(m.payeeAccountno, temp+m.amount);
						mreply= new Message(7,m.myAccountno,m.balance,m.payeeAccountno,m.amount);
					}
					else{
					ObjectOutputStream serverOut = parentServer.outputStreams.get(payeeServerNumber);
					Message transferToServer = new Message(1337,m.myAccountno,m.balance,m.payeeAccountno,m.amount);
					transferToServer.vClock = parentServer.vClock;
					System.out.println("Forwarding transfer to dest server : " + transferToServer);
					serverOut.reset();
					serverOut.writeObject(transferToServer);
					//parentServer.sendVector[payeeServerNumber]++;
					int temp1 = 0;
					temp1 = parentServer.sendCountMap.get(payeeServerNumber);
					temp1++;
					parentServer.sendCountMap.put(payeeServerNumber, temp1);
					
					System.out.println("Account Number "+parentServer.myId+" Sendvector: "+ parentServer.getSendCountMap());
					
					mreply= new Message(7,m.myAccountno,m.balance,m.payeeAccountno,m.amount);
					}
				}
				else {
				mreply=new Message(71,m.myAccountno,m.balance,m.payeeAccountno,m.amount);
				}
				break;
				}
				else
				{//send blocked transfer message to client
					
					System.out.println("Transfer blocked. blocked request = " + m);
					mreply=new Message(72,m.myAccountno,m.balance,m.payeeAccountno,m.amount);
				}
			}
		
			case 8:{
				break;
				}
			
			
			case 13: //received freeze message, send send count vector
				// freeze
			    parentServer.controllers.put(m.getControllerId(),new ControllerEntry(m.getControllerId(), m.getControllerPort(), m.getControllerHostname()));
			    parentServer.listControllers(" to check new controller addition");
			    parentServer.frozen = true;
			    System.out.println("FROZEN");
			    System.out.println("freeze message received = " + m);
			    System.out.println("Received freeze message from controller " + m.getControllerId());
			   
			    // reply with send count vector
			    Message reply = new Message(14, -1, -1, -1, -1);
			    reply.setServerId(parentServer.getMyId());
			    System.out.println("Send vector on this system = " + parentServer.getSendCountMap());
			    reply.setSendCounts(parentServer.getSendCountMap());
			    System.out.println("Send vector on outgoing packet = " + reply.getSendCounts());
			    Socket socketToController = new Socket(m.getControllerHostname(), m.getControllerPort());
			    ObjectOutputStream out = new ObjectOutputStream(socketToController.getOutputStream());
			    out.reset();
			    out.writeObject(reply);
			    System.out.println("Sent freeze reply to controller" + m.getControllerHostname());
			    System.out.println("freeze reply message" + reply);
			    
				break;
				
				
			case 15: // received target send counts , wait till your receive count reaches the limit
			{
				System.out.println("server has sent target count = " + m.targetSendCount);
				System.out.println("current receive count = " + parentServer.receiveCount);
				if(m.targetSendCount==parentServer.receiveCount){
					System.out.println("immediate match of counts. so sending message code 16 ");
					Message m2=new Message(16,-1,-1,-1,-1);
					m2.setServerId(parentServer.myId);
					m2.amount=parentServer.getTotalAmount();
					m2.vClock = parentServer.vClock;
					m2.receiveCount = parentServer.receiveCount;
					Socket s2= new Socket(m.getControllerHostname(),m.getControllerPort());
					ObjectOutputStream o2= new ObjectOutputStream(s2.getOutputStream());
					o2.reset();
					o2.writeObject(m2);
					
					System.out.println("Waiting for MAX transit time to ensure that there are no in transit messages before final receive count is sent for checking");
					try {
						Thread.sleep(16000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Sending receive count for checking = " + parentServer.receiveCount);
					Message m3=new Message(19,-1,-1,-1,-1); 
					m3.setServerId(parentServer.myId);
					m3.amount=parentServer.getTotalAmount();
					m3.vClock = parentServer.vClock;
					m3.receiveCount = parentServer.receiveCount;
					Socket s3= new Socket(m.getControllerHostname(),m.getControllerPort());
					ObjectOutputStream o3= new ObjectOutputStream(s3.getOutputStream());
					o3.reset();
					o3.writeObject(m3);
					
					
				}
				else
				{
					System.out.println("need to wait for one or more receives");
					Waiter w = new Waiter(m,parentServer);
					w.start();
				}
				
				break;
				
			}
			
			case 17:
				System.out.println("Unfreeze message received from " + m.controllerId + " = " + m);
				parentServer.controllers.remove(m.controllerId);
				if(parentServer.controllers.size() == 0)
				{
					System.out.println("UNFROZEN.");
					parentServer.frozen = false;
				}
			
			}
			
			if(mreply!=null)out.writeObject(mreply);
			
			
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			System.out.println("Error creating the Socket");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	}
}
