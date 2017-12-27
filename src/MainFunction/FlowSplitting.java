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

		file_io.filewrite2(OutFileName, "开始grooming");

		ArrayList<VirtualLink> DelNoFlowVlinkToReco = new ArrayList<>();// 最后有可能需要恢复的链路
		ArrayList<Link> DelIPLinklistToReco = new ArrayList<>();

		while (true) {
			LinearRoute newRoute = new LinearRoute(null, 0, null);
			Dijkstra.Dijkstras(srcnode, desnode, iplayer, newRoute, null);
			if (newRoute.getLinklist().size() == 0) {// 说明源结点之间没有路由
				file_io.filewrite2(OutFileName, "IP层没有链路可以路由");
				routeFlag = false;
				break;
			} else {
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
					file_io.filewrite2(OutFileName, "IP链路" + IPlinkOnRoute.getName() + " 上虚拟链路最小剩余容量为：" + MinCap);
					MinResCapVlinkList.add(MinFlowVlink);
				}
				// 在每条IPlink上剩余流量最少的虚拟链路中找出他们之中剩余最少的流量
				double MinCapAmongVlinks = 10000;
				for (VirtualLink vlink : MinResCapVlinkList) {
					if (vlink.getRestcapacity() < MinCapAmongVlinks) {
						MinCapAmongVlinks = vlink.getRestcapacity();
					}
				}
				file_io.filewrite2(OutFileName, "虚拟链路中剩余的最小容量为：" + MinCapAmongVlinks);

				// 找到每段虚拟链路上的最小流量后分两种情况讨论
				if (UnfinishFLow <= MinCapAmongVlinks) {// 未完成的业务小于链路上剩余的流量
														// 此时可以完成业务
					file_io.filewrite2(OutFileName, "未完成的业务小于链路上剩余的流量 此时可以完成业务");
					for (VirtualLink vlink : MinResCapVlinkList) {// 改变每段虚拟链路上的剩余流量
						FlowUseOnLink fuo = new FlowUseOnLink(vlink, UnfinishFLow);// debug这里如果vlink一致的情况下
																					// 会不会多次加入
						UseVlinkList.add(fuo);
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

				else if (UnfinishFLow > MinCapAmongVlinks) {// 未完成的业务大于链路上剩余的流量
															// 此时不可以完成业务
					file_io.filewrite2(OutFileName, "未完成的业务大于链路上剩余的流量 需要下次循环");
					for (VirtualLink vlink : MinResCapVlinkList) {// 改变每段虚拟链路上的剩余流量
						FlowUseOnLink fuo = new FlowUseOnLink(vlink, UnfinishFLow);// debug这里如果vlink一致的情况下
																					// 会不会多次加入
						UseVlinkList.add(fuo);
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
						file_io.filewrite2(OutFileName,
								"IP层链路" + IPlink.getName() + " 上虚拟链路" + vlink.getSrcnode() + "-" + vlink.getDesnode()
										+ "已使用的流量为 " + vlink.getUsedcapacity() + "  剩余的流量为 " + vlink.getRestcapacity());
						if (vlink.getRestcapacity() == 0) {
							DelNoFlowVlinkToReco.add(vlink);
						}
					}
					for (VirtualLink delvlink : DelNoFlowVlinkToReco) {
						IPlink.getVirtualLinkList().remove(delvlink);
					}
					if (IPlink.getVirtualLinkList().size() == 0)
						DelIPLinklistToReco.add(IPlink);
				}

				for (Link link : DelIPLinklistToReco) {
					file_io.filewrite2(OutFileName, "删除可能需要恢复的IP层链路为：" + link.getName());
					iplayer.removeLink(link.getName()); // 这里的IP链路删除如果在最后路由不成功时需要恢复
				}
			}
		}

		// 结束循环
		if (!routeFlag) {
			totallink.clear();
			file_io.filewrite2(OutFileName, "没有路由成功 要恢复虚拟链路上的容量");
			if (UseVlinkList != null&&UseVlinkList.size() != 0 ) {
				for (FlowUseOnLink fuo : UseVlinkList) {
					VirtualLink vlink = fuo.getVlink();
					vlink.setRestcapacity(vlink.getRestcapacity() + fuo.getFlowUseOnLink());
					vlink.setUsedcapacity(vlink.getUsedcapacity() - fuo.getFlowUseOnLink());
					file_io.filewrite2(OutFileName, "虚拟链路" + vlink.getSrcnode() + "-" + vlink.getDesnode() + "已使用的流量为 "
							+ vlink.getUsedcapacity() + "  剩余的流量为 " + vlink.getRestcapacity());
				}
				UseVlinkList.clear();// 路由不成功恢复连路上的流量
			}

			file_io.filewrite2(OutFileName, " ");
			file_io.filewrite2(OutFileName, "没有路由成功 要恢复链路");
			for (VirtualLink link : DelNoFlowVlinkToReco) {// 恢复因为属性为保护删除的虚拟链路
				file_io.filewrite2(OutFileName, "恢复的虚拟链路为：" + link.getSrcnode() + "-" + link.getDesnode());
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
			DelNoFlowVlinkToReco.clear();
			file_io.filewrite2(OutFileName, " ");
			for (Link nowlink : DelIPLinklistToReco) {
				file_io.filewrite2(OutFileName, "恢复的IP链路为：" + nowlink.getName());
				iplayer.addLink(nowlink);
			}
			DelIPLinklistToReco.clear();
		}
		return routeFlag;
	}

}
