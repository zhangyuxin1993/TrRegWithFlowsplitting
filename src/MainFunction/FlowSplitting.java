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

		file_io.filewrite2(OutFileName, "��ʼgrooming");

		ArrayList<VirtualLink> DelNoFlowVlinkToReco = new ArrayList<>();// ����п�����Ҫ�ָ�����·
		ArrayList<Link> DelIPLinklistToReco = new ArrayList<>();

		while (true) {
			LinearRoute newRoute = new LinearRoute(null, 0, null);
			Dijkstra.Dijkstras(srcnode, desnode, iplayer, newRoute, null);
			if (newRoute.getLinklist().size() == 0) {// ˵��Դ���֮��û��·��
				file_io.filewrite2(OutFileName, "IP��û����·����·��");
				routeFlag = false;
				break;
			} else {
				// ���Ȳ���·��������������·����Сʣ������
				file_io.filewrite2(OutFileName, "ѭ���У�IP���ҵ�·��");
				newRoute.OutputRoute_node(newRoute, OutFileName);
				ArrayList<VirtualLink> MinResCapVlinkList = new ArrayList<>();
				VirtualLink MinFlowVlink = new VirtualLink(null, null, 0, 0);
				for (Link IPlinkOnRoute : newRoute.getLinklist()) {
					double MinCap = 10000;
					for (VirtualLink vlink : IPlinkOnRoute.getVirtualLinkList()) { // Ҫ�ҳ�����·��ÿ��IPlink��ʣ��������С��������·
						if (vlink.getRestcapacity() < MinCap) {
							MinCap = vlink.getRestcapacity();
							MinFlowVlink = vlink;
						}
					}
					file_io.filewrite2(OutFileName, "IP��·" + IPlinkOnRoute.getName() + " ��������·��Сʣ������Ϊ��" + MinCap);
					MinResCapVlinkList.add(MinFlowVlink);
				}
				// ��ÿ��IPlink��ʣ���������ٵ�������·���ҳ�����֮��ʣ�����ٵ�����
				double MinCapAmongVlinks = 10000;
				for (VirtualLink vlink : MinResCapVlinkList) {
					if (vlink.getRestcapacity() < MinCapAmongVlinks) {
						MinCapAmongVlinks = vlink.getRestcapacity();
					}
				}
				file_io.filewrite2(OutFileName, "������·��ʣ�����С����Ϊ��" + MinCapAmongVlinks);

				// �ҵ�ÿ��������·�ϵ���С������������������
				if (UnfinishFLow <= MinCapAmongVlinks) {// δ��ɵ�ҵ��С����·��ʣ�������
														// ��ʱ�������ҵ��
					file_io.filewrite2(OutFileName, "δ��ɵ�ҵ��С����·��ʣ������� ��ʱ�������ҵ��");
					for (VirtualLink vlink : MinResCapVlinkList) {// �ı�ÿ��������·�ϵ�ʣ������
						FlowUseOnLink fuo = new FlowUseOnLink(vlink, UnfinishFLow);// debug�������vlinkһ�µ������
																					// �᲻���μ���
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

				else if (UnfinishFLow > MinCapAmongVlinks) {// δ��ɵ�ҵ�������·��ʣ�������
															// ��ʱ���������ҵ��
					file_io.filewrite2(OutFileName, "δ��ɵ�ҵ�������·��ʣ������� ��Ҫ�´�ѭ��");
					for (VirtualLink vlink : MinResCapVlinkList) {// �ı�ÿ��������·�ϵ�ʣ������
						FlowUseOnLink fuo = new FlowUseOnLink(vlink, UnfinishFLow);// debug�������vlinkһ�µ������
																					// �᲻���μ���
						UseVlinkList.add(fuo);
						vlink.setUsedcapacity(vlink.getUsedcapacity() + MinCapAmongVlinks);
						vlink.setRestcapacity(vlink.getRestcapacity() - MinCapAmongVlinks);
						for (Link phlink : vlink.getPhysicallink()) {
							totallink.add(phlink);
						}
					}
					UnfinishFLow = UnfinishFLow - MinCapAmongVlinks;
				}

				// �޸�������������·��ʣ������ʱ�۲��Ƿ���������·��ʣ������Ϊ0 ɾ����������·
				file_io.filewrite2(OutFileName, "ѭ��һ�ν������۲�������·�仯����ɾ��ʣ������Ϊ0 ��������·�Լ�IP��·");
				HashMap<String, Link> linklist2 = iplayer.getLinklist();
				Iterator<String> linkitor2 = linklist2.keySet().iterator();
				while (linkitor2.hasNext()) {
					Link IPlink = (Link) (linklist2.get(linkitor2.next()));
					for (VirtualLink vlink : IPlink.getVirtualLinkList()) {
						file_io.filewrite2(OutFileName,
								"IP����·" + IPlink.getName() + " ��������·" + vlink.getSrcnode() + "-" + vlink.getDesnode()
										+ "��ʹ�õ�����Ϊ " + vlink.getUsedcapacity() + "  ʣ�������Ϊ " + vlink.getRestcapacity());
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
					file_io.filewrite2(OutFileName, "ɾ��������Ҫ�ָ���IP����·Ϊ��" + link.getName());
					iplayer.removeLink(link.getName()); // �����IP��·ɾ����������·�ɲ��ɹ�ʱ��Ҫ�ָ�
				}
			}
		}

		// ����ѭ��
		if (!routeFlag) {
			totallink.clear();
			file_io.filewrite2(OutFileName, "û��·�ɳɹ� Ҫ�ָ�������·�ϵ�����");
			if (UseVlinkList != null&&UseVlinkList.size() != 0 ) {
				for (FlowUseOnLink fuo : UseVlinkList) {
					VirtualLink vlink = fuo.getVlink();
					vlink.setRestcapacity(vlink.getRestcapacity() + fuo.getFlowUseOnLink());
					vlink.setUsedcapacity(vlink.getUsedcapacity() - fuo.getFlowUseOnLink());
					file_io.filewrite2(OutFileName, "������·" + vlink.getSrcnode() + "-" + vlink.getDesnode() + "��ʹ�õ�����Ϊ "
							+ vlink.getUsedcapacity() + "  ʣ�������Ϊ " + vlink.getRestcapacity());
				}
				UseVlinkList.clear();// ·�ɲ��ɹ��ָ���·�ϵ�����
			}

			file_io.filewrite2(OutFileName, " ");
			file_io.filewrite2(OutFileName, "û��·�ɳɹ� Ҫ�ָ���·");
			for (VirtualLink link : DelNoFlowVlinkToReco) {// �ָ���Ϊ����Ϊ����ɾ����������·
				file_io.filewrite2(OutFileName, "�ָ���������·Ϊ��" + link.getSrcnode() + "-" + link.getDesnode());
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
			DelNoFlowVlinkToReco.clear();
			file_io.filewrite2(OutFileName, " ");
			for (Link nowlink : DelIPLinklistToReco) {
				file_io.filewrite2(OutFileName, "�ָ���IP��·Ϊ��" + nowlink.getName());
				iplayer.addLink(nowlink);
			}
			DelIPLinklistToReco.clear();
		}
		return routeFlag;
	}

}
