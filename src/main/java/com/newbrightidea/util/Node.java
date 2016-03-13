package com.newbrightidea.util;

import java.util.LinkedList;

public class Node implements java.io.Serializable {
	private static long id_cnt = 0;
	public final long id;
	final float[] coords;
	final float[] dimensions;
	public LinkedList<Node> children;
	public final boolean leaf;
	private static final long serialVersionUID = 7526721295622776147L;

	private double[] MBR_S = null;
	private double[] MBR_T = null;
	Node parent;
	
	public boolean isRealLeaf(){
		if(children.size()==0){
			return true;
		}else{
			return false;
		}
	}

	public Node(float[] coords, float[] dimensions, boolean leaf) {
		this.coords = new float[coords.length];
		this.dimensions = new float[dimensions.length];
		System.arraycopy(coords, 0, this.coords, 0, coords.length);
		System.arraycopy(dimensions, 0, this.dimensions, 0, dimensions.length);
		this.leaf = leaf;
		children = new LinkedList<Node>();
		id = id_cnt++;
	}

	public double[] MBR_S() {
		if (MBR_S == null) {
			MBR_S = new double[coords.length];
			for (int i = 0; i < coords.length; i++) {
				MBR_S[i] = coords[i];
			}
		}
		return MBR_S;
	}

	public double[] MBR_T() {
		if (MBR_T == null) {
			MBR_T = new double[coords.length];
			for (int i = 0; i < coords.length; i++) {
				MBR_T[i] = coords[i] + dimensions[i];
			}
		}
		return MBR_T;
	}

	public double[] val() {
		return MBR_S();
	}

}
