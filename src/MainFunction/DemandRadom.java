package MainFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import general.file_out_put;
import graphalgorithms.RouteSearching;
import network.Layer;
import network.NodePair;
import randomfunctions.randomfunction;
import subgraph.LinearRoute;

public class DemandRadom {
	
	public ArrayList<NodePair> NodePairRadom(int nodepairNum,String filename,Layer mylayer){//�������nodepair�б�
		ArrayList<LinearRoute> routelist_once=new ArrayList<LinearRoute>();
		int serial=0;
		
		HashMap<String,Integer> nodepair_serial=new HashMap<String,Integer>();
		ArrayList<NodePair> nodepairlist= new ArrayList<NodePair>();
		HashMap<String, NodePair> Snodepair = mylayer.getNodepairlist();
		Iterator<String> iter1 = Snodepair.keySet().iterator();
		while (iter1.hasNext()) 
		{
			
			NodePair nodepairser=(NodePair) (Snodepair.get(iter1.next()));
			nodepair_serial.put(nodepairser.getName(),serial);
			serial++;
//			System.out.println(nodepairser.getName()+"  "+serial);
		}//Ϊnodepair���
		
		////����nodepair
		randomfunction radom=new randomfunction();
		int[] nodepair_num=radom.Dif_random(nodepairNum, mylayer.getNodepairNum());
		int has=0;
		HashMap<String, NodePair> map = mylayer.getNodepairlist();
		Iterator<String> iter = map.keySet().iterator();
		while (iter.hasNext()) 
		{
			has=0;
			routelist_once.clear();
			NodePair nodepair=(NodePair) (map.get(iter.next()));

//			rs.findAllRoute(nodepair.getSrcNode(), nodepair.getDesNode(), mylayer, null, 100, routelist_once);
//			if(routelist_once.size()<3) continue;
			for(int a=0;a<nodepair_num.length;a++){
				if(nodepair_num[a]==nodepair_serial.get(nodepair.getName())){
					has=1;
					break;
				}
					
			}
			if(has==0) continue;//�������demand
			nodepairlist.add(nodepair);
		}
		return nodepairlist;
	}
	
	public void TrafficNumRadom(ArrayList<NodePair>nodepairlist ){
//		String serve = "F:\\zyx\\programFile\\USNET.dat";
//		String local = "D:\\zyx\\programFile\\RegwithProandTrgro\\USNET.dat";
//		file_out_put file_io=new file_out_put();
		randomfunction radom=new randomfunction();
		int setDemand=0,nodepairNum=nodepairlist.size() ;
		for(NodePair nodePair:nodepairlist){
			setDemand++;
//			if(setDemand>nodepairNum/3){
				nodePair.setTrafficdemand(radom.Num_random(1,100)[0]+1);//����200G-1T������
//			}
//			else{
//				nodePair.setTrafficdemand(radom.Num_random(1, 10)[0]+1);
//			}
		}
	}
}
