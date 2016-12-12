package core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import view.View;

public class Engine implements ActionListener {
	//const values
	public static final int ZERO = 0, POSITIVE = 1, NEGATIVE = -1, ERROR = 3;
	public static final int RR = 1, RM = 2, JUMP = 3;
	public static final int FIRST_VAR = 1024, FIRST_ORDER = 2048;
	
	public static Engine current;
	
	private ArrayList<String> orders;
	private HashMap<String, Integer> orderLabels;
	private HashMap<Integer, Integer> orderIndexes; // address -> index
	private ArrayList<Integer> vars;
	private HashMap<String, Integer> varLabels;
	private int[] regs;
	//history stores info in 9-int array: {orderIndex, flag, changed reg (or -1), changed cell (or -1), changed value, 4x View ops}, max 1000 steps 
	private LinkedList<Integer[]> history;
	
	private int currentOrder; //index of order, not address
	private int flag;
	
	private Timer timer;
	private int interval = 100;
	
	public Engine(){
		current = this;
		orders = new ArrayList<>();
		orderLabels = new HashMap<>();
		orderIndexes = new HashMap<>();
		vars = new ArrayList<>();
		varLabels = new HashMap<>();
		history = new LinkedList<>();
		regs = new int[16];
		
		for(@SuppressWarnings("unused") int reg : regs) reg = new Random().nextInt();
		regs[14] = FIRST_VAR;
		regs[15] = FIRST_ORDER;
		currentOrder = 0;
		
		flag = ERROR;
		
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
		varLabels.put(label, vars.size()*4 + regs[14]);
		vars.add(value);
	}
	
	public int getVar(int id){
		return vars.get(id);
	}
	
	public int getVar(String label){
		int id = varLabels.get(label);
		return getVarFromAddress(id);
	}

	public int getVarFromAddress(int address){
		return vars.get((address-regs[14])/4);
	}
		
	public int getVarAddress(int id){
		return id*4 + regs[14];
	}
	
	public void setVar(int id, int value){
		vars.set(id, value);
		View.Instance.updateMemCell(id);
	}
	
	public int getVarCount(){
		return vars.size();
	}
	
	public String getVarLabel(int id){
		int address = getVarAddress(id);
		for(Map.Entry<String, Integer> entry : varLabels.entrySet()){
			if(entry.getValue().intValue() == address) return entry.getKey();
		} return "";
	}
	
	public void addOrder(String label, String order, int address){
		if(label != null) orderLabels.put(label, address);
		orderIndexes.put(address, orders.size());
		orders.add(order);
	}
	
	public int getFlag(){
		return flag;
	}
	
	public void setFlag(int value){
		flag = (value > 0) ? POSITIVE : (value < 0) ? NEGATIVE : ZERO;
	}
	
	//replaced functions
	public void execute(int arg1, int arg2, int type, String cmd){
		if(type==JUMP){
			history.addLast(new Integer[]{currentOrder, flag, -1, -1, 0, -1, -1, -1, -1});
			if(cmd.equals("J") ||
					(cmd.equals("JP") && flag==POSITIVE ) ||
					(cmd.equals("JN") && flag==NEGATIVE ) ||
					(cmd.equals("JZ") && flag==ZERO )) currentOrder = orderIndexes.get(arg1);
			else currentOrder++;
			
			View.Instance.updateValues(-1, -1, (byte) -1, (byte) -1);
		}
		else{
			int varId = (type==RM) ? (arg2-regs[14])/4 : 0;
			int value = (type==RR) ? regs[arg2] : vars.get((arg2-regs[14])/4);
			int oldValue = regs[arg1];
			int result = regs[arg1];
			int vArg1 = (type==RR) ? arg2 : (arg2-regs[14])/4;
			int vArg2 = arg1;
			byte vCmdType = (type==RR) ? View.RR : View.MR;
			byte vOpType = View.ARITM;
			switch(cmd){
				case "A": case "AR": result+=value; break;
				case "S": case "SR": 
				case "C": case "CR": result-=value; break;
				case "M": case "MR": result*=value; break;
				case "D": case "DR": result/=value; break;
				case "L": case "LR": result=value; vOpType = View.LOAD; break;
				case "LA": result=arg2; vOpType = View.LOAD_ADDR; break;
			}
			if(!cmd.startsWith("C")) regs[arg1] = result;
			if(!cmd.startsWith("L")) setFlag(result);
			if(cmd.equals("ST")){
				oldValue = getVar(varId);
				setVar(varId, result);
				int temp = vArg2;
				vArg2 = vArg1;
				vArg1 = temp;
				vCmdType = View.RM;
				vOpType = View.STORE;
			}
			history.addLast(new Integer[]{
					currentOrder, flag, 
					cmd.equals("ST") ? -1 : arg1,
					cmd.equals("ST") ? varId : -1,
					oldValue,
					vArg1, vArg2, (int)vCmdType, (int)vOpType});
			if(history.size() > 1000) history.removeFirst();
			
			currentOrder++;
			View.Instance.updateValues(vArg1, vArg2, vCmdType, vOpType);
		}
	}
	
