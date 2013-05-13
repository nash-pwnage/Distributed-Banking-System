import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import javax.sound.midi.Receiver;


public class Listener extends Thread{

	private int selfId;
	private int listenToId;
	HashMap<Integer,ObjectOutputStream> outs;
	AccountServer parentServer;
	ClientManager getClientoutStream;
	Message mreply;
	// GlobalStatus global;	
	MessageProcessor processor;
	private ObjectInputStream inputStream;
	Client a;
	
	public Listener(int myId,int listenToId,
			ObjectInputStream inputstreams, HashMap<Integer,ObjectOutputStream>outs, AccountServer a, ClientManager b) { //, int distinguished, int parent) {
		super();
		this.parentServer=a;
		this.selfId = myId;
		this.listenToId = listenToId;
		this.inputStream = inputstreams;
		this.outs = outs;
		this.processor = new MessageProcessor(this.outs,inputstreams);
		this.getClientoutStream= b;
	}
	
	public void run()
	{
		System.out.println("Listener is now ready and listening to client " + this.listenToId);
		Message m=null;
		do{
			try {
				m = (Message) inputStream .readObject();
				System.out.println("Message: "+m);
				if(m==null){
					continue;
				}
			//processor.process(m);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			}
			
			if(m!=null)
				{
				// incrementing Recieve Counter
				int senderAccountNumber=parentServer.accountIDandServerID.get(m.myAccountno);
				parentServer.receiveVector[senderAccountNumber]++;
				System.out.println("Transfer Message received = " +m);
				System.out.println("Account Number "+parentServer.myId+" Receive: "+Arrays.toString(parentServer.receiveVector));	
				if (m.messageType==1337) {
				try {
					
					Thread.sleep((new Random().nextInt(15)+1)*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Transfer Succesful from Accno "+m.myAccountno+" for Acc no "+m.payeeAccountno);
				int temp= parentServer.accountIDandBalance.get(m.payeeAccountno);
				parentServer.accountIDandBalance.put(m.payeeAccountno,(temp+m.amount));
				System.out.println("Receive count = " + parentServer.receiveCount);
				parentServer.receiveCount++;
				System.out.println("new Receive count = " + parentServer.receiveCount);
				parentServer.mergeVector(m.vClock);
				parentServer.addEvent();
				parentServer.printVector(parentServer.vClock);
				}
				else
					System.out.println("Processing Message: "+m.messageType);
					processor.process(m);
				}
			
		} while(true);
		
		
		
	}
	
	public int getSelfId() {
		return selfId;
	}
	public void setSelfId(int selfId) {
		this.selfId = selfId;
	}
	public int getListenToId() {
		return listenToId;
	}
	public void setListenToId(int listenToId) {
		this.listenToId = listenToId;
	}
	
	

	public ObjectInputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(ObjectInputStream inputStream) {
		this.inputStream = inputStream;
	}

	

	
	
}
