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
	public boolean ipprotectiongrooming(Layer iplayer, Layer oplayer, NodePair nodepair,int numOfTransponder, boolean flag, ArrayList<WorkandProtectRoute> wprlist) throws IOException {// flag=true表示保护IP层建立的工作路径
		Test t = new Test();
		file_out_put file_io=new file_out_put();
		
		file_io.filewrite2(OutFileName,"  ");
		file_io.filewrite2(OutFileName,"开始保护路由" );
		ArrayList<VirtualLink> DelLinkList = new ArrayList<VirtualLink>();
		ArrayList<VirtualLink> SumDelLinkList = new ArrayList<VirtualLink>();
		ArrayList<Link> DelIPLinkList = new ArrayList<Link>();
		ArrayList<Link> totallink = new ArrayList<>();
		
//		HashMap<String, Link> linklisttest = iplayer.getLinklist();
//		ArrayList<VirtualLink> VirtualLinklist = new ArrayList<VirtualLink>();
//		Iterator<String> linkitortest = linklisttest.keySet().iterator();
//		while (linkitortest.hasNext()) {
//			Link Mlink = (Link) (linklisttest.get(linkitortest.next()));
//			file_io.filewrite2(OutFileName,"IP层上的链路为：" +  Mlink.getName());
//			VirtualLinklist = Mlink.getVirtualLinkList();//取出IP层上的链路对应的虚拟链路 新建一个list使其本身的虚拟链路不改变						
//			for (VirtualLink Vlink : VirtualLinklist) { // 取出link上对应的virtua
//			file_io.filewrite2(OutFileName,"该IP链路上的虚拟链路为：" +  Vlink.getSrcnode()+"-"+Vlink.getDesnode()
//			+"   性质为："+Vlink.getNature()+ "  剩余的流量为"+Vlink.getRestcapacity());
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
		while (linkitor.hasNext()) {// 第一部分 第一步
			Link link = (Link) (linklist.get(linkitor.next()));// IP层上的链路
			for (VirtualLink Vlink : link.getVirtualLinkList()) { // IP层上链路对应的虚拟链路
				if (Vlink.getNature() == 0) {// 0为工作
//					System.out.println("因为属性不同删除的链路：" + link.getName());
					DelLinkList.add(Vlink);
					continue;
				}
				if (Vlink.getRestcapacity() ==0) {// 删去流量为0的链路
					DelLinkList.add(Vlink);
					continue;
				}
			}
			
			WorkandProtectRoute nowdemand = new WorkandProtectRoute(null);

			for (WorkandProtectRoute wpr : wprlist) {
				if (wpr.getdemand().equals(nodepair)) {
					nowdemand = wpr;// 先从所有建立的业务中找出本次业务
				}
			}
			// 删除与当前业务的工作路径的物理链路相交的虚拟保护路径
			for (VirtualLink Vlink : link.getVirtualLinkList()) {
				for (Link phylink : Vlink.getPhysicallink()) {
					if (nowdemand.getworklinklist().contains(phylink)) {
						DelLinkList.add(Vlink);
//						System.out.println("与当前业务相交而删除的虚拟链路 " + link.getName());
					}
				}
			}
		}
		
		WorkandProtectRoute nowdemand = new WorkandProtectRoute(null);
		// 删除与当前业务的工作链路相交的业务的保护路径对应的虚拟链路
		for (WorkandProtectRoute wpr : wprlist) {
			if (wpr.getdemand().equals(nodepair)) {
				nowdemand = wpr;// 先从所有建立的业务中找出本次业务
			}
		}
		for (WorkandProtectRoute wpr : wprlist) {
			if (wpr.getdemand().equals(nodepair)) {
				continue;
			}
		
			int cross = t.linklistcompare(wpr.getworklinklist(), nowdemand.getworklinklist());// 两个工作业务相交删去之前建立业务的保护链路
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
			while (itor.hasNext()) {// 第一部分 第一步
				Link link = (Link) (list.get(itor.next()));// IP层上的链路
//				System.out.println("虚拟链路："+dellink2.getSrcnode()+"-"+dellink2.getDesnode());
//				System.out.println("IP层链路："+link.getName());
				if (link.getNodeA().getName().equals(dellink2.getSrcnode())
						&& link.getNodeB().getName().equals(dellink2.getDesnode())) {// 找到虚拟链路对应的IP层链路
					link.getVirtualLinkList().remove(dellink2);// 删除对应的虚拟链路
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
		// 找出所有需要删除的虚拟链路

		for (Link link : DelIPLinkList) {
//			System.out.println("删除的IP层链路为：" + link.getName());
			iplayer.removeLink(link.getName());
		}
		// 以上为判断ip层中的链路那些需要删除
	
		HashMap<String, Link> linklisttest = iplayer.getLinklist();
		ArrayList<VirtualLink> VirtualLinklist = new ArrayList<VirtualLink>();
		Iterator<String> linkitortest = linklisttest.keySet().iterator();
		while (linkitortest.hasNext()) {
			Link Mlink = (Link) (linklisttest.get(linkitortest.next()));
//			file_io.filewrite2(OutFileName,"IP层上的链路为：" +  Mlink.getName());
			VirtualLinklist = Mlink.getVirtualLinkList();//取出IP层上的链路对应的虚拟链路 新建一个list使其本身的虚拟链路不改变						
			for (VirtualLink Vlink : VirtualLinklist) { // 取出link上对应的virtua
//			file_io.filewrite2(OutFileName,"该IP链路上的虚拟链路为：" +  Vlink.getSrcnode()+"-"+Vlink.getDesnode()
//			+"   性质为："+Vlink.getNature()+ "  剩余的流量为"+Vlink.getRestcapacity());
			
			}
		}
		
		FlowSplitting fs=new FlowSplitting();
		ArrayList<FlowUseOnLink> FlowUseList=new ArrayList<>();
		boolean  ipproflag=fs.flowsplitting(iplayer, nodepair, FlowUseList	, totallink);

		for (Link addlink : DelIPLinkList) {// 恢复iplayer
			iplayer.addLink(addlink);
		}
		DelIPLinkList.clear();

		
		// 恢复链路上对应的虚拟链路
		for (VirtualLink link : SumDelLinkList) {
			// System.out.println(link.getSrcnode()+"-"+link.getDesnode());
			HashMap<String, Link> linklist2 = iplayer.getLinklist();
			Iterator<String> linkitor2 = linklist2.keySet().iterator();
			while (linkitor2.hasNext()) {
				Link link1 = (Link) (linklist2.get(linkitor2.next()));// IPlayer里面的link
				if (link1.getNodeA().getName().equals(link.getSrcnode())
						&& link1.getNodeB().getName().equals(link.getDesnode())) {
					link1.getVirtualLinkList().add(link);
				}
			}
		}
		SumDelLinkList.clear();

//		for (VirtualLink link : DelhighcapVlink) {// 恢复因为容量过剩删除的虚拟链路
////			System.out.println("删除容量过剩的虚拟链路： " + link.getRestcapacity());
//			// System.out.println(link.getSrcnode()+"-"+link.getDesnode());
//			HashMap<String, Link> linklist2 = iplayer.getLinklist();
//			Iterator<String> linkitor2 = linklist2.keySet().iterator();
//			while (linkitor2.hasNext()) {
//				Link link1 = (Link) (linklist2.get(linkitor2.next()));// IPlayer里面的link
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
			file_io.filewrite2(OutFileName,"保护路径在IP层成功路由");
		}
		else{
			file_io.filewrite2(OutFileName,"保护路径在IP层路由失败");
		}
		
		return ipproflag;
	}

}
