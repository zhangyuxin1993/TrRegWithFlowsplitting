package MainFunction;

import java.io.IOException;
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
	public boolean ipWorkingGrooming(NodePair nodepair, Layer iplayer, Layer oplayer,int numOfTransponder, ArrayList<WorkandProtectRoute> wprlist,ArrayList<FlowUseOnLink> FlowUseList) throws IOException {
//		boolean routeFlag=false;
		file_out_put file_io=new file_out_put();
		ArrayList<VirtualLink> DelVirtualLinklist = new ArrayList<VirtualLink>();
//		ArrayList<VirtualLink> SumDelVirtualLinklist = new ArrayList<VirtualLink>();
		ArrayList<Link> DelIPLinklist = new ArrayList<Link>();
		
		ArrayList<VirtualLink> VirtualLinklist = new ArrayList<VirtualLink>();

		//test
//			file_io.filewrite2(OutFileName,"IP���ϵ���·����Ϊ��" +  iplayer.getLinklist().size());
//			HashMap<String, Link> linklisttest = iplayer.getLinklist();
//			Iterator<String> linkitortest = linklisttest.keySet().iterator();
//			while (linkitortest.hasNext()) {
//				Link Mlink = (Link) (linklisttest.get(linkitortest.next()));
//				file_io.filewrite2(OutFileName,"IP���ϵ���·Ϊ��" +  Mlink.getName());
//				VirtualLinklist = Mlink.getVirtualLinkList();//ȡ��IP���ϵ���·��Ӧ��������· �½�һ��listʹ�䱾���������·���ı�						
//				for (VirtualLink Vlink : VirtualLinklist) { // ȡ��link�϶�Ӧ��virtua
//				file_io.filewrite2(OutFileName,"��IP��·�ϵ�������·Ϊ��" +  Vlink.getSrcnode()+"-"+Vlink.getDesnode()
//				+"   ����Ϊ��"+Vlink.getNature()+ "  ʣ�������Ϊ"+Vlink.getRestcapacity());
//				}
//			}
			
//			ɾ��ʣ������Ϊ0��������·  ����Ҫ�����ָ� ֻ������ɾ������·��IP ���⣩����Ҫ�ָ� ��������Ҫ�ָ�
			ArrayList<VirtualLink> DelNoFlowVlink= new ArrayList<>();
			ArrayList<Link> DelIPLinklist1=new ArrayList<>();
			HashMap<String, Link> linklist = iplayer.getLinklist();
			Iterator<String> linkitor = linklist.keySet().iterator();
			while (linkitor.hasNext()) {
				Link IPlink = (Link) (linklist.get(linkitor.next()));
				DelNoFlowVlink.clear();
				for(VirtualLink vlink: IPlink.getVirtualLinkList()){
					if(vlink.getRestcapacity()==0){
						DelNoFlowVlink.add(vlink);
					}
				}
				for(VirtualLink delvlink: DelNoFlowVlink){
					IPlink.getVirtualLinkList().remove(delvlink);
				}
				if (IPlink.getVirtualLinkList().size() == 0)
					DelIPLinklist1.add(IPlink);
			}
			for (Link link : DelIPLinklist1) {
				file_io.filewrite2(OutFileName,"ɾ������Ҫ�ָ���IP����·Ϊ��"+link.getName());
				iplayer.removeLink(link.getName()); //�����IP��·ɾ��֮��Ҳ����Ҫ�ָ�����IP ��·��û�п��õ�������·��
			}
			
			HashMap<String, Link> linklist2 = iplayer.getLinklist();
			Iterator<String> linkitor2 = linklist2.keySet().iterator();
			while (linkitor2.hasNext()) {
				Link Mlink = (Link) (linklist2.get(linkitor2.next()));
				VirtualLinklist = Mlink.getVirtualLinkList();//ȡ��IP���ϵ���·��Ӧ��������· �½�һ��listʹ�䱾���������·���ı�						
				for (VirtualLink Vlink : VirtualLinklist) { // ȡ��link�϶�Ӧ��virtual link
//					System.out.println("IP������·"+Mlink.getName()+"    ��Ӧ��������·��" + Vlink.getSrcnode() + "-" + Vlink.getDesnode()+ "   nature=" + Vlink.getNature()+
//							"    ��������·�϶�Ӧ��ʣ������Ϊ��"+Vlink.getRestcapacity());
					if (Vlink.getNature() == 1) {// ������0 ������1
						DelVirtualLinklist.add(Vlink);
					}
				}
				
				for (VirtualLink nowlink : DelVirtualLinklist) {
//					System.out.println(Mlink.getName()+" ��ɾ����������·Ϊ��"+ nowlink.getSrcnode()+"  "+nowlink.getDesnode());
					Mlink.getVirtualLinkList().remove(nowlink);
				}
				if (Mlink.getVirtualLinkList().size() == 0)
					DelIPLinklist.add(Mlink);
			}
			
			for (Link link : DelIPLinklist) {
//				System.out.println("ɾ����IP����·Ϊ��"+link.getName());
				iplayer.removeLink(link.getName());
			}
			//����Ϊ��һ����===ɾ��IP��������Ϊ��������· ������С����·��ɾ��!
			
			
			WorkandProtectRoute wpr=new WorkandProtectRoute(nodepair);
			Request re=new Request(nodepair);
			ArrayList<Link> totallink=new ArrayList<>();
		
			FlowSplitting fs=new FlowSplitting();
			boolean  routeFlag=fs.flowsplitting(iplayer, nodepair, FlowUseList, totallink);

			// �ָ�iplayer����ɾ����link
			for (Link nowlink : DelIPLinklist) {
				iplayer.addLink(nowlink);
			}
			DelIPLinklist.clear();

				//�ָ���·�϶�Ӧ��������·
				for(VirtualLink link:DelVirtualLinklist){//�ָ���Ϊ����Ϊ����ɾ����������·
//					System.out.println("ɾ������Ϊ������������·�� "+link.getRestcapacity());
					HashMap<String, Link> linklist3 = iplayer.getLinklist();
					Iterator<String> linkitor3 = linklist3.keySet().iterator();
					while (linkitor3.hasNext()) {
						Link link1 = (Link) (linklist3.get(linkitor3.next()));// IPlayer�����link
						if(link1.getNodeA().getName().equals(link.getSrcnode())&&link1.getNodeB().getName().equals(link.getDesnode())){		
							link1.getVirtualLinkList().add(link);
						}
					}
				}
				DelVirtualLinklist.clear();
				
			if(routeFlag) {
				wpr.setrequest(re);
				wpr.setworklinklist(totallink);
				wprlist.add(wpr);
				file_io.filewrite2(OutFileName,"����·����IP��·�ɳɹ�");
			}
			if(!routeFlag){ 
				file_io.filewrite2(OutFileName,"����·��IP��·��ʧ��");
			}
		return routeFlag;
	}
}