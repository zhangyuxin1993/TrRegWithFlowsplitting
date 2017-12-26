package MainFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import demand.Request;
import general.Constant;
import general.file_out_put;
import network.Layer;
import network.Link;
import network.Node;
import network.NodePair;
import network.VirtualLink;
import resource.ResourceOnLink;
import subgraph.LinearRoute;

public class ProregeneratorPlace {
	String OutFileName = Mymain.OutFileName;
	static int totalregNum = 0;

	// ��RSAunderSet ���������ֵ
	public boolean ProRegeneratorPlace(NodePair nodepair, LinearRoute newRoute, ArrayList<WorkandProtectRoute> wprlist,
			double routelength, Layer oplayer, Layer ipLayer, int IPflow, Request request,ArrayList<Double> ProLengthList,float threshold) throws IOException {
		WorkandProtectRoute nowdemand = new WorkandProtectRoute(null);
		ArrayList<VirtualLink> provirtuallinklist = new ArrayList<>();
		ProregeneratorPlace rgp2 = new ProregeneratorPlace();
		Test t = new Test();
		ArrayList<Integer> ShareReg = new ArrayList<>();
		ArrayList<Node> comnodelist = new ArrayList<>();
		ArrayList<Regenerator> sharereglist = new ArrayList<>();
		ArrayList<Regenerator> removereglist = new ArrayList<>();
		ArrayList<Regenerator> addreglist = new ArrayList<>();
		ArrayList<RouteAndRegPlace> regplaceoption = new ArrayList<>();
		ProregeneratorPlace rgp = new ProregeneratorPlace();
		file_out_put file_io = new file_out_put();

		// part1 �ҵ��ñ�����·�����Ѵ��ڵĹ���������
		for (WorkandProtectRoute nowwpr : wprlist) {// �����ұ���nodepair��Ӧ�� wpr
			if (nowwpr.getdemand().equals(nodepair)) {
				nowdemand = nowwpr;
				break;
			}
		}

		for (WorkandProtectRoute wpr : wprlist) {// ���Ѵ��ڵ�ҵ���� �ҳ���ҵ�����Ѵ��ڵĹ���������

			int cross = t.linklistcompare(nowdemand.getworklinklist(), wpr.getworklinklist());
			if (cross == 0) {// �����ж�������²������������Ƿ���Թ���

				for (Regenerator newreg : wpr.getnewreglist()) {// ֻ������·����û���½���������
					Node node = newreg.getnode();
					if (newRoute.getNodelist().contains(node)) {// ���֮ǰ��ҵ����ĳһ�ڵ����Ѿ�������������

						// �жϸ�ҵ������ҵ��ɷ���������������ҵ��Ĺ�����·��Ӧ��������·�Ƿ񽻲棩
						int already = 0, newregg = 0;
						boolean noshareFlag = false;
						for (WorkandProtectRoute comwpr : wprlist) {
							if (wpr.getdemand().equals(comwpr.getdemand()))
								continue;
							for (Regenerator haveshareReg : comwpr.getsharereglist()) {
								if (haveshareReg.equals(newreg)) {// ����ҵ�������������������
									int cross_second = t.linklistcompare(nowdemand.getworklinklist(),
											comwpr.getworklinklist());
									if (cross_second == 1) {
										noshareFlag = true;
										break;
									}
								}
							}
						} // �����ж�֮ǰҵ��ĳһ�ڵ���������ɷ��ڱ���ҵ��ڵ��Ϲ���

						if (!noshareFlag) {// ��ʾ�ýڵ����������ڱ�ҵ����Ҳ���Թ���
							/*
							 * �������޸� ����ѡ��IP������ �ж��IP������ʱѡ�񱣻�·����������� OEO������ͬ��
							 */
							int po = t.nodeindexofroute(node, newRoute);// ��������·�Ͽ��Թ������������λ��
							if (po != 0 && po != newRoute.getNodelist().size() - 1) {// �ж�����·���Ѵ��ڵ��������Ƿ�����·������
								/*
								 * comnodelist�洢�����Ѿ����ڿɹ����������Ľڵ� sharereglist
								 * �洢���Ǿ��жϿ��Թ���������� һ�½���һ���ڵ��϶��������������ɸѡ
								 */
								if (comnodelist.contains(node)) {// ˵���ýڵ����Ѵ��ڿɹ����������
																	// ��ʱ��Ҫѡ�����ĸ�����������
									for (Regenerator alreadyReg : sharereglist) {
										// sharereglist��������Ѿ��жϿ��Թ����������
										// sharelist����ÿ���ڵ���ֻ��һ�����ŵĹ���������
										if (alreadyReg.getnode().equals(node)) {// ��ʱalreadyReg��ʾ�����б����Ѵ��ڵ�reg

											if (alreadyReg.getNature() == 0 && newreg.getNature() == 1) {// ��������������IP������
																											// ԭ�����������Ǵ�OEO����
																											// ��ʱѡ���µ�������
												removereglist.add(alreadyReg);
												addreglist.add(newreg);
											} else if (alreadyReg.getNature() == 1 && newreg.getNature() == 0) {// ��������������OEO������
																												// ԭ������������IP����
																												// ��ʱѡ��ԭ����������
											} else {// ��ʾԭ�������������µ���������������һ��
													// ��ʱ��Ҫ�Ƚ����Ǳ���·������

												for (WorkandProtectRoute comwpr : wprlist) {// һ�±Ƚ��ĸ�������ʹ�õĶ�
													if (comwpr.getRegeneratorlist().contains(alreadyReg)) {
														already++;
													}
													if (comwpr.getRegeneratorlist().contains(newreg)) {
														newregg++;
													}
												}
												if (already < newregg) {// ˵�������ӵ�reg����ı�����·�Ƚ϶�
													removereglist.add(alreadyReg);
													addreglist.add(newreg);
												}
												newregg = 0;
												already = 0;
												break;
											}
										}
									}
									for (Regenerator remoReg : removereglist) {
										sharereglist.remove(remoReg);
									}
									removereglist.clear();// ԭ������Ҫ�𣿣���
									for (Regenerator addReg : addreglist) {
										if (!sharereglist.contains(addReg))
											sharereglist.add(addReg);
									}
									addreglist.clear();// ԭ������Ҫ�𣿣���

								} else {// �²�����������
									comnodelist.add(node);
									sharereglist.add(newreg);
								}
								// System.out.println("�������ĸ�����"+sharereglist.size());
								// for(Regenerator reg:sharereglist){
								// System.out.println(reg.getnode().getName());
								// }
								if (!ShareReg.contains(po))
									ShareReg.add(po); // �������µ�ҵ������Щ�ڵ�������������
							}
						}
					}
				}
			}
		}
		// part1 finish �洢�����и���·�Ͽɹ�����������λ��

		boolean success = false, passflag = false;
		int minRegNum = (int) Math.floor(routelength / 4000);
		int internode = newRoute.getNodelist().size() - 2;
		// debug
//		System.out.println();
//		file_io.filewrite2(OutFileName, "");
//		System.out.println("�ɹ����������ĸ�����" + ShareReg.size() + "��Ҫ������������������" + minRegNum);
//		file_io.filewrite2(OutFileName, "�ɹ����������ĸ�����" + ShareReg.size() + "��Ҫ������������������" + minRegNum);
//
//		for (Regenerator reg : sharereglist) {
//			System.out.print("�ɹ����������� " + reg.getnode().getName() + "�ڵ���,  ");
//			file_io.filewrite_without(OutFileName, "�ɹ����������� " + reg.getnode().getName() + "�ڵ���,  ");
//			if (reg.getNature() == 0) {
//				System.out.println("���Ǵ�OEO������ ");
//				file_io.filewrite2(OutFileName, "���Ǵ�OEO������ ");
//			}
//			if (reg.getNature() == 1) {
//				System.out.print("����IP������ ");
//				file_io.filewrite2(OutFileName, "����IP������ ");
//			}
//		}

		/*
		 * // part2 ��·���Ϲ����������ĸ���С����������������С����ʱ ����set����RSA ����regplaceoption
		 * ����ɹ����������ĸ���С��������Ҫ�����ĸ���ʱ
		 */
		if (ShareReg.size() <= minRegNum) {
			for (int s = minRegNum; s <= internode; s++) {
				if (regplaceoption.size() != 0)
					break;
				Test nOfm = new Test(s, internode); // �������м�ڵ������ѡȡm����������������
				while (nOfm.hasNext()) {
					passflag = false;
					int[] set = nOfm.next(); // �������������������λ��
					for (int num : ShareReg) {
						for (int k = 0; k < set.length; k++) {
							if (num == set[k]) {
								break;
							}
							if (k == set.length - 1 && num != set[k]) {
								passflag = true;
							}
						}
						if (passflag)
							break;
					}
					if (passflag)
						continue;// ���еĹ��������� �Ѿ��ڶ��������в����Ŀ�������Ҫ������Щ������

					// �����������ڵ�֮�����RSA ����optionѡ���·��
					rgp.RSAunderSet(sharereglist, ShareReg, set, newRoute, oplayer, ipLayer, IPflow, regplaceoption,
							wprlist, nodepair,threshold);
				}
			}
		}

		// part3 ��·���Ϲ����������ĸ���������������������С����ʱ ����set����RSA����regplaceoption
		if (ShareReg.size() > minRegNum) {
			for (int s = minRegNum; s <= internode; s++) {
				if (regplaceoption.size() != 0)
					break;
				Test nOfm = new Test(s, internode); // �������м�ڵ������ѡȡm����������������
				while (nOfm.hasNext()) {
					passflag = false;
					int[] set = nOfm.next(); // �������������������λ��
					if (s <= ShareReg.size()) { // ��ʱ����������ӿɹ��������������ѡ��
						for (int p = 0; p < set.length; p++) {
							int p1 = set[p];
							if (!ShareReg.contains(p1)) {
								passflag = true;
								break;
							}
						}
						if (passflag)
							continue;
					}
					if (s > ShareReg.size()) {
						for (int num : ShareReg) {
							for (int k = 0; k < set.length; k++) {
								if (num == set[k]) {
									break;
								}
								if (k == set.length - 1 && num != set[k]) {
									passflag = true;
								}
							}
							if (passflag)
								break;
						}
						if (passflag)
							continue;
					} // ������ҪΪ�˲���set
						// �����������ڵ�֮�����RSA
					rgp.RSAunderSet(sharereglist, ShareReg, set, newRoute, oplayer, ipLayer, IPflow, regplaceoption,
							wprlist, nodepair,threshold);
				}
			}
		}
		// debug ��ѡ��ѡ·��֮ǰ�ȹ۲����������ָ��
		if (regplaceoption.size() != 0) {
			for (RouteAndRegPlace DebugRegRoute : regplaceoption) {
				ArrayList<Integer> NewRegList = new ArrayList<>();
				file_io.filewrite2(OutFileName, " ");
				//System.out.println();
				// ������ɹ���������
				if (DebugRegRoute.getUsedShareReg() != null) {
					file_io.filewrite2(OutFileName, "�ɹ���������� ");
					for (Regenerator reg : DebugRegRoute.getUsedShareReg()) {
						if (reg.getNature() == 0) {
							file_io.filewrite2(OutFileName, reg.getnode().getName() + "  OEO������");
						}
						if (reg.getNature() == 1) {
							file_io.filewrite2(OutFileName, reg.getnode().getName() + "  IP������");
						}
					}
				}
				// �����жϿɷ����ɹ�����Ϊ�½�
				//System.out.println("�½��������� ");
				file_io.filewrite2(OutFileName, "�½��������� ");
				for (int Reg : DebugRegRoute.getregnode()) {
					boolean share = false;
					Node NewRegNode = DebugRegRoute.getRoute().getNodelist().get(Reg);
					for (Regenerator ShareReg2 : DebugRegRoute.getUsedShareReg()) {
						if (ShareReg2.getnode().getName().equals(NewRegNode.getName())) {// �ĵ��ϵ����������Թ��������½�
							share = true;
							break;
						}
					}
					if (!share) {
						NewRegList.add(Reg);
						if (DebugRegRoute.getIPRegnode().contains(Reg)) { // �½�����������IP������
							//System.out.print(NewRegNode.getName() + "  IP������  ");
							file_io.filewrite_without(OutFileName, NewRegNode.getName() + "  IP������  ");
						} else {
							//System.out.print(NewRegNode.getName() + "  OEO������  ");
							file_io.filewrite_without(OutFileName, NewRegNode.getName() + "  OEO������  ");
						}
					}
				}
				file_io.filewrite2(OutFileName, " ");
				//System.out.println("ʣ��������� " + DebugRegRoute.getNumRemainFlow());
				file_io.filewrite2(OutFileName, "ʣ��������� " + DebugRegRoute.getNumRemainFlow());
				//System.out.println("ʹ�õ�newFS������ " + DebugRegRoute.getnewFSnum());
				file_io.filewrite2(OutFileName, "ʹ�õ�newFS������ " + DebugRegRoute.getnewFSnum());
				DebugRegRoute.setNewRegList(NewRegList); // ͳ��ÿ����ѡ·����ʹ�õ�������������
			}
		}

		// part4 �Բ����ı�ѡ��·����ɸѡ���Ҷ�ѡ����·����IP��·
		if (regplaceoption.size() > 0) {
			success = true;
			RouteAndRegPlace finalRoute = new RouteAndRegPlace(null, 1);
			if (regplaceoption.size() > 1)
				finalRoute = rgp2.optionRouteSelect(regplaceoption, wprlist);// �ڷ��������ļ���·����ѡȡ��ѵ�·����Ϊfinaroute
			else
				finalRoute = regplaceoption.get(0);
			// �������Ը�������·����RSA
			rgp2.FinalRouteRSA(nodepair, finalRoute, oplayer, ipLayer, IPflow, wprlist, provirtuallinklist, ShareReg,
					sharereglist, request,ProLengthList);
			// ����finalroute�����������ڵ�洢����
		}
		if (regplaceoption.size() == 0) {
			success = false;
		}
		//System.out.println();
		file_io.filewrite2(OutFileName, "");
		if (success) {
			//System.out.print("����·�����������óɹ�����RSA,���õ�����������Ϊ");
			file_io.filewrite_without(OutFileName, "����·�����������óɹ�����RSA,���õ�����������Ϊ");
			for (WorkandProtectRoute wpr : wprlist) {
				if (wpr.getdemand().equals(nodepair)) {
					wpr.setrequest(request);
					//System.out.println(wpr.getRegeneratorlist().size());
					file_io.filewrite(OutFileName, wpr.getRegeneratorlist().size());
				}
			}

		} else {
			//System.out.println("����·���������������ɹ���·��������");
			file_io.filewrite2(OutFileName, "����·���������������ɹ���·��������");
		}
		return success;
	}// ����������

