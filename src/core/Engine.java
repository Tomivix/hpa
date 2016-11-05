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
	
	
	//@wasp const types because I'm getting lost
	public static final int RR = 1;
	public static final int RM = 2;
	public static final int JUMP = 3;
	public static final int DC = 4;
	public static final int DS = 5;
	
	// HEAD //
	
	private final String[] cmdRR = {
			"AR", "SR", "MR", "DR", "CR", "LR"
	};
		
	private final String[] cmdRM = {
			"ST", "A", "S", "M", "D", "C", "L", "LA", 
	};
		
	private final String[] cmdJ = {
				"JN", "JP", "JZ", "J"
	};
		
	private final String[] cmdM = {
				"DC", "DS"
	};
		
	private final String[][] cmds = {
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
		View.Instance.updateMemCell((id-1024)/4);
	}
	
	public int getVarCount(){
		return vars.size();
	}
	
	public void addOrder(String label, String order, int size){
		if(label != null) orderLabels.put(label, lastOrder);
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
		private int type;
		
		
		public abstract void execute(int arg1, int arg2);
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
		lastVar = 1024;
		vars.clear();
		varLabels.clear();
		String[] lines = s.split("\n");
		for(String line : lines){
			int type = Parser.parse(line, true);
			if(type != 0){
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
					String dir = (type == DC) ? "DC" : "DS";
					int c1 = line.indexOf(dir);
					String countS = line.substring(c1+2, c2);
					count = Integer.parseInt(countS);
				}
				
				//check if value is known
				int v1 = line.indexOf('(');
				int v2 = line.indexOf(')');
				if(v1 > 0){
					String valueS = line.substring(v1+1, v2);
					value = Integer.parseInt(valueS);
				}
				
				addVar(label, value);
				for(int i=1; i<count; i++) addVar(value);
				
			}
		}
		System.out.println(vars);
		System.out.println(varLabels);
		
		View.Instance.setRegisters();	//Needed to coorectly display graphics
		View.Instance.setMemoryCells();
	}
	
	public void buildOrdersFromString(String s){
		lastOrder = 2048;
		orders.clear();
		orderLabels.clear();
		String[] lines = s.split("\n");
		for(String line : lines){
			int type = Parser.parse(line, false);
			if(type != 0){
				//remove all white spaces
				line = line.replaceAll("\\s+", "");
				
				//set basic values;
				int count = 1;
				int value = new Random().nextInt();
				
				//get label and order
				int parser = line.indexOf(':');
				String label = (parser < 0) ? null : line.substring(0, parser);
				String order = line.substring(parser+1);
				
				//size depending on type
				int size = (type == JUMP) ? 2 : 4;
				addOrder(label, order, size);
			}
		}
		System.out.println(orders);
		System.out.println(orderLabels);
	}
	
	public void run(){
		for(int i=currentOrder; i<lastOrder;) step();
	}
	
	public void step(){
		//TODO
		System.out.println(currentOrder);
		if(currentOrder < lastOrder){
			myStep();
			for(int reg : regs) System.out.print(reg + " ");
			System.out.print("  -flag= " + flag);
			System.out.println();
		
			View.Instance.setRegisters();
			View.Instance.setMemoryCells();
		}
		else System.out.println("end of orders");
		
		//Set the arrow pointing on register and memory panel 
		//View.Instance.updateValues(<last_modified_source>, <last_modified_destination>, <MODE>);
		//MODE:
		//		-View.RM (Register -> Memory cell)
		//		-View.RR (Register -> Register)
		//		-View.MR (Memory cell -> Register)
	}
	
	
	public void myStep(){
		String order = orders.get(currentOrder);
		/**/ System.out.println(order);
		String command = order.substring(0, 2);
		for(String cmd : cmdJ){
			if(command.contains(cmd)){
				int actualOrder = currentOrder;
				int beginId = order.indexOf(cmd) + cmd.length();
				String label = order.substring(beginId);
				int arg1 = orderLabels.get(label);
				functions.get(cmd).execute(arg1, 0);
				if(actualOrder == currentOrder)currentOrder += 2;
				return;
			}
		}
		
		for(String cmd : cmdRR){
			if(command.contains(cmd)){
				int beginId = order.indexOf(cmd) + cmd.length();
				String args = order.substring(beginId);
				int parser = args.indexOf(',');
				String arg1S = args.substring(0, parser);
				String arg2S = args.substring(parser+1);
				int arg1 = Integer.parseInt(arg1S);
				int arg2 = Integer.parseInt(arg2S);
				functions.get(cmd).execute(arg1, arg2);
				currentOrder += 4;
				return;
			}
		}
		
		for(String cmd : cmdRM){
			if(command.contains(cmd)){
				int beginId = order.indexOf(cmd) + cmd.length();
				String args = order.substring(beginId);
				int parser = args.indexOf(',');
				String arg1S = args.substring(0, parser);
				String arg2S = args.substring(parser+1);
				int arg1 = Integer.parseInt(arg1S);
				System.out.println(arg2S);
				int arg2 = varLabels.get(arg2S);
				functions.get(cmd).execute(arg1, arg2);
				currentOrder += 4;
				return;
			}
		}
	}
	
}
