package MainFunction;

import network.Node;

public class Regenerator {
	private Node node = new Node(null, 0, null, null, 0, 0);
	private int index = 0;
	private int propathNum = 0;
	private int RegType = 2;//�������� =0��ʾpyhsical layer ����������� ��=1��ʾ��IP��������
	
	public Regenerator(Node node) {
		super();
		this.node = node;
	}
	
	public void setNature(int nature) {
		 this.RegType=nature;
	}
	public int getNature() {
		return RegType;
	}
	
	public void setPropathNum(int propathNum) {
		 this.propathNum=propathNum;
	}
	public int getPropathNum() {
		return propathNum;
	}
	
	
	public void setnode(Node node) {
		 this.node=node;
	}
	public Node getnode() {
		return node;
	}
	
	public void setindex(int index) {
		 this.index=index;
	}
	public int getindex() {
		return index;
	}
	


}
