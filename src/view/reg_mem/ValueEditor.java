package view.reg_mem;

import javax.swing.JDialog;

public class ValueEditor {
	public static void displayCellValue(int index, String label){
		displayValue(false, index, label);
	}
	
	public static void displayRegisterValue(int index){
		displayValue(true, index, null);
	}
	
	private static void displayValue(boolean isRegister, int index, String label ){
		JDialog valueFrame = new JDialog();
		String title = "Edit: " + (isRegister ? "Register" : "Memory Cell") + " " + index;
		if(label != null){
			title += " (" + label + ")";
		}
		valueFrame.setTitle(title);
		valueFrame.setSize(500, 200);
		valueFrame.setLocationRelativeTo(null);
		valueFrame.setVisible(true);
	}
}
