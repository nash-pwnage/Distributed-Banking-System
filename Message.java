import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable{
	
	int messageType;		// 0=checkBalanceRequest, 1=checkBalanceReply, 2= depositRequest, 3= depositReply, 4= withdrawalRequest, 5= withdrawalReply, 6= transferRequest, 7= transferReply, 8= totalMoney, 13 = freeze, 14= freezeReply 15=targetSendCount
	int message;
	int myAccountno, balance, payeeAccountno, amount;
	
	HashMap<Integer,Integer> sendCounts;
	int receiveCount;
	int targetSendCount;
	int serverId;
	int controllerPort;
	String controllerHostname;
	int controllerId;
	
	HashMap<Integer,Integer> vClock;
		
	public Message(int messageType, int myAccountno, int balance,
			int payeeAccountno, int amount) {
		super();
		this.messageType = messageType;
		this.myAccountno = myAccountno;
		this.balance = balance;
		this.payeeAccountno = payeeAccountno;
		this.amount = amount;
	}

	
	public Message() {
		super();
		this.messageType = -1;
		this.message = -1;
		this.myAccountno = -1;
		this.balance = -1;
		this.payeeAccountno = -1;
		this.amount = -1;
	}


	public int getMyAccountno() {
		return myAccountno;
	}

	public void setMyAccountno(int myAccountno) {
		this.myAccountno = myAccountno;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getPayeeAccountno() {
		return payeeAccountno;
	}

	public void setPayeeAccountno(int payeeAccountno) {
		this.payeeAccountno = payeeAccountno;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getMessageType() {
		return messageType;
	}	
	
	public void setMessageType(int message) {
		this.messageType = message;
	}
	
	public int getMessage() {
		return messageType;
	}
	
	public void setMessage(int msg) {
		this.message = msg;
	}

	
	
	
	
	
	public int getServerId() {
		return serverId;
	}


	public void setServerId(int serverId) {
		this.serverId = serverId;
	}


	public HashMap<Integer, Integer> getSendCounts() {
		return sendCounts;
	}


	public void setSendCounts(HashMap<Integer, Integer> sendCounts) {
		this.sendCounts = sendCounts;
	}


	public int getReceiveCount() {
		return receiveCount;
	}


	public void setReceiveCount(int receiveCount) {
		this.receiveCount = receiveCount;
	}


	public int getTargetSendCount() {
		return targetSendCount;
	}


	public void setTargetSendCount(int targetSendCount) {
		this.targetSendCount = targetSendCount;
	}


	public int getControllerPort() {
		return controllerPort;
	}


	public void setControllerPort(int controllerPort) {
		this.controllerPort = controllerPort;
	}


	public String getControllerHostname() {
		return controllerHostname;
	}


	public void setControllerHostname(String controllerHostname) {
		this.controllerHostname = controllerHostname;
	}

	
	public int getControllerId() {
		return controllerId;
	}


	public void setControllerId(int controllerId) {
		this.controllerId = controllerId;
	}


	@Override
	public String toString() {
		return "Message [messageType=" + messageType + ", message=" + message
				+ ", myAccountno=" + myAccountno + ", balance=" + balance
				+ ", payeeAccountno=" + payeeAccountno + ", amount=" + amount
				+ ", sendCounts=" + sendCounts + ", receiveCount="
				+ receiveCount + ", targetSendCount=" + targetSendCount
				+ ", serverId=" + serverId + ", controllerPort="
				+ controllerPort + ", controllerHostname=" + controllerHostname
				+ ", controllerId=" + controllerId + ", vClock=" + vClock + "]";
	}



	


	
	

	
}
