package MainFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import general.Constant;
import general.file_out_put;
import network.Layer;
import network.Link;
import network.Network;
import network.NodePair;
import subgraph.LinearRoute;

public class Mymain {
	public static String OutFileName = "D:\\zyx\\programFile\\RegwithProandTrgro\\cost239.dat";
	public static String FinalResultFile = "D:\\zyx\\programFile\\RegwithProandTrgro\\cost239FinalResult.dat";
	public static void main(String[] args) throws IOException {
		String TopologyName = "D:/zyx/Topology/cost239.csv";
		int DemandNum=40;
		ParameterTransfer pt=new ParameterTransfer();
		file_out_put file_io=new file_out_put();
		Mymain mm=new Mymain();
		ArrayList<NodePair> RadomNodepairlist=new ArrayList<NodePair>();
		Network network_base = new Network("ip over EON", 0, null);
		network_base.readPhysicalTopology(TopologyName);
		network_base.copyNodes();
		network_base.createNodepair();// 每个layer都生成节点对 产生节点对的时候会自动生成nodepair之间的demand
		Layer iplayer_base = network_base.getLayerlist().get("Layer0");
	
		DemandRadom dr=new DemandRadom();
		RadomNodepairlist=dr.NodePairRadom(DemandNum,TopologyName,iplayer_base);//随机产生结对
		dr.TrafficNumRadom(RadomNodepairlist);
		for(NodePair np:RadomNodepairlist){//输出随机产生节点对的大小
			file_io.filewrite2(FinalResultFile, np.getName());
		}
		for(NodePair np:RadomNodepairlist){//输出随机产生节点对的大小
			file_io.filewrite(FinalResultFile, np.getTrafficdemand());
		}
		//以下可以读取表格中的业务
//		ReadDemand rd=new ReadDemand();
//		RadomNodepairlist=rd.readDemand(iplayer_base, "D:\\6Traffic.csv");
// 
		/*
		 * 设置threshold循环
		 */
		for(float threshold=(float) 0;threshold<=1.05; threshold=(float) (threshold+0.1)){
			ArrayList<Double> results=new ArrayList<>();
			double bestResult=100000,AverageCost=0;
			int bestshuffle=1000,NumOfIPreg=0,NumofOEOreg=0,NumofTrans=0;
			int bestSingleshuffle=0,bestAllshuffle=0;
			int MinSlotofAllShuffleOnSingleLink=10000;
			int MinSlotofAllShuffleofAllLink=10000;
			
		for(int shuffle=0;shuffle<50;shuffle++){//打乱次序100次
			double TotalWorkCost=0,TotalProCost=0;
			pt.setNumOfTransponder(0);
			pt.setcost_of_tranp(0);
			pt.setcostOfIPreg(0);
			file_io.filewrite2(OutFileName, " ");
			file_io.filewrite2(FinalResultFile, " ");
			file_io.filewrite2(OutFileName, "threshold="+threshold);
			file_io.filewrite2(FinalResultFile, "threshold="+threshold);
			file_io.filewrite2(OutFileName, "shuffle="+shuffle);
			file_io.filewrite2(FinalResultFile, "shuffle="+shuffle);
		
			Collections.shuffle(RadomNodepairlist);//打乱产生的业务100次
			for(NodePair nodepair: RadomNodepairlist){
				file_io.filewrite2(FinalResultFile, "节点对  "+nodepair.getName()+"  流量：" + nodepair.getTrafficdemand());
			}
			
			// 产生的节点对之间的容量(int)(Math.random()*(2*Constant.AVER_DEMAND-20));
			ArrayList<WorkandProtectRoute> wprlist = new ArrayList<>();
			ArrayList<NodePair> SmallNodePairList = new ArrayList<NodePair>();
		
			Network network = new Network("ip over EON", 0, null);
			network.readPhysicalTopology(TopologyName);
			network.copyNodes();
			network.createNodepair();// 每个layer都生成节点对 产生节点对的时候会自动生成nodepair之间的demand
			
			Layer iplayer = network.getLayerlist().get("Layer0");
			Layer oplayer = network.getLayerlist().get("Physical");
		
			mm.NodepairListset(iplayer, RadomNodepairlist);//在IP层设置nodepairList
			ArrayList<NodePair> demandlist = mm.getDemandList(iplayer,RadomNodepairlist);
			
			for (int n = 0; n < demandlist.size(); n++) {
				NodePair nodepair = demandlist.get(n);
				file_io.filewrite2(OutFileName, "");
				file_io.filewrite2(OutFileName, "");
				System.out.println("正在操作的节点对： " + nodepair.getName() + "  他的流量需求是： " + nodepair.getTrafficdemand());
				file_io.filewrite2(OutFileName, "正在操作的节点对： " + nodepair.getName() + "  他的流量需求是： " + nodepair.getTrafficdemand());
				
				file_io.filewrite2(OutFileName, "Total numberof transponder " + pt.getNumOfTransponder());
				if(nodepair.getTrafficdemand()<50 ){
					SmallNodePairList.add(nodepair);
					continue;
				}
				mm.mainMethod(nodepair, iplayer, oplayer, pt, wprlist,threshold);
			}
				if(SmallNodePairList!=null&&SmallNodePairList.size()!=0){
					for(NodePair smallnodepair:SmallNodePairList){
						file_io.filewrite2(OutFileName, "");
						file_io.filewrite2(OutFileName, "");
						file_io.filewrite2(OutFileName, "正在操作的节点对： " + smallnodepair.getName() + "  他的流量需求是： " + smallnodepair.getTrafficdemand());
						file_io.filewrite2(OutFileName, "Total numberof transponder " + pt.getNumOfTransponder());
						mm.mainMethod(smallnodepair, iplayer, oplayer, pt, wprlist,threshold);
					}
				}
//结束主循环
			file_io.filewrite2(FinalResultFile, "");
			file_io.filewrite2(FinalResultFile, "");
			file_io.filewrite2(FinalResultFile, "业务个数：" + wprlist.size());
			
			int demandnum=0,TotalWorkRegNum=0,TotalWorkIPReg=0,
					TotalProRegNum=0,TotalProIPReg=0;
			ArrayList<Regenerator> reglist=new ArrayList<>();
//			if(wprlist.size()!=DemandNum) {
//				file_io.filewrite2(FinalResultFile, "此次shuffle无法完成所有业务" );
//				continue;
//			}
			for (WorkandProtectRoute wpr : wprlist) {
				demandnum++;
				file_io.filewrite2(FinalResultFile, "业务：" + demandnum+"  "+wpr.getdemand().getName());
				file_io.filewrite_without(FinalResultFile, "工作路径：");
				for (Link link : wpr.getworklinklist()) {
					file_io.filewrite_without(FinalResultFile, link.getName() + "     ");
				}
				file_io.filewrite2(FinalResultFile, " " );
//				工作路径放置再生器
				if(wpr.getdemand().getFinalRoute()!=null){//说明该链路需要放置再生器
					RouteAndRegPlace FinalRoute= wpr.getdemand().getFinalRoute();
					file_io.filewrite_without(FinalResultFile, "工作路径放置再生器的位置为：");
					for(int reg: FinalRoute.getregnode()){
						TotalWorkRegNum++;
						file_io.filewrite_without(FinalResultFile, reg +"  ");
					}
					file_io.filewrite2(FinalResultFile, "");
					if(FinalRoute.getIPRegnode()!=null){
						file_io.filewrite_without(FinalResultFile, "工作路径放置IP再生器的位置为：");
						for(int reg: FinalRoute.getIPRegnode()){
							TotalWorkIPReg++;
							file_io.filewrite_without(FinalResultFile, reg +"  ");
						}
						file_io.filewrite2(FinalResultFile, "  ");
					}
					//计算价格
					//工作的cost  
//					/*
					double WorkCost=0;
					int linkNum=0;
					for(int count=0;count<wpr.getRegWorkLengthList().size();count++){
						double cost=0;
						if(FinalRoute.getregnode().contains(count+1)){//该点放置再生器
							if(FinalRoute.getIPRegnode().contains(count+1)){//说明该节点上的是IP再生器
								file_io.filewrite2(FinalResultFile,"工作路径上第"+count+"个再生器(IP)两端的cost");
								for(int num=linkNum;num<=linkNum+1;num++){
									double length=	wpr.getRegWorkLengthList().get(num);
									file_io.filewrite2(FinalResultFile,"距离为 "+length);
									if (length > 2000 && length <= 4000) {
										cost=Constant.Cost_IP_reg_BPSK;
									} else if (length > 1000 && length <= 2000) {
										cost=Constant.Cost_IP_reg_QPSK;
									} else if (length > 500 && length <= 1000) {
										cost=Constant.Cost_IP_reg_8QAM;
									} else if (length > 0 && length <= 500) {
										cost=Constant.Cost_IP_reg_16QAM;
									}
									WorkCost=WorkCost+cost;
									pt.setcostOfIPreg(pt.getcostOfIPreg()+cost);//加入工作IPreg cost
									file_io.filewrite2(FinalResultFile,"cost=："+cost);
								}
							}
							else{
								file_io.filewrite2(FinalResultFile,"工作路径上第"+count+"个再生器(OEO)两端的cost");
								for(int num=linkNum;num<=linkNum+1;num++){
									double length=	wpr.getRegWorkLengthList().get(num);
									file_io.filewrite2(FinalResultFile,"距离为 "+length);
									if (length > 2000 && length <= 4000) {
										cost=Constant.Cost_OEO_reg_BPSK;
									} else if (length > 1000 && length <= 2000) {
										cost=Constant.Cost_OEO_reg_QPSK;
									} else if (length > 500 && length <= 1000) {
										cost=Constant.Cost_OEO_reg_8QAM;
									} else if (length > 0 && length <= 500) {
										cost=Constant.Cost_OEO_reg_16QAM;
									}
									WorkCost=WorkCost+cost;
									file_io.filewrite2(FinalResultFile,"cost=："+cost);
								}
							}
							linkNum++;
						}
					}
					file_io.filewrite2(FinalResultFile,"工作再生器总的cost为："+ WorkCost);
					file_io.filewrite2(FinalResultFile, " ");
					TotalWorkCost=TotalWorkCost+WorkCost;
//					*/
				}
				else{
					file_io.filewrite2(FinalResultFile, "该工作链路不需要放置再生器");
				}
				
				file_io.filewrite_without(FinalResultFile,"保护路径：");
				if(wpr.getproroute()!=null)
				wpr.getproroute().OutputRoute_node(wpr.getproroute(), FinalResultFile);
				else file_io.filewrite_without(FinalResultFile,"保护路径在IP层grooming成功");
				file_io.filewrite2(FinalResultFile, "");
				
				file_io.filewrite_without(FinalResultFile,"保护路径放置共享再生器节点：");
				for (Regenerator reg : wpr.getsharereglist()) {
					reg.setPropathNum(reg.getPropathNum()+1);
					if(!reglist.contains(reg)){
						reglist.add(reg);
					}
					if(reg.getNature()==0)
						file_io.filewrite_without(FinalResultFile,reg.getnode().getName() + "     "+"再生器在节点上的序号: "+reg.getindex()+" 是OEO再生器  ");
					
					if(reg.getNature()==1)
						file_io.filewrite_without(FinalResultFile,reg.getnode().getName() + "     "+"再生器在节点上的序号: "+reg.getindex()+" 是IP再生器  ");
				}
				
				
				file_io.filewrite2(FinalResultFile, "");
				file_io.filewrite_without(FinalResultFile,"保护路径放置新再生器节点：");
				
				for (Regenerator reg : wpr.getnewreglist()) {
					reg.setPropathNum(reg.getPropathNum()+1);
					if(!reglist.contains(reg)){
						TotalProRegNum++;
						reglist.add(reg);
					}
					if(reg.getNature()==0)
						file_io.filewrite_without(FinalResultFile,reg.getnode().getName() + "     "+"再生器在节点上的序号: "+reg.getindex()+" 是OEO再生器  ");
					
					if(reg.getNature()==1){
						file_io.filewrite_without(FinalResultFile,reg.getnode().getName() + "     "+"再生器在节点上的序号: "+reg.getindex()+" 是IP再生器  ");
						TotalProIPReg++;
					}
					
				}
				file_io.filewrite2(FinalResultFile," ");
				
				//计算保护路径的cost
				double ProEachcost=0;
				if(wpr.getnewreglist().size()!=0){
					ProEachcost=mm.ProCostCalculate(wpr,pt);
				}
					file_io.filewrite2(FinalResultFile,"保护路径再生器的cost= " +ProEachcost);
					TotalProCost=TotalProCost+ProEachcost;
					file_io.filewrite2(FinalResultFile,"cost of trans="+ wpr.getcostoftransForSingle());
					file_io.filewrite2(FinalResultFile, "");
					
//				测试共享个数				
//				for(Regenerator reg:reglist){
//					file_io.filewrite2(OutFileName,reg.getnode().getName() + "     "+"再生器在节点上的序号:"+reg.getindex()+"   "+"该再生器已经被"+reg.getpropathNum()+"条路径共享");
//				}
				
				
//				if(wpr.getregthinglist()!=null){
//					for(int t:wpr.getregthinglist().keySet()){
//						file_io.filewrite2(OutFileName, "hashmap里面的键 "+t+" 对应的节点为："+wpr.getregthinglist().get(t).getnode().getName());
//					}
//					file_io.filewrite2(OutFileName, "");
//				}
//				else{
//					file_io.filewrite2(OutFileName, "该业务保护路径不需要再生器");
//				}
//				file_io.filewrite2(OutFileName, "");
				
//				ArrayList<FSshareOnlink> FSassignOneachLink=wpr.getFSoneachLink();
//				file_io.filewrite2(OutFileName, "此时的request为"+ wpr.getrequest().getNodepair().getName()+"分配保护路径FS如下");
				
//				if(FSassignOneachLink!=null){
//				for(FSshareOnlink fsassignoneachlink: FSassignOneachLink){
//					file_io.filewrite_without(OutFileName, "链路"+fsassignoneachlink.getlink().getName()+"上分配的FS为   ");
//					for(int fs:fsassignoneachlink.getslotIndex()){
//						file_io.filewrite_without(OutFileName, fs+"   ");
//					}
//					file_io.filewrite2(OutFileName, "");
//				}
//			}
//				file_io.filewrite2(OutFileName, "");
//				if(FSassignOneachLink==null){
//					file_io.filewrite2(OutFileName, "该保护路径在IP层grooming成功");
//				}
				
//				HashMap<String, Node> testmap2 = oplayer.getNodelist();
//				Iterator<String> testiter2 = testmap2.keySet().iterator();
//				while (testiter2.hasNext()) {
//					Node node = (Node) (testmap2.get(testiter2.next()));
//					file_io.filewrite2(OutFileName, node.getName()+"上面再生器的个数："+node.getregnum());
//				}
					
			}
		//计算每条链路上的FS使用量
			file_io.filewrite2(FinalResultFile, "   ");
			HashMap<String, Link> testmap4 = oplayer.getLinklist();
			Iterator<String> testiter4 = testmap4.keySet().iterator();
			int maxSlotofOneShuffle=0, AllSoltUse=0;
			while (testiter4.hasNext()) {
				Link link=(Link) (testmap4.get(testiter4.next()));
				int Maxslot=0;
				for (int start = link.getSlotsarray().size()-1; start >= 0; start--) {
						if (link.getSlotsarray().get(start).getoccupiedreqlist().size() != 0) {// 该波长已经被占用
							Maxslot=start;
							break;
						}
				}
				//计算所有链路使用的slot数
				int singleslot=0;
				for (int start = link.getSlotsarray().size()-1; start >= 0; start--) {
					if (link.getSlotsarray().get(start).getoccupiedreqlist().size() != 0) {// 该波长已经被占用
						AllSoltUse++;
						singleslot++;
					}
			}
				file_io.filewrite2(FinalResultFile, "link="+link.getName()+" 上使用的FS="+ singleslot);
				file_io.filewrite2(FinalResultFile, "link "+ link.getName()+" MaxSlot="+ Maxslot);
				if(Maxslot>maxSlotofOneShuffle){
					maxSlotofOneShuffle=Maxslot;
				}
			}
			
			if(AllSoltUse<MinSlotofAllShuffleofAllLink){
				bestAllshuffle=shuffle;
				MinSlotofAllShuffleofAllLink=AllSoltUse;
			}
			
			if(maxSlotofOneShuffle<MinSlotofAllShuffleOnSingleLink){
				 bestSingleshuffle=shuffle;
				MinSlotofAllShuffleOnSingleLink=maxSlotofOneShuffle;
			}	
			file_io.filewrite2(FinalResultFile, " ");
			file_io.filewrite2(FinalResultFile, "本次shuffle中 单个链路最大slot ="+ maxSlotofOneShuffle+" shuffle="+ shuffle);
			file_io.filewrite2(FinalResultFile, "本次shuffle中 使用的总FS= "+ AllSoltUse+" shuffle="+ shuffle);
			
			file_io.filewrite2(FinalResultFile, "NumberofTransbonder:"+ pt.getNumOfTransponder());
			file_io.filewrite2(FinalResultFile, "CostofTransbonder:"+ pt.getcost_of_tranp());
			
			int IPregnum=TotalWorkIPReg+TotalProIPReg;
			int Allregnum=TotalWorkRegNum+TotalProRegNum;
			int OEOregnum=Allregnum-IPregnum;
			file_io.filewrite2(FinalResultFile, "IP再生器的个数："+ IPregnum);
			file_io.filewrite2(FinalResultFile, "OEO再生器的个数："+ OEOregnum);
			file_io.filewrite2(FinalResultFile, "所有再生器的个数："+ Allregnum);

			double TotalCost=TotalProCost+TotalWorkCost;
			
			file_io.filewrite2(FinalResultFile, " ");
			file_io.filewrite2(FinalResultFile, "Total cost of IP reg in network："+ pt.getcostOfIPreg());
			file_io.filewrite2(FinalResultFile, "Total cost of reg in network："+ TotalCost);
//			double costOftrans=pt.getcost_of_tranp()+pt.getcostOfIPreg();
			double costOftrans=pt.getcost_of_tranp();
			file_io.filewrite2(FinalResultFile, "Total cost of trans in network："+ costOftrans);
			double RegwithTrans=TotalCost+costOftrans;
			file_io.filewrite2(FinalResultFile, "RegCost+TransCost："+ RegwithTrans);
			
			results.add(RegwithTrans);//保存每次的cost结果
			if(RegwithTrans<bestResult){
				bestResult=RegwithTrans;
				bestshuffle=shuffle;
				NumOfIPreg=TotalProIPReg+TotalWorkIPReg;
				NumofOEOreg=TotalProRegNum+TotalWorkRegNum-TotalProIPReg-TotalWorkIPReg;
				NumofTrans=pt.getNumOfTransponder();
			}
			
		}//结束单一的一次shuflle
		double AllShuffleCost=0,Times=0;
		for(double c: results){
			Times++;
			AllShuffleCost=AllShuffleCost+c;
		}
		file_io.filewrite2(FinalResultFile, "");	
		file_io.filewrite2(FinalResultFile, "所有shuffle中 单个链路最大slot 最小的是： "+ MinSlotofAllShuffleOnSingleLink+" shuffle="+ bestSingleshuffle);
		file_io.filewrite2(FinalResultFile, "所有shuffle中 所有链路slot综合 最小的是： "+ MinSlotofAllShuffleofAllLink+" shuffle="+ bestAllshuffle);
		
		file_io.filewrite2(FinalResultFile, "Best shuffle="+bestshuffle+"  Best Result="+bestResult);
		file_io.filewrite2(FinalResultFile, "Num of IP reg="+NumOfIPreg+"  Num of OEO reg="+NumofOEOreg);
		file_io.filewrite2(FinalResultFile, "Num of trans="+NumofTrans);
		AverageCost=AllShuffleCost/Times;
		file_io.filewrite2(FinalResultFile, "total="+AllShuffleCost+"   times="+ Times +"  Average cost="+AverageCost);
		double add=0;
		for(double c:results){
			double minusSqur=Math.pow(c-AverageCost, 2);
			add=add+minusSqur;
		}
		double SqureError=Math.sqrt(add/Times);//计算出平方差
		double ErrorResult=SqureError*1.96;
		file_io.filewrite2(FinalResultFile, "ErrorArea="+ErrorResult);
		file_io.filewrite2(FinalResultFile, "Finish a threshold");
		}//完成一个threshold
		System.out.println("Finish");
	}
//main函数结束
	public static ArrayList<NodePair> Rankflow(Layer IPlayer) {
		ArrayList<NodePair> nodepairlist = new ArrayList<NodePair>(2000);
		HashMap<String, NodePair> map3 = IPlayer.getNodepairlist();
		Iterator<String> iter3 = map3.keySet().iterator();
		while (iter3.hasNext()) {
			NodePair np = (NodePair) (map3.get(iter3.next()));
			if (nodepairlist.size() == 0)
				nodepairlist.add(np);
			else {
				boolean insert = false;
				for (int i = 0; i < nodepairlist.size(); i++) {
					int m_flow = np.getTrafficdemand();
					int n_flow = nodepairlist.get(i).getTrafficdemand();

					if (m_flow > n_flow) {
						nodepairlist.add(i, np);
						insert = true;
						break;
					}

				}

				if (insert == false)
					nodepairlist.add(np);
			}
		}
		return nodepairlist;
	}

