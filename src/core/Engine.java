package core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import view.View;

public class Engine implements ActionListener {
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
	
	private Timer timer;
	private int interval = 100;
	
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
		
		timer = new Timer(interval, this);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				View v = new View();
				v.setRegisters();
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
		return getVarFromAdress(id);
	}
	
	public int getVarFromAdress(int id){
		return vars.get(id);
	}
	
	public void setVar(int id, int value){
		vars.replace(id, value);
		View.Instance.updateMemCell((id-1024)/4);
	}
	
	public int getVarCount(){
		return vars.size();
	}
	
	public String getVarLabel(int address){
		for(Map.Entry<String, Integer> entry : varLabels.entrySet()){
			if(entry.getValue().intValue() == address){
				return entry.getKey();
			}
		}
		return address+":";
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
					View.Instance.updateValues(arg2, arg1, View.RR, View.ARITM);
				}
			});
		}
		
		functions.put("LR", new Function(){
			public void execute(int arg1, int arg2){
				setReg(arg1, getReg(arg2));
				View.Instance.updateValues(arg2, arg1, View.RR, View.LOAD);
			}
		});		
		
		// register - memory functions -------------------------------------------// 
		
		for(final String op : mathOps) {
			functions.put(op, new Function() {
				public void execute(int arg1, int arg2) {
					int reg = 0; switch(op) {
						case "A": reg = getReg(arg1) + getVarFromAdress(arg2); break;
						case "S": reg = getReg(arg1) - getVarFromAdress(arg2); break;
						case "M": reg = getReg(arg1) * getVarFromAdress(arg2); break;
						case "D": reg = getReg(arg1) / getVarFromAdress(arg2); break;
						case "C": reg = getReg(arg1) - getVarFromAdress(arg2); break;
					} if(!op.equals("C")) setReg(arg1, reg); setFlag(reg);
					View.Instance.updateValues((arg2-1024)/4, arg1, View.MR, View.ARITM);
				}
			});
		}
		
		functions.put("L", new Function(){
			public void execute(int arg1, int arg2){
				setReg(arg1, getVarFromAdress(arg2));
				View.Instance.updateValues((arg2-1024)/4, arg1, View.MR, View.LOAD);
			}
		});
		
		functions.put("LA", new Function(){
			public void execute(int arg1, int arg2){
				setReg(arg1, arg2);
				View.Instance.updateValues((arg2-1024)/4, arg1, View.MR, View.LOAD_ADDR);
			}
		});
		
		functions.put("ST", new Function(){
			public void execute(int arg1, int arg2){
				setVar(arg2, getReg(arg1));
				View.Instance.updateValues(arg1, (arg2-1024)/4, View.RM, View.STORE);
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
				View.Instance.updateValues(-1, -1, (byte) -1, (byte) -1);
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
		View.Instance.resetLastEdited();
		vars.clear();
		varLabels.clear();
		String[] lines = s.split("\n");
		for(String line : lines){
			int res[][] = Parser.getPos(line, true);
			
			if(res[1][0] < 0){
				//probably unnecessary if we block build when there are some errors in code
				System.out.println(line);
				System.out.println("error, incorrect command");
			}
			else{
				String label = line.substring(res[0][0], res[0][1] +1);
				String dir = line.substring(res[1][0], res[1][1] +1);
				String arg1 = (res[2][0] < 0) ? null : line.substring(res[2][0], res[2][1] +1);
				String arg2 = (res[3][0] < 0) ? null : line.substring(res[3][0], res[3][1] +1);

				int count = (arg1==null) ? 1 : Integer.parseInt(arg1);
				int value = (arg2==null) ? 0 : Integer.parseInt(arg2);
				
				if(arg2==null){
					Random r = new Random();
					addVar(label, r.nextInt(2000)-1000);
					for(int i=1; i<count; i++) addVar(r.nextInt(2000)-1000);
				}
				else{
					addVar(label, value);
					for(int i=1; i<count; i++) addVar(value);
				}
			}
		}
		System.out.println(vars);
		System.out.println(varLabels);
		
		//FIXME GUI - memory cells doesn't refresh automatically
		//Needed to coorectly display graphics
		View.Instance.setMemoryCells();
	}
	
	//@mrwasp
	public void buildOrdersFromString(String s){
		lastOrder = currentOrder = 2048;
		orders.clear();
		orderLabels.clear();
		String[] lines = s.split("\n");
		for(String line : lines){
			int res[][] = Parser.getPos(line, false);
			
			if(res[1][0] < 0){
				//probably unnecessary if we block build when there are some errors in code
				System.out.println(line);
				System.out.println("error, incorrect command");
			}
			else{
				String label = (res[0][0] < 0) ? null : line.substring(res[0][0], res[0][1] +1);
				String order = line.substring(res[1][0]);
				
				int size = (Parser.parse(line, false) == JUMP) ? 2 : 4;
				addOrder(label, order, size);
			}
		}
		System.out.println(orders);
		System.out.println(orderLabels);
		
		//FIXME GUI - memory cells doesn't store orders
	}
	
	//@mrwasp
	public void run(){
		timer.start();
	}
	
	
	//@mrwasp
	public void step(){
		/**/System.out.print(currentOrder + " - ");
		if(currentOrder >= lastOrder){
			System.out.println("end of orders");
			return;
		}
		
		String order = orders.get(currentOrder);
		/**/ System.out.println(order);
		
		int res[][] = Parser.getPos(order, false);
		int type = Parser.parse(order, false);
		int actualOrder = currentOrder;
		String command = order.substring(res[1][0], res[1][1] +1);
		String arg1s = (res[2][0] < 0) ? null :  order.substring(res[2][0], res[2][1] +1);
		String arg2s = (res[3][0] < 0) ? null :  order.substring(res[3][0], res[3][1] +1);
		
		//arg1 = label if JUMP, regId if RR/RM
		//arg2 = regId if RR, memId if RM, 0 if JUMP
		//actualOrder - if jump change current order we don't jump to next command
		
		int arg1 = (type==JUMP) ? orderLabels.get(arg1s) : Integer.parseInt(arg1s);
		int arg2 = (type==RR) ? Integer.parseInt(arg2s) : 0;
		if(type==RM){
			if(varLabels.containsKey(arg2s)) arg2 = varLabels.get(arg2s);
			else{
				int p1 = arg2s.indexOf('(');
				int p2 = arg2s.indexOf(')');
				int offset = Integer.parseInt(arg2s.substring(0, p1));
				int reg = Integer.parseInt(arg2s.substring(p1+1, p2));
				arg2 = getReg(reg) + offset;
			}
		}
		
		functions.get(command).execute(arg1, arg2);
		if(actualOrder == currentOrder) currentOrder += (type==JUMP) ? 2 : 4;

		/*prints for debugging*/
		for(int reg : regs) System.out.print(reg + " ");
		System.out.print("  flag= " + flag);
		System.out.println();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		step();
		if(currentOrder >= lastOrder){
			timer.stop();
		}
	}
	
	public void setRunInterval(int interval){
		this.interval = interval;
		timer.setDelay(interval);
	}
	
	public void pause(){
		timer.stop();
	}
	
}
