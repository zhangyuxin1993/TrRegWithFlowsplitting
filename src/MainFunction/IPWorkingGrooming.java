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
//			file_io.filewrite2(OutFileName,"IP层上的链路条数为：" +  iplayer.getLinklist().size());
//			HashMap<String, Link> linklisttest = iplayer.getLinklist();
//			Iterator<String> linkitortest = linklisttest.keySet().iterator();
//			while (linkitortest.hasNext()) {
//				Link Mlink = (Link) (linklisttest.get(linkitortest.next()));
//				file_io.filewrite2(OutFileName,"IP层上的链路为：" +  Mlink.getName());
//				VirtualLinklist = Mlink.getVirtualLinkList();//取出IP层上的链路对应的虚拟链路 新建一个list使其本身的虚拟链路不改变						
//				for (VirtualLink Vlink : VirtualLinklist) { // 取出link上对应的virtua
//				file_io.filewrite2(OutFileName,"该IP链路上的虚拟链路为：" +  Vlink.getSrcnode()+"-"+Vlink.getDesnode()
//				+"   性质为："+Vlink.getNature()+ "  剩余的流量为"+Vlink.getRestcapacity());
//				}
//			}
			
//			删除剩余流量为0的虚拟链路  不需要在最后恢复 只有这里删除的链路（IP 虚拟）不需要恢复 其他均需要恢复
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
				file_io.filewrite2(OutFileName,"删除不需要恢复的IP层链路为："+link.getName());
				iplayer.removeLink(link.getName()); //这里的IP链路删除之后也不需要恢复（该IP 链路上没有可用的虚拟链路）
			}
			
			HashMap<String, Link> linklist2 = iplayer.getLinklist();
			Iterator<String> linkitor2 = linklist2.keySet().iterator();
			while (linkitor2.hasNext()) {
				Link Mlink = (Link) (linklist2.get(linkitor2.next()));
				VirtualLinklist = Mlink.getVirtualLinkList();//取出IP层上的链路对应的虚拟链路 新建一个list使其本身的虚拟链路不改变						
				for (VirtualLink Vlink : VirtualLinklist) { // 取出link上对应的virtual link
//					System.out.println("IP层上链路"+Mlink.getName()+"    对应的虚拟链路：" + Vlink.getSrcnode() + "-" + Vlink.getDesnode()+ "   nature=" + Vlink.getNature()+
//							"    该虚拟链路上对应的剩余容量为："+Vlink.getRestcapacity());
					if (Vlink.getNature() == 1) {// 工作是0 保护是1
						DelVirtualLinklist.add(Vlink);
					}
				}
				
				for (VirtualLink nowlink : DelVirtualLinklist) {
//					System.out.println(Mlink.getName()+" 上删除的虚拟链路为："+ nowlink.getSrcnode()+"  "+nowlink.getDesnode());
					Mlink.getVirtualLinkList().remove(nowlink);
				}
				if (Mlink.getVirtualLinkList().size() == 0)
					DelIPLinklist.add(Mlink);
			}
			
			for (Link link : DelIPLinklist) {
//				System.out.println("删除的IP层链路为："+link.getName());
				iplayer.removeLink(link.getName());
			}
			//以上为第一部分===删除IP层上属性为保护的链路 容量过小的链路不删除!
			
			
			WorkandProtectRoute wpr=new WorkandProtectRoute(nodepair);
			Request re=new Request(nodepair);
			ArrayList<Link> totallink=new ArrayList<>();
		
			FlowSplitting fs=new FlowSplitting();
			boolean  routeFlag=fs.flowsplitting(iplayer, nodepair, FlowUseList, totallink);

			// 恢复iplayer里面删除的link
			for (Link nowlink : DelIPLinklist) {
				iplayer.addLink(nowlink);
			}
			DelIPLinklist.clear();

				//恢复链路上对应的虚拟链路
				for(VirtualLink link:DelVirtualLinklist){//恢复因为属性为保护删除的虚拟链路
//					System.out.println("删除属性为保护的虚拟链路： "+link.getRestcapacity());
					HashMap<String, Link> linklist3 = iplayer.getLinklist();
					Iterator<String> linkitor3 = linklist3.keySet().iterator();
					while (linkitor3.hasNext()) {
						Link link1 = (Link) (linklist3.get(linkitor3.next()));// IPlayer里面的link
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
				file_io.filewrite2(OutFileName,"工作路径在IP层路由成功");
			}
			if(!routeFlag){ 
				file_io.filewrite2(OutFileName,"工作路径IP层路由失败");
			}
		return routeFlag;
	}
}