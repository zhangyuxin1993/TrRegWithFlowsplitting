package MainFunction;

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

public class RegeneratorPlace {
	public int newFS = 0;
	static int totalregNum = 0;
	String OutFileName = Mymain.OutFileName;

	public boolean regeneratorplace(int IPflow, double routelength, LinearRoute newRoute, Layer oplayer, Layer ipLayer,
			ArrayList<WorkandProtectRoute> wprlist, NodePair nodepair,ArrayList<Double> RegLengthList,ArrayList<RequestOnWorkLink> rowList,float threshold,
			ParameterTransfer ptOftransp) { // ע�����������������ļ���
		// /*
		// �ڶ��ַ������ж�һ��·������ʹ�õ��������ĸ��� Ȼ������е������ѡ�������� ���õ�λ��
		file_out_put file_io = new file_out_put();
		int minRegNum = (int) Math.floor(routelength / 4000);// ���ٵ��������ĸ���
		int internode = newRoute.getNodelist().size() - 2;
		int FStotal = 0, n = 0;
		double length = 0;
		ArrayList<Link> linklist = new ArrayList<>();
		boolean partworkflag = false, RSAflag = false, regflag = false, success = false;
		ArrayList<RouteAndRegPlace> regplaceoption = new ArrayList<>();
		RouteAndRegPlace finalRoute = new RouteAndRegPlace(null, 0);
		
		// �ҵ����п��Գɹ�·�ɵ�·�� part1
		for (int s = minRegNum; s <= internode; s++) {
			if (partworkflag || regplaceoption.size() != 0)// ����������������ٵ�ʱ���Ѿ�����RSA��ô�Ͳ���Ҫ�����������ĸ���
				break;
			Test nOfm = new Test(s, internode); // �������м�ڵ������ѡȡm����������������
			while (nOfm.hasNext()) {
				RSAflag = false;
				regflag = false;
				partworkflag = false;
				n = 0;
				length = 0;
				FStotal = 0;
				linklist.clear();
				int[] set = nOfm.next(); // �������������������λ��
				ArrayList<Float> RemainRatio = new ArrayList<>();// ��¼ÿ����·��ʣ���flow
				float NumRemainFlow = 0;

				for (int i = 0; i < set.length + 1; i++) {// RSA�Ĵ������������ĸ�����1  ��ĳһ����·��ĳ��set�������½���RSA
					if (!partworkflag && RSAflag)
						break;
					if (i < set.length) {
						//System.out.println("****************������������λ��Ϊ��" + set[i]); // set�������Ӧ���ǽڵ��λ��+1��
						file_io.filewrite2(OutFileName, "****************������������λ��Ϊ��" + set[i]);
					} else {
						//System.out.println("************���һ�����������ս��֮���RSA ");
						file_io.filewrite2(OutFileName, "************���һ�����������ս��֮���RSA ");
						regflag = true;
					}
					do {// ͨ��һ��
						Node nodeA = newRoute.getNodelist().get(n);
						Node nodeB = newRoute.getNodelist().get(n + 1);
						Link link = oplayer.findLink(nodeA, nodeB);
						//System.out.println(link.getName());
						file_io.filewrite2(OutFileName, link.getName());
						length = length + link.getLength();
						linklist.add(link);
						n = n + 1;
						if (!regflag) {// δ�������һ��·����RSA
							if (n == set[i]) {
								ParameterTransfer pt = new ParameterTransfer();
								partworkflag = vertify(IPflow, length, linklist, oplayer, ipLayer, wprlist, nodepair,pt);//
								RemainRatio.add(pt.getRemainFlowRatio());
								NumRemainFlow = NumRemainFlow + pt.getNumremainFlow();
								FStotal = FStotal + newFS;
								length = 0;
								RSAflag = true;
								linklist.clear();
								break;
							}
						}
						if (n == newRoute.getNodelist().size() - 1) {// ���һ��·��
							ParameterTransfer pt = new ParameterTransfer();
							partworkflag = vertify(IPflow, length, linklist, oplayer, ipLayer, wprlist, nodepair, pt);// ��ʱ��n�����������
							RemainRatio.add(pt.getRemainFlowRatio());
							NumRemainFlow = NumRemainFlow + pt.getNumremainFlow();
							FStotal = FStotal + newFS;
						}
						if (!partworkflag && RSAflag)
							break;
					} while (n != newRoute.getNodelist().size() - 1);
					// ���·�ɳɹ��򱣴��·�ɶ����������ķ���
				}
				if (partworkflag) {// ��һ���ض���set�½���·�ɽ���
					RouteAndRegPlace rarp = new RouteAndRegPlace(newRoute, 0);
					rarp.setnewFSnum(FStotal);
					ArrayList<Integer> setarray = new ArrayList<>();
					ArrayList<Integer> IPRegarray = new ArrayList<>();
//					file_io.filewrite2(OutFileName, "");
					
					for (int k = 0; k < set.length; k++) {
						setarray.add(set[k]);
						if (RemainRatio.get(k) >= threshold || RemainRatio.get(k + 1) >= threshold) {// ֻҪ������ǰ����ߺ�����һ��δ���ʹ�������IP������
							IPRegarray.add(set[k]);// �洢IP���������ýڵ�
						}
					}

//					file_io.filewrite2(OutFileName, " ");
					rarp.setIPRegnode(IPRegarray);
					rarp.setregnode(setarray);
					rarp.setregnum(setarray.size());
					rarp.setNumRemainFlow(NumRemainFlow);
					regplaceoption.add(rarp);
					//System.out.println("��·���ɹ�RSA, �ѳɹ�RSA������Ϊ��" + regplaceoption.size());// �������ĸ����ӽ�ȥ
					file_io.filewrite2(OutFileName, "��·���ɹ�RSA, �ѳɹ�RSA������Ϊ��" + regplaceoption.size());
				}
			}
		} // part1 finish
		/*
		 * debug print��ÿ����ѡ·���ϵ�IP·�������� ʣ������� �Լ�ʹ�õ�FS��
		 */
		//System.out.println();

		for (RouteAndRegPlace DebugRegRoute : regplaceoption) {
			file_io.filewrite2(OutFileName, " ");
			//System.out.println();
			//System.out.print("����������λ�ã� ");
			file_io.filewrite_without(OutFileName, "����������λ�ã� ");
			for (int Reg : DebugRegRoute.getregnode()) {
				//System.out.print(Reg + "  ");
				file_io.filewrite_without(OutFileName, Reg + "  ");
			}
			//System.out.println();
			file_io.filewrite2(OutFileName, "  ");
			if (DebugRegRoute.getIPRegnode().size() != 0) {
				//System.out.print("IP����������λ�� �� ");
				file_io.filewrite_without(OutFileName, "IP����������λ�� �� ");
				for (int IPReg : DebugRegRoute.getIPRegnode()) {
					//System.out.print(IPReg + "  ");
					file_io.filewrite_without(OutFileName, IPReg + "  ");
				}
			}else{
				//System.out.println("��������Ϊ��OEO����");
				file_io.filewrite2(OutFileName, "��������Ϊ��OEO����");
			}
			
			//System.out.println("ʣ��������� " + DebugRegRoute.getNumRemainFlow());
			file_io.filewrite2(OutFileName, "ʣ��������� " + DebugRegRoute.getNumRemainFlow());
			//System.out.println("ʹ�õ�newFS������ " + DebugRegRoute.getnewFSnum());
			file_io.filewrite2(OutFileName, "ʹ�õ�newFS������ " + DebugRegRoute.getnewFSnum());
		}

		// �����ڱ�ѡ·����ͨ��3��ѡ������·��
		if (regplaceoption.size() != 0) {
			success = true;
			ArrayList<RouteAndRegPlace> RemoveRoute = new ArrayList<>();
			// ȷ��final route
			// ��һ��ѡ�� ѡ��IP�������ٵ�·��
			if (regplaceoption.size() == 1) {
				finalRoute = regplaceoption.get(0);
			} else {
				for(int standard=0; standard< regplaceoption.size()-1; standard++){
					RouteAndRegPlace StandardRoute = regplaceoption.get(standard);
					if(RemoveRoute.contains(StandardRoute)) 
						continue;
//					System.out.print("��һ��ɸѡ ��׼·�ɵ�IP����������Ϊ ��");
//					System.out.println(StandardRoute.getIPRegnode().size());
					
					for (int k = standard+1; k < regplaceoption.size(); k++) {
						RouteAndRegPlace CompareRoute = regplaceoption.get(k);
						if(RemoveRoute.contains(CompareRoute)) 
							continue;
//						System.out.print("��һ��ɸѡ �Ƚ�·�ɵ�IP����������Ϊ��");
//						System.out.println(CompareRoute.getIPRegnode().size());
						if (StandardRoute.getIPRegnode().size() > CompareRoute.getIPRegnode().size()) {
							RemoveRoute.add(StandardRoute);// ɾȥIP���������·��
							break;
						}
						if (StandardRoute.getIPRegnode().size() < CompareRoute.getIPRegnode().size()) {
							RemoveRoute.add(CompareRoute);// �Ƚϵ�û�б�׼��
						}
					}
				}
				for (RouteAndRegPlace rag : RemoveRoute) {
					regplaceoption.remove(rag);
				}
				RemoveRoute.clear();
				// �ڶ���ѡ�� ��·����������ͬ�������ѡ����·��ʣ������������
				if (regplaceoption.size() == 1) {
					finalRoute = regplaceoption.get(0);
				} else {
					for(int standard=0; standard< regplaceoption.size()-1; standard++){
						RouteAndRegPlace StandardRoute_2 = regplaceoption.get(standard);
						if(RemoveRoute.contains(StandardRoute_2)) 
							continue;
//						System.out.print("�ڶ���ɸѡ ��׼·��ʣ������Ϊ��");
//						System.out.println(StandardRoute_2.getNumRemainFlow());
						
						for (int k = standard+1; k < regplaceoption.size(); k++) {
							RouteAndRegPlace CompareRoute_2 = regplaceoption.get(k);
							if(RemoveRoute.contains(CompareRoute_2)) 
								continue;
//							System.out.print("�ڶ���ɸѡ �Ƚ�·��ʣ������Ϊ��");
//							System.out.println(CompareRoute_2.getNumRemainFlow());
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
						for(int standard=0; standard< regplaceoption.size()-1; standard++){
							RouteAndRegPlace StandardRoute_3 = regplaceoption.get(standard);
							if(RemoveRoute.contains(StandardRoute_3)) 
								continue;
//							System.out.print("������ɸѡ ��׼·��ʹ�õ�FSΪ��");
//							System.out.println(StandardRoute_3.getnewFSnum());
						
							for (int k = standard+1; k < regplaceoption.size(); k++) {
								RouteAndRegPlace CompareRoute_3 = regplaceoption.get(k);
								if(RemoveRoute.contains(CompareRoute_3)) 
									continue;
//								System.out.print("������ɸѡ �Ƚ�·��ʹ�õ�FSΪ��");
//								System.out.println(CompareRoute_3.getnewFSnum());
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
				
				}
			}
			nodepair.setFinalRoute(finalRoute);
			RegeneratorPlace regp = new RegeneratorPlace();
			regp.FinalRouteRSA(finalRoute, oplayer, ipLayer, IPflow,RegLengthList,  rowList,ptOftransp);
		}

		if (regplaceoption.size() == 0) {
			success = false;
		}
		//System.out.println();
		if (success) {
			//System.out.print("���������óɹ�����RSA,���õ�����������Ϊ" + finalRoute.getregnum() + "  λ��Ϊ��");
			file_io.filewrite_without(OutFileName, "���������óɹ�����RSA,���õ�����������Ϊ" + finalRoute.getregnum() + "  λ��Ϊ��");
				
			for (int p = 0; p < finalRoute.getregnode().size(); p++) {
				//System.out.print(finalRoute.getregnode().get(p) + "     ");
				file_io.filewrite_without(OutFileName, finalRoute.getregnode().get(p) + "     ");
			}
			//System.out.println();
//			if (finalRoute.getIPRegnode().size() != 0) {
//				System.out.print("IP����������λ�� �� ");
//				file_io.filewrite_without(OutFileName, "IP����������λ�� �� ");
//				for (int IPReg : finalRoute.getIPRegnode()) {
//					System.out.print(IPReg + "  ");
//					file_io.filewrite_without(OutFileName, IPReg + "  ");
//				}
//			}else{
//				System.out.println("��������Ϊ��OEO����"); 
//				file_io.filewrite2(OutFileName,  "��������Ϊ��OEO����");
//			}//����·��ȫ������OEO������
				
			//System.out.println();
			totalregNum = totalregNum + finalRoute.getregnum();
			//System.out.println("*******����·��һ����Ҫ������������" + totalregNum);
			file_io.filewrite2(OutFileName, "");
			file_io.filewrite2(OutFileName, "******����·��һ����Ҫ������������" + totalregNum);
		} else {
			//System.out.println("����·���޷��ɹ�����������");
			file_io.filewrite2(OutFileName, "����·���޷��ɹ�����������");
		}
		return success;

		// */
		/*
		 * ��һ������ͨ��������������������������� //
		 */
		/*
		 * double length=0; int n=0; boolean
		 * brokeflag=false,opworkflag=false,partworkflag=false,RSAflag=false;
		 * ArrayList<Link> linklist=new ArrayList<Link>();
		 * 
		 * for(Link link:newRoute.getLinklist()){//�ж�route��ÿһ����·�����Ƿ񳬹�����ƾ���
		 * if(link.getLength()>4000) { System.out.println(link.getName()+
		 * " �ľ������ ҵ�����"); brokeflag=true; break; } }
		 * 
		 * if(!brokeflag){ do{ Node nodeA=newRoute.getNodelist().get(n); Node
		 * nodeB=newRoute.getNodelist().get(n+1);
		 * System.out.println(nodeA.getName()+"-"+nodeB.getName());
		 * 
		 * Link link=oplayer.findLink(nodeA, nodeB);
		 * length=length+link.getLength(); if(length<=4000) { n=n+1;
		 * linklist.add(link); if(n==newRoute.getNodelist().size()-1)
		 * partworkflag=modifylinkcapacity(IPflow,length, linklist,
		 * oplayer,ipLayer);//ΪĿ�Ľڵ�ǰ��ʣ����·����RSA totalregNum++; } if(length>4000)
		 * { length=length-link.getLength();
		 * partworkflag=modifylinkcapacity(IPflow,length, linklist,
		 * oplayer,ipLayer);//��ʱ��n����������� totalregNum++; length=0; RSAflag=true;
		 * linklist.clear(); } if(!partworkflag&&RSAflag) break;
		 * }while(n!=newRoute.getNodelist().size()-1); }
		 * System.out.println("һ����Ҫ������������Ϊ��"+totalregNum); if(partworkflag)
		 * opworkflag=true; return opworkflag;
		 */

	}

	public void FinalRouteRSA(RouteAndRegPlace finalRoute, Layer oplayer, Layer ipLayer, int IPflow,ArrayList<Double>RegLengthList,
			ArrayList<RequestOnWorkLink> rowList,ParameterTransfer ptOftransp) {
		// ������Ҫ����ͬ�������� ���첻ͬ��IP������·����IP��
		// IP������ ������Ҫ��������������· oeo������ֻ��Ҫ����һ��������·
		ArrayList<Link> phyLinklist=new ArrayList<>();
		ParameterTransfer pt = new ParameterTransfer();
		RegeneratorPlace rp=new RegeneratorPlace();
		finalRoute.getRoute().OutputRoute_node(finalRoute.getRoute());
		int count = 0;
		double length2 = 0;
		boolean regflag2 = false;
		ArrayList<Link> linklist2 = new ArrayList<>();
		file_out_put file_io = new file_out_put();
		ArrayList<Double> ResFlowOnlinks=new ArrayList<Double>();
		
		pt.setStartNode(finalRoute.getRoute().getNodelist().get(0));// �������ø���·����ʼ�ڵ�
		pt.setMinRemainFlowRSA(10000);//���ȳ�ʼ��
		file_io.filewrite2(OutFileName, "");
		
		for (int i = 0; i < finalRoute.getregnum() + 1; i++) {
			if (i >= finalRoute.getregnum())
				regflag2 = true;
			do {
				Node nodeA = finalRoute.getRoute().getNodelist().get(count);
				Node nodeB = finalRoute.getRoute().getNodelist().get(count + 1);
				
				Link link = oplayer.findLink(nodeA, nodeB);
				//System.out.println("������·RSA��" + link.getName());
				file_io.filewrite2(OutFileName, "������·RSA��" + link.getName());
				length2 = length2 + link.getLength();
				linklist2.add(link);
				count = count + 1;
				if (!regflag2) {// δ�������һ��·����RSA
					if (count == finalRoute.getregnode().get(i)) {// ���ȸõ������������
						pt.setEndNode(finalRoute.getRoute().getNodelist().get(count));//������ֹ�ڵ�
								if(count==1){//��ʱΪtransponder�ķ�����·
								double costOfStart=rp.transpCostCal( length2 );
								ptOftransp.setcost_of_tranp(ptOftransp.getcost_of_tranp()+costOfStart);
								file_io.filewrite2(OutFileName, "transponder���cost" + costOfStart+
										"   ��ʱtransponder cost=" + ptOftransp.getcost_of_tranp());
							}
								// �õ������IP������
						if (finalRoute.getIPRegnode().contains(count)) {
							//������count����transponder��cost
						
							modifylinkcapacity(true, IPflow, length2, linklist2, oplayer, ipLayer, pt,  rowList,ResFlowOnlinks,phyLinklist);
							file_io.filewrite2(OutFileName, "����RSA����Ϊ��" + length2);
							RegLengthList.add(length2);
							length2 = 0;
							linklist2.clear();
							break;
						}
						// �õ���ô�OEO������
						else {
							modifylinkcapacity(false, IPflow, length2, linklist2, oplayer, ipLayer, pt, rowList,ResFlowOnlinks,phyLinklist);
							RegLengthList.add(length2);
							file_io.filewrite2(OutFileName, "����RSA����Ϊ��" + length2);
							length2 = 0;
							linklist2.clear();
							break;
						}
					}
				}
				if (count == finalRoute.getRoute().getNodelist().size() - 1) {// ���һ�����������յ�֮���RSA
					double costOfEnd=rp.transpCostCal( length2 );
					ptOftransp.setcost_of_tranp(ptOftransp.getcost_of_tranp()+costOfEnd);
					file_io.filewrite2(OutFileName, "transponder�յ�cost" + costOfEnd+
							"   ��ʱtransponder cost=" + ptOftransp.getcost_of_tranp());
					pt.setEndNode(finalRoute.getRoute().getNodelist().get(count));//������ֹ�ڵ�
					modifylinkcapacity(true, IPflow, length2, linklist2, oplayer, ipLayer, pt, rowList,ResFlowOnlinks,phyLinklist);// ��ʱ��n�����������
					RegLengthList.add(length2);
					file_io.filewrite2(OutFileName, "����RSA����Ϊ��" + length2);
					linklist2.clear();
				}
			} while (count != finalRoute.getRoute().getNodelist().size() - 1);
		}
	}

	public Boolean vertify(int IPflow, double routelength, ArrayList<Link> linklist, Layer oplayer, Layer iplayer,
			ArrayList<WorkandProtectRoute> wprlist, NodePair nodepair, ParameterTransfer RemainRatio) {
		// �ж�ĳһ��transparent��·�Ƿ��ܹ��ɹ�RSA ���Ҽ�¼��ʹ�õ�FS�����͸���·�ϵ�ʣ������

		double X = 1;
		int slotnum = 0;
		file_out_put file_io = new file_out_put();
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
			//System.out.println("����·����slot���� " + slotnum);
			if(slotnum<Constant.MinSlotinLightpath){
				slotnum=Constant.MinSlotinLightpath;
			}
			newFS = slotnum * linklist.size();

			ArrayList<Integer> index_wave = new ArrayList<Integer>();
			Mymain spa = new Mymain();
			index_wave = spa.spectrumallocationOneRoute(false, null, linklist, slotnum);
			if (index_wave.size() == 0) {
				//System.out.println("·������ ��������Ƶ����Դ");
				file_io.filewrite2(OutFileName, "·������ ��������Ƶ����Դ");
			} else {
				RemainRatio.setRemainFlowRatio((float) ((slotnum * X - IPflow) / (slotnum * X)));
				RemainRatio.setNumremainFlow((float) (slotnum * X - IPflow));
				file_io.filewrite2(OutFileName, "����ͨ���������� " + slotnum * X + "   ҵ������ " + IPflow + "   ʣ����������� "
						+ RemainRatio.getRemainFlowRatio() + "   ʣ���ҵ������" + RemainRatio.getNumremainFlow()+"  ��Ҫ��FS������"+slotnum+"  FS��ʼ��"+index_wave.get(0));
				opworkflag = true;
				//System.out.println("���Խ���RSA ");
				file_io.filewrite2(OutFileName, "���Խ���RSA");
			}
		}
		return opworkflag;
	}

	public boolean modifylinkcapacity(Boolean IPorOEO, int IPflow, double routelength, ArrayList<Link> linklist,
			Layer oplayer, Layer iplayer, ParameterTransfer pt,ArrayList<RequestOnWorkLink> rowList,ArrayList<Double> ResFlowOnlinks,ArrayList<Link> phyLinklist) {// true��ʾIP������
																	// false��ʾ��OEO������
		double X = 1;
		int slotnum = 0;
		boolean opworkflag = false;
		file_out_put file_io = new file_out_put();
		Node srcnode = new Node(null, 0, null, iplayer, 0, 0);
		Node desnode = new Node(null, 0, null, iplayer, 0, 0);
		double resflow=0;
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
			if(slotnum<Constant.MinSlotinLightpath){
				slotnum=Constant.MinSlotinLightpath;
			}
			float RemainFlow=(float)(slotnum*X-IPflow);
			 resflow=slotnum*X-IPflow;
		     ResFlowOnlinks.add(resflow);//�洢��OEO�������νӵ���·��ʣ�������
			if(RemainFlow<pt.getMinRemainFlowRSA()){//���м侭��OEO��������ô�洢ʣ���С��flow
				pt.setMinRemainFlowRSA(RemainFlow);
			}
//			System.out.println("��ʱ����Сʣ������Ϊ��" + pt.getMinRemainFlowRSA());
			
			// ��������Ҫ��FS�� ���ҹ۲�ÿ����·�Ͽ��õ�Ƶ�״�
//			System.out.println("����·����slot���� " + slotnum);
			file_io.filewrite2(OutFileName, "����·����slot���� " + slotnum);
			ArrayList<Integer> index_wave = new ArrayList<Integer>();
			Mymain spa = new Mymain();
			index_wave = spa.spectrumallocationOneRoute(false, null, linklist, slotnum);
			if (index_wave.size() == 0) {
				//System.out.println("·������ ��������Ƶ����Դ");
				file_io.filewrite2(OutFileName, "·������ ��������Ƶ����Դ");
			} else {
				
				RequestOnWorkLink row=new RequestOnWorkLink(null, null, 0, 0);
				opworkflag = true;
				double length1 = 0;
				double cost = 0;
				file_io.filewrite_without(OutFileName, "������Ƶ�ף�");
				file_io.filewrite2(OutFileName, "FS��ʼֵ��" + index_wave.get(0) + "  ����" + slotnum);
				// ������link �ı������link����ʣ���FS��
				for (Link link : linklist) {
					length1 = length1 + link.getLength();
					cost = cost + link.getCost();
					Request request = null;
					ResourceOnLink ro = new ResourceOnLink(request, link, index_wave.get(0), slotnum);
					link.setMaxslot(slotnum + link.getMaxslot());
					row.setWorkLink(link);row.setWorkRequest(request);
					row.setStartFS(index_wave.get(0)); row.setSlotNum(slotnum);
					rowList.add(row);
					phyLinklist.add(link);//��¼������������·��Ӧ��������·
//					System.out.println();
//					System.out.println("��· " + link.getName() + "�����slot�ǣ� " + link.getMaxslot() + " ����Ƶ�״�����"
//							+ link.getSlotsindex().size());
				}

				if (IPorOEO) {// true��ʱ���ʾ���õ���IP������ ��Ҫ��IP�㽨����·
//					�ı���ʼ�ڵ� ʣ������
					//����Ѱ����Ҫ����IP��·����ʼ�ڵ����ֹ�ڵ�
					Node startnode = pt.getStartNode();
					Node endnode = pt.getEndNode();
					
					// 	��IP����Ѱ��transparent��·������
					for (int num = 0; num < iplayer.getNodelist().size() - 1; num++) {
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
					if (srcnode.getIndex() > desnode.getIndex()) {
						Node internode = srcnode;
						srcnode = desnode;
						desnode = internode;
					}
					
					String name = srcnode.getName() + "-" + desnode.getName();
					int index = iplayer.getLinklist().size();// ��Ϊiplayer�����link��һ��һ������ȥ�Ĺ���������index

					Link finlink = iplayer.findLink(srcnode, desnode);
					Link createlink = new Link(null, 0, null, iplayer, null, null, 0, 0);
					boolean findflag = false;
					try {
						System.out.println(finlink.getName());
						file_io.filewrite2(OutFileName,finlink.getName());
						findflag = true;
					} catch (java.lang.NullPointerException ex) {
						createlink = new Link(name, index, null, iplayer, srcnode, desnode, length1, cost);
						iplayer.addLink(createlink);
					}

					double minflow=10000;
					for(double resflow2:ResFlowOnlinks){//Ѱ�Ҳ�ͬ��·��ʣ��������С����·
						if(minflow>resflow2){
							minflow=resflow2;
						}
					}
					ResFlowOnlinks.clear();
					VirtualLink Vlink = new VirtualLink(srcnode.getName(), desnode.getName(), 0, 0);
					Vlink.setnature(0);
					Vlink.setRestcapacity(minflow);
					Vlink.setcost(cost);
					Vlink.setPhysicallink(phyLinklist);
					phyLinklist.clear();
					
					if (findflag) {// �����IP�����Ѿ��ҵ�����·
						finlink.getVirtualLinkList().add(Vlink);
						file_io.filewrite2(OutFileName,"IP���Ѵ��ڵ���· " + finlink.getName() +  "    Ԥ����flow��  " + Vlink.getRestcapacity());
						//System.out.println("������·�ڹ���½�����·��  " + finlink.getName() + "  �ϵ�������·������ "
								//+ finlink.getVirtualLinkList().size());
						file_io.filewrite2(OutFileName, "������·�ڹ���½�����·��  " + finlink.getName() + "  �ϵ�������·������ "
								+ finlink.getVirtualLinkList().size());
					} else {
						createlink.getVirtualLinkList().add(Vlink);
						file_io.filewrite2(OutFileName,"IP�����½���· " + createlink.getName() + "    Ԥ����flow��  " + Vlink.getRestcapacity());
						file_io.filewrite2(OutFileName, "������·�ڹ���½�����·��  " + createlink.getName() + "  �ϵ�������·������ "
								+ createlink.getVirtualLinkList().size());
					}
					//�����Ѿ��ɹ�����IP������· Ҫ���Ĵ˴ε���ֹ�ڵ�Ϊ�´ε���ʼ�ڵ� ���ҳ�ʼ����·����Сʣ������
					pt.setMinRemainFlowRSA(10000);
				}
			}
		}
		return opworkflag;
	}

public double transpCostCal(double routelength) {
	double costOftransp=0;
	if (routelength > 2000 && routelength <= 4000) {
		costOftransp=Constant.Cost_IP_reg_BPSK;
	} else if (routelength > 1000 && routelength <= 2000) {
		costOftransp=Constant.Cost_IP_reg_QPSK;
	} else if (routelength > 500 && routelength <= 1000) {
		costOftransp=Constant.Cost_IP_reg_8QAM;
	} else if (routelength > 0 && routelength <= 500) {
		costOftransp=Constant.Cost_IP_reg_16QAM;
	}
	return costOftransp;
}
}
