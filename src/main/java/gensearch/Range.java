package gensearch;

import net.objecthunter.exp4j.ExpressionBuilder;

/*
 * Value range 
 * 
 * in increasing order
 * [1,2] + [3,4]...
 * 
 * int ranges [n][2]
 * 		[0][2] : [1,2]
 * 		[1][2] : [3,4]
 * 		...
 */
public class Range {

	public final double[][] datas;
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	
	public Range(double min, double max){
		datas = new double[1][2];
		datas[0][LEFT] = min;
		datas[0][RIGHT] = max;
	}
	
	public Range(double ... ds){
		int num = ds.length/2;
		datas = new double[num][2];
		for(int i=0; i<num; i++){
			datas[i][LEFT] = ds[i*2];
			datas[i][RIGHT] = ds[i*2+1];
		}
	}
	
	public Range(double[][] ranges){
		this.datas = ranges;
	}
	
	public int getNum(){
		return datas.length;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder(datas.length+":");
		for(int i=0;i<datas.length; i++){
			sb.append(i+":["+datas[i][LEFT]+","+datas[i][RIGHT]+"],");
		}
		sb.append("\n");
		return sb.toString();
	}
	
	public boolean hasChangedSign(){
		byte s = 0;
		for(int i=0; i<datas.length; i++){
			for(int j=0; j<datas[i].length; j++){
				if(datas[i][j]!=0){
					if(s==0){
						s = getSign(datas[i][j]);
					}else{
						byte nextS = getSign(datas[i][j]);
						if(nextS!=s){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	private byte getSign(double d){
		if(d==0){
			return 0;
		}else if(d<0){
			return -1;
		}else{
			return 1;
		}
	}
	
	public static void main(String[] args) {
		double res = new ExpressionBuilder("x1*x2/x3").variables("x1","x2","x3").build()
				.setVariable("x1", 2)
				.setVariable("x2", 3)
				.setVariable("x3", 4)
				.evaluate();
		System.out.println(res);
		
	}

	public double getLeft() {
		return datas[0][LEFT];
	}
	
	public double getRight() {
		return datas[0][RIGHT];
	}

}
