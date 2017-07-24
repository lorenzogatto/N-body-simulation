package pap.exam;

public class Counter {

	private int count;
	
	public Counter(int v){
		this.count = v;
	}
	
	public void inc(){
		count++;
	}
	
	public int getValue(){
		return count;
	}
	
	public String toString() {
		return new Integer(count).toString();
	}
}
