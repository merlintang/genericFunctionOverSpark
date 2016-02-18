package gensearch;

import java.util.ArrayList;

public class PerfectRTree {

	private int numDimensions,numLevels,fanout,numSeps;
	
	private Range[] domains;
	
	//R-tree root
	public RTreeNode root;
	
	public PerfectRTree(int numDimensions, int numSeps, int numLevels){
		this.numDimensions = numDimensions;
		
		//how many seperates for a node on one axis
		this.numSeps = numSeps;
		//like when numSeps = 3, Dimensions=3, then fanout = 3^3 = 27
		this.fanout = (int)Math.pow(numSeps, numDimensions);
		
		this.numLevels = numLevels;
		
		//set all dimensions' domains as 0~10
		domains = new Range[numDimensions];
		for(int i=0; i<numDimensions; i++){
			domains[i] = new Range(0,10);
		}
	}
	
	public PerfectRTree setDomain(int dimensionNum, double min, double max){
		domains[dimensionNum] = new Range(min,max);
		return this;
	}
	
	public PerfectRTree buildRandomly(){
		//1.build empty tree first
		root = new RTreeNode();
		buildNextLevel(root,numLevels);
		
		//2.fill random data
		//fill data recursively visiting non-leaf node, and calculate MBRs after done filling
		fillNextLevel(root,numLevels,domains);
		
		return this;
	}
	
	private void fillNextLevel(RTreeNode node, int remainLevels, final Range[] currDomains) {
		if(remainLevels == 1){
			//fill each dimension using currDomain
			for(int i=0; i<numDimensions; i++){
				node.val[i] = getRand(currDomains[i]);
			}
		}else{
			//1. generate new domains using steps and visiting sub-nodes
			//1.1 get all sub-domains of the currDomain
			ArrayList<Range[]> moreDomains = new ArrayList<Range[]>();
			getMoreDomain(currDomains,0,moreDomains);
			if(moreDomains.size() != fanout){
				throw new IllegalArgumentException("This can't happen!!");
			}
			//1.2 apply on each subNode
			for(int i=0; i<fanout;i++){
				fillNextLevel(node.subNodes.get(i),remainLevels-1,moreDomains.get(i));
			}
			
			//2.calculate MBR
			//if subNodes are non-leaf nodes, then calculate from MBRs, 
			//otherwise calculate from val
			if(node.subNodes.get(0).hasSubNodes()){
				for(int i=0; i<numDimensions; i++){
					double min=Double.POSITIVE_INFINITY;
					double max=Double.NEGATIVE_INFINITY;
					for(int j=0;j<fanout;j++){
						if(node.subNodes.get(j).MBR_S[i] < min){
							min = node.subNodes.get(j).MBR_S[i];
						}
						if(node.subNodes.get(j).MBR_T[i] > max){
							max = node.subNodes.get(j).MBR_T[i];
						}
					}
					node.MBR_S[i] = min;
					node.MBR_T[i] = max;
				}
			}else{
				for(int i=0; i<numDimensions; i++){
					double min=Double.POSITIVE_INFINITY;
					double max=Double.NEGATIVE_INFINITY;
					for(int j=0;j<fanout;j++){
						if(node.subNodes.get(j).val[i] < min){
							min = node.subNodes.get(j).val[i];
						}
						if(node.subNodes.get(j).val[i] > max){
							max = node.subNodes.get(j).val[i];
						}
					}
					node.MBR_S[i] = min;
					node.MBR_T[i] = max;
				}
			}
		}
	}

	private double getRand(Range range) {
		double min = range.getLeft();
		double max = range.getRight();
		//get a random number between min and max
		double rand = min + (max-min)*Math.random();
		return rand;
	}

	private void getMoreDomain(Range[] currDomains,int currDimIdx, ArrayList<Range[]> moreDomains) {
		
		//0.save domain at current dimension
		Range currDomain = currDomains[currDimIdx];
		
		//1.change currDomains at current Dimension
		for(int i=1; i<=numSeps; i++){
			
			double step = (currDomain.getRight()-currDomain.getLeft())/numSeps;
			currDomains[currDimIdx] = new Range(currDomain.getLeft() + (i-1)*step, 
					currDomain.getLeft()+i*step);
			
			//2. if at the last then write into moreDomains, otherwise goto next level
			if(currDimIdx==numDimensions-1){//at last dimension,write into moreDomains
				//copy a new one
				Range[] copyCurrDomains = new Range[numDimensions];
				for(int j=0;j<numDimensions;j++){
					copyCurrDomains[j] = new Range(currDomains[j].getLeft(),currDomains[j].getRight());
				}
				moreDomains.add(copyCurrDomains);
				
			}else{//for current Dimension, make numSeps pieces
				getMoreDomain(currDomains,currDimIdx+1,moreDomains);
			}
		}
		//3. recover currDomains
		currDomains[currDimIdx] = currDomain;
		
	}

	/**
	 * build Node hierarchy Recursively
	 * @param node
	 * @param remainLevels
	 */
	private void buildNextLevel(RTreeNode node, int remainLevels) {
		if(remainLevels==1){
			node.val = new double[numDimensions];
		}else{
			node.MBR_S = new double[numDimensions];
			node.MBR_T = new double[numDimensions];
			node.subNodes = new ArrayList<RTreeNode>(fanout);
			for(int i=0;i<fanout;i++){
				RTreeNode tmpNode = new RTreeNode();
				buildNextLevel(tmpNode, remainLevels-1);
				node.subNodes.add(tmpNode);
			}
		}
	}

	public static void main(String[] args) {
		PerfectRTree prt = new PerfectRTree(3,2,2);
		prt.setDomain(1, 0, 100);
		prt.setDomain(2, -1000,1000);
		prt.buildRandomly();
		System.out.println(prt.root);

	}

}