	public void buildDirectivesFromString(String s){
		View.Instance.resetLastEdited();
		vars.clear(); varLabels.clear();
		String[] lines = s.split("\n");
		for(String line : lines){
			int res[][] = Parser.getPos(line, true);
			if(res[1][0] < 0) System.out.println("Invalid syntax: " + line);
			else {
				String label = line.substring(res[0][0], res[0][1] + 1);
				String arg1 = (res[2][0] < 0) ? null : line.substring(res[2][0], res[2][1] + 1);
				String arg2 = (res[3][0] < 0) ? null : line.substring(res[3][0], res[3][1] + 1);
				
				int count = (arg1 == null) ? 1 : Integer.parseInt(arg1);
				int value = (arg2 == null) ? 0 : Integer.parseInt(arg2);
				
				if(arg2 == null){
					addVar(label, new Random().nextInt(2000) - 1000);
					for(int i = 1; i < count; i++) vars.add(new Random().nextInt(2000) - 1000);
				} else {
					addVar(label, value); for(int i = 1; i < count; i++) vars.add(value);
				}
			}
		}
		View.Instance.setMemoryCells();
	}
	
	public void buildOrdersFromString(String s){
		int address = currentOrder = 0;
		orders.clear(); orderLabels.clear(); orderIndexes.clear(); history.clear();
		String[] lines = s.split("\n");
		for(String line : lines){
			int res[][] = Parser.getPos(line, false);
			if(res[1][0] < 0) System.out.println("Invalid syntax: " + line);
			else {
				String label = (res[0][0] < 0) ? null : line.substring(res[0][0], res[0][1] + 1);
				String order = line.substring(res[1][0]);
				address += (Parser.parse(line, false) == 3) ? 2 : 4;
				addOrder(label, order, regs[15] + address);
			}
		}
	}

	public void run(){
		timer.start();
	}
	
	public void step(){
		if(currentOrder >= orders.size()) return;
		
		String order = orders.get(currentOrder);
		int res[][] = Parser.getPos(order, false);
		int type = Parser.parse(order, false);
		String command = order.substring(res[1][0], res[1][1] + 1);
		String arg1s = (res[2][0] < 0) ? null : order.substring(res[2][0], res[2][1] + 1);
		String arg2s = (res[3][0] < 0) ? null : order.substring(res[3][0], res[3][1] + 1);
		
		//arg1 = label if JUMP, regId if RR/RM
		//arg2 = regId if RR, memId if RM, 0 if JUMP
		//actualOrder - if jump change current order we don't jump to next command
		int arg1 = (type == JUMP) ? orderLabels.get(arg1s) : Integer.parseInt(arg1s);
		int arg2 = (type == RR) ? Integer.parseInt(arg2s) : 0;
		if(type == RM){
			if(varLabels.containsKey(arg2s)) arg2 = varLabels.get(arg2s);
			else {
				int p1 = arg2s.indexOf('('), p2 = arg2s.indexOf(')');
				int offset = Integer.parseInt(arg2s.substring(0, p1));
				int reg = Integer.parseInt(arg2s.substring(p1 + 1, p2));
				arg2 = getReg(reg) + offset;
			}
		}
		View.Instance.highlightLine(currentOrder+1);
		execute(arg1, arg2, type, command);
		
		//prints for debugging
		System.out.println(currentOrder + " - " + order);
		for(int reg : regs) System.out.print(reg + " ");
		System.out.println("  flag = " + flag);
	}
	
	
	
	public void backStep(){
		if(history.isEmpty()) return;
		Integer[] step = history.removeLast();
		currentOrder = step[0];
		flag = step[1];
		if(step[2] != -1) setReg(step[2], step[4]);
		if(step[3] != -1) setVar(step[3], step[4]);
		View.Instance.updateValues(step[5], step[6], (byte) (int)step[7], (byte) (int)step[8]);
		View.Instance.highlightLine(currentOrder+1);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		step(); if(currentOrder >= orders.size()) timer.stop();
	}

	public void setRunInterval(int interval){
		this.interval = interval; timer.setDelay(interval);
	}
	
	public void pause(){
		timer.stop();
	}	
}
