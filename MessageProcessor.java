import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;


public class MessageProcessor {
	
	
	HashMap<Integer,ObjectOutputStream> outs;
	ObjectInputStream ins;
	
	public MessageProcessor(HashMap<Integer,ObjectOutputStream> outs, ObjectInputStream ins) {
		super();
		this.outs = outs;
		this.ins=ins;
		
	}
	// 0=checkBalanceRequest, 1=checkBalanceReply, 2= depositRequest, 3= depositReply, 4= withdrawalRequest, 5= withdrawalReply, 6= transferRequest, 7= transferReply, 8= totalMoney
	public void process(Message m)
	{	
		switch(m.messageType) { 
				//checkBalanceRequest message
			

		}
	} 
}