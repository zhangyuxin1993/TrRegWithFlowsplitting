package MainFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import graphalgorithms.RouteSearching;
import network.Layer;
import network.Link;
import network.Network;
import network.Node;
import network.NodePair;
import network.VirtualLink;
import subgraph.LinearRoute;

public class Test {
	private int m;
	private int n;
	private int[] set;
	private boolean first;
	private int position;

	public Test(int n, int m) {
		this.m = m;
		this.n = n;
		first = true;
		position = n - 1;
		set = new int[n];
		for (int i = 0; i < n; i++)
			set[i] = i + 1;
	}

	public Test() {

	}

	public boolean hasNext() {
		if (m == n && first) {
			return true;
		} else {
			return set[0] < m - set.length + 1;
		}
	}

	public int[] next() {
		if (first) {
			first = false;
			return set;
		}
		if (set[set.length - 1] == m)
			position--;
		else
			position = set.length - 1;
		set[position]++;
		for (int i = position + 1; i < set.length; i++)
			set[i] = set[i - 1] + 1;
		return set;
	}

	public static void main(String[] args) {
		 Network network = new Network("ip over EON", 0, null);
		 network.readPhysicalTopology("G:/Topology/testtop.csv");
		 network.copyNodes();
		 network.createNodepair();//
		ArrayList<Node> no=new ArrayList<>();
		 
		 Layer oplayer = network.getLayerlist().get("Physical");
		 Layer iplayer = network.getLayerlist().get("Layer0");

		Node node1=new Node("N2", 0, null, oplayer, 0, 0);
		no.add(node1);
		Node node2=new Node("N2", 0, null, oplayer, 0, 0);
		no.add(node2);
		
		System.out.println(no.size());
		no.remove(node2);
		System.out.println(no.size());

		for(Node node:no){
			System.out.println(node.getName());
		}
		 /*
		 * ���Դ�M�����������ȡm�������������
		 */
		// Test nOfm = new Test(5, 5);
		// while (nOfm.hasNext()) {
		// int[] set = nOfm.next();
		// for (int i = 0; i < set.length; i++) {
		// System.out.print(set[i]);
		// }
		// System.out.println();
		// }
		
		// Network network = new Network("ip over EON", 0, null);
		// network.readPhysicalTopology("G:/Topology/testtop.csv");
		// network.copyNodes();
		// network.createNodepair();//
		//
		// Layer oplayer = network.getLayerlist().get("Physical");
		// Layer iplayer = network.getLayerlist().get("Layer0");
		// Node nodeA=new Node(null, 0, null, oplayer, 0, 0);
		// Node nodeB=new Node(null, 0, null, oplayer, 0, 0);
		//
		// oplayer.generateNodepairs();
		// HashMap<String,Node>map=oplayer.getNodelist();
		// Iterator<String>iter=map.keySet().iterator();
		// while(iter.hasNext()){
		// Node node=(Node)(map.get(iter.next()));
		// if(node.getName().equals("N1")) nodeA=node;
		// if(node.getName().equals("N7")) nodeB=node;
		// }

		// HashMap<String,NodePair>map2=oplayer.getNodepairlist();
		// Iterator<String>iter2=map2.keySet().iterator();
		// while(iter2.hasNext()){
		// NodePair nodepair=(NodePair)(map2.get(iter2.next()));
		// RouteSearching rs=new RouteSearching();
		// LinearRoute newRoute=new LinearRoute(null, 0, null);
		// rs.Dijkstras(nodepair.getSrcNode(), nodepair.getDesNode(), oplayer,
		// newRoute, null);
		// System.out.print(nodepair.getName()+" ");
		// newRoute.OutputRoute_node(newRoute);
		// }
		// System.out.println(nodeA.getName()+" - "+nodeB.getName());
		// RouteSearching rs=new RouteSearching();
		// LinearRoute newRoute=new LinearRoute(null, 0, null);
		// rs.Dijkstras(nodeA, nodeB, oplayer, newRoute, null);
		// newRoute.OutputRoute_node(newRoute);
		// RegeneratorPlace rp=new RegeneratorPlace();
		// double length=newRoute.getlength();
		// rp.regeneratorplace(145, length, newRoute, oplayer, iplayer);

		/*
		 * ����С���� ����IP�����Ƿ��Ѿ���ĳ����·
		 */
		/*
		 * Network network = new Network("ip over EON", 0, null);
		 * network.readPhysicalTopology("G:/Topology/10.csv");
		 * network.copyNodes(); network.createNodepair();// Layer iplayer =
		 * network.getLayerlist().get("Layer0"); Layer oplayer =
		 * network.getLayerlist().get("Physical"); Node nodeA=new Node(null, 0,
		 * null, oplayer, 0, 0); Node nodeB=new Node(null, 0, null, oplayer, 0,
		 * 0); Node nodeC=new Node(null, 0, null, oplayer, 0, 0); Node nodeD=new
		 * Node(null, 0, null, oplayer, 0, 0);
		 * 
		 * // System.out.println(oplayer.getLinklist().size());
		 * HashMap<String,Node>map=oplayer.getNodelist();
		 * Iterator<String>iter=map.keySet().iterator(); while(iter.hasNext()){
		 * Node node=(Node)(map.get(iter.next()));
		 * if(node.getName().equals("N0")) nodeA=node;
		 * if(node.getName().equals("N5")) nodeB=node; //
		 * if(node.getName().equals("N3")) nodeC=node; //
		 * if(node.getName().equals("N4")) nodeD=node; } //
		 * System.out.println(nodeA.getName()+"     "+nodeB.getName());
		 * RouteSearching rs=new RouteSearching(); LinearRoute newRoute=new
		 * LinearRoute(null, 0, null); rs.Dijkstras(nodeA, nodeB, oplayer,
		 * newRoute, null); newRoute.OutputRoute_node(newRoute);
		 * 
		 * Test t=new Test();
		 * 
		 * 
		 * for(Node node: newRoute.getNodelist()){ int
		 * po=t.nodeindexofroute(node,newRoute);
		 * System.out.println(node.getName()+"  "+po); }
		 * 
		 * 
		 * String name=nodeA.getName()+"-"+nodeB.getName(); String
		 * name2=nodeC.getName()+"-"+nodeD.getName(); System.out.println(name+
		 * "   "+name2); Link newlink=new Link(name, 0, null, iplayer, nodeA,
		 * nodeB, 0, 0); iplayer.addLink(newlink); Link
		 * findledink=iplayer.findLink(nodeA, nodeB); Link
		 * findledink2=iplayer.findLink(nodeC, nodeD);// �Ҳ���link��ʱ����ô�죿����
		 * System.out.println(findledink.getName()); // try{ //
		 * System.out.println(findledink2.getName()); //
		 * }catch(java.lang.NullPointerException ex){ // System.out.println(
		 * "IP ��û�и���·"); // }
		 * 
		 * VirtualLink Vlink = new VirtualLink(nodeA.getName(), nodeB.getName(),
		 * 0, 0); Vlink.setnature(0); Vlink.setUsedcapacity(100);
		 * Vlink.setFullcapacity(200);// �������flow�Ǵ����������
		 * Vlink.setRestcapacity(Vlink.getFullcapacity() -
		 * Vlink.getUsedcapacity()); Vlink.setlength(4500); Vlink.setcost(0);
		 * 
		 * VirtualLink Vlink2 = new VirtualLink(nodeA.getName(),
		 * nodeB.getName(), 0, 0); Vlink.setnature(0);
		 * Vlink.setUsedcapacity(100); Vlink.setFullcapacity(200);//
		 * �������flow�Ǵ���������� Vlink.setRestcapacity(Vlink.getFullcapacity() -
		 * Vlink.getUsedcapacity()); Vlink.setlength(4500); Vlink.setcost(0);
		 * 
		 * 
		 * findledink.getVirtualLinkList().add(Vlink);
		 * findledink.getVirtualLinkList().add(Vlink2);
		 * System.out.println("������·������"+findledink.getVirtualLinkList().size());
		 * //һ��IP ��·�Ͽ��ԼӶ��������·������Щ������·��ʹ�������ض�һ��Ҳ�����ظ�
		 */
	}