	public void RSAunderSet(ArrayList<Regenerator> sharereglist, ArrayList<Integer> ShareReg, int[] set,
			LinearRoute newRoute, Layer oplayer, Layer ipLayer, int IPflow, ArrayList<RouteAndRegPlace> regplaceoption,
			ArrayList<WorkandProtectRoute> wprlist, NodePair nodepair,float threshold) {
		// �����µ������� ���ҿ�����ֵ
		boolean partworkflag = false, RSAflag = false, regflag = false;
		double length = 0;
		file_out_put file_io = new file_out_put();
		ArrayList<Link> linklist = new ArrayList<>();
		int FStotal = 0, n = 0;
		ProregeneratorPlace rp = new ProregeneratorPlace();
		ArrayList<Float> RemainRatio = new ArrayList<>();// ��¼ÿ����·��ʣ���flow
		float NumRemainFlow = 0;
		ArrayList<Regenerator> UseShareReg = new ArrayList<>();

		for (int i = 0; i < set.length + 1; i++) {// RSA�Ĵ������������ĸ�����1
			if (!partworkflag && RSAflag)
				break;
			if (i < set.length) {
				//System.out.println("****************��������λ��Ϊ��" + set[i]); // set�������Ӧ���ǽڵ��λ��+1��
				file_io.filewrite2(OutFileName, "****************��������λ��Ϊ��" + set[i]);
			} else {
				//System.out.println("************���һ�����������ս��֮���RSA ");
				file_io.filewrite2(OutFileName, "************���һ�����������ս��֮���RSA ");
				regflag = true;
			}
			do {// ͨ��һ��
				Node nodeA = newRoute.getNodelist().get(n);
				Node nodeB = newRoute.getNodelist().get(n + 1);
				Link link = oplayer.findLink(nodeA, nodeB);
//				System.out.println(link.getName());
				file_io.filewrite2(OutFileName, link.getName());
				length = length + link.getLength();
				linklist.add(link);
				n = n + 1;
				if (!regflag) {// δ�������һ��·����RSA
					if (n == set[i]) {
						ParameterTransfer pt = new ParameterTransfer();
						partworkflag = rp.vertify(IPflow, length, linklist, oplayer, ipLayer, wprlist, nodepair, pt);
						RemainRatio.add(pt.getRemainFlowRatio());
						NumRemainFlow = NumRemainFlow + pt.getNumremainFlow();
						FStotal = FStotal + nodepair.getSlotsnum();
						length = 0;
						RSAflag = true;
						linklist.clear();
						break;
					}
				}
				if (n == newRoute.getNodelist().size() - 1) {
					ParameterTransfer pt = new ParameterTransfer();
					partworkflag = rp.vertify(IPflow, length, linklist, oplayer, ipLayer, wprlist, nodepair, pt);
					NumRemainFlow = NumRemainFlow + pt.getNumremainFlow();
					RemainRatio.add(pt.getRemainFlowRatio());
					FStotal = FStotal + nodepair.getSlotsnum();
				}
				if (!partworkflag && RSAflag)// ���֮ǰ����·�Ѿ�RSAʧ�� ʣ�µ���·Ҳû��RSA�ı�Ҫ
					break;
			} while (n != newRoute.getNodelist().size() - 1);
			// ���·�ɳɹ��򱣴��·�ɶ����������ķ���
		}
		if (partworkflag) {// ˵����set�¿���RSA ��ʱ��Ҫ�����µ�������
			RouteAndRegPlace rarp = new RouteAndRegPlace(newRoute, 1);
			rarp.setnewFSnum(FStotal);
			ArrayList<Integer> setarray = new ArrayList<>();
			ArrayList<Integer> IPRegarray = new ArrayList<>();
			for (int k = 0; k < set.length; k++) {
				setarray.add(set[k]);
				if (!ShareReg.contains(set[k])) {// �����������½��� ��Ҫ�ж������� ����ǹ����
													// ��ô���������Ѿ�ȷ�� ����Ҫ�ж�
					if (RemainRatio.get(k) >= threshold || RemainRatio.get(k + 1) >= threshold) {// ֻҪ������ǰ����ߺ�����һ��δ���ʹ�������IP������
						IPRegarray.add(set[k]);// �洢IP���������ýڵ�
					}
				} else {// ʹ���˸ÿɹ����������
					for (Regenerator UsedShareReg : sharereglist) {
						if (UsedShareReg.getnode().getName().equals(newRoute.getNodelist().get(set[k]).getName())) {
							UseShareReg.add(UsedShareReg);// ����ʹ���˵Ĺ���������
							break;
						}
					}
				}
			}
//			file_io.filewrite2(OutFileName, " ");
			rarp.setUsedShareReg(UseShareReg); // ��¼ʹ�õĹ���������
			rarp.setIPRegnode(IPRegarray);// ע�������IP������������ȫ���������� �����½���IP������
			rarp.setregnode(setarray);
			rarp.setregnum(setarray.size());
			rarp.setNumRemainFlow(NumRemainFlow);
			regplaceoption.add(rarp);
			//System.out.println("��·���ɹ�RSA, �ѳɹ�RSA������Ϊ��" + regplaceoption.size());// �������ĸ����ӽ�ȥ
			file_io.filewrite2(OutFileName, "��·���ɹ�RSA, �ѳɹ�RSA������Ϊ��" + regplaceoption.size());
		}
	}

