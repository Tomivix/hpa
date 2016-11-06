package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
	// public boolean parse(String line) returns boolean
	// checks if the raw command provided by user has correct HPA-compliant syntax
	// returns true only if the command has valid syntax (can possibly be executed)
	// this method (so far) doesn't check if specified memory cells were declared!
	// previous label checking regex ([A-Z0-9]+ : |^)
	public static int parse(String line, boolean mode) {
		int type = 0;
		if(!mode) {
			type = (line.matches("([A-Z][A-Z0-9]* : |^)(AR|SR|MR|DR|CR|LR) [0-9]+,[0-9]+")) ? 1 : type;
			type = (line.matches("([A-Z][A-Z0-9]* : |^)(A|S|M|D|C|L|LA|ST) [0-9]+,([A-Z][A-Z0-9]*|[0-9]+\\([0-9]+\\))")) ? 2 : type;
			type = (line.matches("([A-Z][A-Z0-9]* : |^)(J|JN|JP|JZ) ([A-Z][A-Z0-9]*|[0-9]+\\([0-9]+\\))")) ? 3 : type;
		} else {
			type = (line.matches("[A-Z][A-Z0-9]* : DC ([0-9]+\\*|)(INTEGER|INT)\\(-?[0-9]+\\)")) ? 4 : type;
			type = (line.matches("[A-Z][A-Z0-9]* : DS ([0-9]+\\*|)(INTEGER|INT)")) ? 5 : type;
		}
		return type;
	}
	//@param mode: true - directives, false - orders
	// takes string line and type of command from parse() (subject to change)
	// returns array res containing start/end index of each part depending on type
	// (0 - possible label) 1 - command name 2 - first parameter (3 - second parameter)
	// -1 means no match was found (part with [-1,-1] return doesn't exist in given string)
	public static int[][] getPos(String line, boolean mode) {		
		int temp; int res[][] = new int[4][2];		
		for(int[] sub : res) Arrays.fill(sub, -1);
		int type; if((type = Parser.parse(line, mode)) == 0) {
			/*this.flag = ERROR;*/ return res; // <-- what should I do with flag setting?
		} 
		if((temp = line.indexOf(" : ") + 1) != 0) {
			res[0][1] = temp - 2; res[0][0] = 0;
			temp += 2; line = line.substring(temp);
		}
		res[1][1] = line.indexOf(" ") + temp - 1;
		res[1][0] = temp; res[2][0] = res[1][1] + 2;
		if(type < 3) {
			res[2][1] = line.indexOf(",") + temp - 1;
			res[3][1] = line.length() + temp - 1;
			res[3][0] = res[2][1] + 2;
		} else if(type > 3) { int temp2; 
			if((temp2 = line.indexOf("*") - 1) != -2)
			res[2][1] = temp + temp2; else res[2][0] = -1;
			if(type == 4) {				
				res[3][1] = line.indexOf(")") + temp - 1;
				res[3][0] = line.indexOf("(") + temp + 1;
			}
		} else res[2][1] = line.length() + temp - 1;
		return res;
	}
	
	// takes all the lines of the code and returns int[][] containing indexes
	// return syntax (different from suggested by Aicedosh to match above functions)
	// int[0] = {Label1Index, Label1Length, Label2Index, Label2Length, ...}
	// int[1] = {Order1Index, Order1Length, Order2Index, Order2Length, ...}
	// int[2] = {Parameter1Index, Parameter1Length, Parameter2Index, Parameter2Length, ...}
	// int[3] = {InvalidLine1Index, InvalidLine1Length, InvalidLine2Index, InvalidLine2Length, ...}
	public static int[][] split(String raw, boolean mode) {
		if(raw.length() == 0) return new int[][]{new int[]{},new int[]{},new int[]{},new int[]{}};
	       List<List<Integer>> temp = new ArrayList<List<Integer>>();
	       for(int i = 0; i < 4; i++) temp.add(new ArrayList<Integer>());
		String[] lines = raw.split("\n"); int last = 0;
		for(String line : lines) {
			if(line.length() == 0) {last++; continue;} 
			if(Parser.parse(line, mode) == 0) {
				temp.get(3).add(last); temp.get(3).add(line.length());
			} else {
				int[][] res = Parser.getPos(line, mode);
				for(int i = 0; i < 4; i++) {
					if(!Arrays.equals(res[i], new int[]{-1,-1})) {
						int j = (i == 3) ? 2 : i;
						temp.get(j).add(res[i][0] + last);
						temp.get(j).add(res[i][1] - res[i][0] + 1);
					}
				}
			} last += line.length() + 1;
		} int[][] res = new int[4][];
		for(int i = 0; i < 4; i++) {
			res[i] = new int[temp.get(i).size()]; int j = 0;
			for(Integer pos : temp.get(i)) res[i][j++] = pos;				
		} return res;
	}
}
