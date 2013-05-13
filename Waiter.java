import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class Waiter extends Thread {
	Message m;
	AccountServer parentServer;
	
	Waiter(Message a,AccountServer b)
	{
		this.m=a;
		this.parentServer=b;
	}
	public void run()
	{
		System.out.println("Waiter Initiated");
		System.out.println("server has sent target count = " + m.targetSendCount);
		System.out.println("current receive count = " + parentServer.receiveCount);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			while(m.targetSendCount>parentServer.receiveCount)
			{
				
				System.out.println("Waiter: server has sent target count = " + m.targetSendCount);
				System.out.println("Waiter: current receive count = " + parentServer.receiveCount);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		System.out.println(" waiting ends. Sending Msgcode 16 ");
		Message m2=new Message(16,-1,-1,-1,-1);
		m2.setServerId(parentServer.myId);
		m2.amount=parentServer.getTotalAmount();
		m2.setServerId(parentServer.getMyId());
		m2.vClock = parentServer.vClock;
		m2.receiveCount = parentServer.receiveCount;
		Socket s2;
		try {
			s2 = new Socket(m.getControllerHostname(),m.getControllerPort());
			ObjectOutputStream o2= new ObjectOutputStream(s2.getOutputStream());
			o2.writeObject(m2);
			System.out.println("Sending Msg code 16");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(16000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Message m3=new Message(19,-1,-1,-1,-1);
		m3.setServerId(parentServer.myId);
		m3.amount=parentServer.getTotalAmount();
		m3.vClock = parentServer.vClock;
		m3.receiveCount = parentServer.receiveCount;
		try {
			Socket s3= new Socket(m.getControllerHostname(),m.getControllerPort());
			ObjectOutputStream o3= new ObjectOutputStream(s3.getOutputStream());
			o3.reset();
			o3.writeObject(m3);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

		
	}

}