	public ArrayList<Integer> spectrumallocationOneRoute(Boolean routeflag, LinearRoute route, ArrayList<Link> linklist,int slotnum) {
		
		//debug 测试每段链路上可用的频谱
//		file_out_put file_io = new file_out_put();
//		if(linklist!=null&&linklist.size()!=0){
//		for(Link link: linklist){
//			file_io.filewrite2(OutFileName, "链路:"+ link.getName()+" 上面可以使用的slotindex");
//			for (int num = 5; num < link.getSlotsarray().size() ; num++) {//分配的FS必须是连续的
//				if (link.getSlotsarray().get(num).getoccupiedreqlist().size() == 0) {//  链路上面该slot未被占用
//					file_io.filewrite_without(OutFileName, num+"  ");
//				}
//			}
//			file_io.filewrite2(OutFileName, " ");
//		}
//		}
		
		
		ArrayList<Link> linklistOnroute = new ArrayList<Link>();
		if (routeflag) {
			linklistOnroute = route.getLinklist();
		} else {
			linklistOnroute = linklist;
		}
		for (Link link : linklistOnroute) {
			link.getSlotsindex().clear();
		
			for (int start = 0; start < link.getSlotsarray().size() - slotnum; start++) {
				int flag = 0;
				for (int num = start; num < slotnum + start; num++) {//分配的FS必须是连续的
					if (link.getSlotsarray().get(num).getoccupiedreqlist().size() != 0) {// 该波长已经被占用
						flag = 1;
						break;
					}
				}
				if (flag == 0) {
					link.getSlotsindex().add(start);// 查找可用slot的起点
				}
			}
		} // 以上所有的link分配完

		Link firstlink = linklistOnroute.get(0);
		ArrayList<Integer> sameindex = new ArrayList<Integer>();
		sameindex.clear();

		for (int s = 0; s < firstlink.getSlotsindex().size(); s++) {
			int index = firstlink.getSlotsindex().get(s);
			int flag = 1;

			for (Link otherlink : linklistOnroute) {
				if (otherlink.getName().equals(firstlink.getName()))
					continue;
				if (!otherlink.getSlotsindex().contains(index)) {
					flag = 0;
					break;
				}
			}
			if (flag == 1) {
				sameindex.add(index); // 挑选出该路径上所有link共同的slot start数
			}
		}
		// 测试频谱分配问题
		// for (Link link : linklistOnroute) {
		// System.out.println("");
		// System.out.println("测试频谱分配：");
		// System.out.println("链路： "+link.getName()+"
		// "+link.getSlotsindex().size());
		// }
		return sameindex;
	}
	