	public Boolean vertify(int IPflow, double routelength, ArrayList<Link> linklist, Layer oplayer, Layer iplayer,
			ArrayList<WorkandProtectRoute> wprlist, NodePair nodepair, ParameterTransfer RemainRatio) {
		// �ж�ĳһ��transparent��·�Ƿ��ܹ��ɹ�RSA ���Ҽ�¼��ʹ�õ�FS����
		// workOrproflag=true��ʱ���ʾ�ǹ��� false��ʱ���ʾ����
		file_out_put file_io = new file_out_put();
		nodepair.setSlotsnum(0);
		double X = 1;
		opProGrooming opg = new opProGrooming();
		int slotnum = 0;
		boolean opworkflag = false;
		if (routelength > 4000) {
			//System.out.println("��·�����޷�RSA");
			file_io.filewrite2(OutFileName, "��·�����޷�RSA");
		}
		if (routelength < 4000) {
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
			// System.out.println("ÿ����·�����slot��Ϊ�� " + slotnum);
			// file_io.filewrite2(OutFileName, "ÿ����·�����slot��Ϊ�� " + slotnum);
	if(slotnum<Constant.MinSlotinLightpath){
				slotnum=Constant.MinSlotinLightpath;
			}
			ArrayList<Integer> index_wave = new ArrayList<Integer>();
			index_wave = opg.FSassignOnlink(linklist, wprlist, nodepair, slotnum, oplayer);// �ڿ��ǹ��������·���Ƶ��
			
			if (index_wave.size() != 0) {
				opworkflag = true;
				RemainRatio.setRemainFlowRatio((float) ((slotnum * X - IPflow) / (slotnum * X)));
				RemainRatio.setNumremainFlow((float) (slotnum * X - IPflow));
//				System.out.println("����ͨ���������� " + slotnum * X + "   ҵ������ " + IPflow + "   ʣ����������� "
//						+ RemainRatio.getRemainFlowRatio() + "   ʣ���ҵ������" + RemainRatio.getNumremainFlow());
//				file_io.filewrite2(OutFileName,
//						"����ͨ���������� " + slotnum * X + "   ҵ������ " + IPflow + "   ʣ����������� "
//								+ RemainRatio.getRemainFlowRatio() + "   ʣ���ҵ������" + RemainRatio.getNumremainFlow()
//								+ "  ��Ҫ��FS������" + slotnum);
			} else {
				//System.out.println("Ƶ�ײ����޷�RSA");
				file_io.filewrite2(OutFileName, "Ƶ�ײ����޷�RSA");
			}

		}

		return opworkflag;
	}

