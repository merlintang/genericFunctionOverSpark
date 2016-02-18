package gensearch;

import java.util.ArrayList;
import java.util.Collections;

import net.objecthunter.exp4j.Expression;

/**
 * vertex of MBR
 * @author qlong
 *
 */
public class Vertex implements Comparable<Vertex> {

	public String index;
	public double expVal;
	
	public Vertex(String index){
		this.index = index;
	}
	
	public Vertex(String index, double val){
		this.index = index;
		this.expVal = val;
	}
	
	public String toString(){
		return "("+index+"):"+expVal;
	}
	
	public static void main(String[] args) {
		ArrayList<Vertex> list = new ArrayList<Vertex>();
		list.add(new Vertex("",5));
		list.add(new Vertex("",2));
		list.add(new Vertex("",3));
		Collections.sort(list);
		System.out.println(list);
	}

	public void eval(Expression exp, int numDimensions, double[] mBR_S, double[] mBR_T) {
		for(int i=1; i<=numDimensions; i++){
			String varN = "x"+i;
			if(index.charAt(i-1)=='0'){
				exp.setVariable(varN, mBR_S[i-1]);
			}else{
				exp.setVariable(varN, mBR_T[i-1]);
			}
			
		}
		expVal = exp.evaluate();
	}

	@Override
	public int compareTo(Vertex v2) {
		if(this.expVal==v2.expVal){
			return 0;
		}else if(this.expVal>v2.expVal){
			return 1;
		}else{
			return -1;
		}
	}

	
}
