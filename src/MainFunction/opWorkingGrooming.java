package MainFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import demand.Request;
import general.Constant;
import general.file_out_put;
import graphalgorithms.RouteSearching;
import network.Layer;
import network.Link;
import network.Node;
import network.NodePair;
import network.VirtualLink;
import resource.ResourceOnLink;
import subgraph.LinearRoute;

public class opWorkingGrooming {
	String OutFileName = Mymain.OutFileName;

	public boolean opWorkingGrooming(NodePair nodepair, Layer iplayer, Layer oplayer, LinearRoute opnewRoute,
			ArrayList<WorkandProtectRoute> wprlist, ArrayList<RequestOnWorkLink> rowList) throws IOException {
		RouteSearching Dijkstra = new RouteSearching();
		boolean opworkflag = false;
		Node srcnode = nodepair.getSrcNode();
		Node desnode = nodepair.getDesNode();
		double routelength = 0;
		LinearRoute route_out = new LinearRoute(null, 0, null);
		file_out_put file_io = new file_out_put();
		ArrayList<Double> RegLengthList = new ArrayList<>();
		file_io.filewrite2(OutFileName, " ");
		ArrayList<LinearRoute> routeList = new ArrayList<>();
		// System.out.println("IP�㹤��·�ɲ��ɹ�����Ҫ�½���·");
		file_io.filewrite2(OutFileName, "IP�㹤��·�ɲ��ɹ�����Ҫ�½���·");
		Node opsrcnode = oplayer.getNodelist().get(srcnode.getName());
		Node opdesnode = oplayer.getNodelist().get(desnode.getName());

		// �ڹ���½���·��ʱ����Ҫ��������������

		Dijkstra.Kshortest(opsrcnode, opdesnode, oplayer, 10, routeList);

		for (int count = 0; count < routeList.size(); count++) {
			opnewRoute = routeList.get(count);
			file_io.filewrite_without(OutFileName, "count=" + count + "  ����·��·�ɣ�");
			opnewRoute.OutputRoute_node(opnewRoute, OutFileName);
			file_io.filewrite2(OutFileName, "");

			if (opnewRoute.getLinklist().size() == 0) {
				// System.out.println("������·��");
				file_io.filewrite2(OutFileName, "������·��");
			} else {
				// System.out.print("�������·��Ϊ��");
				file_io.filewrite_without(OutFileName, "�������·��Ϊ��");
				opnewRoute.OutputRoute_node(opnewRoute);
				route_out.OutputRoute_node(opnewRoute, OutFileName);
				// System.out.println(); file_io.filewrite2(OutFileName,"");

				int slotnum = 0;
				int IPflow = nodepair.getTrafficdemand();
				double X = 1;// 2000-4000 BPSK,1000-2000
								// QBSK,500-1000��8QAM,0-500 16QAM

				for (Link link : opnewRoute.getLinklist()) {
					// System.out.println(link.getName());
					routelength = routelength + link.getLength();
				}
				// System.out.println("����·���ĳ����ǣ�"+routelength);
				// ͨ��·���ĳ������仯���Ƹ�ʽ �����ж������� ��ʹ��

				if (routelength < 4000) {// �ҵ���·������Ҫ�������Ϳ���ֱ��ʹ��
					if (routelength > 2000 && routelength <= 4000) {
						X = 12.5;
					} else if (routelength > 1000 && routelength <= 2000) {
						X = 25.0;
					} else if (routelength > 500 && routelength <= 1000) {
						X = 37.5;
					} else if (routelength > 0 && routelength <= 500) {
						X = 50.0;
					}
					slotnum = (int) Math.ceil(IPflow / X);// ����ȡ��

					if (slotnum < Constant.MinSlotinLightpath) {
						slotnum = Constant.MinSlotinLightpath;
					}
					opnewRoute.setSlotsnum(slotnum);
					// System.out.println("����Ҫ������ ����·����slot���� " + slotnum);
					file_io.filewrite2(OutFileName, "����Ҫ������ ����·����slot���� " + slotnum);
					ArrayList<Integer> index_wave = new ArrayList<Integer>();
					Mymain spa = new Mymain();
					index_wave = spa.spectrumallocationOneRoute(true, opnewRoute, null, slotnum);
					if (index_wave.size() == 0) {
						// System.out.println("·������ ��������Ƶ����Դ");
						file_io.filewrite2(OutFileName, "·������ ��������Ƶ����Դ");
					} else {
						RequestOnWorkLink row = new RequestOnWorkLink(null, null, 0, 0);
						file_io.filewrite2(OutFileName, "");
						file_io.filewrite2(OutFileName, "������·����Ҫ������ʱ�ڹ�����Ƶ�ף�");
						file_io.filewrite2(OutFileName, "FS��ʼֵ��" + index_wave.get(0) + "  ����" + slotnum);
						opworkflag = true;
						double length1 = 0;
						double cost = 0;

						for (Link link : opnewRoute.getLinklist()) {// ������link
							length1 = length1 + link.getLength();
							cost = cost + link.getCost();
							Request request = null;
							ResourceOnLink ro = new ResourceOnLink(request, link, index_wave.get(0), slotnum);
							row.setWorkLink(link);
							row.setWorkRequest(request);
							row.setStartFS(index_wave.get(0));
							row.setSlotNum(slotnum);
							rowList.add(row);
							// here
							// ֮���ڹ���·����Ҫ�������ĵط�Ҳ�������
							// Ȼ���ڱ����޷�����ʱҪɾȥ֮ǰ�Ѿ�ռ�õ�FS
							link.setMaxslot(slotnum + link.getMaxslot());
						} // �ı�������ϵ���·���� �Ա�����һ���½�ʱ����slot

						String name = opsrcnode.getName() + "-" + opdesnode.getName();
						int index = iplayer.getLinklist().size();// ��Ϊiplayer�����link��һ��һ������ȥ��
																	// ����������index

						Link finlink = iplayer.findLink(srcnode, desnode);
						Link createlink = new Link(null, 0, null, iplayer, null, null, 0, 0);
						boolean findflag = false;
						// System.out.println();
						try {
							System.out.println("IP�����ҵ�������·" + finlink.getName());
							file_io.filewrite2(OutFileName, "IP�����ҵ�������·" + finlink.getName());
							findflag = true;
						} catch (java.lang.NullPointerException ex) {
							System.out.println("IP ��û�иù�����·��Ҫ�½���·");
							file_io.filewrite2(OutFileName, "IP ��û�иù�����·��Ҫ�½���·");
							createlink = new Link(name, index, null, iplayer, srcnode, desnode, length1, cost);
							iplayer.addLink(createlink);
						}

						VirtualLink Vlink = new VirtualLink(srcnode.getName(), desnode.getName(), 0, 0);
						Vlink.setnature(0);
						Vlink.setUsedcapacity(Vlink.getUsedcapacity() + nodepair.getTrafficdemand());
						Vlink.setFullcapacity(slotnum * X);// �������flow�Ǵ����������
						Vlink.setRestcapacity(Vlink.getFullcapacity() - Vlink.getUsedcapacity());
						Vlink.setlength(length1);
						Vlink.setcost(cost);
						Vlink.setPhysicallink(opnewRoute.getLinklist());

						if (findflag) {// �����IP�����Ѿ��ҵ�����·
							finlink.getVirtualLinkList().add(Vlink);
							// System.out.println("IP���Ѵ��ڵ���· " +
							// finlink.getName() + " �����µı���������· ���������flow: "
							// + Vlink.getUsedcapacity() + "\n"+"���е�flow: " +
							// Vlink.getFullcapacity()
							// + " Ԥ����flow�� " + Vlink.getRestcapacity());
							file_io.filewrite2(OutFileName,
									"IP���Ѵ��ڵ���· " + finlink.getName() + " �����µı���������· ���������flow: "
											+ Vlink.getUsedcapacity() + "\n" + "���е�flow:  " + Vlink.getFullcapacity()
											+ "    Ԥ����flow��  " + Vlink.getRestcapacity());
							// System.out.println("������·�ڹ���½�����·��
							// "+finlink.getName()+" �ϵ�������·������ "+
							// finlink.getVirtualLinkList().size());
							file_io.filewrite2(OutFileName, "������·�ڹ���½�����·��  " + finlink.getName() + "  �ϵ�������·������ "
									+ finlink.getVirtualLinkList().size());
						} else {
							createlink.getVirtualLinkList().add(Vlink);
							// System.out.println("IP�����½���· " +
							// createlink.getName() + " �����µĹ���������· ���������flow: "
							// + Vlink.getUsedcapacity() + "\n"+"���е�flow: " +
							// Vlink.getFullcapacity()
							// + " Ԥ����flow�� " + Vlink.getRestcapacity());
							file_io.filewrite2(OutFileName,
									"IP�����½���· " + createlink.getName() + " �����µĹ���������· ���������flow: "
											+ Vlink.getUsedcapacity() + "\n" + "���е�flow:  " + Vlink.getFullcapacity()
											+ "    Ԥ����flow��  " + Vlink.getRestcapacity());
							// System.out.println("*********������·�ڹ���½�����·��
							// "+createlink.getName()+" �ϵ�������·������ "+
							// createlink.getVirtualLinkList().size());
							file_io.filewrite2(OutFileName, "*********������·�ڹ���½�����·��  " + createlink.getName()
									+ "  �ϵ�������·������ " + createlink.getVirtualLinkList().size());
						}
						// numOfTransponder = numOfTransponder + 2;
					}
				}

				if (routelength > 4000) {
					RegeneratorPlace regplace = new RegeneratorPlace();
					opworkflag = regplace.regeneratorplace(IPflow, routelength, opnewRoute, oplayer, iplayer, wprlist,
							nodepair, RegLengthList, rowList);
				}
			}
			if (opworkflag) {
				// &&routelength<=4000) {
				// System.out.println();
				// System.out.println("����·���ڹ��ɹ�·�ɲ���RSA");
				file_io.filewrite2(OutFileName, "����·���ڹ��ɹ�·�ɲ���RSA");
				WorkandProtectRoute wpr = new WorkandProtectRoute(nodepair);
				Request re = new Request(nodepair);
				ArrayList<Link> totallink = new ArrayList<>();
				totallink = opnewRoute.getLinklist();
				wpr.setrequest(re);
				wpr.setworklinklist(totallink);
				wpr.setRegWorkLengthList(RegLengthList);
				wprlist.add(wpr);
				break;

			}
			if (!opworkflag) {
				// System.out.println("����·�ɹ��·��ʧ�� ��ҵ������");
				file_io.filewrite2(OutFileName, "����·���ڹ���޷�����");
			}
		}
		return opworkflag;
	}
}
