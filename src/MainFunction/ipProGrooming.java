package MainFunction;

import java.io.IOException;
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
	public boolean ipprotectiongrooming(Layer iplayer, Layer oplayer, NodePair nodepair,int numOfTransponder, boolean flag, ArrayList<WorkandProtectRoute> wprlist) throws IOException {// flag=true��ʾ����IP�㽨���Ĺ���·��
		Test t = new Test();
		file_out_put file_io=new file_out_put();
		
		file_io.filewrite2(OutFileName,"  ");
		file_io.filewrite2(OutFileName,"��ʼ����·��" );
		ArrayList<VirtualLink> DelLinkList = new ArrayList<VirtualLink>();
		ArrayList<VirtualLink> SumDelLinkList = new ArrayList<VirtualLink>();
		ArrayList<Link> DelIPLinkList = new ArrayList<Link>();
		ArrayList<Link> totallink = new ArrayList<>();
		
//		HashMap<String, Link> linklisttest = iplayer.getLinklist();
//		ArrayList<VirtualLink> VirtualLinklist = new ArrayList<VirtualLink>();
//		Iterator<String> linkitortest = linklisttest.keySet().iterator();
//		while (linkitortest.hasNext()) {
//			Link Mlink = (Link) (linklisttest.get(linkitortest.next()));
//			file_io.filewrite2(OutFileName,"IP���ϵ���·Ϊ��" +  Mlink.getName());
//			VirtualLinklist = Mlink.getVirtualLinkList();//ȡ��IP���ϵ���·��Ӧ��������· �½�һ��listʹ�䱾���������·���ı�						
//			for (VirtualLink Vlink : VirtualLinklist) { // ȡ��link�϶�Ӧ��virtua
//			file_io.filewrite2(OutFileName,"��IP��·�ϵ�������·Ϊ��" +  Vlink.getSrcnode()+"-"+Vlink.getDesnode()
//			+"   ����Ϊ��"+Vlink.getNature()+ "  ʣ�������Ϊ"+Vlink.getRestcapacity());
//			
//			}
//		}

		//test
//		for(WorkandProtectRoute wprpro:wprlist){
//			System.out.println(wprpro.getdemand().getName());
//			for(VirtualLink vprolink:wprpro.getprovirtuallinklist()){
//				System.out.println(vprolink.getSrcnode()+"  "+vprolink.getDesnode());
//			}
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
				if (Vlink.getRestcapacity() ==0) {// ɾȥ����Ϊ0����·
					DelLinkList.add(Vlink);
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
				for (VirtualLink vlink : wpr.getprovirtuallinklist()) {
					if (!DelLinkList.contains(vlink)) {
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
	
		HashMap<String, Link> linklisttest = iplayer.getLinklist();
		ArrayList<VirtualLink> VirtualLinklist = new ArrayList<VirtualLink>();
		Iterator<String> linkitortest = linklisttest.keySet().iterator();
		while (linkitortest.hasNext()) {
			Link Mlink = (Link) (linklisttest.get(linkitortest.next()));
//			file_io.filewrite2(OutFileName,"IP���ϵ���·Ϊ��" +  Mlink.getName());
			VirtualLinklist = Mlink.getVirtualLinkList();//ȡ��IP���ϵ���·��Ӧ��������· �½�һ��listʹ�䱾���������·���ı�						
			for (VirtualLink Vlink : VirtualLinklist) { // ȡ��link�϶�Ӧ��virtua
//			file_io.filewrite2(OutFileName,"��IP��·�ϵ�������·Ϊ��" +  Vlink.getSrcnode()+"-"+Vlink.getDesnode()
//			+"   ����Ϊ��"+Vlink.getNature()+ "  ʣ�������Ϊ"+Vlink.getRestcapacity());
			
			}
		}
		
		FlowSplitting fs=new FlowSplitting();
		ArrayList<FlowUseOnLink> FlowUseList=new ArrayList<>();
		boolean  ipproflag=fs.flowsplitting(iplayer, nodepair, FlowUseList	, totallink);

		for (Link addlink : DelIPLinkList) {// �ָ�iplayer
			iplayer.addLink(addlink);
		}
		DelIPLinkList.clear();

		
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

//		for (VirtualLink link : DelhighcapVlink) {// �ָ���Ϊ������ʣɾ����������·
////			System.out.println("ɾ��������ʣ��������·�� " + link.getRestcapacity());
//			// System.out.println(link.getSrcnode()+"-"+link.getDesnode());
//			HashMap<String, Link> linklist2 = iplayer.getLinklist();
//			Iterator<String> linkitor2 = linklist2.keySet().iterator();
//			while (linkitor2.hasNext()) {
//				Link link1 = (Link) (linklist2.get(linkitor2.next()));// IPlayer�����link
//				if (link1.getNodeA().getName().equals(link.getSrcnode())
//						&& link1.getNodeB().getName().equals(link.getDesnode())) {
//					link1.getVirtualLinkList().add(link);
//				}
//			}
//		}
//		DelhighcapVlink.clear();
		
		if(ipproflag){
			for (WorkandProtectRoute wpr : wprlist) {
				if (wpr.getdemand().equals(nodepair)) {
					wpr.setprolinklist(totallink);
				}
			}
			file_io.filewrite2(OutFileName,"����·����IP��ɹ�·��");
		}
		else{
			file_io.filewrite2(OutFileName,"����·����IP��·��ʧ��");
		}
		
		return ipproflag;
	}

}
