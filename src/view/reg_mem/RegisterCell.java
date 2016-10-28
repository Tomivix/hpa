package view.reg_mem;

import view.View;

public class RegisterCell extends EditableCell {
	private static final long serialVersionUID = 1L;

	private int index; 
	public RegisterCell(int topX, int topY, int index) {
		super(topX, topY);
		this.index = index;
		setupLabels();
	}
	
	@Override
	protected int getValue() {
		return View.registers[index];
	}

	@Override
	protected String getLabel() {
		
		return index+":";
	}

}
