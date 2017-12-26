package MainFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import demand.Request;
import general.file_out_put;
import graphalgorithms.RouteSearching;
import network.Layer;
import network.Link;
import network.Network;
import network.Node;
import network.NodePair;
import network.VirtualLink;
import subgraph.LinearRoute;

public class IPWorkingGrooming {
	String OutFileName =Mymain.OutFileName;
	public boolean ipWorkingGrooming(NodePair nodepair, Layer iplayer, Layer oplayer,int numOfTransponder,LinearRoute newRoute
			, ArrayList<WorkandProtectRoute> wprlist,ArrayList<FlowUseOnLink> FlowUseList) {
		boolean routeFlag=false;
		file_out_put file_io=new file_out_put();
		RouteSearching Dijkstra = new RouteSearching();
		ArrayList<VirtualLink> DelVirtualLinklist = new ArrayList<VirtualLink>();
		ArrayList<VirtualLink> SumDelVirtualLinklist = new ArrayList<VirtualLink>();
		ArrayList<Link> DelIPLinklist = new ArrayList<Link>();
		
		ArrayList<VirtualLink> VirtualLinklist = new ArrayList<VirtualLink>();
		// ����list����Ľڵ��
			Node srcnode = nodepair.getSrcNode();
			Node desnode = nodepair.getDesNode();
	
			//test
//			System.out.println("IP���ϵ���·����Ϊ��" +  iplayer.getLinklist().size());
//			file_io.filewrite2(OutFileName,"֮ǰIP���ϵ���·����Ϊ��" +  iplayer.getLinklist().size());
//			HashMap<String, Link> linklisttest = iplayer.getLinklist();
//			Iterator<String> linkitortest = linklisttest.keySet().iterator();
//			while (linkitortest.hasNext()) {
//				Link Mlink = (Link) (linklisttest.get(linkitortest.next()));
//				file_io.filewrite2(OutFileName,"IP���ϵ���·Ϊ��" +  Mlink.getName());
//				VirtualLinklist = Mlink.getVirtualLinkList();//ȡ��IP���ϵ���·��Ӧ��������· �½�һ��listʹ�䱾���������·���ı�						
//				for (VirtualLink Vlink : VirtualLinklist) { // ȡ��link�϶�Ӧ��virtua
//				file_io.filewrite2(OutFileName,"��IP��·�ϵ�������·Ϊ��" +  Vlink.getSrcnode()+"-"+Vlink.getDesnode()
//				+"   ����Ϊ��"+Vlink.getNature()+ "  ʣ�������Ϊ"+Vlink.getRestcapacity());
//				
//				}
//			}
			
			HashMap<String, Link> linklist = iplayer.getLinklist();
			Iterator<String> linkitor = linklist.keySet().iterator();
			while (linkitor.hasNext()) {
				Link Mlink = (Link) (linklist.get(linkitor.next()));
				VirtualLinklist = Mlink.getVirtualLinkList();//ȡ��IP���ϵ���·��Ӧ��������· �½�һ��listʹ�䱾���������·���ı�						
				for (VirtualLink Vlink : VirtualLinklist) { // ȡ��link�϶�Ӧ��virtual
															// link
//					System.out.println("IP������·"+Mlink.getName()+"    ��Ӧ��������·��" + Vlink.getSrcnode() + "-" + Vlink.getDesnode()+ "   nature=" + Vlink.getNature()+
//							"    ��������·�϶�Ӧ��ʣ������Ϊ��"+Vlink.getRestcapacity());
					if (Vlink.getNature() == 1) {// ������0 ������1
						DelVirtualLinklist.add(Vlink);
						continue;
					}
					if (Vlink.getRestcapacity() < nodepair.getTrafficdemand()) {
						DelVirtualLinklist.add(Vlink);
						continue;
					}
				}
				for (VirtualLink nowlink : DelVirtualLinklist) { //  ͳ������ɾ����������·
					if (!SumDelVirtualLinklist.contains(nowlink)) {
						SumDelVirtualLinklist.add(nowlink);
					}
				}
				
				for (VirtualLink nowlink : SumDelVirtualLinklist) {
//					System.out.println(Mlink.getName()+" ��ɾ����������·Ϊ��"+ nowlink.getSrcnode()+"  "+nowlink.getDesnode());
					Mlink.getVirtualLinkList().remove(nowlink);
				}
				
				DelVirtualLinklist.clear();
				if (Mlink.getVirtualLinkList().size() == 0)
					DelIPLinklist.add(Mlink);
			}
			
			for (Link link : DelIPLinklist) {
//				System.out.println("ɾ����IP����·Ϊ��"+link.getName());
				iplayer.removeLink(link.getName());
			}
			//����Ϊ��һ����===ɾ��IP����������������·
			
//         ��Ϊһ��IP��·���ܻ��Ӧ����������· ��������ʹ��ʣ���������ٵ�·�� һ��IP��·�Ͻ�����ʣ���������ٵ���· 
//			�����������·��ɾ��	
		   ArrayList<VirtualLink> DelhighcapVlink=new ArrayList<>();
			HashMap<String, Link> Dijlinklist = iplayer.getLinklist();
			Iterator<String> Dijlinkitor = Dijlinklist.keySet().iterator();
			while (Dijlinkitor.hasNext()) {
				double minrescapacity=10000;
				Link Dijlink = (Link) (Dijlinklist.get(Dijlinkitor.next()));
				for(VirtualLink vlink:Dijlink.getVirtualLinkList()){
//					System.out.println(vlink.getSrcnode()+"   "+vlink.getDesnode());
					if(vlink.getRestcapacity()<minrescapacity){
						minrescapacity=vlink.getRestcapacity();
					}
				}//�ҳ�һ��IP��·������������·��ʣ���������ٵ�����
				
				for(VirtualLink vlink: Dijlink.getVirtualLinkList()){
					if(vlink.getRestcapacity()>minrescapacity){
						DelhighcapVlink.add(vlink);
					}
				}
				for(VirtualLink vlink:DelhighcapVlink){
					Dijlink.getVirtualLinkList().remove(vlink);
				}//������ʣ������������Сʣ����������·ȫ�����뼯�� ��ɾ��
			}
			
			WorkandProtectRoute wpr=new WorkandProtectRoute(nodepair);
			Request re=new Request(nodepair);
			ArrayList<Link> totallink=new ArrayList<>();
		
			
			Dijkstra.Dijkstras(srcnode, desnode, iplayer, newRoute, null);

			// �ָ�iplayer����ɾ����link
			for (Link nowlink : DelIPLinklist) {
				iplayer.addLink(nowlink);
			}
			DelIPLinklist.clear();

			// ����dijkstra��������· ���Ҹı���Щ��·�ϵ�����
			if (newRoute.getLinklist().size() != 0) {// ����·��·�ɳɹ�
//				System.out.print("part2==��IP���ҵ�·��:");
//				file_io.filewrite_without(OutFileName,"part2==����·����IP���ҵ�·��:");
				newRoute.OutputRoute_node(newRoute);
				routeFlag=true;
				file_io.filewrite2(OutFileName,"ip����·����·��");
				for (int c = 0; c < newRoute.getLinklist().size(); c++) {
					Link link = newRoute.getLinklist().get(c); // �ҵ���·�������link
					file_io.filewrite_without(OutFileName,link.getName()+"  ");
					
					HashMap<String, Link> linklist2 = iplayer.getLinklist();
					Iterator<String> linkitor2 = linklist2.keySet().iterator();
					while (linkitor2.hasNext()) {
						Link link1 = (Link) (linklist2.get(linkitor2.next()));// IPlayer�����link
						if(link1.getNodeA().getName().equals(link.getNodeA().getName())&&link1.getNodeB().getName().equals(link.getNodeB().getName())){			
						//��IP���ҵ���·�� �޸�������·������ֵ��ʱÿ��IP��·��ֻ��һ��������·
							FlowUseOnLink fuo=new FlowUseOnLink(null, 0);
								VirtualLink Vlink=link1.getVirtualLinkList().get(0);
								Vlink.setUsedcapacity(Vlink.getUsedcapacity() + nodepair.getTrafficdemand());
								Vlink.setRestcapacity(Vlink.getFullcapacity() - Vlink.getUsedcapacity());
								fuo.setvlink(Vlink); fuo.setFlowUseOnLink(nodepair.getTrafficdemand());
								FlowUseList.add(fuo);
//								System.out.println("IP����· "+ link1.getName()+"��������·ʣ������Ϊ��"+ Vlink.getRestcapacity());
								for(Link worklink:Vlink.getPhysicallink()){//��ҵ��������·�϶�Ӧ��������·ȫ������totallink��
									totallink.add(worklink);
								}
						}
						}
					}
				wpr.setrequest(re);
				wpr.setworklinklist(totallink);
				wprlist.add(wpr);
			}
				//�ָ���·�϶�Ӧ��������·
				for(VirtualLink link:SumDelVirtualLinklist){//�ָ���Ϊ��������ɾ����������·
//					System.out.println("ɾ��������������Բ��Ե�������·�� "+link.getRestcapacity());
					HashMap<String, Link> linklist2 = iplayer.getLinklist();
					Iterator<String> linkitor2 = linklist2.keySet().iterator();
					while (linkitor2.hasNext()) {
						Link link1 = (Link) (linklist2.get(linkitor2.next()));// IPlayer�����link
						if(link1.getNodeA().getName().equals(link.getSrcnode())&&link1.getNodeB().getName().equals(link.getDesnode())){		
							link1.getVirtualLinkList().add(link);
						}
					}
				}
				SumDelVirtualLinklist.clear();
				
				for(VirtualLink link:DelhighcapVlink){//�ָ���Ϊ������ʣɾ����������·
//					System.out.println("ɾ��������ʣ��������·�� "+link.getRestcapacity());
//					System.out.println(link.getSrcnode()+"-"+link.getDesnode());
					HashMap<String, Link> linklist2 = iplayer.getLinklist();
					Iterator<String> linkitor2 = linklist2.keySet().iterator();
					while (linkitor2.hasNext()) {
						Link link1 = (Link) (linklist2.get(linkitor2.next()));// IPlayer�����link
						if(link1.getNodeA().getName().equals(link.getSrcnode())&&link1.getNodeB().getName().equals(link.getDesnode())){		
							link1.getVirtualLinkList().add(link);
						}
					}
				}
				DelhighcapVlink.clear();
			
			
			if(routeFlag) {
				//System.out.println("����·����IP��ɹ�·��");
				file_io.filewrite2(OutFileName,"����·����IP��·�ɳɹ�");
			}
			if(!routeFlag){ 
				//System.out.println("����·��IP��·��ʧ��");
				file_io.filewrite2(OutFileName,"����·��IP��·��ʧ��");
			}
		return routeFlag;
	}
}