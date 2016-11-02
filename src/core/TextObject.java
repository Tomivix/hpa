package core;

public class TextObject {
	private int start;
	private int length;
	private int type;
	
	TextObject(int start, int length, int type){
		this.start = start;
		this.length = length;
		this.type = type;
	}
	
	
	public int getStart(){
		return start;
	}
	
	public int getLength(){
		return length;
	}
	
	public int getType(){
		return type;
	}
	
}