	public void FinalRouteRSA(NodePair nodepair, RouteAndRegPlace finalRoute, Layer oplayer, Layer ipLayer, int IPflow,
			ArrayList<WorkandProtectRoute> wprlist, ArrayList<VirtualLink> provirtuallinklist,
			ArrayList<Integer> ShareReg, ArrayList<Regenerator> sharereglist, Request request,ArrayList<Double> ProLengthList) {
		// ��������·�� ͨ���������������� ����RSA���Ƿ���IP��·�� ���Ҵ洢Wpr ����������λ�� ����
		ParameterTransfer pt = new ParameterTransfer();
		file_out_put file_io = new file_out_put();
		ArrayList<Link> alllinklist = new ArrayList<>();
		ArrayList<Regenerator> regthinglist = new ArrayList<>();
		Test t = new Test();
		file_io.filewrite2(OutFileName, "");
		//System.out.println("");
		//System.out.println("������·������RSA��");
		file_io.filewrite2(OutFileName, "������·������RSA��");
		pt.setStartNode(finalRoute.getRoute().getNodelist().get(0));// �������ø���·����ʼ�ڵ�
		pt.setMinRemainFlowRSA(10000);// ���ȳ�ʼ��

		finalRoute.getRoute().OutputRoute_node(finalRoute.getRoute());
		int count = 0;
		double length2 = 0;
		boolean regflag2 = false;
		ArrayList<Link> linklist2 = new ArrayList<>();
		ArrayList<FSshareOnlink> FSoneachLink = new ArrayList<FSshareOnlink>();
		file_io.filewrite2(OutFileName, "!!��������·����RSA����·��"  );                      
	
		for (int i = 0; i < finalRoute.getregnum() + 1; i++) {
			if (i >= finalRoute.getregnum())
				regflag2 = true;
			do {
				Node nodeA = finalRoute.getRoute().getNodelist().get(count);
				Node nodeB = finalRoute.getRoute().getNodelist().get(count + 1);

				Link link = oplayer.findLink(nodeA, nodeB);
				file_io.filewrite_without(OutFileName, link.getName()+"  ");
				length2 = length2 + link.getLength();
				linklist2.add(link);
				count = count + 1;
				if (!regflag2) {// δ�������һ��·����RSA
					if (count == finalRoute.getregnode().get(i)) {
						pt.setEndNode(finalRoute.getRoute().getNodelist().get(count));// ������ֹ�ڵ�

						if (ShareReg.contains(count)) {// ���жϸ��������Ƿ��ǹ����
							for (Regenerator reg : sharereglist) {
								if (reg.getnode().getName()
										.equals(finalRoute.getRoute().getNodelist().get(count).getName())) {
									if (reg.getNature() == 0) {// OEO������
										Prolinkcapacitymodify(false, IPflow, length2, linklist2, oplayer, ipLayer,
												provirtuallinklist, wprlist, nodepair, FSoneachLink, request,
												sharereglist, pt);// ��ʱ��n�����������
										ProLengthList.add(length2);
									} else if (reg.getNature() == 1) {
										Prolinkcapacitymodify(true, IPflow, length2, linklist2, oplayer, ipLayer,
												provirtuallinklist, wprlist, nodepair, FSoneachLink, request,
												sharereglist, pt);// ��ʱ��n�����������
										ProLengthList.add(length2);
									}
								}
							}
						} else {// �����������ǹ���������
							if (finalRoute.getIPRegnode().contains(count)) {// �½�����������IP������
								Prolinkcapacitymodify(true, IPflow, length2, linklist2, oplayer, ipLayer,
										provirtuallinklist, wprlist, nodepair, FSoneachLink, request, sharereglist, pt);
								ProLengthList.add(length2);
							} else {// �½����������Ǵ�OEO������
								Prolinkcapacitymodify(false, IPflow, length2, linklist2, oplayer, ipLayer,
										provirtuallinklist, wprlist, nodepair, FSoneachLink, request, sharereglist, pt);
								ProLengthList.add(length2);
							}
						}

						for (Link addlink : linklist2) {
							alllinklist.add(addlink);
						}
						length2 = 0;
						linklist2.clear();
						break;
					}
				}
				if (count == finalRoute.getRoute().getNodelist().size() - 1) {// ���һ����·��RSA
					pt.setEndNode(finalRoute.getRoute().getNodelist().get(count));// ������ֹ�ڵ�
					Prolinkcapacitymodify(true, IPflow, length2, linklist2, oplayer, ipLayer, provirtuallinklist,
							wprlist, nodepair, FSoneachLink, request, sharereglist, pt);// ΪĿ�Ľڵ�ǰ��ʣ����·����RSA
					ProLengthList.add(length2);
					for (Link addlink : linklist2) {
						alllinklist.add(addlink);
					}
					linklist2.clear();
				}
			} while (count != finalRoute.getRoute().getNodelist().size() - 1);
		}

		ArrayList<Regenerator> shareReg = new ArrayList<>();
		ArrayList<Regenerator> newReg = new ArrayList<>();
		HashMap<Integer, Regenerator> hashregthinglist = new HashMap<Integer, Regenerator>();
		//System.out.println("������·����·�����������ڵ��������" + finalRoute.getregnode().size());
		file_io.filewrite2(OutFileName, "��������·�����������ڵ��������" + finalRoute.getregnode().size());

		for (int i : finalRoute.getregnode()) {// ȡ��·���������������ڵ�
			Node regnode = finalRoute.getRoute().getNodelist().get(i);// �ж��������ǹ������Ļ����½���
			file_io.filewrite_without(OutFileName, regnode.getName() + " �ڵ��� ������������");

			if (ShareReg.contains(i)) {// ����������ͨ������õ���
				for (Regenerator r : sharereglist) {
					if (r.getnode().equals(regnode)) {
						if (r.getNature() == 0)
							file_io.filewrite_without(OutFileName, "��ͨ������õ��Ĵ�OEO������");
						else if (r.getNature() == 1)
							file_io.filewrite_without(OutFileName, "��ͨ������õ���IP������");
						regthinglist.add(r);// �ҳ��ɹ���������� ��������������
						hashregthinglist.put(t.nodeindexofroute(regnode, finalRoute.getRoute()), r); // ����Hashmap!!!
						shareReg.add(r);// ��������ڸ���·�Ŀɹ�������������
					}
				}
			} else {// ��ʾ�����Թ��� ��ʱҪ�����µ������� ���Ҹı�node�����������ĸ���
				regnode.setregnum(regnode.getregnum() + 1);
				int index = regnode.getregnum();
				Regenerator reg = new Regenerator(regnode);
				if (finalRoute.getIPRegnode().contains(i)) {
					reg.setNature(1);// �����½�����������IP������
					file_io.filewrite_without(OutFileName, "���½���IP������");
				} else {
					reg.setNature(0); // �����½�����������OEO������
					file_io.filewrite_without(OutFileName, "���½���OEO������");
				}
				reg.setindex(index);
				regthinglist.add(reg);
				hashregthinglist.put(t.nodeindexofroute(regnode, finalRoute.getRoute()), reg); // ����Hashmap!!!
				newReg.add(reg);
			}
		}
	

		for (WorkandProtectRoute wpr : wprlist) {
			if (wpr.getdemand().equals(nodepair)) {
				wpr.setproroute(finalRoute.getRoute());
				wpr.setRegProLengthList(ProLengthList);
				wpr.setFSoneachLink(FSoneachLink);
				wpr.setregthinglist(hashregthinglist);
				wpr.setRegeneratorlist(regthinglist);
				wpr.setprolinklist(alllinklist);
				wpr.setnewreglist(newReg);
				wpr.setsharereglist(finalRoute.getUsedShareReg());
				wpr.setprovirtuallinklist(provirtuallinklist);
			}
		}
	}

