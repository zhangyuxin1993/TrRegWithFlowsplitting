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

public class FlowSplitting {
	String OutFileName = Mymain.OutFileName;

	public boolean flowsplitting(Layer iplayer, NodePair nodepair, ArrayList<FlowUseOnLink> UseVlinkList,
			ArrayList<Link> totallink) throws IOException {
		RouteSearching Dijkstra = new RouteSearching();
		file_out_put file_io = new file_out_put();

		boolean routeFlag = false;
		double UnfinishFLow = nodepair.getTrafficdemand();
		Node srcnode = nodepair.getSrcNode();
		Node desnode = nodepair.getDesNode();
		//DEBUG
		ArrayList<VirtualLink> VirtualLinklist = new ArrayList<VirtualLink>();
		file_io.filewrite2(OutFileName, "开始grooming");
		file_io.filewrite2(OutFileName,"IP层上的链路条数为：" +  iplayer.getLinklist().size());
		HashMap<String, Link> linklisttest = iplayer.getLinklist();
		Iterator<String> linkitortest = linklisttest.keySet().iterator();
		while (linkitortest.hasNext()) {
			Link Mlink = (Link) (linklisttest.get(linkitortest.next()));
			file_io.filewrite2(OutFileName,"IP层上的链路为：" +  Mlink.getName());
			VirtualLinklist = Mlink.getVirtualLinkList();//取出IP层上的链路对应的虚拟链路 新建一个list使其本身的虚拟链路不改变						
			for (VirtualLink Vlink : VirtualLinklist) { // 取出link上对应的virtua
			file_io.filewrite2(OutFileName,"该IP链路上的虚拟链路为：" +  Vlink.getSrcnode()+"-"+Vlink.getDesnode()
			+"   性质为："+Vlink.getNature()+ "  剩余的流量为"+Vlink.getRestcapacity());
			}
		}
		
		
		ArrayList<VirtualLink> DelNoFlowVlinkToReco = new ArrayList<>();// 每次循环要删除的虚拟链路
		ArrayList<VirtualLink> AllDelNoFlowVlinkToReco = new ArrayList<>(); // 一共需要删除的虚拟链路
		ArrayList<Link> DelIPLinklistToReco = new ArrayList<>();
		ArrayList<Link> AllDelIPLinklistToReco = new ArrayList<>();

		while (true) {
			LinearRoute newRoute = new LinearRoute(null, 0, null);
			Dijkstra.Dijkstras(srcnode, desnode, iplayer, newRoute, null);
			if (newRoute.getLinklist().size() == 0) {// 说明源结点之间没有路由
				file_io.filewrite2(OutFileName, "IP层没有链路可以路由");
				routeFlag = false;
				break;
			} else {
				file_io.filewrite2(OutFileName, " ");
				DelIPLinklistToReco.clear();
				DelNoFlowVlinkToReco.clear();
				// 首先查找路由上所有虚拟链路的最小剩余流量
				file_io.filewrite2(OutFileName, "循环中：IP层找到路由");
				newRoute.OutputRoute_node(newRoute, OutFileName);

				ArrayList<VirtualLink> MinResCapVlinkList = new ArrayList<>();
				VirtualLink MinFlowVlink = new VirtualLink(null, null, 0, 0);
				for (Link IPlinkOnRoute : newRoute.getLinklist()) {
					double MinCap = 10000;
					for (VirtualLink vlink : IPlinkOnRoute.getVirtualLinkList()) { // 要找出这条路由每段IPlink上剩余流量最小的虚拟链路
						if (vlink.getRestcapacity() < MinCap) {
							MinCap = vlink.getRestcapacity();
							MinFlowVlink = vlink;
						}
					}
//					file_io.filewrite2(OutFileName, "找到路由上IP链路" + IPlinkOnRoute.getName() + " 上虚拟链路最小剩余容量为：" + MinCap);
					MinResCapVlinkList.add(MinFlowVlink);
				}
				// 在每条IPlink上剩余流量最少的虚拟链路中找出他们之中剩余最少的流量
				double MinCapAmongVlinks = 10000;
				for (VirtualLink vlink : MinResCapVlinkList) {
					if (vlink.getRestcapacity() < MinCapAmongVlinks) {
						MinCapAmongVlinks = vlink.getRestcapacity();
					}
				}
//				file_io.filewrite2(OutFileName, "路由上所有虚拟链路中剩余的最小容量为：" + MinCapAmongVlinks);

				// 找到每段虚拟链路上的最小流量后分两种情况讨论
				if (UnfinishFLow <= MinCapAmongVlinks) {// 未完成的业务小于链路上剩余的流量此时可以完成业务
					file_io.filewrite2(OutFileName, "未完成的业务小于链路上剩余的流量 此时可以完成业务");
					for (VirtualLink vlink : MinResCapVlinkList) {// 改变每段虚拟链路上的剩余流量
						boolean findFlag = false;
						for (FlowUseOnLink fuo : UseVlinkList) {
							if (fuo.getVlink().equals(vlink)) {// 在集合中已经存在该虚拟链路
								fuo.setFlowUseOnLink(fuo.getFlowUseOnLink() + UnfinishFLow);
//								file_io.filewrite2(OutFileName, "集合中已存在该虚拟链路" + fuo.getVlink().getSrcnode() + "-"
//										+ fuo.getVlink().getDesnode()+"  虚拟链路使用的流量" + fuo.getFlowUseOnLink());
								findFlag = true;
								break;
							}
						}
						if (!findFlag) {// 在原来的集合中没有该虚拟链路
							FlowUseOnLink fuo = new FlowUseOnLink(vlink, UnfinishFLow);// debug这里如果vlink一致的情况下会不会多次加入
							UseVlinkList.add(fuo);
//							file_io.filewrite2(OutFileName, "集合中不存在该虚拟链路" + fuo.getVlink().getSrcnode() + "-"
//									+ fuo.getVlink().getDesnode() + "  使用的流量为 " + fuo.getFlowUseOnLink());
						}
						vlink.setUsedcapacity(vlink.getUsedcapacity() + UnfinishFLow);
						vlink.setRestcapacity(vlink.getRestcapacity() - UnfinishFLow);
						for (Link phlink : vlink.getPhysicallink()) {
							totallink.add(phlink);
						}
					}
					UnfinishFLow = 0;
					routeFlag = true;
					break;
				}

				else if (UnfinishFLow > MinCapAmongVlinks) {// 未完成的业务大于链路上剩余的流量 此时不可以完成业务
					file_io.filewrite2(OutFileName, "未完成的业务大于链路上剩余的流量 需要下次循环");
					for (VirtualLink vlink : MinResCapVlinkList) {// 改变每段虚拟链路上的剩余流量
						boolean findFlag = false;
						for (FlowUseOnLink fuo : UseVlinkList) {
							if (fuo.getVlink().equals(vlink)) {// 在集合中已经存在该虚拟链路
								fuo.setFlowUseOnLink(fuo.getFlowUseOnLink() + MinCapAmongVlinks);
								file_io.filewrite2(OutFileName, "集合中已存在该虚拟链路" + fuo.getVlink().getSrcnode() + "-"
										+ fuo.getVlink().getDesnode() + "虚拟链路使用的流量" + fuo.getFlowUseOnLink());
								findFlag = true;
								break;
							}
						}
						if (!findFlag) {// 在原来的集合中没有该虚拟链路
							FlowUseOnLink fuo = new FlowUseOnLink(vlink, MinCapAmongVlinks);// debug这里如果vlink一致的情况下会不会多次加入
							UseVlinkList.add(fuo);
							file_io.filewrite2(OutFileName, "集合中不存在该虚拟链路" + fuo.getVlink().getSrcnode() + "-"
									+ fuo.getVlink().getDesnode() + "  使用的流量为 " + fuo.getFlowUseOnLink());
						}

						vlink.setUsedcapacity(vlink.getUsedcapacity() + MinCapAmongVlinks);
						vlink.setRestcapacity(vlink.getRestcapacity() - MinCapAmongVlinks);
						for (Link phlink : vlink.getPhysicallink()) {
							totallink.add(phlink);
						}
					}
					UnfinishFLow = UnfinishFLow - MinCapAmongVlinks;
				}

				// 修改完所有虚拟链路的剩余流量时观察是否有虚拟链路的剩余流量为0 删除该虚拟链路

				file_io.filewrite2(OutFileName, "循环一次结束，观察虚拟链路变化，并删除剩余流量为0 的虚拟链路以及IP链路");
				HashMap<String, Link> linklist2 = iplayer.getLinklist();
				Iterator<String> linkitor2 = linklist2.keySet().iterator();
				while (linkitor2.hasNext()) {
					Link IPlink = (Link) (linklist2.get(linkitor2.next()));
					for (VirtualLink vlink : IPlink.getVirtualLinkList()) {
						if (vlink.getRestcapacity() == 0) {
							DelNoFlowVlinkToReco.add(vlink);
//							file_io.filewrite2(OutFileName, "IP链路：" + IPlink.getName() + " 剩余流量为0的虚拟链路为："
//									+ vlink.getSrcnode() + "-" + vlink.getDesnode() + "性质为：" + vlink.getNature());
						}
					}
				}

				HashMap<String, Link> linklist3 = iplayer.getLinklist();
				Iterator<String> linkitor3 = linklist3.keySet().iterator();
				while (linkitor3.hasNext()) {
					Link IPlink = (Link) (linklist3.get(linkitor3.next()));
					for (VirtualLink delvlink : DelNoFlowVlinkToReco) {
						if(IPlink.getNodeA().getName().equals(delvlink.getSrcnode())&&IPlink.getNodeB().getName().equals(delvlink.getDesnode())){
//							file_io.filewrite2(OutFileName,"删除的虚拟链路为：" + delvlink.getSrcnode() + "-" + delvlink.getDesnode() + "性质为："
//									+ delvlink.getNature() );
							IPlink.getVirtualLinkList().remove(delvlink);
							AllDelNoFlowVlinkToReco.add(delvlink);
							continue;
						}
					}
					if (IPlink.getVirtualLinkList().size() == 0)
						DelIPLinklistToReco.add(IPlink);
				}
				file_io.filewrite2(OutFileName, "一共删除的虚拟链路条数为：" + AllDelNoFlowVlinkToReco.size());

				for (Link link : DelIPLinklistToReco) {
					file_io.filewrite2(OutFileName, "删除可能需要恢复的IP层链路为：" + link.getName());
					AllDelIPLinklistToReco.add(link);
					iplayer.removeLink(link.getName()); // 这里的IP链路删除如果在最后路由不成功时需要恢复
				}
			}
		}

		// 结束循环
		if(routeFlag){//路由成功也要恢复删除掉的链路 只恢复链路 不恢复容量
			file_io.filewrite2(OutFileName, " ");
			file_io.filewrite2(OutFileName, "路由成功 恢复删除的IP链路");
			for (Link nowlink : AllDelIPLinklistToReco) {
				file_io.filewrite2(OutFileName, "恢复的IP链路为：" + nowlink.getName());
				iplayer.addLink(nowlink);
			}
			AllDelIPLinklistToReco.clear();
			
			file_io.filewrite2(OutFileName, "路由成功 恢复删除的虚拟链路");
			for (VirtualLink link : AllDelNoFlowVlinkToReco) {
				file_io.filewrite2(OutFileName, "恢复的虚拟链路条数为：" + AllDelNoFlowVlinkToReco.size());
//				file_io.filewrite2(OutFileName, "恢复的虚拟链路为：" + link.getSrcnode() + "-" + link.getDesnode() + "性质为："
//						+ link.getNature() + "  剩余流量为 " + link.getRestcapacity());
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
			AllDelNoFlowVlinkToReco.clear();
		}
		
		
		if (!routeFlag) {
			totallink.clear();
			file_io.filewrite2(OutFileName, "没有路由成功 要恢复虚拟链路上的容量");
			if (UseVlinkList != null && UseVlinkList.size() != 0) {
				for (FlowUseOnLink fuo : UseVlinkList) {
					VirtualLink vlink = fuo.getVlink();
					vlink.setRestcapacity(vlink.getRestcapacity() + fuo.getFlowUseOnLink());
					vlink.setUsedcapacity(vlink.getUsedcapacity() - fuo.getFlowUseOnLink());
					file_io.filewrite2(OutFileName, "需要恢复流量的虚拟链路" + vlink.getSrcnode() + "-" + vlink.getDesnode()
							+ "已使用的流量为 " + vlink.getUsedcapacity() + "  剩余的流量为 " + vlink.getRestcapacity());
				}
				UseVlinkList.clear();// 路由不成功恢复连路上的流量
			}

			file_io.filewrite2(OutFileName, " ");
			file_io.filewrite2(OutFileName, "没有路由成功 要恢复链路");
			for (Link nowlink : AllDelIPLinklistToReco) {
				file_io.filewrite2(OutFileName, "恢复的IP链路为：" + nowlink.getName());
				iplayer.addLink(nowlink);
			}
			AllDelIPLinklistToReco.clear();

			for (VirtualLink link : AllDelNoFlowVlinkToReco) {
				file_io.filewrite2(OutFileName, "恢复的虚拟链路条数为：" + AllDelNoFlowVlinkToReco.size());
				file_io.filewrite2(OutFileName, "恢复的虚拟链路为：" + link.getSrcnode() + "-" + link.getDesnode() + "性质为："
						+ link.getNature() + "  剩余流量为 " + link.getRestcapacity());
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
			AllDelNoFlowVlinkToReco.clear();
		}
		
		
		return routeFlag;
	}

}
