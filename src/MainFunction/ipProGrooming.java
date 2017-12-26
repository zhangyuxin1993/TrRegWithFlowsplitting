package MainFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import general.file_out_put;
import graphalgorithms.RouteSearching;
import network.Layer;
import network.Link;
import network.Node;
import network.NodePair;
import network.VirtualLink;
import subgraph.LinearRoute;

public class ipProGrooming {
	String OutFileName =Mymain.OutFileName;
	public boolean ipprotectiongrooming(Layer iplayer, Layer oplayer, NodePair nodepair, LinearRoute route,
			int numOfTransponder, boolean flag, ArrayList<WorkandProtectRoute> wprlist) {// flag=true��ʾ����IP�㽨���Ĺ���·��
		// flag=flase��ʾ��㽨���Ĺ���·��
		RouteSearching Dijkstra = new RouteSearching();
		boolean ipproflag = false;
		Test t = new Test();
//		String OutFileName = "F:\\programFile\\RegwithProandTrgro\\NSFNET.dat";
		file_out_put file_io=new file_out_put();
		
		file_io.filewrite2(OutFileName,"  ");
		file_io.filewrite2(OutFileName,"��ʼ����·��" );
		ArrayList<VirtualLink> DelLinkList = new ArrayList<VirtualLink>();
		ArrayList<VirtualLink> SumDelLinkList = new ArrayList<VirtualLink>();
		ArrayList<Link> DelIPLinkList = new ArrayList<Link>();
		Node srcnode = nodepair.getSrcNode();
		Node desnode = nodepair.getDesNode();
//		System.out.println("IP�����·������ " + iplayer.getLinklist().size());
//		file_io.filewrite2(OutFileName,"IP�����·������ " + iplayer.getLinklist().size());
		
		//test
//		for(WorkandProtectRoute wprpro:wprlist){
//			System.out.println(wprpro.getdemand().getName());
//			for(VirtualLink vprolink:wprpro.getprovirtuallinklist()){
//				System.out.println(vprolink.getSrcnode()+"  "+vprolink.getDesnode());
//			}
//		}
		
//		HashMap<String, Link> testlinklist = iplayer.getLinklist();
//		Iterator<String> testlinkitor = testlinklist.keySet().iterator();
//		while (testlinkitor.hasNext()) {// ��һ���� ��һ��
//			Link link = (Link) (testlinklist.get(testlinkitor.next()));// IP���ϵ���·
//			System.out.println(link.getName());
//		}

		HashMap<String, Link> linklist = iplayer.getLinklist();
		Iterator<String> linkitor = linklist.keySet().iterator();
		while (linkitor.hasNext()) {// ��һ���� ��һ��
			Link link = (Link) (linklist.get(linkitor.next()));// IP���ϵ���·
			for (VirtualLink Vlink : link.getVirtualLinkList()) { // IP������·��Ӧ��������·
				if (Vlink.getNature() == 0) {// 0Ϊ����
//					System.out.println("��Ϊ���Բ�ͬɾ������·��" + link.getName());
					DelLinkList.add(Vlink);
					continue;
				}
				if (Vlink.getRestcapacity() < nodepair.getTrafficdemand()) {// ɾȥ������������·
					DelLinkList.add(Vlink);
//					System.out.println("��Ϊ��������ɾ������·��" + link.getName());
					continue;
				}
			}
			
			WorkandProtectRoute nowdemand = new WorkandProtectRoute(null);

			for (WorkandProtectRoute wpr : wprlist) {
				if (wpr.getdemand().equals(nodepair)) {
					nowdemand = wpr;// �ȴ����н�����ҵ�����ҳ�����ҵ��
				}
			}
			// ɾ���뵱ǰҵ��Ĺ���·����������·�ཻ�����Ᵽ��·��
			for (VirtualLink Vlink : link.getVirtualLinkList()) {
				for (Link phylink : Vlink.getPhysicallink()) {
					if (nowdemand.getworklinklist().contains(phylink)) {
						DelLinkList.add(Vlink);
//						System.out.println("�뵱ǰҵ���ཻ��ɾ����������· " + link.getName());
					}
				}
			}
		}
		
		WorkandProtectRoute nowdemand = new WorkandProtectRoute(null);
		// ɾ���뵱ǰҵ��Ĺ�����·�ཻ��ҵ��ı���·����Ӧ��������·
		for (WorkandProtectRoute wpr : wprlist) {
			if (wpr.getdemand().equals(nodepair)) {
				nowdemand = wpr;// �ȴ����н�����ҵ�����ҳ�����ҵ��
			}
		}
		for (WorkandProtectRoute wpr : wprlist) {
			if (wpr.getdemand().equals(nodepair)) {
				continue;
			}
		
			int cross = t.linklistcompare(wpr.getworklinklist(), nowdemand.getworklinklist());// ��������ҵ���ཻɾȥ֮ǰ����ҵ��ı�����·
			if (cross == 1) {
//				System.out.println("��ǰҵ�� ");
//				for (Link link1 : nowdemand.getworklinklist()) {
//					System.out.print(link1.getName() + "  ");
//				}
//				System.out.println();
//				System.out.println("֮ǰ������ҵ�� " + wpr.getdemand().getName());
//
//				for (Link link1 : wpr.getworklinklist()) {
//					System.out.print(link1.getName() + "  ");
//				}
//				System.out.println();
				
				for (VirtualLink vlink : wpr.getprovirtuallinklist()) {
					if (!DelLinkList.contains(vlink)) {
//						System.out.println(
//								"ɾ���뵱ǰҵ��Ĺ�����·�ཻ��ҵ��ı���·����Ӧ��������·" + vlink.getSrcnode() + "  " + vlink.getDesnode());
						DelLinkList.add(vlink);
					}
				}
			}
		}
		for (VirtualLink dellink2 : DelLinkList) {
			if (!SumDelLinkList.contains(dellink2))
				SumDelLinkList.add(dellink2);
		}

		for (VirtualLink dellink2 : SumDelLinkList) {
			HashMap<String, Link> list = iplayer.getLinklist();
			Iterator<String> itor = list.keySet().iterator();
			while (itor.hasNext()) {// ��һ���� ��һ��
				Link link = (Link) (list.get(itor.next()));// IP���ϵ���·
//				System.out.println("������·��"+dellink2.getSrcnode()+"-"+dellink2.getDesnode());
//				System.out.println("IP����·��"+link.getName());
				if (link.getNodeA().getName().equals(dellink2.getSrcnode())
						&& link.getNodeB().getName().equals(dellink2.getDesnode())) {// �ҵ�������·��Ӧ��IP����·
					link.getVirtualLinkList().remove(dellink2);// ɾ����Ӧ��������·
				break;
				}
			}
		}
		HashMap<String, Link> list = iplayer.getLinklist();
		Iterator<String> itor = list.keySet().iterator();
		while (itor.hasNext()) {
			Link link = (Link) (list.get(itor.next()));
			if (link.getVirtualLinkList().size() == 0) {
				DelIPLinkList.add(link);
			}
		}
		// �ҳ�������Ҫɾ����������·

		for (Link link : DelIPLinkList) {
//			System.out.println("ɾ����IP����·Ϊ��" + link.getName());
			iplayer.removeLink(link.getName());
		}
		// ����Ϊ�ж�ip���е���·��Щ��Ҫɾ��

		// ɾ��IP����������ʣ��������·
		ArrayList<VirtualLink> DelhighcapVlink = new ArrayList<>();
		HashMap<String, Link> Dijlinklist = iplayer.getLinklist();
		Iterator<String> Dijlinkitor = Dijlinklist.keySet().iterator();
		while (Dijlinkitor.hasNext()) {
			double minrescapacity = 10000;
			Link Dijlink = (Link) (Dijlinklist.get(Dijlinkitor.next()));
			for (VirtualLink vlink : Dijlink.getVirtualLinkList()) {
				// System.out.println(vlink.getSrcnode()+"
				// "+vlink.getDesnode());
				if (vlink.getRestcapacity() < minrescapacity) {
					minrescapacity = vlink.getRestcapacity();
				}
			} // �ҳ�һ��IP��·������������·��ʣ���������ٵ�����

			for (VirtualLink vlink : Dijlink.getVirtualLinkList()) {
				if (vlink.getRestcapacity() > minrescapacity) {
					DelhighcapVlink.add(vlink);
				}
			}
			for (VirtualLink vlink : DelhighcapVlink) {
				Dijlink.getVirtualLinkList().remove(vlink);
			} // ������ʣ������������Сʣ����������·ȫ�����뼯�� ��ɾ��
		}
		LinearRoute newRoute = new LinearRoute(null, 0, null);
		Dijkstra.Dijkstras(srcnode, desnode, iplayer, newRoute, null);// ��iplayer������Ѱ��̱���·��

		for (Link addlink : DelIPLinkList) {// �ָ�iplayer
			iplayer.addLink(addlink);
		}
		DelIPLinkList.clear();

		if (newRoute.getNodelist().size() != 0) {
			ipproflag = true;
			//System.out.print("**************����·����IP���ҵ�·��  ");
			file_io.filewrite_without(OutFileName,"**************����·����IP���ҵ�·��  ");
			newRoute.OutputRoute_node(newRoute);
			ArrayList<Link> totallink = new ArrayList<>();
			file_io.filewrite2(OutFileName,"IP·���ϵ���·��" );
			for (int c = 0; c < newRoute.getLinklist().size(); c++) {
				Link link = newRoute.getLinklist().get(c); // �ҵ���·�������link
				//System.out.println("IP·���ϵ���·��" + link.getName());
				file_io.filewrite_without(OutFileName, link.getName()+"  ");
				/*
				 * ���·�ɳɹ� ����Ҫ�ҵ�IP���ϵ�link��Ӧ��������· �ı�������
				 */

				HashMap<String, Link> linklist2 = iplayer.getLinklist();
				Iterator<String> linkitor2 = linklist2.keySet().iterator();
				while (linkitor2.hasNext()) {
					Link link1 = (Link) (linklist2.get(linkitor2.next()));// IPlayer�����link
					if (link1.getNodeA().getName().equals(link.getNodeA().getName())
							&& link1.getNodeB().getName().equals(link.getNodeB().getName())) {
				
						VirtualLink Vlink = link1.getVirtualLinkList().get(0);
						// System.out.println(Vlink.getSrcnode() + " " +Vlink.getDesnode() + " "+ Vlink.getRestcapacity());
						Vlink.setUsedcapacity(Vlink.getUsedcapacity() + nodepair.getTrafficdemand());
						Vlink.setRestcapacity(Vlink.getFullcapacity() - Vlink.getUsedcapacity());
						// System.out.println(Vlink.getRestcapacity());
						for (Link worklink : Vlink.getPhysicallink()) {// ��ҵ��������·�϶�Ӧ��������·ȫ������totallink��
							totallink.add(worklink);
						}
					}
				}
			}
			for (WorkandProtectRoute wpr : wprlist) {
				if (wpr.getdemand().equals(nodepair)) {
					wpr.setprolinklist(totallink);
				}
			}

		}
		// �ָ���·�϶�Ӧ��������·
		for (VirtualLink link : SumDelLinkList) {
			// System.out.println(link.getSrcnode()+"-"+link.getDesnode());
			HashMap<String, Link> linklist2 = iplayer.getLinklist();
			Iterator<String> linkitor2 = linklist2.keySet().iterator();
			while (linkitor2.hasNext()) {
				Link link1 = (Link) (linklist2.get(linkitor2.next()));// IPlayer�����link
				if (link1.getNodeA().getName().equals(link.getSrcnode())
						&& link1.getNodeB().getName().equals(link.getDesnode())) {
					link1.getVirtualLinkList().add(link);
				}
			}
		}
		SumDelLinkList.clear();

		for (VirtualLink link : DelhighcapVlink) {// �ָ���Ϊ������ʣɾ����������·
//			System.out.println("ɾ��������ʣ��������·�� " + link.getRestcapacity());
			// System.out.println(link.getSrcnode()+"-"+link.getDesnode());
			HashMap<String, Link> linklist2 = iplayer.getLinklist();
			Iterator<String> linkitor2 = linklist2.keySet().iterator();
			while (linkitor2.hasNext()) {
				Link link1 = (Link) (linklist2.get(linkitor2.next()));// IPlayer�����link
				if (link1.getNodeA().getName().equals(link.getSrcnode())
						&& link1.getNodeB().getName().equals(link.getDesnode())) {
					link1.getVirtualLinkList().add(link);
				}
			}
		}
		DelhighcapVlink.clear();
		if(ipproflag){
			file_io.filewrite2(OutFileName,"����·����IP��ɹ�·��");
		}
		else{
			file_io.filewrite2(OutFileName,"����·����IP��·��ʧ��");
		}
		return ipproflag;
	}

}