	public boolean Prolinkcapacitymodify(Boolean IPorOEO, int IPflow, double routelength, ArrayList<Link> linklist,
			Layer oplayer, Layer iplayer, ArrayList<VirtualLink> provirtuallinklist,
			ArrayList<WorkandProtectRoute> wprlist, NodePair nodepair, ArrayList<FSshareOnlink> FSoneachLink,
			Request request, ArrayList<Regenerator> sharereglist, ParameterTransfer pt) {
		// ����������· �������� RSA
		double X = 1;
		opProGrooming opg = new opProGrooming();
		int slotnum = 0, shareFS = 0;
		boolean opworkflag = false, shareFlag = true;
		Node srcnode = new Node(null, 0, null, iplayer, 0, 0);
		Node desnode = new Node(null, 0, null, iplayer, 0, 0);
		file_out_put file_io = new file_out_put();

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
	if(slotnum<Constant.MinSlotinLightpath){
				slotnum=Constant.MinSlotinLightpath;
			}
		opworkflag = true;
		double length1 = 0;
		double cost = 0;
		ArrayList<Integer> index_wave = new ArrayList<Integer>();
		index_wave = opg.FSassignOnlink(linklist, wprlist, nodepair, slotnum, oplayer);// �ڿ��ǹ��������·���Ƶ��
																						// ��δʵʩռ��

		for (Link link : linklist) {
			ArrayList<Integer> index_wave1 = new ArrayList<Integer>();
			length1 = length1 + link.getLength();
			cost = cost + link.getCost();
			ResourceOnLink ro = new ResourceOnLink(request, link, index_wave.get(0), slotnum);
			link.setMaxslot(slotnum + link.getMaxslot());
			//System.out.print("��· " + link.getName() + "�Ϸ����FSΪ ");
			file_io.filewrite_without(OutFileName, "��· " + link.getName() + "�Ϸ����FSΪ ");
			file_io.filewrite2(OutFileName, "");
			int m = index_wave.get(0);
			for (int n = 0; n < slotnum; n++) {
				index_wave1.add(m);
				//System.out.print(m);
				file_io.filewrite_without(OutFileName, m + "  ");
				m++;
			}
			//System.out.println();
			file_io.filewrite2(OutFileName, " ");
			FSshareOnlink fsonLink = new FSshareOnlink(link, index_wave1);
			FSoneachLink.add(fsonLink);
		}

		// ������·Ƶ�׷������ ���濪ʼ����IP���·
		// ����ȡ��linklist�����ǰ������·�����������·
		if (IPorOEO) {
			Node startnode = pt.getStartNode();
			Node endnode = pt.getEndNode();

			for (int num = 0; num < iplayer.getNodelist().size() - 1; num++) {// ��IP����Ѱ��transparent��·������
				boolean srcflag = false, desflag = false;
				HashMap<String, Node> map = iplayer.getNodelist();
				Iterator<String> iter = map.keySet().iterator();
				while (iter.hasNext()) {
					Node node = (Node) (map.get(iter.next()));
					if (node.getName().equals(startnode.getName())) {
						srcnode = node;
						srcflag = true;
					}
					if (node.getName().equals(endnode.getName())) {
						desnode = node;
						desflag = true;
					}
				}
				if (srcflag && desflag)
					break;
			}
			pt.setStartNode(desnode);
			int index = iplayer.getLinklist().size();// ��Ϊiplayer�����link��һ��һ������ȥ�Ĺ���������index

			if (srcnode.getIndex() > desnode.getIndex()) {
				Node internode = srcnode;
				srcnode = desnode;
				desnode = internode;
			}
			String name = srcnode.getName() + "-" + desnode.getName();
			// file_io.filewrite2(OutFileName,"��ʱ��ԭ�ڵ�Ϊ:"+srcnode.getName()+"
			// �ս��Ϊ"+desnode.getName());
			Link finlink = iplayer.findLink(srcnode, desnode);
			Link createlink = new Link(null, 0, null, iplayer, null, null, 0, 0);
			boolean findflag = false;
			try {
				System.out.println(finlink.getName());
				findflag = true;
			} catch (java.lang.NullPointerException ex) {
				System.out.println("IP ��û�и���·��Ҫ�½���·");
				file_io.filewrite2(OutFileName, "IP ��û�и���·��Ҫ�½���·");
				file_io.filewrite2(OutFileName, "��ʱ��ԭ�ڵ�Ϊ:" + srcnode.getName() + "  �ս��Ϊ" + desnode.getName());
				createlink = new Link(name, index, null, iplayer, srcnode, desnode, length1, cost);
				iplayer.addLink(createlink);
			}

			VirtualLink Vlink = new VirtualLink(srcnode.getName(), desnode.getName(), 1, 0);
			if (!shareFlag || shareFS <= slotnum) {// ��ʾ��linklist������·���ܹ���FS���߾����Թ���ʱ�����FSС����Ҫ��FS
				Vlink.setnature(1);
				Vlink.setUsedcapacity(Vlink.getUsedcapacity() + IPflow);
				Vlink.setFullcapacity(slotnum * X);// �������flow�Ǵ����������
				Vlink.setRestcapacity(Vlink.getFullcapacity() - Vlink.getUsedcapacity());
				// Vlink.setlength(length1);
				Vlink.setcost(cost);
				Vlink.setPhysicallink(linklist);
				provirtuallinklist.add(Vlink);
			}
			if (shareFS > slotnum) {// ��ʾ��linklist������·���ܹ���FS���߾����Թ���ʱ�����FSС����Ҫ��FS
				Vlink.setnature(1);
				Vlink.setUsedcapacity(Vlink.getUsedcapacity() + IPflow);
				Vlink.setFullcapacity(shareFS * X);// �������flow�Ǵ����������
				Vlink.setRestcapacity(Vlink.getFullcapacity() - Vlink.getUsedcapacity());
				Vlink.setlength(length1);
				Vlink.setcost(cost);
				Vlink.setPhysicallink(linklist);
				provirtuallinklist.add(Vlink);
			}
			file_io.filewrite2(OutFileName, "");
			if (findflag) {// �����IP�����Ѿ��ҵ�����·
				finlink.getVirtualLinkList().add(Vlink);
				//System.out.println("IP���Ѵ��ڵ���· " + finlink.getName() + "    Ԥ����flow��  " + Vlink.getRestcapacity());
				//System.out.println(
						//"������·�ڹ���½�����·��  " + finlink.getName() + "  �ϵ�������·������ " + finlink.getVirtualLinkList().size());
				file_io.filewrite2(OutFileName,
						"IP���Ѵ��ڵ���· " + finlink.getName() + "    Ԥ����flow��  " + Vlink.getRestcapacity());
				file_io.filewrite2(OutFileName,
						"������·�ڹ���½�����·��  " + finlink.getName() + "  �ϵ�������·������ " + finlink.getVirtualLinkList().size());
			} else {
				createlink.getVirtualLinkList().add(Vlink);
				//System.out.println("IP�����½���· " + createlink.getName() + "    Ԥ����flow��  " + Vlink.getRestcapacity());
				//System.out.println("������·�ڹ���½�����·��  " + createlink.getName() + "  �ϵ�������·������ "
						//+ createlink.getVirtualLinkList().size());
				file_io.filewrite2(OutFileName,
						"IP�����½���· " + createlink.getName() + "    Ԥ����flow��  " + Vlink.getRestcapacity());
				file_io.filewrite2(OutFileName, "������·�ڹ���½�����·��  " + createlink.getName() + "  �ϵ�������·������ "
						+ createlink.getVirtualLinkList().size());
			}
			pt.setMinRemainFlowRSA(10000);
		}
		return opworkflag;
	}

