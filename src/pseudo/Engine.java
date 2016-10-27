package pseudo;

import java.util.HashMap;
import java.util.Random;

public class Engine {
	//const values
	
	public static final byte ZERO = 0;   // <-- do we really need to make constants for that?
	public static final byte POSITIVE = 1;
	public static final byte NEGATIVE = 2;
	public static final byte ERROR = 3;
	
	private final String[] cmdRR = {
			"AR", "SR", "MR", "DR", "CR", "LR"
	};
		
	private final String[] cmdRM = {
			"A", "S", "M", "D", "C", "L", "LA", "ST"
	};
		
	private final String[] cmdJ = {
				"J", "JN", "JP", "JZ"
	};
		
	private final String[] cmdM = {
				"DC", "DS"
	};
		
	private final String[][] commands = {  // <-- what is that array supposed to do?
				cmdRR, cmdRM, cmdJ, cmdM
	};
	
	//all the rest
	
	private HashMap<Integer, String> orders;
	private HashMap<String, Integer> orderLabels;
	private HashMap<Integer, Integer> vars;
	private HashMap<String, Integer> varLabels;
	private HashMap<String, Function> functions;
	private String[] mathOps; //new code
	private int[] regs;
	
	private int lastVar;
	private int currentOrder;
	private int lastOrder;
	private byte flag;
	
	Engine(){
		orders = new HashMap<>();
		orderLabels = new HashMap<>();
		vars = new HashMap<>();
		varLabels = new HashMap<>();
		functions = new HashMap<>();
		
		mathOps = new String[]{"A","S","M","D","C"};  //new code

		regs = new int[16];
		for(int reg : regs) reg = 0;
		
		lastVar = 0;
		currentOrder = 0;
		lastOrder = 0;
		flag = ERROR;
		
		createFunctions();
	}
	
	public String debug(){
		addOrder("LS", 2);
		addOrder("CD", 4);
		addOrder("A", 4);
		addOrder("Siea", 2);
		addOrder("YOYO", 4);
		return orders.toString() + lastOrder;
	}
	
	
	public int getReg(int id){
		return regs[id];
	}
	
	public void setReg(int id, int value){
		regs[id] = value;
	}
	
	public void addVar(int value){
		vars.put(lastVar, value);
		lastVar += 4;
	}
	
	public int getVar(int id){
		return vars.get(id);
	}
	
	public void setVar(int id, int value){
		vars.replace(id, value);
	}
	
	public void addOrder(String order, int size){
		orders.put(lastOrder, order);
		lastOrder += size;
	}
	
	
	public void setCurrentOrder(int id){
		this.currentOrder = id;
	}
	
	public byte getFlag(){
		return flag;
	}
	