	public int nodeindexofroute(Node node, LinearRoute route) {
		int position = 0;
		for (int n = 0; n < route.getNodelist().size(); n++) {
			if (node.getName().equals(route.getNodelist().get(n).getName())) {
				position = n;
				break;
			}
		}
		return position;
	}

	public int linklistcompare(ArrayList<Link> linklist1, ArrayList<Link> linklist2) {// �÷��������ж�����linklist�Ƿ�����·

		boolean findflag = false;
		int D_value = 0;
		for (Link link1 : linklist1) {
			for (Link link2 : linklist2) {
				if (link1.equals(link2)) {
					findflag = true;
					D_value = 1;
					break;
				}
			}
			if (findflag)
				break;
		}
		return D_value;
	}

//	public void check(Layer iplayer) {
//		ArrayList<VirtualLink> VirtualLinklist = new ArrayList<>();
//		HashMap<String, Link> linklist = iplayer.getLinklist();
//		Iterator<String> linkitor = linklist.keySet().iterator();
//		while (linkitor.hasNext()) {
//			Link Mlink = (Link) (linklist.get(linkitor.next()));
//			VirtualLinklist = Mlink.getVirtualLinkList();// ȡ��IP���ϵ���·��Ӧ��������·
//															// �½�һ��listʹ�䱾���������·���ı�
//			for (VirtualLink Vlink : VirtualLinklist) { // ȡ��link�϶�Ӧ��virtual
//				// link
//				System.out.println(
//						"IP������·" + Mlink.getName() + "    ��Ӧ��������·��" + Vlink.getSrcnode() + "-" + Vlink.getDesnode()
//								+ "   nature=" + Vlink.getNature() + "    ��������·�϶�Ӧ��ʣ������Ϊ��" + Vlink.getRestcapacity());
//
//			}
//		}
//	}
}
