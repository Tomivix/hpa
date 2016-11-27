package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
	// public static int parse(String line, boolean mode) returns int (\\s*:\\s*)
	// checks if the raw command provided by user has correct HPA-compliant syntax
	// returns true only if the command has valid syntax (can possibly be executed)
	// this method (so far) doesn't check if specified memory cells were declared!
	public static int parse(String line, boolean mode) {
		int type = 0;
		if(!mode) {
			type = (line.matches("\\s*([A-Z][A-Z0-9]*\\s*:\\s*|)(AR|SR|MR|DR|CR|LR)\\s+[0-9]+\\s*,\\s*"
					+ "[0-9]+\\s*")) ? 1 : type;
			type = (line.matches("\\s*([A-Z][A-Z0-9]*\\s*:\\s*|)(A|S|M|D|C|L|LA|ST)\\s+[0-9]+\\s*,\\s*"
					+ "([A-Z][A-Z0-9]*|[0-9]+\\s*\\(\\s*[0-9]+\\s*\\))\\s*")) ? 2 : type;
			type = (line.matches("\\s*([A-Z][A-Z0-9]*\\s*:\\s*|)(J|JN|JP|JZ)\\s+([A-Z][A-Z0-9]*|[0-9]+"
					+ "\\s*\\(\\s*[0-9]+\\s*\\))\\s*")) ? 3 : type;
		} else {
			type = (line.matches("\\s*[A-Z][A-Z0-9]*\\s*:\\s*DC\\s+([0-9]+\\s*\\*\\s*|)(INTEGER|INT)"
					+ "\\s*\\(\\s*-?[0-9]+\\s*\\)\\s*")) ? 4 : type;
			type = (line.matches("\\s*[A-Z][A-Z0-9]*\\s*:\\s*DS\\s+([0-9]+\\s*\\*\\s*|)(INTEGER|INT)"
					+ "\\s*")) ? 5 : type;
		} return type;
	}
	// just a simple overlay function for compatibility with the newer less strict syntax parsing
	// takes exactly the same input as getPosOld and converts it to be compatible with old syntax
	// then calls getPosOld with modified input string and gets positions of parts in that string
	// finally it looks for those parts in the original string and returns their original indexes
	public static int[][] getPos(String line, boolean mode) {
		String temp = line.trim().replaceAll("\\s+", " ").replaceAll("( (?=[():*,-])|(?<=[():,*]) )","");
		int[][] res = Parser.getPosOld(temp, mode);
		int len, cut = 0;
		for(int pos[] : res) {
			if(!Arrays.equals(pos, new int[]{-1,-1})) {
				len = pos[1] - pos[0];
				pos[0] = line.indexOf(temp.substring(pos[0], pos[1] + 1)) + cut;
				pos[1] = pos[0] + len;
				line = line.substring(pos[1] - cut + 1);
				cut = pos[1] + 1;
			}
		} return res;
	}
	// takes string line and type of command from parse() (subject to change)
	// returns array res containing start/end index of each part depending on type
	// (0 - possible label) 1 - command name 2 - first parameter (3 - second parameter)
	// -1 means no match was found (part with [-1,-1] return doesn't exist in given string)
	// parameter mode switches between directives and orders: true - directives, false - orders
	public static int[][] getPosOld(String line, boolean mode) {
		int temp; int res[][] = new int[4][2];
		for(int[] sub : res) Arrays.fill(sub, -1);
		int type; if((type = Parser.parse(line, mode)) == 0) {
			/*this.flag = ERROR;*/ return res; // <-- what should I do with flag setting?
		} 
		if((temp = line.indexOf(":") + 1) != 0) {
			res[0][1] = temp - 2; res[0][0] = 0;
			line = line.substring(temp);
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
