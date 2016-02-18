package gensearch;

import java.util.ArrayList;

public class RTreeNode {

	private static long idcnt = 0;
	
	public final long id;
	
	//if it's NOT  leaf node
	public ArrayList<RTreeNode> subNodes;
	public double[] MBR_S,MBR_T;
	
	//if it's a leaf node
	public double[] val; 

	public RTreeNode(){	
		id = idcnt ++;
	}
	
	public RTreeNode(double[] val){
		id = idcnt ++;
		this.val=val.clone();
	}
	
	public boolean hasSubNodes(){
		if(subNodes==null || subNodes.size()==0){
			return false;
		}else{
			return true;
		}
	}
	
	public String toString(String prefix){
		prefix = prefix+"#";
		if(hasSubNodes()){
			StringBuilder sb = new StringBuilder(prefix+"{");
			
			//print MBR
			sb.append("(");
			for(int i=0;i<MBR_S.length;i++){
				sb.append( (Math.round( MBR_S[i] * 100 )/100.0) +",");
			}
			sb.append(")~(");
			for(int i=0;i<MBR_T.length;i++){
				sb.append( (Math.round( MBR_T[i] * 100 )/100.0) +",");
			}
			sb.append(")");
			
			for(int i=0; i<subNodes.size(); i++){
				sb.append(subNodes.get(i).toString(prefix)+",");
			}
			sb.append("}\n");
			return sb.toString();
		}else{
			StringBuilder sb = new StringBuilder(prefix+"[");
			for(int i=0;i<val.length;i++){
				sb.append( (Math.round( val[i] * 100 )/100.0) +",");
				//sb.append( ( val[i] ) +",");
			}
			sb.append("]\n");
			return sb.toString();
		}
	}
	
	public String toString(){
		return toString("");
	}
	
	public static void main(String[] args) {
		RTreeNode rt = new RTreeNode();
		rt.subNodes = new ArrayList<RTreeNode>(10);
		
		for(int i=0;i<10;i++){
			double[] ds = {0,i};
			rt.subNodes.add( new RTreeNode( ds ) );
		}
		System.out.println(rt);
	}

}