	public void setFlag(int value){
		if(value > 0) flag = POSITIVE;
		else if(value < 0) flag = NEGATIVE;
		else flag = ZERO;
	}
	
	
	private abstract class Function {
		public abstract void execute(int arg1, int arg2);
	}
	
	
	private void createFunctions(){
		
		// register - register functions -------------------------------------------//		
		
		// bulk function for adding AR,SR,MR,DR,CR arithmetic operations
		// takes less space at the cost of unnoticeably longer compiling time
		
		for(String op : mathOps) {
			functions.put(op.concat("R"), new Function() {
				public void execute(int arg1, int arg2) {
					int reg = 0;
					switch(op) {
						case "A": reg = getReg(arg1) + getReg(arg2); break;
						case "S": reg = getReg(arg1) - getReg(arg2); break;
						case "M": reg = getReg(arg1) * getReg(arg2); break;
						case "D": reg = getReg(arg1) / getReg(arg2); break;
						case "C": reg = getReg(arg1) - getReg(arg2); break;
					}
					if(!op.equals("C")) setReg(arg1, reg); setFlag(reg);
				}
			});
		}
		
		/* bulk function above takes care of those, will get removed
		
		functions.put("AR", new Function(){
			public void execute(int arg1, int arg2){
				int reg = getReg(arg1) + getReg(arg2);
				setReg(arg1, reg);
				setFlag(reg);
			}
		});
		
		functions.put("SR", new Function(){
			public void execute(int arg1, int arg2){
				int reg = getReg(arg1) - getReg(arg2);
				setReg(arg1, reg);
				setFlag(reg);
			}
		});
		
		functions.put("MR", new Function(){
			public void execute(int arg1, int arg2){
				int reg = getReg(arg1) * getReg(arg2);
				setReg(arg1, reg);
				setFlag(reg);
			}
		});
	
		functions.put("DR", new Function(){
			public void execute(int arg1, int arg2){
				int reg = getReg(arg1) / getReg(arg2);
				setReg(arg1, reg);
				setFlag(reg);
			}
		});
		
		functions.put("CR", new Function(){
			public void execute(int arg1, int arg2){
				int reg = getReg(arg1) - getReg(arg2);
				setFlag(reg);
			}
		});
		*/
		
		functions.put("LR", new Function(){
			public void execute(int arg1, int arg2){
				int reg = getReg(arg2);
				setReg(arg1, reg);
			}
		});
		
		
		
		// register - memory functions -------------------------------------------//
		
		// bulk function for adding A,S,M,D,C arithmetic operations
		// takes less space at the cost of unnoticeably longer compiling time 
		
		for(String op : mathOps) {
			functions.put(op, new Function() {
				public void execute(int arg1, int arg2) {
					int reg = 0;
					switch(op) {
						case "A": reg = getReg(arg1) + getVar(arg2); break;
						case "S": reg = getReg(arg1) - getVar(arg2); break;
						case "M": reg = getReg(arg1) * getVar(arg2); break;
						case "D": reg = getReg(arg1) / getVar(arg2); break;
						case "C": reg = getReg(arg1) - getVar(arg2); break;
					}
					if(!op.equals("C")) setReg(arg1, reg); setFlag(reg);
				}
			});
		}
		
		/* bulk function above takes care of those, will get removed
		
		functions.put("A", new Function(){
			public void execute(int arg1, int arg2){
				int reg = getReg(arg1) + getVar(arg2);
				setReg(arg1, reg);
				setFlag(reg);
			}
		});
		
		functions.put("S", new Function(){
			public void execute(int arg1, int arg2){
				int reg = getReg(arg1) - getVar(arg2);
				setReg(arg1, reg);
				setFlag(reg);
			}
		});
		
		functions.put("M", new Function(){
			public void execute(int arg1, int arg2){
				int reg = getReg(arg1) * getVar(arg2);
				setReg(arg1, reg);
				setFlag(reg);
			}
		});
	
		functions.put("D", new Function(){
			public void execute(int arg1, int arg2){
				int reg = getReg(arg1) / getVar(arg2);
				setReg(arg1, reg);
				setFlag(reg);
			}
		});
		
		functions.put("C", new Function(){
			public void execute(int arg1, int arg2){
				int reg = getReg(arg1) - getVar(arg2);
				setFlag(reg);
			}
		});
		*/
		
		functions.put("L", new Function(){
			public void execute(int arg1, int arg2){
				int reg = getVar(arg2);
				setReg(arg1, reg);
			}
		});
		
		functions.put("LA", new Function(){
			public void execute(int arg1, int arg2){
				int reg = arg2;
				setReg(arg1, reg);
			}
		});
		
		functions.put("ST", new Function(){
			public void execute(int arg1, int arg2){
				int reg = getReg(arg1);
				setVar(arg2, reg);
			}
		});
		
		// jump functions -------------------------------------------//
		
		// TODO come up with some bulk function for all types of jumps
		
		functions.put("J", new Function(){
			public void execute(int arg1, int arg2){
				setCurrentOrder(arg1);
			}
		});
		
		functions.put("JN", new Function(){
			public void execute(int arg1, int arg2){
				if(getFlag() == NEGATIVE) setCurrentOrder(arg1);
			}
		});
		
		functions.put("JP", new Function(){
			public void execute(int arg1, int arg2){
				if(getFlag() == POSITIVE) setCurrentOrder(arg1);
			}
		});
		
		functions.put("JZ", new Function(){
			public void execute(int arg1, int arg2){
				if(getFlag() == ZERO) setCurrentOrder(arg1);
			}
		});
		
		
		// memory functions -------------------------------------------//
		
		functions.put("DC", new Function(){
			public void execute(int arg1, int arg2){
				for(int i=0; i<arg1; i++)
					addVar(arg2);
			}
		});
		
		functions.put("DS", new Function(){
			public void execute(int arg1, int arg2){
				for(int i=0; i<arg1; i++){
					int value = (new Random()).nextInt();
					addVar(value);
				}
			}
		});
		
	}
	
	// public boolean parse(String line) returns boolean
	// checks if the raw command provided by user has correct HPA-compliant syntax
	// returns true only if the command has valid syntax (can possibly be executed)
	// this method (so far) doesn't check if specified memory cells were declared!
	public boolean parse(String line) {
		if(
			line.matches("([A-Z0-9]+\\s:\\s|^)(AR|SR|MR|DR|CR|LR)\\s[0-9]{1,2},[0-9]{1,2}") ||
			line.matches("([A-Z0-9]+\\s:\\s|^)(A|S|M|D|C|L|LA|ST)\\s[0-9]{1,2},[A-Z0-9]+") ||
			line.matches("([A-Z0-9]+\\s:\\s|^)(J|JN|JP|JZ)\\s[A-Z0-9]+") ||
			line.matches("[A-Z0-9]+\\s:\\s(DC|DS)\\s([0-9]+\\*|)INT\\([0-9]+\\)")
		) return true;
		else return false;
	}
	
	// dummy main method, remove if no longer needed
	public static void main(String[] args) {
		Engine Engine = new Engine();
		Engine.setReg(1, 123);
		Engine.setReg(2, 17);
		Engine.functions.get("AR").execute(1, 2);
		//System.out.println(Engine.getReg(1));
		System.out.println(Engine.parse("A : DC 1*INT(100)"));
	}

}
