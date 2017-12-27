package MainFunction;

import network.VirtualLink;

public class FlowUseOnLink {
	private VirtualLink vlink;
	private double FlowUseOnLink;
 
	public FlowUseOnLink(VirtualLink vlink, double flowUseOnLink) {
		super();
		this.vlink = vlink;
		FlowUseOnLink = flowUseOnLink;
	}
	
	public void setvlink(VirtualLink vlink) {
		this.vlink=vlink;
	}
	public VirtualLink getVlink() {
		return vlink;
	}
	
	public void setFlowUseOnLink(double FlowUseOnLink) {
		this.FlowUseOnLink=FlowUseOnLink;
	}
	public double getFlowUseOnLink() {
		return FlowUseOnLink;
	}
	

}