	public double ProCostCalculate(WorkandProtectRoute wpr,ParameterTransfer pt) {
		double TotalProCost=0;
		file_out_put file_io=new file_out_put();
		for(int count=0;count<wpr.getRegProLengthList().size()-1;count++){
			Regenerator reg= wpr.getRegeneratorlist().get(count);
			if(wpr.getnewreglist().contains(reg)){//该再生器为新建的再生器
				if(reg.getNature()==0){//OEO再生器
					double cost=0;
					file_io.filewrite2(FinalResultFile,"保护路径上第"+count+"个OEO再生器两端的cost");
					for(int num=count;num<=count+1;num++){
					double length=	wpr.getRegProLengthList().get(num);
					file_io.filewrite2(FinalResultFile,"length=："+length);
					if (length > 2000 && length <= 4000) {
						cost=Constant.Cost_OEO_reg_BPSK;
					} else if (length > 1000 && length <= 2000) {
						cost=Constant.Cost_OEO_reg_QPSK;
					} else if (length > 500 && length <= 1000) {
						cost=Constant.Cost_OEO_reg_8QAM;
					} else if (length > 0 && length <= 500) {
						cost=Constant.Cost_OEO_reg_16QAM;
					}
						TotalProCost = TotalProCost + cost;
						file_io.filewrite2(FinalResultFile,"cost=："+cost);
						
					}
				}
				if(reg.getNature()==1){//IP再生器
					double cost=0;
					file_io.filewrite2(FinalResultFile,"保护路径上第"+count+"个IP再生器两端的cost");
					for(int num=count;num<=count+1;num++){
					double length=	wpr.getRegProLengthList().get(num);
					file_io.filewrite2(FinalResultFile,"length= "+length);
					if (length > 2000 && length <= 4000) {
						cost=Constant.Cost_IP_reg_BPSK;
					} else if (length > 1000 && length <= 2000) {
						cost=Constant.Cost_IP_reg_QPSK;
					} else if (length > 500 && length <= 1000) {
						cost=Constant.Cost_IP_reg_8QAM;
					} else if (length > 0 && length <= 500) {
						cost=Constant.Cost_IP_reg_16QAM;
					}
						TotalProCost = TotalProCost + cost;
						pt.setcostOfIPreg(pt.getcostOfIPreg()+cost);	//加入保护IPreg cost
						file_io.filewrite2(FinalResultFile,"cost=："+cost);
					}
				}
				}
			}
	
		return TotalProCost;
	}
public void NodepairListset(Layer ipLayer,ArrayList<NodePair> nodepairlist) {
	HashMap<String, NodePair> IPnodePairList =  new HashMap<String, NodePair>();
	
	HashMap<String, NodePair> map3 = ipLayer.getNodepairlist();
	Iterator<String> iter3 = map3.keySet().iterator();

	while (iter3.hasNext()) {
		NodePair NodePair = (NodePair) (map3.get(iter3.next()));
		for(int n=0;n<nodepairlist.size();n++){
			NodePair nodePairinList=nodepairlist.get(n);
			if(nodePairinList.getName().equals(NodePair.getName())){
				NodePair.setTrafficdemand(nodePairinList.getTrafficdemand());
				IPnodePairList.put(NodePair.getName(), NodePair);
				break;
			}
		}
	}
	ipLayer.setNodepairlist(IPnodePairList);
}
public ArrayList<NodePair> getDemandList(Layer ipLayer,ArrayList<NodePair> RadomNodepairlist) {
	ArrayList<NodePair> demandList=new ArrayList<NodePair>();
	
	ArrayList<NodePair> NewFormatnodePairList=new ArrayList<NodePair>();
	HashMap<String,NodePair> nodepairlist=ipLayer.getNodepairlist();
     Iterator<String> iter1=nodepairlist.keySet().iterator();
     while(iter1.hasNext()){
    	 NodePair nodepair=(NodePair)(nodepairlist.get(iter1.next()));
    	 NewFormatnodePairList.add(nodepair);
     }
     	for(NodePair np: RadomNodepairlist){
     		for(NodePair nodepairInIP: NewFormatnodePairList){
     			if(np.getName().equals(nodepairInIP.getName())){
     				demandList.add(nodepairInIP);
     				break;
     			}
     		}
     	}
	return demandList;
}

public void mainMethod(NodePair nodepair, Layer iplayer, Layer oplayer,ParameterTransfer ptoftransp, ArrayList<WorkandProtectRoute> wprlist,float threshold) throws IOException {
	
	boolean iproutingFlag = false;
	boolean ipproFlag = false;
	LinearRoute opWorkRoute = new LinearRoute(null, 0, null);
	ArrayList<RequestOnWorkLink> rowList=new ArrayList<>();
	ArrayList<FlowUseOnLink> FlowUseList=new ArrayList<>();
	ptoftransp.setcostOftransForsingle(0);
	
	IPWorkingGrooming ipwg = new IPWorkingGrooming();
	iproutingFlag = ipwg.ipWorkingGrooming(nodepair, iplayer, oplayer, wprlist,FlowUseList);// 在ip层工作路由
	if (iproutingFlag) {// ip层工作路由成功 建立保护
		ipProGrooming ipprog = new ipProGrooming();
		ipproFlag = ipprog.ipprotectiongrooming(iplayer, oplayer, nodepair, true,wprlist);
		if (!ipproFlag) {// 在ip层保护路由受阻 则在光层路由保护
			opProGrooming opg = new opProGrooming();
			opg.opprotectiongrooming(iplayer, oplayer, nodepair, ptoftransp, true, wprlist,threshold,rowList,FlowUseList);
		}
	}
	
	// ip层工作路由不成功 在光层路由工作
	if (!iproutingFlag) {
		opWorkingGrooming opwg = new opWorkingGrooming();
		opWorkRoute = opwg.opWorkingGrooming(nodepair, iplayer, oplayer, wprlist,rowList,threshold,ptoftransp);
		if (opWorkRoute.getLinklist().size()!=0) {// 在光层成功建立工作路径后建立保护路径
			ipProGrooming ipprog = new ipProGrooming();
			ipproFlag = ipprog.ipprotectiongrooming(iplayer, oplayer, nodepair,false, wprlist);
			if (!ipproFlag) {// 在ip层保护路由受阻 则在光层路由保护
				opProGrooming opg = new opProGrooming();
				opg.opprotectiongrooming(iplayer, oplayer, nodepair, ptoftransp, false,
						wprlist,threshold,rowList,FlowUseList);
			}
			
		}
	}
	
}
}