	public RouteAndRegPlace optionRouteSelect(ArrayList<RouteAndRegPlace> regplaceoption,ArrayList<WorkandProtectRoute> wprlist) throws IOException {
		// ���㷨�ĺ���˼�� ���ﻹδ����
		// ͨ�����ĸ����㷨���ڲ�ͬ��������ѡ�� ���ﵽ�������ܵ�����
		// ��һ����дһ��final routeRSA + ��ͬ�������½���IP��· ���Ҹ��ı��㷨
		file_out_put file_io = new file_out_put();
		RouteAndRegPlace finalRoute = new RouteAndRegPlace(null, 1);
		if (regplaceoption.size() == 1) {
			finalRoute = regplaceoption.get(0);
		} else if (regplaceoption.size() != 0) {
			ArrayList<RouteAndRegPlace> RemoveRoute = new ArrayList<>();

			for (int standard = 0; standard < regplaceoption.size() - 1; standard++) {// ��׼
				RouteAndRegPlace StandardRoute = regplaceoption.get(standard);
				int StandardIP = 0, CompareIP = 0;
				if (RemoveRoute.contains(StandardRoute))
					continue;

				for (int k = standard + 1; k < regplaceoption.size(); k++) {// �Ƚ�
					RouteAndRegPlace CompareRoute = regplaceoption.get(k);
					if (RemoveRoute.contains(CompareRoute))
						continue;

					for (Regenerator shareReg : StandardRoute.getUsedShareReg()) {
						if (shareReg.getNature() == 1)
							StandardIP++;
					}
//					System.out.print("��һ��ɸѡ ��׼·�ɹ����IP����������Ϊ ��");
//					file_io.filewrite2(OutFileName, "��һ��ɸѡ ��׼·�ɹ����IP����������Ϊ ��"+StandardIP);
//					System.out.println(StandardIP);
					for (Regenerator shareReg : CompareRoute.getUsedShareReg()) {
						if (shareReg.getNature() == 1)
							CompareIP++;
					}
//					System.out.print("��һ��ɸѡ �Ƚ�·�ɹ����IP����������Ϊ��");
//					System.out.println(CompareIP);
//					file_io.filewrite2(OutFileName, "��һ��ɸѡ �Ƚ�·�ɹ����IP����������Ϊ��"+CompareIP);
					if (StandardRoute.getNewRegList().size() == 0) {// ������ȫ�ǿ�����õ���
																	// ���ʱ����ѡ��IP���������·��
						if (StandardIP < CompareIP) {
							RemoveRoute.add(StandardRoute);// ɾȥ����IP�������ٵ�·��
							break;
						}
						if (StandardIP > CompareIP) {
							RemoveRoute.add(CompareRoute);// �Ƚϵ�û�б�׼��
						}
					} else {// ���½��������� ˵������������ȫ��ʹ�� ��ʱ����Ҫ�жϹ�����������ѡȡ
						// ���½�����������ʹ��OEO���������·��
						if (StandardIP > CompareIP) {
							RemoveRoute.add(StandardRoute);// ɾȥ�½�IP���������·��
							break;
						}
						if (StandardIP < CompareIP) {
							RemoveRoute.add(CompareRoute);// �Ƚϵ�û�б�׼��
						}
					}
				}
			}
			for (RouteAndRegPlace rag : RemoveRoute) {
				regplaceoption.remove(rag);
			}
			RemoveRoute.clear();
			// �ڶ���Ƚ�
			if (regplaceoption.size() == 1) {
				finalRoute = regplaceoption.get(0);
			} else {
				for (int standard = 0; standard < regplaceoption.size() - 1; standard++) {
					RouteAndRegPlace StandardRoute_2 = regplaceoption.get(standard);
					if (RemoveRoute.contains(StandardRoute_2))
						continue;
//					System.out.print("�ڶ���ɸѡ ��׼·��ʣ������Ϊ��");
//					System.out.println(StandardRoute_2.getNumRemainFlow());
//					file_io.filewrite2(OutFileName, "�ڶ���ɸѡ ��׼·��ʣ��������"+StandardRoute_2.getNumRemainFlow());
					for (int k = standard + 1; k < regplaceoption.size(); k++) {
						RouteAndRegPlace CompareRoute_2 = regplaceoption.get(k);
						if (RemoveRoute.contains(CompareRoute_2))
							continue;
//						System.out.print("�ڶ���ɸѡ �Ƚ�·��ʣ������Ϊ��");
//						System.out.println(CompareRoute_2.getNumRemainFlow());
//						file_io.filewrite2(OutFileName, "�ڶ���ɸѡ �Ƚ�·��ʣ��������"+CompareRoute_2.getNumRemainFlow());
						if (StandardRoute_2.getNumRemainFlow() < CompareRoute_2.getNumRemainFlow()) {
							RemoveRoute.add(StandardRoute_2);// ɾȥʣ�������ٵ�·��
							break;
						}
						if (StandardRoute_2.getNumRemainFlow() > CompareRoute_2.getNumRemainFlow()) {
							RemoveRoute.add(CompareRoute_2);// �Ƚϵ�û�б�׼��
						}
					}
				}
				for (RouteAndRegPlace rag : RemoveRoute) {
					regplaceoption.remove(rag);
				}
				RemoveRoute.clear();

				// ������ѡ�� ѡ����ʹ��FS���ٵ�·��
				if (regplaceoption.size() == 1) {
					finalRoute = regplaceoption.get(0);
				} else {
					for (int standard = 0; standard < regplaceoption.size() - 1; standard++) {
						RouteAndRegPlace StandardRoute_3 = regplaceoption.get(standard);
						if (RemoveRoute.contains(StandardRoute_3))
							continue;
//						System.out.print("������ɸѡ ��׼·��ʹ�õ�FSΪ��");
//						System.out.println(StandardRoute_3.getnewFSnum());
//						file_io.filewrite2(OutFileName, "������ɸѡ ��׼·��ʹ�õ�FSΪ��"+StandardRoute_3.getnewFSnum());
						
						for (int k = standard + 1; k < regplaceoption.size(); k++) {
							RouteAndRegPlace CompareRoute_3 = regplaceoption.get(k);
							if (RemoveRoute.contains(CompareRoute_3))
								continue;
//							System.out.print("������ɸѡ �Ƚ�·��ʹ�õ�FSΪ��");
//							System.out.println(CompareRoute_3.getnewFSnum());
//							file_io.filewrite2(OutFileName, "������ɸѡ �Ƚ�·��ʹ�õ�FSΪ��"+CompareRoute_3.getnewFSnum());
							if (StandardRoute_3.getnewFSnum() > CompareRoute_3.getnewFSnum()) {
								RemoveRoute.add(StandardRoute_3);// ɾȥʹ��FS�϶��·��
								break;
							}
							if (StandardRoute_3.getnewFSnum() < CompareRoute_3.getnewFSnum()) {
								RemoveRoute.add(CompareRoute_3);// �Ƚϵ�û�б�׼��
							}
						}
					}
					for (RouteAndRegPlace rag : RemoveRoute) {
						regplaceoption.remove(rag);
					}
					RemoveRoute.clear();
				}
				finalRoute = regplaceoption.get(0);// ���ղ����Ƿ�ֻʣһ����· ��ѡ���һ����Ϊ������·

				// file_io.filewrite2(OutFileName, "��������������ʱ��nodepairΪ"+nodepair.getName());
				// if(nodepair.getFinalRoute()!=null){
				// file_io.filewrite2(OutFileName, "������������ �ù�����·��Ҫ������");
				// }
			}
		}
		return finalRoute;
	}
}
