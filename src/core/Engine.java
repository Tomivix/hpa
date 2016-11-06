package core;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	//@wasp I need following arrays :)
	// HEAD //
	
	private final String[] cmdRR = {
			"AR", "SR", "MR", "DR", "CR", "LR"
	};
		
	private final String[] cmdRM = {
			"A", "S", "M", "D", "C", "L", "LA", "ST"
	};
		
	private final String[] cmdJ = {
			"J", "JN", "JP", "JZ",
	};
		
	private final String[] cmdM = {
			"DC", "DS"
	};
		
	private final String[][] commands = {
				cmdRR, cmdRM, cmdJ,
	};
	
	// TAIL // <-- what are all those arrays for?
	
	//all the rest
	
	//@mrwasp It's temporary or we really will use Singletons like this or View.Instance?
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
		
		for(final String cmd : cmdJ){
			functions.put(cmd, new Function(){
				public void execute(int arg1, int arg2){
				switch(cmd){
					case "J": setCurrentOrder(arg1); break;
					case "JP": if(getFlag() == POSITIVE) setCurrentOrder(arg1); break;
					case "JN": if(getFlag() == NEGATIVE) setCurrentOrder(arg1); break;
					case "JZ": if(getFlag() == ZERO) setCurrentOrder(arg1); break;
				
				}
				
			}
		});
		}
		
		//@mrwasp
		//it seems following functions are unnecessary since we add variables by #buildDirectivesFromString
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
	
	//@mrwasp
	//maybe we should use separate function for clearing datas?
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
		
		//FIXME GUI - memory cells doesn't refresh automatically
		View.Instance.setRegisters();	//Needed to coorectly display graphics
		View.Instance.setMemoryCells();
	}
	
	//@mrwasp
	public void buildOrdersFromString(String s){
		lastOrder = currentOrder = 2048;
		orders.clear();
		orderLabels.clear();
		String[] lines = s.split("\n");
		for(String line : lines){
			int type = Parser.parse(line, false);
			if(type != 0){
				//split into label and order
				int parser = line.indexOf(':');
				String label = (parser < 0) ? null : line.substring(0, parser);
				String order = line.substring(parser+1);
				
				
				//clear label
				if(label!=null)label = label.replaceAll("\\s+", "");
				
				//clear order - syntax "ORDER*ARGUMENTS"
				//any ideas how to do it easier?
				Pattern orderPattern = Pattern.compile("AR|SR|MR|DR|CR|LR|A|S|M|D|C|L|LA|ST|J|JN|JP|JZ");
				Matcher matcher = orderPattern.matcher(order);
				if(matcher.find()){
					int start = matcher.start();
					int end = order.indexOf(" ", start);
					String order1 = order.substring(start, end);
					String order2 = order.substring(end);
					order2 = order2.replaceAll("\\s+", "");
					order = order1 + "*" + order2;
				}
			
				//size depending on type
				int size = (type == JUMP) ? 2 : 4;
				
				addOrder(label, order, size);
			}
		}
		System.out.println(orders);
		System.out.println(orderLabels);
		
		//FIXME GUI - memory cells doesn't store orders
	}
	
	//@mrwasp
	public void run(){
		while(currentOrder < lastOrder){
			step();
			try { Thread.sleep(1000);}
			catch(InterruptedException ex) { Thread.currentThread().interrupt();}
		}
	}
	
	
	//@mrwasp
	public void step(){
		/**/System.out.println(currentOrder);
		if(currentOrder >= lastOrder){
			System.out.println("end of orders");
			return;
		}
		
		String order = orders.get(currentOrder);
		/**/ System.out.println(order);
		int end =  order.indexOf('*');
		String command = order.substring(0, end);
		String args = order.substring(end+1);
		
		for(String cmd : cmdJ){
			if(command.equals(cmd)){
				int actualOrder = currentOrder;
				int arg1 = orderLabels.get(args);
				functions.get(cmd).execute(arg1, 0);
				if(actualOrder == currentOrder)currentOrder += 2;
			}
		}
		
		for(String cmd : cmdRR){
			if(command.equals(cmd)){
				int p = args.indexOf(',');
				int arg1 = Integer.parseInt( args.substring(0, p) );
				int arg2 = Integer.parseInt( args.substring(p+1) );
				functions.get(cmd).execute(arg1, arg2);
				currentOrder += 4;
			}
		}
		
		for(String cmd : cmdRM){
			if(command.equals(cmd)){
				int p = args.indexOf(',');
				int arg1 = Integer.parseInt( args.substring(0, p) );
				int arg2 = varLabels.get( args.substring(p+1) );
				functions.get(cmd).execute(arg1, arg2);
				currentOrder += 4;
			}
		}
		
		/*prints for debugging*/
		for(int reg : regs) System.out.print(reg + " ");
		System.out.print("  flag= " + flag);
		System.out.println();
		/**/
		
		View.Instance.setRegisters();
		View.Instance.setMemoryCells();
		
		//@mrwasp Not sure how exactly it should works
		//TODO
		//Set the arrow pointing on register and memory panel 
		//View.Instance.updateValues(<last_modified_source>, <last_modified_destination>, <MODE>);
		//MODE:
		//		-View.RM (Register -> Memory cell)
		//		-View.RR (Register -> Register)
		//		-View.MR (Memory cell -> Register)
	}
	
}
