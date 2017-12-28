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
		file_io.filewrite2(OutFileName, "��ʼgrooming");
		file_io.filewrite2(OutFileName,"IP���ϵ���·����Ϊ��" +  iplayer.getLinklist().size());
		HashMap<String, Link> linklisttest = iplayer.getLinklist();
		Iterator<String> linkitortest = linklisttest.keySet().iterator();
		while (linkitortest.hasNext()) {
			Link Mlink = (Link) (linklisttest.get(linkitortest.next()));
			file_io.filewrite2(OutFileName,"IP���ϵ���·Ϊ��" +  Mlink.getName());
			VirtualLinklist = Mlink.getVirtualLinkList();//ȡ��IP���ϵ���·��Ӧ��������· �½�һ��listʹ�䱾���������·���ı�						
			for (VirtualLink Vlink : VirtualLinklist) { // ȡ��link�϶�Ӧ��virtua
			file_io.filewrite2(OutFileName,"��IP��·�ϵ�������·Ϊ��" +  Vlink.getSrcnode()+"-"+Vlink.getDesnode()
			+"   ����Ϊ��"+Vlink.getNature()+ "  ʣ�������Ϊ"+Vlink.getRestcapacity());
			}
		}
		
		
		ArrayList<VirtualLink> DelNoFlowVlinkToReco = new ArrayList<>();// ÿ��ѭ��Ҫɾ����������·
		ArrayList<VirtualLink> AllDelNoFlowVlinkToReco = new ArrayList<>(); // һ����Ҫɾ����������·
		ArrayList<Link> DelIPLinklistToReco = new ArrayList<>();
		ArrayList<Link> AllDelIPLinklistToReco = new ArrayList<>();

		while (true) {
			LinearRoute newRoute = new LinearRoute(null, 0, null);
			Dijkstra.Dijkstras(srcnode, desnode, iplayer, newRoute, null);
			if (newRoute.getLinklist().size() == 0) {// ˵��Դ���֮��û��·��
				file_io.filewrite2(OutFileName, "IP��û����·����·��");
				routeFlag = false;
				break;
			} else {
				file_io.filewrite2(OutFileName, " ");
				DelIPLinklistToReco.clear();
				DelNoFlowVlinkToReco.clear();
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
//					file_io.filewrite2(OutFileName, "�ҵ�·����IP��·" + IPlinkOnRoute.getName() + " ��������·��Сʣ������Ϊ��" + MinCap);
					MinResCapVlinkList.add(MinFlowVlink);
				}
				// ��ÿ��IPlink��ʣ���������ٵ�������·���ҳ�����֮��ʣ�����ٵ�����
				double MinCapAmongVlinks = 10000;
				for (VirtualLink vlink : MinResCapVlinkList) {
					if (vlink.getRestcapacity() < MinCapAmongVlinks) {
						MinCapAmongVlinks = vlink.getRestcapacity();
					}
				}
//				file_io.filewrite2(OutFileName, "·��������������·��ʣ�����С����Ϊ��" + MinCapAmongVlinks);

				// �ҵ�ÿ��������·�ϵ���С������������������
				if (UnfinishFLow <= MinCapAmongVlinks) {// δ��ɵ�ҵ��С����·��ʣ���������ʱ�������ҵ��
					file_io.filewrite2(OutFileName, "δ��ɵ�ҵ��С����·��ʣ������� ��ʱ�������ҵ��");
					for (VirtualLink vlink : MinResCapVlinkList) {// �ı�ÿ��������·�ϵ�ʣ������
						boolean findFlag = false;
						for (FlowUseOnLink fuo : UseVlinkList) {
							if (fuo.getVlink().equals(vlink)) {// �ڼ������Ѿ����ڸ�������·
								fuo.setFlowUseOnLink(fuo.getFlowUseOnLink() + UnfinishFLow);
//								file_io.filewrite2(OutFileName, "�������Ѵ��ڸ�������·" + fuo.getVlink().getSrcnode() + "-"
//										+ fuo.getVlink().getDesnode()+"  ������·ʹ�õ�����" + fuo.getFlowUseOnLink());
								findFlag = true;
								break;
							}
						}
						if (!findFlag) {// ��ԭ���ļ�����û�и�������·
							FlowUseOnLink fuo = new FlowUseOnLink(vlink, UnfinishFLow);// debug�������vlinkһ�µ�����»᲻���μ���
							UseVlinkList.add(fuo);
//							file_io.filewrite2(OutFileName, "�����в����ڸ�������·" + fuo.getVlink().getSrcnode() + "-"
//									+ fuo.getVlink().getDesnode() + "  ʹ�õ�����Ϊ " + fuo.getFlowUseOnLink());
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

				else if (UnfinishFLow > MinCapAmongVlinks) {// δ��ɵ�ҵ�������·��ʣ������� ��ʱ���������ҵ��
					file_io.filewrite2(OutFileName, "δ��ɵ�ҵ�������·��ʣ������� ��Ҫ�´�ѭ��");
					for (VirtualLink vlink : MinResCapVlinkList) {// �ı�ÿ��������·�ϵ�ʣ������
						boolean findFlag = false;
						for (FlowUseOnLink fuo : UseVlinkList) {
							if (fuo.getVlink().equals(vlink)) {// �ڼ������Ѿ����ڸ�������·
								fuo.setFlowUseOnLink(fuo.getFlowUseOnLink() + MinCapAmongVlinks);
								file_io.filewrite2(OutFileName, "�������Ѵ��ڸ�������·" + fuo.getVlink().getSrcnode() + "-"
										+ fuo.getVlink().getDesnode() + "������·ʹ�õ�����" + fuo.getFlowUseOnLink());
								findFlag = true;
								break;
							}
						}
						if (!findFlag) {// ��ԭ���ļ�����û�и�������·
							FlowUseOnLink fuo = new FlowUseOnLink(vlink, MinCapAmongVlinks);// debug�������vlinkһ�µ�����»᲻���μ���
							UseVlinkList.add(fuo);
							file_io.filewrite2(OutFileName, "�����в����ڸ�������·" + fuo.getVlink().getSrcnode() + "-"
									+ fuo.getVlink().getDesnode() + "  ʹ�õ�����Ϊ " + fuo.getFlowUseOnLink());
						}

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
						if (vlink.getRestcapacity() == 0) {
							DelNoFlowVlinkToReco.add(vlink);
//							file_io.filewrite2(OutFileName, "IP��·��" + IPlink.getName() + " ʣ������Ϊ0��������·Ϊ��"
//									+ vlink.getSrcnode() + "-" + vlink.getDesnode() + "����Ϊ��" + vlink.getNature());
						}
					}
				}

				HashMap<String, Link> linklist3 = iplayer.getLinklist();
				Iterator<String> linkitor3 = linklist3.keySet().iterator();
				while (linkitor3.hasNext()) {
					Link IPlink = (Link) (linklist3.get(linkitor3.next()));
					for (VirtualLink delvlink : DelNoFlowVlinkToReco) {
						if(IPlink.getNodeA().getName().equals(delvlink.getSrcnode())&&IPlink.getNodeB().getName().equals(delvlink.getDesnode())){
//							file_io.filewrite2(OutFileName,"ɾ����������·Ϊ��" + delvlink.getSrcnode() + "-" + delvlink.getDesnode() + "����Ϊ��"
//									+ delvlink.getNature() );
							IPlink.getVirtualLinkList().remove(delvlink);
							AllDelNoFlowVlinkToReco.add(delvlink);
							continue;
						}
					}
					if (IPlink.getVirtualLinkList().size() == 0)
						DelIPLinklistToReco.add(IPlink);
				}
				file_io.filewrite2(OutFileName, "һ��ɾ����������·����Ϊ��" + AllDelNoFlowVlinkToReco.size());

				for (Link link : DelIPLinklistToReco) {
					file_io.filewrite2(OutFileName, "ɾ��������Ҫ�ָ���IP����·Ϊ��" + link.getName());
					AllDelIPLinklistToReco.add(link);
					iplayer.removeLink(link.getName()); // �����IP��·ɾ����������·�ɲ��ɹ�ʱ��Ҫ�ָ�
				}
			}
		}

		// ����ѭ��
		if(routeFlag){//·�ɳɹ�ҲҪ�ָ�ɾ��������· ֻ�ָ���· ���ָ�����
			file_io.filewrite2(OutFileName, " ");
			file_io.filewrite2(OutFileName, "·�ɳɹ� �ָ�ɾ����IP��·");
			for (Link nowlink : AllDelIPLinklistToReco) {
				file_io.filewrite2(OutFileName, "�ָ���IP��·Ϊ��" + nowlink.getName());
				iplayer.addLink(nowlink);
			}
			AllDelIPLinklistToReco.clear();
			
			file_io.filewrite2(OutFileName, "·�ɳɹ� �ָ�ɾ����������·");
			for (VirtualLink link : AllDelNoFlowVlinkToReco) {
				file_io.filewrite2(OutFileName, "�ָ���������·����Ϊ��" + AllDelNoFlowVlinkToReco.size());
//				file_io.filewrite2(OutFileName, "�ָ���������·Ϊ��" + link.getSrcnode() + "-" + link.getDesnode() + "����Ϊ��"
//						+ link.getNature() + "  ʣ������Ϊ " + link.getRestcapacity());
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
			AllDelNoFlowVlinkToReco.clear();
		}
		
		
		if (!routeFlag) {
			totallink.clear();
			file_io.filewrite2(OutFileName, "û��·�ɳɹ� Ҫ�ָ�������·�ϵ�����");
			if (UseVlinkList != null && UseVlinkList.size() != 0) {
				for (FlowUseOnLink fuo : UseVlinkList) {
					VirtualLink vlink = fuo.getVlink();
					vlink.setRestcapacity(vlink.getRestcapacity() + fuo.getFlowUseOnLink());
					vlink.setUsedcapacity(vlink.getUsedcapacity() - fuo.getFlowUseOnLink());
					file_io.filewrite2(OutFileName, "��Ҫ�ָ�������������·" + vlink.getSrcnode() + "-" + vlink.getDesnode()
							+ "��ʹ�õ�����Ϊ " + vlink.getUsedcapacity() + "  ʣ�������Ϊ " + vlink.getRestcapacity());
				}
				UseVlinkList.clear();// ·�ɲ��ɹ��ָ���·�ϵ�����
			}

			file_io.filewrite2(OutFileName, " ");
			file_io.filewrite2(OutFileName, "û��·�ɳɹ� Ҫ�ָ���·");
			for (Link nowlink : AllDelIPLinklistToReco) {
				file_io.filewrite2(OutFileName, "�ָ���IP��·Ϊ��" + nowlink.getName());
				iplayer.addLink(nowlink);
			}
			AllDelIPLinklistToReco.clear();

			for (VirtualLink link : AllDelNoFlowVlinkToReco) {
				file_io.filewrite2(OutFileName, "�ָ���������·����Ϊ��" + AllDelNoFlowVlinkToReco.size());
				file_io.filewrite2(OutFileName, "�ָ���������·Ϊ��" + link.getSrcnode() + "-" + link.getDesnode() + "����Ϊ��"
						+ link.getNature() + "  ʣ������Ϊ " + link.getRestcapacity());
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
			AllDelNoFlowVlinkToReco.clear();
		}
		
		
		return routeFlag;
	}

}
