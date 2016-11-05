package core;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.SwingUtilities;

import view.View;

public class Engine {
	//const values
	
	public static final byte ZERO = 0;
	public static final byte POSITIVE = 1;
	public static final byte NEGATIVE = 2;
	public static final byte ERROR = 3;
	
	// HEAD //
	
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
		
	private final String[][] commands = {
				cmdRR, cmdRM, cmdJ, cmdM
	};
	
	// TAIL // <-- what are all those arrays for?
	
	//all the rest
	
	public static Engine current;
	
	private HashMap<Integer, String> orders;
	private HashMap<String, Integer> orderLabels;
	private HashMap<Integer, Integer> vars;
	private HashMap<String, Integer> varLabels;
	private HashMap<String, Function> functions;
	private String[] mathOps;
	private int[] regs;
	
	private int lastVar;
	private int currentOrder;
	private int lastOrder;
	private byte flag;
	
	public Engine(){
		current = this;
		orders = new HashMap<>();
		orderLabels = new HashMap<>();
		vars = new HashMap<>();
		varLabels = new HashMap<>();
		functions = new HashMap<>();
		
		mathOps = new String[]{"A","S","M","D","C"};

		regs = new int[16];
		for(int reg : regs) reg = (new Random()).nextInt();
		regs[14] = lastVar = 1024;
		regs[15] = lastOrder = currentOrder = 2048;

		flag = ERROR;
		
		createFunctions();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				View v = new View();
				v.setRegisters();
				v.setMemoryCells();
			}
		});
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
		View.Instance.updateRegister(id);
	}
	
	public int getRegCount(){
		return regs.length;
	}
	
	public void addVar(String label, int value){
		varLabels.put(label, lastVar);
		addVar(value);
	}
	
	public void addVar(int value){
		vars.put(lastVar, value);
		lastVar += 4;
	}
	
	public Map<Integer, Integer> getAllVars(){
		return vars;
	}
	
	public int getVar(String label){
		int id = varLabels.get(label);
		return getVar(id);
	}
	
	public int getVar(int id){
		return vars.get(id);
	}
	
	public void setVar(int id, int value){
		vars.replace(id, value);
		View.Instance.updateMemCell(id/4);
	}
	
	public int getVarCount(){
		return vars.size();
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
	
	private void buildVariables(String code){
		String[] lines = code.split("\n");
		for(String line : lines){
			int type = Parser.parse(line, true);
			if(type == 4 || type == 5){
				//remove all white spaces
				line = line.replaceAll("\\s+", "");
				
				//set basic values;
				int count = 1;
				int value = new Random().nextInt();
				
				//get label
				String label = line.substring(0, line.indexOf(':'));
				
				//check if is more than one 
				int c2 = line.indexOf('*');
				if(c2>0){
					String dir = (type == 4) ? "DC" : "DS";
					int c1 = line.indexOf(dir);
					String countS = line.substring(c1+2, c2);
					count = Integer.parseInt(countS);
				}
				
				//check if value is known
				int v1 = line.indexOf('(');
				int v2 = line.indexOf(')');
				if(v1+v1 > 0){
					String valueS = line.substring(v1+1, v2);
					value = Integer.parseInt(valueS);
				}
				
				addVar(label, value);
				for(int i=1; i<count; i++) addVar(value);
				
				System.out.println(vars);
				
			}
		}
		
		
		
		
	}
	
	private void createFunctions(){
		
		// register - register functions -------------------------------------------//		
		
		for(final String op : mathOps) {
			functions.put(op.concat("R"), new Function() {
				public void execute(int arg1, int arg2) {
					int reg = 0; switch(op) {
						case "A": reg = getReg(arg1) + getReg(arg2); break;
						case "S": reg = getReg(arg1) - getReg(arg2); break;
						case "M": reg = getReg(arg1) * getReg(arg2); break;
						case "D": reg = getReg(arg1) / getReg(arg2); break;
						case "C": reg = getReg(arg1) - getReg(arg2); break;
					} if(!op.equals("C")) setReg(arg1, reg); setFlag(reg);
				}
			});
		}
		
		functions.put("LR", new Function(){
			public void execute(int arg1, int arg2){
				setReg(arg1, getReg(arg2));
			}
		});		
		
		// register - memory functions -------------------------------------------// 
		
		for(final String op : mathOps) {
			functions.put(op, new Function() {
				public void execute(int arg1, int arg2) {
					int reg = 0; switch(op) {
						case "A": reg = getReg(arg1) + getVar(arg2); break;
						case "S": reg = getReg(arg1) - getVar(arg2); break;
						case "M": reg = getReg(arg1) * getVar(arg2); break;
						case "D": reg = getReg(arg1) / getVar(arg2); break;
						case "C": reg = getReg(arg1) - getVar(arg2); break;
					} if(!op.equals("C")) setReg(arg1, reg); setFlag(reg);
				}
			});
		}
		
		functions.put("L", new Function(){
			public void execute(int arg1, int arg2){
				setReg(arg1, getVar(arg2));
			}
		});
		
		functions.put("LA", new Function(){
			public void execute(int arg1, int arg2){
				setReg(arg1, arg2);
			}
		});
		
		functions.put("ST", new Function(){
			public void execute(int arg1, int arg2){
				setVar(arg2, getReg(arg1));
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
				for(int i=0; i<arg1; i++) addVar("label", arg2);
			}
		});
		
		functions.put("DS", new Function(){
			public void execute(int arg1, int arg2){
				for(int i=0; i<arg1; i++)
					addVar("label", new Random().nextInt());
			}
		});		
	}
	
	public void buildDirectivesFromString(String s){
		//TODO
		//System.out.println(s);
		buildVariables(s);
		
		View.Instance.setRegisters();	//Needed to coorectly display graphics
		View.Instance.setMemoryCells();
	}
	
	public void buildOrdersFromString(String s){
		//TODO
		System.out.println(s);
	}
	
	public void run(){
		//TODO
		//Probably will run step every few seconds
	}
	
	public void step(){
		//TODO
		
		//Set the arrow pointing on register and memory panel 
		//View.Instance.updateValues(<last_modified_source>, <last_modified_destination>, <MODE>);
		//MODE:
		//		-View.RM (Register -> Memory cell)
		//		-View.RR (Register -> Register)
		//		-View.MR (Memory cell -> Register)
	}
}
