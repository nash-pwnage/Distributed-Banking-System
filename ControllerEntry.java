
public class ControllerEntry {
	
	int controllerId;
	int controllerPort;
	String controllerHostname;
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
	public String getControllerHostname() {
		return controllerHostname;
	}
	public void setControllerHostname(String controllerString) {
		this.controllerHostname = controllerString;
	}
	public ControllerEntry(int controllerId, int controllerPort,
			String controllerString) {
		super();
		this.controllerId = controllerId;
		this.controllerPort = controllerPort;
		this.controllerHostname = controllerString;
	}
	@Override
	public String toString() {
		return "ControllerEntry [controllerId=" + controllerId
				+ ", controllerPort=" + controllerPort
				+ ", controllerHostname=" + controllerHostname + "]";
	}
	
	
	
	

}
